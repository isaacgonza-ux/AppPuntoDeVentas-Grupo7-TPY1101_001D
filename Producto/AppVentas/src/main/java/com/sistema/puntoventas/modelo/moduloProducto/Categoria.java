package com.sistema.puntoventas.modelo.moduloProducto;

public class Categoria {
    private int id;
    private String nombreCategoria;
    private String descripcion;
    private boolean activa;

    public Categoria() {
    }

    public Categoria(int id, boolean activa, String descripcion, String nombreCategoria) {
        this.id = id;
        this.activa = activa;
        this.descripcion = descripcion;
        this.nombreCategoria = nombreCategoria;
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
