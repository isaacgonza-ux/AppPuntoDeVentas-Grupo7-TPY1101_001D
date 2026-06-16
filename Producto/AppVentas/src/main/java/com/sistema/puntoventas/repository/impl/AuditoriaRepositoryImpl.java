package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.repository.IAuditoriaRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaRepositoryImpl implements IAuditoriaRepository {

    private final String url = "jdbc:sqlite:DBventasInventario.db";

    @Override
    public List<AuditoriaEvento> obtenerUltimosEventos(int limite) {
        List<AuditoriaEvento> lista = new ArrayList<>();
        String sql = "SELECT id, fecha, modulo, entidad, accion, idEntidad, detalle, idUsuario FROM auditoria_eventos ORDER BY datetime(fecha) DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditoriaEvento e = new AuditoriaEvento();
                    e.setId(rs.getInt("id"));
                    e.setFecha(rs.getString("fecha"));
                    e.setModulo(rs.getString("modulo"));
                    e.setEntidad(rs.getString("entidad"));
                    e.setAccion(rs.getString("accion"));
                    e.setIdEntidad(rs.getInt("idEntidad"));
                    e.setDetalle(rs.getString("detalle"));
                    int idUsuario = rs.getInt("idUsuario");
                    if (!rs.wasNull()) e.setIdUsuario(idUsuario);
                    lista.add(e);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al leer auditoria_eventos: " + ex.getMessage());
        }

        return lista;
    }

    @Override
    public void registrarEvento(AuditoriaEvento auditoriaEvento) {
        String sql = "INSERT INTO auditoria_eventos (fecha, modulo, entidad, accion, idEntidad, detalle, idUsuario) " +
                     "VALUES (datetime('now','localtime'), ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, auditoriaEvento.getModulo());
                pstmt.setString(2, auditoriaEvento.getEntidad());
                pstmt.setString(3, auditoriaEvento.getAccion());
                // idEntidad puede ser null -> usar setObject para insertar NULL en la BD cuando corresponda
                if (auditoriaEvento.getIdEntidad() != null) {
                    pstmt.setObject(4, auditoriaEvento.getIdEntidad(), java.sql.Types.INTEGER);
                } else {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                }
                pstmt.setString(5, auditoriaEvento.getDetalle());
                // idUsuario puede ser null; si es null guardar NULL (antes forzaba 1)
                if (auditoriaEvento.getIdUsuario() != null) {
                    pstmt.setObject(6, auditoriaEvento.getIdUsuario(), java.sql.Types.INTEGER);
                } else {
                    pstmt.setNull(6, java.sql.Types.INTEGER);
                }

                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error al registrar auditoría: " + e.getMessage());
            }

    }
}

