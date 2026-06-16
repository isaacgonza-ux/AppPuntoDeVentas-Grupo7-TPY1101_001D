package com.sistema.puntoventas.modelo.moduloProducto;

public class RankingProductosDTO {
    String nombreProducto;
    int CantidadVendida;

    public RankingProductosDTO(String nombreProducto, int cantidadVendida) {
        this.nombreProducto = nombreProducto;
        CantidadVendida = cantidadVendida;
    }

    public RankingProductosDTO() {
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidadVendida() {
        return CantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        CantidadVendida = cantidadVendida;
    }
}
