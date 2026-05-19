package com.sistema.puntoventas.repository;

import java.util.List;

import com.sistema.puntoventas.modelo.detalleVenta;
import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.ventaAplicacion;

public interface IDetalleVenta {

    public List<detalleVenta> obtenerDetalleVentasporIdVenta(int id);
    public List<String> obtenerInfoVentaDetalle(int id); //idDetalleVenta

    public List<ventaAplicacion> obtenerTodasLasVentas();
    public List<ventaAplicacion> obtenerTodasLasVentasporFecha(String fecha);

}
