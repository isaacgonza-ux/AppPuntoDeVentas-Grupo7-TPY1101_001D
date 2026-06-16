package com.sistema.puntoventas.modelo;

public class ReporteVentaDto {
    private String periodo;
    private double totalIngresos;
    private double PromedioVentas;

    public ReporteVentaDto(String periodo, double totalIngresos, double promedioVentas) {
        this.periodo = periodo;
        this.totalIngresos = totalIngresos;
        PromedioVentas = promedioVentas;
    }

    public ReporteVentaDto() {
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public double getPromedioVentas() {
        return PromedioVentas;
    }

    public void setPromedioVentas(double promedioVentas) {
        PromedioVentas = promedioVentas;
    }
}
