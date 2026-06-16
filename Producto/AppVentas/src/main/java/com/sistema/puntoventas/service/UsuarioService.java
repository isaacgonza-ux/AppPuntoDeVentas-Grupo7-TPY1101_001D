package com.sistema.puntoventas.service;
import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.repository.IUsuarioRepository;
import com.sistema.puntoventas.repository.impl.ProductoRepositoryImpl;
import com.sistema.puntoventas.repository.impl.UsuarioRepositoryImpl;

import java.util.List;

public class UsuarioService {
    private IUsuarioRepository usuarioRepository;
    private AuditoriaService auditoriaService;
    public UsuarioService(){
        this.usuarioRepository= new UsuarioRepositoryImpl();
        this.auditoriaService = new AuditoriaService();



    }
    public String registrarNuevoUsuario(Usuario usuario){
        if (usuario == null ){
            return "El usuario no puede ser nulo";
        }
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()){
            return "El nombre del usuario no puede ser nulo o vacío";
        }
        if (usuario.getApellido() == null || usuario.getApellido().isEmpty()){
            return "El apellido del usuario no puede ser nulo o vacío";
        }
        if (usuario.getRut() == null || usuario.getRut().isEmpty()){
            return "El RUT del usuario no puede ser nulo o vacío";
        }
        if (usuario.getContraseña() == null || usuario.getContraseña().isEmpty()){
            return "La contraseña del usuario no puede ser nula o vacía";
        }
        // Verificar si el usuario ya existe
        if (usuarioRepository.obtenerUsuarioPorRut(usuario.getRut()) != null) {
            return "El usuario con este RUT ya existe";
        }
        // Registrar el usuario
        boolean registrado = usuarioRepository.registrarUsuario(usuario);
        if (registrado) {

            AuditoriaEvento evento = new AuditoriaEvento();
            evento.setModulo("Usuarios");
            evento.setEntidad("Usuario");
            evento.setAccion("Registro");
            evento.setDetalle("Se registro un nuevo usuario: " + usuario.getNombre());

            boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
            if (!auditoriaRegistrada) {
                System.out.println("El usuario se registro, pero no se pudo guardar el evento de auditoria.");
            }
            System.out.println("Evento registrado"+evento.getAccion()+" para el usuario: " + usuario.getNombre());

            return "Usuario registrado exitosamente";


        } else {
            return "Error al registrar el usuario";
        }
    }

    public Usuario iniciarSesion(String rut, String contraseña) {
        if (rut == null || rut.isEmpty() || contraseña == null || contraseña.isEmpty()) {
            return null;
        }
        return usuarioRepository.iniciarSesion(rut, contraseña);
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.obtenerUsuarios();
    }

    public Usuario obtenerUsuarioPorRut(String rut) {
        if (rut == null || rut.isEmpty()) {
            return null;
        }
        return usuarioRepository.obtenerUsuarioPorRut(rut);
    }

    public Usuario eliminarUsuario(String rut) {
        if (rut == null || rut.isEmpty()) {
            return null;
        }

        Usuario usuarioEliminado = usuarioRepository.eliminarUsuario(rut);
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("Usuarios");
        evento.setEntidad("Usuario");
        evento.setAccion("Eliminación");
        evento.setDetalle("Se eliminó un nuevo usuario: " + usuarioEliminado.getNombre());

        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            System.out.println("El usuario se eliminó, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el usuario: " + usuarioEliminado.getNombre());

        return usuarioEliminado;



    }
    public String actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getRut() == null || usuario.getRut().isEmpty()) {
            return "El RUT es obligatorio para actualizar";
        }

        boolean actualizado = usuarioRepository.actualizarUsuario(usuario);

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("Usuarios");
        evento.setEntidad("Usuario");
        evento.setAccion("Actualización");
        evento.setDetalle("Se actualizo un  usuario: " + usuario.getNombre());

        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            System.out.println("El usuario se actualizo, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el usuario: " + usuario.getNombre());

        if (actualizado) {
            return "Usuario actualizado exitosamente";
        } else {
            return "Error al actualizar el usuario en la base de datos";
        }
    }
}

