package com.sistema.puntoventas.modelo.moduloProducto;

public class DetallePlatillo {
    private int id;
    private int idPlatillo;
    private Platillo platillo;
    private Producto producto;
    private double cantidadIngrediente;

    public DetallePlatillo() {
    }

    public DetallePlatillo(int id, double cantidadIngrediente, Platillo platillo,Producto producto, int idPlatillo) {
        this.id = id;
        this.cantidadIngrediente = cantidadIngrediente;
        this.platillo = platillo;
        this.idPlatillo = idPlatillo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlatillo() {
        return idPlatillo;
    }

    public void setIdPlatillo(int idPlatillo) {
        this.idPlatillo = idPlatillo;
    }

    public Platillo getPlatillo() {
        return platillo;
    }

    public void setPlatillo(Platillo platillo) {
        this.platillo = platillo;
    }

    public double getCantidadIngrediente() {
        return cantidadIngrediente;
    }

    public void setCantidadIngrediente(double cantidadIngrediente) {
        this.cantidadIngrediente = cantidadIngrediente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
