package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.MovimientoInventario;
import com.sistema.puntoventas.modelo.TipoMovimiento;
import com.sistema.puntoventas.repository.IMovimientoRepository;

// 1. LOS IMPORTS QUE FALTABAN PARA LA FECHA
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoRepositoryImpl implements IMovimientoRepository {

    // Misma URL de conexión que tienes en tu DbManager
    private final String url = "jdbc:sqlite:DBventasInventario.db";

    @Override
    public boolean registrarMovimiento(MovimientoInventario movimiento) {
        String sql = "INSERT INTO historial_inventario (idProducto, tipoMovimiento, cantidad, motivo, idUsuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movimiento.getIdProducto());
            pstmt.setString(2, movimiento.getTipoMovimiento().name()); // Guardamos el Enum como texto
            pstmt.setInt(3, movimiento.getCantidad());
            pstmt.setString(4, movimiento.getMotivo());
            pstmt.setInt(5, movimiento.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar movimiento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarStockFisico(int idProducto, int cantidad) {
        // La consulta suma la cantidad al stock actual.
        // Si 'cantidad' es negativa (ej. -5 por una venta o merma), automáticamente restará.
        String sql = "UPDATE producto SET stockActual = stockActual + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idProducto);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar stock físico de stockActual: " + e.getMessage());
            return false;
        }
    }
    public boolean actualizarCantidadFisica(int idProducto, Double cantidad){
        String sql = "UPDATE producto SET cantidad = cantidad + ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, cantidad);
            pstmt.setInt(2, idProducto);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar stock físico de cantidad: " + e.getMessage());
            return false;
        }

    }

    @Override
    public void generarAlertaStock() {
        // Busca los productos cuyo stock actual es menor o igual al stock mínimo
        String sql = "SELECT id, nombre, stockActual, stockMinimo FROM producto WHERE stockActual <= stockMinimo";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("--- ALERTAS DE STOCK CRÍTICO ---");
            boolean hayAlertas = false;

            while (rs.next()) {
                hayAlertas = true;
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int stockActual = rs.getInt("stockActual");
                int stockMinimo = rs.getInt("stockMinimo");

                // Aquí en el futuro puedes conectar esto con JavaFX para mostrar una alerta visual
                System.out.printf("¡ALERTA! Producto: %s (ID: %d) - Stock Actual: %d (Mínimo permitido: %d)%n",
                        nombre, id, stockActual, stockMinimo);
            }

            if (!hayAlertas) {
                System.out.println("No hay productos en estado crítico.");
            }

        } catch (SQLException e) {
            System.err.println("Error al generar alertas de stock: " + e.getMessage());
        }
    }

    @Override
    public List<MovimientoInventario> listarMovimientosPorProducto(int idProducto) {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM historial_inventario WHERE idProducto = ? ORDER BY fecha DESC";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar movimientos del producto: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<MovimientoInventario> obtenerUltimosMovimientos() {
        List<MovimientoInventario> lista = new ArrayList<>();
        // Obtiene los últimos 15 movimientos a nivel general, ideal para el panel principal
        String sql = "SELECT * FROM historial_inventario ORDER BY fecha DESC LIMIT 15";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener últimos movimientos: " + e.getMessage());
        }
        return lista;
    }

    // Método auxiliar para evitar repetir código al leer los datos de la base de datos
    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(rs.getInt("idMovimiento"));
        mov.setIdProducto(rs.getInt("idProducto"));
        mov.setTipoMovimiento(TipoMovimiento.valueOf(rs.getString("tipoMovimiento")));
        mov.setCantidad(rs.getInt("cantidad"));
        mov.setMotivo(rs.getString("motivo"));
        mov.setIdUsuario(rs.getInt("idUsuario"));

        // 2. EL BLOQUE QUE FALTA PARA LEER Y CONVERTIR LA FECHA A LOCALDATETIME
        String fechaTexto = rs.getString("fecha");
        if (fechaTexto != null && !fechaTexto.isEmpty()) {
            try {
                // Formato estándar de SQLite
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                mov.setFecha(LocalDateTime.parse(fechaTexto, formatter));
            } catch (Exception e) {
                // Por si acaso SQLite lo guardó con formato ISO (con la letra 'T')
                mov.setFecha(LocalDateTime.parse(fechaTexto));
            }
        }

        return mov;
    }
}