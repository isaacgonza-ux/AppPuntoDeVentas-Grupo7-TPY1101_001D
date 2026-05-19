
package com.sistema.puntoventas.modelo;

public class venta {
    
    private int idVenta; 
    private String fechaHora;
    private int idUsuario;
    private double totalVenta;
    private Boolean estado;  //estado para ver si esta valida la venta o no (true si, false no)
    private String tipoPago;
    private String descripcion;
    
    public venta() {
    }

    public venta(int idVenta, String fechaHora, int idUsuario, double totalVenta, String tipoPago, String descripcion) {
        this.idVenta = idVenta;
        this.fechaHora = fechaHora;
        this.idUsuario = idUsuario;
        this.totalVenta = totalVenta;
        this.tipoPago = tipoPago;
        this.descripcion = descripcion;
        this.estado = true;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public Boolean getEstado() {
        return estado;
    }


    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    //toString
    @Override
    public String toString() {
        return "venta{" +
                "idVenta=" + idVenta +
                ", fechaHora='" + fechaHora + '\'' +
                ", idUsuario=" + idUsuario +
                ", totalVenta=" + totalVenta +
                ", tipoPago='" + tipoPago + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                '}';
    }

}
