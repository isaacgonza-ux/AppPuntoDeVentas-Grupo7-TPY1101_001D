package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.repository.SesionRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

public class SesionService {

    private static final String TOKEN_FILE = System.getProperty("user.home") + "/.eluney-session.token";
    private static final int TOKEN_BYTES = 32;

    private final SesionRepository sesionRepository = new SesionRepository();
    private final UsuarioService usuarioService = new UsuarioService();

    public void iniciarSesionPersistente(int idUsuario) {
        try {
            byte[] tokenBytes = new byte[TOKEN_BYTES];
            new SecureRandom().nextBytes(tokenBytes);
            String token = HexFormat.of().formatHex(tokenBytes);

            String tokenHash = hashSHA256(token);
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            sesionRepository.eliminarPorUsuario(idUsuario);
            sesionRepository.guardarToken(idUsuario, tokenHash, fecha);

            Files.writeString(Paths.get(TOKEN_FILE), token);
            System.out.println("Sesión persistente guardada para usuario ID: " + idUsuario);
        } catch (Exception e) {
            System.err.println("Error al guardar sesión persistente: " + e.getMessage());
        }
    }

    public Usuario verificarSesionPersistente() {
        try {
            Path path = Paths.get(TOKEN_FILE);
            if (!Files.exists(path)) {
                return null;
            }

            String token = Files.readString(path).trim();
            if (token.isEmpty()) {
                Files.deleteIfExists(path);
                return null;
            }

            String tokenHash = hashSHA256(token);
            int idUsuario = sesionRepository.buscarPorTokenHash(tokenHash);

            if (idUsuario == -1) {
                Files.deleteIfExists(path);
                return null;
            }

            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (usuario == null || Boolean.FALSE.equals(usuario.getEstado())) {
                Files.deleteIfExists(path);
                sesionRepository.eliminarPorUsuario(idUsuario);
                return null;
            }

            return usuario;
        } catch (Exception e) {
            System.err.println("Error al verificar sesión persistente: " + e.getMessage());
            return null;
        }
    }

    public void cerrarSesionPersistente() {
        try {
            Path path = Paths.get(TOKEN_FILE);
            if (Files.exists(path)) {
                String token = Files.readString(path).trim();
                if (!token.isEmpty()) {
                    String tokenHash = hashSHA256(token);
                    sesionRepository.eliminarToken(tokenHash);
                }
                Files.deleteIfExists(path);
                
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión persistente: " + e.getMessage());
        }
    }

    private String hashSHA256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes());
        return HexFormat.of().formatHex(hashBytes);
    }
}
