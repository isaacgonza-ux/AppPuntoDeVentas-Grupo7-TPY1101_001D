package com.sistema.puntoventas.modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {
    private int idMovimiento;
    private int idProducto;
    private TipoMovimiento tipoMovimiento; // Usamos el Enum
    private int cantidad;
    private LocalDateTime fecha; // Mejor que String para manejo de fechas
    private String motivo;
    private int idUsuario;

    // Constructor vacío
    public MovimientoInventario() {}

    // Constructor con parámetros (sin ID, ya que la BD suele autogenerarlo)
    public MovimientoInventario(int idProducto, TipoMovimiento tipoMovimiento, int cantidad, String motivo, int idUsuario) {
        this.idProducto = idProducto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.idUsuario = idUsuario;
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
                ", tipoMovimiento=" + tipoMovimiento +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", motivo='" + motivo + '\'' +
                ", idUsuario=" + idUsuario +
                '}';
    }
}