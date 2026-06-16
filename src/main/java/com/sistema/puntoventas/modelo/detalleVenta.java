
package com.sistema.puntoventas.modelo;

public class detalleVenta {
    private int idDetalle;
    private int idVenta;
    private int idProducto; 
    private int idPlatillo;


    //constructor

    public detalleVenta() {
    }

    public detalleVenta(int idDetalle, int idVenta, int idProducto, int idPlatillo) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.idPlatillo = idPlatillo;
    }

    /**
     * Constructor sobrecargado para facilitar la creación de detalles sin ID (autoincremental).
     */
    public detalleVenta(int idVenta, int idProducto, int idPlatillo) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.idPlatillo = idPlatillo;
    }

    //getters y setters 
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }   

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdPlatillo() {
        return idPlatillo;
    }

    public void setIdPlatillo(int idPlatillo) {
        this.idPlatillo = idPlatillo;
    }

    //toString
    @Override
    public String toString() {
        return "detalleVenta{" +
                "idDetalle=" + idDetalle +
                ", idVenta=" + idVenta +
                ", idProducto=" + idProducto +
                ", idPlatillo=" + idPlatillo +
                '}';
    }

}
