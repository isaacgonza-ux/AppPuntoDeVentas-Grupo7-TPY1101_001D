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
                + " imagen TEXT DEFAULT 'IMG',"
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
            System.out.println("Tabla 'historial_inventario' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla historial_inventario: " + e.getMessage());
        }
    }

    public void crearTodasLasTablas() {
        crearTablaUsuario();
        crearTablaCategoria();
        creartablaVentas();
        crearTablaDetalleVenta();
        crearTablaProductos();
        crearTablaHistorialInventario();
        crearTablaAuditoria();
        crearTablaPlatillo();
        crearTablaDetallePlatillo();
        System.out.println("Inicialización de todas las tablas completada.");
    }

    public void crearUsuarioAdmin() {
        String sql = "INSERT OR IGNORE INTO usuario (nombre, apellido, rut, password, telefono, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Admin");
            pstmt.setString(2, "Admin");
            pstmt.setString(3, "12345678-9");
            pstmt.setString(4, "admin");
            pstmt.setString(5, "99999999");
            pstmt.setString(6, "ADMIN");
            pstmt.setInt(7, 1);
            pstmt.executeUpdate();
            System.out.println("Usuario administrador verificado/creado.");
        } catch (SQLException e) {
            System.err.println("Error al crear admin: " + e.getMessage());
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