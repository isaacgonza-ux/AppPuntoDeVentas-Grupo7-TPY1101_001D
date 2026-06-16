package com.sistema.puntoventas.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SesionRepository {

    private static final String url = "jdbc:sqlite:DBventasInventario.db";

    public void guardarToken(int idUsuario, String tokenHash, String fechaCreacion) {
        String sql = "INSERT INTO sesion (idUsuario, tokenHash, fechaCreacion) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setString(2, tokenHash);
            pstmt.setString(3, fechaCreacion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar token de sesión: " + e.getMessage());
        }
    }

    public int buscarPorTokenHash(String tokenHash) {
        String sql = "SELECT idUsuario FROM sesion WHERE tokenHash = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tokenHash);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idUsuario");
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar token de sesión: " + e.getMessage());
        }
        return -1;
    }

    public void eliminarToken(String tokenHash) {
        String sql = "DELETE FROM sesion WHERE tokenHash = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tokenHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar token de sesión: " + e.getMessage());
        }
    }

    public void eliminarPorUsuario(int idUsuario) {
        String sql = "DELETE FROM sesion WHERE idUsuario = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar sesiones del usuario: " + e.getMessage());
        }
    }
}
