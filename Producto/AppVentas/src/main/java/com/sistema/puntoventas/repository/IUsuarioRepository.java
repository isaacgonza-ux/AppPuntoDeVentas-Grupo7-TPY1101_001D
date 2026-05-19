package com.sistema.puntoventas.repository;

import com.sistema.puntoventas.modelo.Usuario;

import java.util.List;


public interface IUsuarioRepository {
    boolean registrarUsuario (Usuario Usuario );
    List<Usuario>obtenerUsuarios();
    Usuario obtenerUsuarioPorRut (String rut );

    Usuario iniciarSesion (String rut, String contraseña);
    Usuario eliminarUsuario(String rut);
    boolean actualizarUsuario(Usuario usuario);

}
