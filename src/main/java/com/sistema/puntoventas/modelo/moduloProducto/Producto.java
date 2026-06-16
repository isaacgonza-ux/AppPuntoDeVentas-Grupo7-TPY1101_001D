package com.sistema.puntoventas.modelo.moduloProducto;

import lombok.Builder;

@Builder
public class Producto {
    private int id ;
    private String nombre;
    private double precioCompra;
    private double precioVenta;
    private Categoria categoria;
    private String fechaVenc;
    private int stockActual;
    private int stockMinimo;
    private Boolean activo = true;
    private UnidadMedida unidadMedida;
    private  double cantidad;
    private TipoProducto tipoProducto;
    private double cantidadDefault;

    public Producto() {
    }

    public Producto(int id, String nombre, double precioCompra, double precioVenta, Categoria categoria, String fechaVenc, int stockActual, int stockMinimo, Boolean activo, UnidadMedida unidadMedida, double cantidad, TipoProducto tipoProducto, double cantidadDefault) {
        this.id = id;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.categoria = categoria;
        this.fechaVenc = fechaVenc;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.unidadMedida = unidadMedida;
        this.cantidad = cantidad;
        this.tipoProducto = tipoProducto;
        this.cantidadDefault = cantidadDefault;
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

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getFechaVenc() {
        return fechaVenc;
    }

    public void setFechaVenc(String fechaVenc) {
        this.fechaVenc = fechaVenc;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCantidadDefault() {
        return cantidadDefault;
    }

    public void setCantidadDefault(double cantidadDefault) {
        this.cantidadDefault = cantidadDefault;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", categoria=" + categoria +
                ", fechaVenc='" + fechaVenc + '\'' +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", activo=" + activo +
                ", unidadMedida=" + unidadMedida +
                ", cantidad=" + cantidad +
                ", tipoProducto=" + tipoProducto +
                ", cantidadDefault=" + cantidadDefault +
                '}';
    }
}
