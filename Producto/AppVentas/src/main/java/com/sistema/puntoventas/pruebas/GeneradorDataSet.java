package com.sistema.puntoventas.pruebas;

import com.sistema.puntoventas.conexion.DbManager;
import com.sistema.puntoventas.modelo.Role;
import com.sistema.puntoventas.modelo.TipoMovimiento;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class GeneradorDataSet {

    private static final String URL = "jdbc:sqlite:DBventasInventario.db";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Faker faker = new Faker(new Locale("es", "CL"));
    private final Random random = new Random();

    private static class ProductoSeed {
        int id;
        String nombre;
        double precioCompra;
        double precioVenta;
        TipoProducto tipo;
        int stock;

        ProductoSeed(int id, String nombre, double precioCompra, double precioVenta, TipoProducto tipo, int stock) {
            this.id = id;
            this.nombre = nombre;
            this.precioCompra = precioCompra;
            this.precioVenta = precioVenta;
            this.tipo = tipo;
            this.stock = stock;
        }
    }

    public static void main(String[] args) {
        new GeneradorDataSet().generar();
    }

    public void generar() {
        DbManager dbManager = new DbManager();
        dbManager.crearTodasLasTablas();

        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            ejecutar(conn, "PRAGMA foreign_keys = ON");

            // Asegura el esquema minimo si alguna tabla no se creo por SQL previo.


            limpiarTablas(conn);

            List<Integer> categorias = insertarCategorias(conn);
            List<Integer> usuarios = insertarUsuarios(conn);
            List<ProductoSeed> productos = insertarProductos(conn, categorias);
            List<Integer> platillos = insertarPlatillos(conn, categorias);
            insertarDetallePlatillo(conn, platillos, productos);

            generarActividad30Dias(conn, usuarios, productos, platillos);

            conn.commit();
            System.out.println("Dataset generado correctamente");
        } catch (Exception e) {
            System.err.println("Error al generar dataset: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarTablas(Connection conn) throws SQLException {
        ejecutar(conn, "DELETE FROM detalle_venta");
        ejecutar(conn, "DELETE FROM venta");
        ejecutar(conn, "DELETE FROM auditoria_eventos");
        ejecutar(conn, "DELETE FROM historial_inventario");
        ejecutar(conn, "DELETE FROM detalle_platillo");
        ejecutar(conn, "DELETE FROM platillo");
        ejecutar(conn, "DELETE FROM producto");
        ejecutar(conn, "DELETE FROM categoria");
        ejecutar(conn, "DELETE FROM usuario");
    }

    private List<Integer> insertarCategorias(Connection conn) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "INSERT INTO categoria(nombreCategoria, descripcion, activa) VALUES (?, ?, ?)";

        String[][] data = {
                {"Bebidas", "Bebidas frias y calientes"},
                {"Panaderia", "Pan y masas para elaboracion"},
                {"Comida Rapida", "Productos listos para venta"},
                {"Insumos", "Materia prima de cocina"},
                {"Proteinas", "Carnes y derivados"},
                {"Verduras", "Vegetales frescos"},
                {"Salsas", "Salsas y condimentos"},
                {"Postres", "Productos dulces"}
        };

        for (String[] c : data) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c[0]);
                ps.setString(2, c[1]);
                ps.setInt(3, 1);
                ps.executeUpdate();
                ids.add(leerGeneratedKey(ps));
            }
        }
        return ids;
    }

    private List<Integer> insertarUsuarios(Connection conn) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "INSERT INTO usuario(nombre, apellido, rut, password, telefono, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Admin fijo para facilitar pruebas de login
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Admin");
            ps.setString(2, "Sistema");
            ps.setString(3, "12345678-9");
            ps.setString(4, "admin");
            ps.setString(5, "999999999");
            ps.setString(6, Role.ADMIN.name());
            ps.setInt(7, 1);
            ps.executeUpdate();
            ids.add(leerGeneratedKey(ps));
        }

        for (int i = 0; i < 6; i++) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                String rut = String.format("%08d-%d", 10000000 + i, (i + 1) % 10);
                ps.setString(1, faker.name().firstName());
                ps.setString(2, faker.name().lastName());
                ps.setString(3, rut);
                ps.setString(4, "1234");
                ps.setString(5, faker.phoneNumber().cellPhone());
                ps.setString(6, Role.VENDEDOR.name());
                ps.setInt(7, 1);
                ps.executeUpdate();
                ids.add(leerGeneratedKey(ps));
            }
        }

        return ids;
    }

    private List<ProductoSeed> insertarProductos(Connection conn, List<Integer> categorias) throws SQLException {
        List<ProductoSeed> list = new ArrayList<>();
        String sql = "INSERT INTO producto(nombre, precioCompra, precioVenta, idCategoria, fechaVenc, stockActual, stockMinimo, imagen, unidadMedida, cantidad, tipoProducto) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<String> nombres = Arrays.asList(
                "Harina Panadera", "Azucar Blanca", "Aceite Vegetal", "Leche Entera", "Cafe Molido",
                "Bebida Cola", "Agua Mineral", "Jugo Naranja", "Pan Hamburguesa", "Pan HotDog",
                "Pollo Desmenuzado", "Carne Molida", "Queso Mozzarella", "Lechuga Fresca", "Tomate",
                "Salsa Tomate", "Papas Fritas Bolsa"
        );

        for (int i = 0; i < nombres.size(); i++) {
            TipoProducto tipo;
            double precioVenta;
            double cantidadDefault;
            
            if (i < 5) {
                // Primeros 5: SOLO_INVENTARIO
                tipo = TipoProducto.SOLO_INVENTARIO;
                precioVenta = 1; // Sin precio de venta
                cantidadDefault = 1000; // Gramos por default
            } else {
                // Resto: DIRECTO
                tipo = TipoProducto.DIRECTO;
                double compra = 700 + random.nextInt(5500);
                precioVenta = compra * (1.35 + (random.nextDouble() * 0.5));
                cantidadDefault = 1; // Cantidad default = 1
            }

            UnidadMedida unidad = (i % 3 == 0) ? UnidadMedida.UNIDAD : (i % 3 == 1 ? UnidadMedida.GRAMOS : UnidadMedida.MILILITROS);
            int stock = 60 + random.nextInt(180);
            int stockMin = 15 + random.nextInt(30);
            double compra = 700 + random.nextInt(5500);
            LocalDate venc = LocalDate.now().plusDays(45 + random.nextInt(120));

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombres.get(i));
                ps.setDouble(2, redondear(compra));
                ps.setDouble(3, tipo == TipoProducto.SOLO_INVENTARIO ? 0 : redondear(precioVenta));
                ps.setInt(4, categorias.get(i % categorias.size()));
                ps.setString(5, venc.toString());
                ps.setInt(6, stock);
                ps.setInt(7, stockMin);
                ps.setString(8, "img_producto_" + (i + 1) + ".png");
                ps.setString(9, unidad.name());
                ps.setDouble(10, cantidadDefault);
                ps.setString(11, tipo.name());
                ps.executeUpdate();

                int id = leerGeneratedKey(ps);
                list.add(new ProductoSeed(id, nombres.get(i), redondear(compra), 
                        tipo == TipoProducto.SOLO_INVENTARIO ? 0 : redondear(precioVenta), tipo, stock));
            }
        }

        return list;
    }

    private List<Integer> insertarPlatillos(Connection conn, List<Integer> categorias) throws SQLException {
        List<Integer> ids = new ArrayList<>();

        boolean tieneFabricables = existeColumna(conn, "platillo", "fabricables");
        boolean tieneStockActual = existeColumna(conn, "platillo", "stockActual");

        String sql;
        if (tieneFabricables) {
            sql = "INSERT INTO platillo(nombre, precio, idCategoria, estado, costoProduccion, fabricables, tipoProducto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else if (tieneStockActual) {
            sql = "INSERT INTO platillo(nombre, precio, idCategoria, estado, costoProduccion, stockActual, tipoProducto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO platillo(nombre, precio, idCategoria, estado, costoProduccion, tipoProducto) VALUES (?, ?, ?, ?, ?, ?)";
        }

        String[] nombres = {
                "Empanada de pollo", "Hamburguesa clasica", "Pizza familiar", "Ensalada cesar",
                "Cafe latte", "Torta de chocolate"
        };

        for (int i = 0; i < nombres.length; i++) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                double precio = 3500 + random.nextInt(9000);
                double costo = precio * (0.45 + random.nextDouble() * 0.2);
                int categoria = categorias.get((i + 2) % categorias.size());

                ps.setString(1, nombres[i]);
                ps.setDouble(2, redondear(precio));
                ps.setInt(3, categoria);
                ps.setInt(4, 1);
                ps.setDouble(5, redondear(costo));

                if (tieneFabricables || tieneStockActual) {
                    ps.setInt(6, 10 + random.nextInt(30));
                    ps.setString(7, TipoProducto.PLATILLO.name());
                } else {
                    ps.setString(6, TipoProducto.PLATILLO.name());
                }

                ps.executeUpdate();
                ids.add(leerGeneratedKey(ps));
            }
        }

        return ids;
    }

    private void insertarDetallePlatillo(Connection conn, List<Integer> platillos, List<ProductoSeed> productos) throws SQLException {
        String sql = "INSERT INTO detalle_platillo(idPlatillo, idProducto, cantidadIngrediente) VALUES (?, ?, ?)";

        List<ProductoSeed> ingredientes = new ArrayList<>();
        for (ProductoSeed p : productos) {
            if (p.tipo == TipoProducto.SOLO_INVENTARIO) {
                ingredientes.add(p);
            }
        }

        for (Integer idPlatillo : platillos) {
            int cantidadIngredientes = 3 + random.nextInt(2);
            for (int i = 0; i < cantidadIngredientes; i++) {
                ProductoSeed ing = ingredientes.get(random.nextInt(ingredientes.size()));
                double cantidad = 50 + random.nextInt(300);

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idPlatillo);
                    ps.setInt(2, ing.id);
                    ps.setDouble(3, cantidad);
                    ps.executeUpdate();
                }
            }
        }
    }

    private void generarActividad30Dias(Connection conn, List<Integer> usuarios, List<ProductoSeed> productos, List<Integer> platillos) throws SQLException {
        List<ProductoSeed> vendibles = new ArrayList<>();
        for (ProductoSeed p : productos) {
            if (p.tipo != TipoProducto.SOLO_INVENTARIO) {
                vendibles.add(p);
            }
        }
        boolean detalleTienePlatillo = existeColumna(conn, "detalle_venta", "idPlatillo");
        LocalDate inicio = LocalDate.now().minusDays(59); // 60 días de historial
        int idUsuario = usuarios.get(0);

        System.out.println("Generando 60 días de ventas constantes para TODOS los productos...");

        // 1. FORZAR VENTAS DIARIAS: Todos los días vendemos entre 3 y 6 unidades de cada producto
        // Esto le enseña a la IA que la demanda diaria promedio es de ~4.5 unidades.
        for (int d = 0; d < 60; d++) {
            LocalDateTime fecha = inicio.plusDays(d).atTime(12 + random.nextInt(8), random.nextInt(60));
            double totalVenta = 0;
            List<ProductoSeed> itemsVenta = new ArrayList<>();

            for (ProductoSeed p : vendibles) {
                int cantidadComprada = 3 + random.nextInt(4); // 3 a 6 unidades
                for (int c = 0; c < cantidadComprada; c++) {
                    itemsVenta.add(p);
                    totalVenta += p.precioVenta;
                }
            }

            int idVenta = insertarVenta(conn, fecha, idUsuario, totalVenta);
            insertarDetalleVenta(conn, idVenta, itemsVenta, platillos, detalleTienePlatillo);

            // Registrar salidas
            for (ProductoSeed p : itemsVenta) {
                insertarMovimiento(conn, p.id, TipoMovimiento.SALIDA_VENTA, 1, fecha, "Venta IA", idUsuario);
            }
        }

        System.out.println("Ajustando stock final para mostrar todos los niveles de riesgo...");

        // 2. TRUCO FINAL: Sabiendo que la IA predecirá una demanda de ~5 diarias,
        // manipulamos el stock actual de los productos para forzar los resultados.
        for (int i = 0; i < vendibles.size(); i++) {
            ProductoSeed p = vendibles.get(i);
            int stockMagico;

            // Dividimos los productos en 5 grupos según su posición en la lista
            if (i % 5 == 0) {
                stockMagico = 0; // CRÍTICO: 0 stock
            } else if (i % 5 == 1) {
                stockMagico = 10; // ALTO: 10 stock / 5 diarios = 2 días restantes
            } else if (i % 5 == 2) {
                stockMagico = 25; // MEDIO: 25 stock / 5 diarios = 5 días restantes
            } else if (i % 5 == 3) {
                stockMagico = 50; // PREVENTIVO: 50 stock / 5 diarios = 10 días restantes
            } else {
                stockMagico = 150; // BAJO: 150 stock / 5 diarios = 30 días restantes
            }

            // Actualizar directo a la BD para engañar a la IA
            actualizarStockProducto(conn, p.id, stockMagico);
            LocalDateTime fechaFin = inicio.plusDays(59).atTime(23, 59);
            insertarMovimiento(conn, p.id, TipoMovimiento.ENTRADA, stockMagico, fechaFin, "Ajuste IA", idUsuario);
        }
    }

    private int insertarVenta(Connection conn, LocalDateTime fecha, int idUsuario, double total) throws SQLException {
        String sql = "INSERT INTO venta(fechaHora, idUsuario, totalVenta, tipoPago, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fecha.format(FMT));
            ps.setInt(2, idUsuario);
            ps.setDouble(3, redondear(total));
            ps.setString(4, metodoPagoRandom());
            ps.setString(5, "Venta generada automaticamente para pruebas");
            ps.setInt(6, 1);
            ps.executeUpdate();
            return leerGeneratedKey(ps);
        }
    }

    private void insertarDetalleVenta(Connection conn, int idVenta, List<ProductoSeed> items, List<Integer> platillos, boolean tieneIdPlatillo) throws SQLException {
        String sql = tieneIdPlatillo
                ? "INSERT INTO detalle_venta(idVenta, idProducto, idPlatillo) VALUES (?, ?, ?)"
                : "INSERT INTO detalle_venta(idVenta, idProducto) VALUES (?, ?)";

        for (ProductoSeed item : items) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idVenta);
                ps.setInt(2, item.id);
                if (tieneIdPlatillo) {
                    if (item.tipo == TipoProducto.PLATILLO && !platillos.isEmpty()) {
                        ps.setInt(3, platillos.get(random.nextInt(platillos.size())));
                    } else {
                        ps.setNull(3, java.sql.Types.INTEGER);
                    }
                }
                ps.executeUpdate();
            }
        }
    }

    private void insertarMovimiento(Connection conn, int idProducto, TipoMovimiento tipo, int cantidad,
                                    LocalDateTime fecha, String motivo, int idUsuario) throws SQLException {
        String sql = "INSERT INTO historial_inventario(idProducto, tipoMovimiento, cantidad, fecha, motivo, idUsuario) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setString(2, tipo.name());
            ps.setInt(3, cantidad);
            ps.setString(4, fecha.format(FMT));
            ps.setString(5, motivo);
            ps.setInt(6, idUsuario);
            ps.executeUpdate();
        }
    }

    private void insertarAuditoria(Connection conn, LocalDateTime fecha, String modulo, String entidad,
                                   String accion, Integer idEntidad, String detalle, Integer idUsuario) throws SQLException {
        String sql = "INSERT INTO auditoria_eventos(fecha, modulo, entidad, accion, idEntidad, detalle, idUsuario) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha.format(FMT));
            ps.setString(2, modulo);
            ps.setString(3, entidad);
            ps.setString(4, accion);
            if (idEntidad == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, idEntidad);
            }
            ps.setString(6, detalle);
            if (idUsuario == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, idUsuario);
            }
            ps.executeUpdate();
        }
    }

    private void actualizarStockProducto(Connection conn, int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE producto SET stockActual = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    private boolean existeColumna(Connection conn, String tabla, String columna) throws SQLException {
        String sql = "PRAGMA table_info(" + tabla + ")";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeTabla(Connection conn, String tabla) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tabla);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int leerGeneratedKey(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo leer el ID generado.");
    }

    private void ejecutar(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    private String metodoPagoRandom() {
        int n = random.nextInt(3);
        if (n == 0) return "EFECTIVO";
        if (n == 1) return "TARJETA";
        return "TRANSFERENCIA";
    }

    private double redondear(double n) {
        return Math.round(n * 100.0) / 100.0;
    }
}
