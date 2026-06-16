package com.sistema.puntoventas.modelo.moduloProducto;


import lombok.Builder;

@Builder
public class Categoria {
    private int id;
    private String nombreCategoria;
    private String descripcion;
    private boolean activa;

    public Categoria() {
    }

    public Categoria(int id, String nombreCategoria, String descripcion, boolean activa) {
        this.id = id;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return this.nombreCategoria;
    }
}
