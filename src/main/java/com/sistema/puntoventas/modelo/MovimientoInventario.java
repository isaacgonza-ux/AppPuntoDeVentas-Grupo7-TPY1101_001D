package com.sistema.puntoventas.modelo;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public class MovimientoInventario {
    private int idMovimiento;
    private int idProducto;
    private String nombreProducto; // Para mostrar el nombre del producto en el historial
    private TipoMovimiento tipoMovimiento; // Usamos el Enum
    private int cantidad;
    private LocalDateTime fecha; // Mejor que String para manejo de fechas
    private String motivo;
    private int idUsuario;

    public MovimientoInventario(int idMovimiento, int idProducto, String nombreProducto, TipoMovimiento tipoMovimiento, int cantidad, LocalDateTime fecha, String motivo, int idUsuario) {
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
        this.idUsuario = idUsuario;
    }

    public MovimientoInventario() {
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "MovimientoInventario{" +
                "idMovimiento=" + idMovimiento +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", tipoMovimiento=" + tipoMovimiento +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", motivo='" + motivo + '\'' +
                ", idUsuario=" + idUsuario +
                '}';
    }
}