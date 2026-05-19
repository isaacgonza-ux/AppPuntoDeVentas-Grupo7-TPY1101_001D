package com.sistema.puntoventas.modelo;

public class RankingVendedoresDTO {
    private String nombreVendedor;
    private int cantidadVentas;

    public RankingVendedoresDTO() {
    }

    public RankingVendedoresDTO(String nombreVendedor, int cantidadVentas) {
        this.nombreVendedor = nombreVendedor;
        this.cantidadVentas = cantidadVentas;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    @Override
    public String toString() {
        return "RankingVendedoresDTO{" +
                "nombreVendedor='" + nombreVendedor + '\'' +
                ", cantidadVentas=" + cantidadVentas +
                '}';
    }
}
