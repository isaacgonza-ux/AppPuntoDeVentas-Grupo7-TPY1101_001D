package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.repository.ILLMRepository;

import java.sql.*;

public class LLMRepositoryImpl implements ILLMRepository {

    private static final String url = "jdbc:sqlite:DBventasInventario.db";

    @Override
    public void guardarApiKey(String apiKey) {
        String sql = "INSERT OR REPLACE INTO llm (id, key) VALUES (1, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, apiKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar API key: " + e.getMessage());
        }
    }

    @Override
    public void eliminarApiKey() {
        String sql = "DELETE FROM llm WHERE id = 1";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar API key: " + e.getMessage());
        }
    }

    @Override
    public String obtenerApiKey() {
        String sql = "SELECT key FROM llm WHERE id = 1";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("key");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener API key: " + e.getMessage());
        }
        return null;
    }
}
