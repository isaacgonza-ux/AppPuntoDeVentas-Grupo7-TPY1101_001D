package com.sistema.puntoventas.repository.impl;

//import com.sistema.puntoventas.conexion.Conexion;
import com.sistema.puntoventas.modelo.EstadisticasUsuario;
import com.sistema.puntoventas.repository.IEstadisticasUsuario;
import java.sql.*;

/* 
public class EstadisticasUsuarioImpl implements IEstadisticasUsuario {

    @Override
    public EstadisticasUsuario obtenerVentasUsuario(int idUsuario) {
        EstadisticasUsuario stats = null;

        String sql = "SELECT u.id, u.nombre, SUM(v.total) as totalVendido, COUNT(v.id) as ventasRealizadas " +
                "FROM usuario u " +
                "LEFT JOIN venta v ON u.id = v.id_usuario " +
                "WHERE u.id = ? " +
                "GROUP BY u.id, u.nombre";

        // AQUÍ EL CAMBIO CLAVE
        try (Connection con = Conexion.DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stats = new EstadisticasUsuario();
                stats.setIdUsuario(rs.getInt("id"));
                stats.setNombreUsuario(rs.getString("nombre"));
                stats.setTotalVendido(rs.getDouble("totalVendido"));
                stats.setVentasRealizadas(rs.getInt("ventasRealizadas"));
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerVentasUsuario: " + e.getMessage());
        }

        return stats;
    }
}
    */