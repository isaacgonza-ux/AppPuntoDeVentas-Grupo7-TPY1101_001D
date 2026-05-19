package com.sistema.puntoventas.modelo;

public class BalanceFinancieroDTO {
    private String periodo;
    private double ingresosTotales;
    private double perdidasTotales;
    private double utilidadNeta;

    public BalanceFinancieroDTO(String periodo, double ingresosTotales, double perdidasTotales, double utilidadNeta) {
        this.periodo = periodo;
        this.ingresosTotales = ingresosTotales;
        this.perdidasTotales = perdidasTotales;
        this.utilidadNeta = utilidadNeta;
    }

    public BalanceFinancieroDTO() {
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(double ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }

    public double getPerdidasTotales() {
        return perdidasTotales;
    }

    public void setPerdidasTotales(double perdidasTotales) {
        this.perdidasTotales = perdidasTotales;
    }

    public double getUtilidadNeta() {
        return utilidadNeta;
    }

    public void setUtilidadNeta(double utilidadNeta) {
        this.utilidadNeta = utilidadNeta;
    }
}
