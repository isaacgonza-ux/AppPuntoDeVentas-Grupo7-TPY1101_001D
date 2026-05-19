package com.sistema.puntoventas.modelo;

public class EstadisticasUsuario {
    private int idUsuario;
    private String nombreUsuario;
    private Double totalVendido;
    private int ventasRealizadas;

    public EstadisticasUsuario(int idUsuario, String nombreUsuario, Double totalVendido, int ventasRealizadas) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.totalVendido = totalVendido;
        this.ventasRealizadas = ventasRealizadas;
    }

    public EstadisticasUsuario() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Double getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(Double totalVendido) {
        this.totalVendido = totalVendido;
    }

    public int getVentasRealizadas() {
        return ventasRealizadas;
    }

    public void setVentasRealizadas(int ventasRealizadas) {
        this.ventasRealizadas = ventasRealizadas;
    }

    @Override
    public String toString() {
        return "EstadisticasUsuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", totalVendido=" + totalVendido +
                ", ventasRealizadas=" + ventasRealizadas +
                '}';
    }
}
