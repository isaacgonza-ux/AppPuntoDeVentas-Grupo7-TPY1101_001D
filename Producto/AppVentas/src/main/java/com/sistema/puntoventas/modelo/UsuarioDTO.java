package com.sistema.puntoventas.modelo;

public class UsuarioDTO {
    private String nombre;
    private String apelllido;
    private Role rol;

    public UsuarioDTO(String nombre, String apelllido, Role rol) {
        this.nombre = nombre;
        this.apelllido = apelllido;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApelllido() {
        return apelllido;
    }

    public void setApelllido(String apelllido) {
        this.apelllido = apelllido;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }
}

