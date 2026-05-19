package com.sistema.puntoventas.modelo.moduloProducto;

import java.util.List;

public class Platillo {
    private int id;
    private String nombre;
    private List<DetallePlatillo> ingrediente;
    private Categoria categoria;
    private double precio;
    private boolean estado;
    private double costoProduccion;
    private int stockActual;
    private TipoProducto tipoProducto;

    public Platillo(int stockActual, double costoProduccion, boolean estado, double precio, Categoria categoria, List<DetallePlatillo> ingrediente, String nombre, int id, TipoProducto tipoProducto) {
        this.stockActual = stockActual;
        this.costoProduccion = costoProduccion;
        this.estado = estado;
        this.precio = precio;
        this.categoria = categoria;
        this.ingrediente = ingrediente;
        this.nombre = nombre;
        this.id = id;
        this.tipoProducto = tipoProducto;
    }

    public Platillo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<DetallePlatillo> getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(List<DetallePlatillo> ingrediente) {
        this.ingrediente = ingrediente;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public double getCostoProduccion() {
        return costoProduccion;
    }

    public void setCostoProduccion(double costoProduccion) {
        this.costoProduccion = costoProduccion;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }
}
