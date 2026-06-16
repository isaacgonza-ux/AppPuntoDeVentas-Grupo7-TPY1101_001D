package com.sistema.puntoventas.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encriptador {

    /**
     * Convierte una cadena de texto plano a un hash SHA-256.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convertir el arreglo de bytes a formato Hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al inicializar el algoritmo de cifrado", e);
        }
    }
}