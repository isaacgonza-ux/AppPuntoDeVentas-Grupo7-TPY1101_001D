package com.sistema.puntoventas;

import com.sistema.puntoventas.conexion.DbManager;
import com.sistema.puntoventas.modelo.MovimientoInventario;
import com.sistema.puntoventas.modelo.TipoMovimiento;
import com.sistema.puntoventas.repository.impl.MovimientoRepositoryImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PruebaInventario {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE MÚLTIPLES PRODUCTOS ---");

        // 1. Preparar la Base de Datos
        DbManager dbManager = new DbManager();
        dbManager.crearTodasLasTablas();
        dbManager.crearUsuarioAdmin(); // El Admin es el ID 1

        // 2. Crear VARIOS productos de prueba (ID 1 y ID 2)
        System.out.println("Insertando productos en la base de datos...");
        insertarProductosDePrueba();

        // 3. Registrar movimientos para ambos productos
        MovimientoRepositoryImpl repo = new MovimientoRepositoryImpl();

        System.out.println("Registrando movimientos...");

        // ---> MOVIMIENTO 1: Para el Producto 1 (Galletas)
        MovimientoInventario mov1 = new MovimientoInventario(
                1, // ID del Producto 1
                TipoMovimiento.ENTRADA,
                25, // Sumamos 25
                "Ingreso de Galletas",
                1 // ID del Usuario (Admin)
        );
        repo.registrarMovimiento(mov1);
        repo.actualizarStockFisico(1, 25);

        // ---> MOVIMIENTO 2: Para el Producto 2 (Bebidas)
        MovimientoInventario mov2 = new MovimientoInventario(
                2, // ID del Producto 2 (¡Ahora sí existe!)
                TipoMovimiento.ENTRADA,
                40, // Sumamos 50
                "Ingreso de Bebidas",
                1 // ID del Usuario (Sigue siendo el Admin 1)
        );
        repo.registrarMovimiento(mov2);
        repo.actualizarStockFisico(2, 50);

        // ---> MOVIMIENTO 3: Una venta del Producto 1 (Galletas)
        MovimientoInventario mov3 = new MovimientoInventario(
                1, // Volvemos al Producto 1
                TipoMovimiento.SALIDA_VENTA,
                -5, // Restamos 5 unidades
                "Venta en caja",
                1 // ID del Usuario
        );
        repo.registrarMovimiento(mov3);
        repo.actualizarStockFisico(1, -5);

        // ---> MOVIMIENTO 3: Una venta del Producto 1 (Galletas)
        MovimientoInventario mov4 = new MovimientoInventario(
                6, // Volvemos al Producto 1
                TipoMovimiento.AJUSTE,
                8, // Restamos 5 unidades
                "Venta en caja",
                1 // ID del Usuario
        );
        repo.registrarMovimiento(mov4);
        repo.actualizarStockFisico(6, 8);

        // 4. Mostrar todo el historial
        System.out.println("\n--- RESULTADOS EN LA BASE DE DATOS ---");
        mostrarTodosLosDatos();
    }

    // --- MÉTODOS AUXILIARES ---

    private static void insertarProductosDePrueba() {
        String url = "jdbc:sqlite:DBventasInventario.db";
        String sql = "INSERT OR REPLACE INTO producto (id, nombre, precioCompra, precioVenta, categoria, stockActual, stockMinimo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Insertar Producto 1
            pstmt.setInt(1, 1); // ID 1
            pstmt.setString(2, "Galletas de Prueba");
            pstmt.setDouble(3, 500);
            pstmt.setDouble(4, 1000);
            pstmt.setString(5, "Snacks");
            pstmt.setInt(6, 0); // Stock inicial
            pstmt.setInt(7, 5); // Stock mínimo
            pstmt.executeUpdate();

            // Insertar Producto 2
            pstmt.setInt(1, 2); // ID 2
            pstmt.setString(2, "Bebida Cola 2L");
            pstmt.setDouble(3, 1200);
            pstmt.setDouble(4, 2000);
            pstmt.setString(5, "Bebidas");
            pstmt.setInt(6, 0); // Stock inicial
            pstmt.setInt(7, 10); // Stock mínimo
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al insertar productos: " + e.getMessage());
        }
    }

    private static void mostrarTodosLosDatos() {
        String url = "jdbc:sqlite:DBventasInventario.db";
        try (Connection conn = DriverManager.getConnection(url)) {

            // Ver el stock de todos los productos (Usamos WHILE en lugar de IF para leer varios)
            System.out.println(" INVENTARIO ACTUAL:");
            var rsProd = conn.createStatement().executeQuery("SELECT id, nombre, stockActual FROM producto");
            while (rsProd.next()) {
                System.out.println("   - [" + rsProd.getInt("id") + "] " + rsProd.getString("nombre") + " | STOCK: " + rsProd.getInt("stockActual"));
            }

            // Ver todos los movimientos registrados
            System.out.println("\n HISTORIAL DE MOVIMIENTOS:");
            var rsHist = conn.createStatement().executeQuery("SELECT idProducto, tipoMovimiento, cantidad, motivo FROM historial_inventario");
            while (rsHist.next()) {
                System.out.println("   - Producto ID " + rsHist.getInt("idProducto") + " | " + rsHist.getString("tipoMovimiento") + " | Cantidad: " + rsHist.getInt("cantidad") + " | Motivo: " + rsHist.getString("motivo"));
            }

        } catch (SQLException e) {
            System.out.println("Error al leer datos: " + e.getMessage());
        }
    }
}