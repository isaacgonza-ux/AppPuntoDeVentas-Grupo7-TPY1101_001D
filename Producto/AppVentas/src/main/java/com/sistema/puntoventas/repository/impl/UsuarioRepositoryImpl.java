package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.Role;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.repository.IUsuarioRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements IUsuarioRepository {

    // URL de conexión consistente con ProductoRepositoryImpl
    private static final String url = "jdbc:sqlite:DBventasInventario.db";

    @Override
    public boolean registrarUsuario(Usuario usuario) {
        // Se cambia 'contraseña' por 'password' para evitar errores de caracteres especiales
        String sql = "INSERT INTO Usuario (nombre, apellido, rut, password, telefono, rol, estado) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getRut());
            ps.setString(4, usuario.getContraseña());
            ps.setString(5, usuario.getTelefono());
            // Conversión de Enum Role a String para la base de datos
            ps.setString(6, usuario.getRol().name());
            // Uso de setBoolean para el tipo de dato correcto
            ps.setBoolean(7, usuario.getEstado());

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario iniciarSesion(String rut, String contraseña) {
        // Se utiliza 'password' para la consulta SQL
        String sql = "SELECT * FROM Usuario WHERE rut = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            ps.setString(2, contraseña);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en inicio de sesión: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Usuario obtenerUsuarioPorRut(String rut) {
        String sql = "SELECT * FROM Usuario WHERE rut = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por RUT: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lista de usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public Usuario eliminarUsuario(String rut) {
        Usuario usuario = obtenerUsuarioPorRut(rut);
        if (usuario == null) return null;

        String sql = "DELETE FROM Usuario WHERE rut = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            ps.executeUpdate();
            return usuario;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return null;
        }
    }

    // Método auxiliar para mapear los resultados respetando los tipos del modelo
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario user = new Usuario();

        // === AQUÍ ESTÁ LA LÍNEA AGREGADA PARA EL ID ===
        user.setId(rs.getInt("id"));

        user.setNombre(rs.getString("nombre"));
        user.setApellido(rs.getString("apellido"));
        user.setRut(rs.getString("rut"));
        // Se lee la columna 'password' de la DB
        user.setContraseña(rs.getString("password"));
        user.setTelefono(rs.getString("telefono"));
        // Conversión del String de la DB al Enum Role
        user.setRol(Role.valueOf(rs.getString("rol")));
        // Uso de getBoolean para el estado
        user.setEstado(rs.getBoolean("estado"));
        return user;
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        // El RUT no se cambia (es el WHERE), actualizamos el resto
        String sql = "UPDATE Usuario SET nombre = ?, apellido = ?, password = ?, telefono = ?, rol = ?, estado = ? WHERE rut = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getContraseña());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getRol().name());
            ps.setBoolean(6, usuario.getEstado());
            ps.setString(7, usuario.getRut()); // El filtro por RUT

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
}