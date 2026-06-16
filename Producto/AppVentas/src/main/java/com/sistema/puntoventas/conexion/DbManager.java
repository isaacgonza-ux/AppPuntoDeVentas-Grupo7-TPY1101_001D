package com.sistema.puntoventas.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbManager {
    // URL de conexión consistente con el resto de los repositorios
    private final String url = "jdbc:sqlite:DBventasInventario.db";

    public void conectarBD() {
        try (var conn = DriverManager.getConnection(this.url)) {
            System.out.println("Conexión a SQLite establecida correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }





    public void crearTablaUsuario(){

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS usuario ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombre TEXT NOT NULL,"
                + " apellido TEXT NOT NULL,"
                + " rut TEXT UNIQUE NOT NULL,"
                + " password TEXT NOT NULL,"
                + " telefono TEXT,"
                + " rol TEXT,"
                + " estado INTEGER"
                + ");";

        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'usuario' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla usuario: " + e.getMessage());
        }
    }

    public void crearTablaProductos() {
        String sql = "CREATE TABLE IF NOT EXISTS producto ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombre TEXT NOT NULL,"
                + " precioCompra REAL,"
                + " precioVenta REAL,"
                + " idCategoria INTEGER NOT NULL,"
                + " fechaVenc TEXT,"
                + " stockActual INTEGER,"
                + " stockMinimo INTEGER,"
                + " activo BOOLEAN DEFAULT 1,"
                + " unidadMedida TEXT, "
                + " cantidad DOUBLE, "
                + " tipoProducto TEXT,"
                + " cantidadDefault DOUBLE,"
                + " FOREIGN KEY (idCategoria) REFERENCES categoria(id) ON UPDATE CASCADE ON DELETE RESTRICT"
                + ");";

        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'producto' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla productos: " + e.getMessage());
        }
    }

    public void creartablaVentas() {
        String sql = "CREATE TABLE IF NOT EXISTS venta ("
                + " idVenta INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " fechaHora TEXT NOT NULL,"
                + " idUsuario INTEGER,"
                + " totalVenta REAL,"
                + " tipoPago TEXT,"
                + " descripcion TEXT,"
                + " estado INTEGER,"
                + " FOREIGN KEY (idUsuario) REFERENCES usuario(id)"
                + ");";
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'venta' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla ventas: " + e.getMessage());
        }
    }

    public void crearTablaDetalleVenta() {
        String sql = "CREATE TABLE IF NOT EXISTS detalle_venta ("
                + " idDetalle INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " idVenta INTEGER,"
                + " idProducto INTEGER,"
                + " idPlatillo INTEGER,"
                + " FOREIGN KEY (idVenta) REFERENCES venta(idVenta) ON DELETE CASCADE,"
                + " FOREIGN KEY (idProducto) REFERENCES producto(id),"
                + " FOREIGN KEY (idPlatillo) REFERENCES platillo(id)"
                + ");";
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'detalle_venta' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla detalle_venta: " + e.getMessage());
        }
    }

    public void crearTablaHistorialInventario() {
        String sql = "CREATE TABLE IF NOT EXISTS historial_inventario ("
                + " idMovimiento INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " idProducto INTEGER NOT NULL,"
                + " nombreProducto TEXT,"
                + " tipoMovimiento TEXT NOT NULL,"
                + " cantidad INTEGER NOT NULL,"
                + " fecha TEXT DEFAULT CURRENT_TIMESTAMP,"
                + " motivo TEXT NOT NULL,"
                + " idUsuario INTEGER NOT NULL,"
                + " FOREIGN KEY (idProducto) REFERENCES producto(id),"
                + " FOREIGN KEY (idUsuario) REFERENCES usuario(id)"
                + ");";

        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Agregar columna si la tabla ya existía sin ella
            try {
                stmt.execute("ALTER TABLE historial_inventario ADD COLUMN nombreProducto TEXT");
            } catch (SQLException ignored) {
                // La columna ya existe
            }
            System.out.println("Tabla 'historial_inventario' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla historial_inventario: " + e.getMessage());
        }
    }

    public void crearTablaSesion() {
        String sql = "CREATE TABLE IF NOT EXISTS sesion ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " idUsuario INTEGER NOT NULL,"
                + " tokenHash TEXT NOT NULL UNIQUE,"
                + " fechaCreacion TEXT NOT NULL,"
                + " FOREIGN KEY (idUsuario) REFERENCES usuario(id)"
                + ");";
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'sesion' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla sesion: " + e.getMessage());
        }
    }

    public void crearTablaLLM() {
        String sql = "CREATE TABLE IF NOT EXISTS llm ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " key TEXT NOT NULL"
                + ");";
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'llm' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla llm: " + e.getMessage());
        }
    }

    public void crearTodasLasTablas() {
        crearTablaUsuario();
        crearTablaCategoria();
        insertarCategoriaPorDefecto();
        creartablaVentas();
        crearTablaDetalleVenta();
        crearTablaProductos();
        crearTablaHistorialInventario();
        crearTablaAuditoria();
        crearTablaPlatillo();
        crearTablaDetallePlatillo();
        crearTablaSesion();
        crearTablaLLM();
        System.out.println("Inicialización de todas las tablas completada.");
    }

    public void crearUsuarioAdmin() {
        String sql = "INSERT OR IGNORE INTO usuario (nombre, apellido, rut, password, telefono, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Admin");
            pstmt.setString(2, "Admin");
            pstmt.setString(3, "12345678-9");
            pstmt.setString(4, "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"); //admin
            pstmt.setString(5, "99999999");
            pstmt.setString(6, "ADMIN");
            pstmt.setInt(7, 1);
            pstmt.executeUpdate();
            System.out.println("Usuario administrador verificado/creado.");
        } catch (SQLException e) {
            System.err.println("Error al crear admin: " + e.getMessage());
        }
    }

    public void crearUsuarioVendedor() {
        String sql = "INSERT OR IGNORE INTO usuario (nombre, apellido, rut, password, telefono, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "Vendedor");
            pstmt.setString(2, "Juan");
            pstmt.setString(3, "98765432-1"); // RUT de ejemplo para el vendedor
            pstmt.setString(4, "vendedor123");  // Contraseña de acceso
            pstmt.setString(5, "88888888");
            pstmt.setString(6, "VENDEDOR");    // Rol configurado exactamente como tu Enum (Role.VENDEDOR)
            pstmt.setInt(7, 1);                // Estado Activo (1)

            pstmt.executeUpdate();
            System.out.println("Usuario vendedor verificado/creado con éxito.");
        } catch (SQLException e) {
            System.err.println("Error al crear vendedor por defecto: " + e.getMessage());
        }
    }


    public void crearTablaCategoria() {
        String sql = "CREATE TABLE IF NOT EXISTS categoria ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombreCategoria TEXT UNIQUE NOT NULL,"
                + " descripcion TEXT,"
                +  "activa boolean"
                + ");";

        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'categoria' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla categoria: " + e.getMessage());
        }
    }

    //insertar categoria "otros" por defecto en tabla categoria , para cuando se inicia el programa por primera vez y no hay datos
    public void insertarCategoriaPorDefecto() {
        String sql = "INSERT OR IGNORE INTO categoria (nombreCategoria, descripcion, activa) VALUES (?, ?, ?)";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Otros");
            pstmt.setString(2, "Categoría por defecto para productos sin clasificación específica");
            pstmt.setBoolean(3, true);
            pstmt.executeUpdate();
            System.out.println("Categoría por defecto 'Otros' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear categoría por defecto: " + e.getMessage());
        }
    }


    public void crearTablaPlatillo(){
        String sql = "CREATE TABLE IF NOT EXISTS platillo ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " nombre TEXT UNIQUE NOT NULL,"
            + " precio DOUBLE, "
            + " idCategoria INTEGER, "    
            + " estado boolean DEFAULT 1, "
            + " costoProduccion double DEFAULT 0.0, "
            + " fabricables INTEGER DEFAULT 0, "
            + " tipoProducto TEXT DEFAULT 'PLATILLO', "
            + " FOREIGN KEY (idCategoria) REFERENCES categoria(id) ON UPDATE CASCADE ON DELETE RESTRICT"
            + ");";
        try(var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println("Tabla 'platillo ' verificada/creada.");

        } catch (SQLException e) {
            System.err.println("Error al crear tabla platillo: "+e.getMessage());
        }
    }

    public void crearTablaDetallePlatillo() {
        String sql = "CREATE TABLE IF NOT EXISTS detalle_platillo ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " idPlatillo INTEGER NOT NULL, "
            + " idProducto INTEGER NOT NULL, "
            + " cantidadIngrediente DOUBLE NOT NULL, "
            + " FOREIGN KEY (idPlatillo) REFERENCES platillo(id) ON DELETE CASCADE, "
            + " FOREIGN KEY (idProducto) REFERENCES producto(id)"
            + ");";
        try(var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println("Tabla 'detalle_platillo' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla detalle_platillo: " + e.getMessage());
        }
    }

    public void crearTablaAuditoria() {
        String sql = "CREATE TABLE IF NOT EXISTS auditoria_eventos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " fecha TEXT DEFAULT CURRENT_TIMESTAMP,"
                + " modulo TEXT,"
                + " entidad TEXT,"
                + " accion TEXT,"
                + " idEntidad INTEGER,"
                + " detalle TEXT,"
                + " idUsuario INTEGER,"
                + " FOREIGN KEY (idUsuario) REFERENCES usuario(id)"
                + ");";

        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'auditoria_eventos' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla auditoria_eventos: " + e.getMessage());
        }
    }

}