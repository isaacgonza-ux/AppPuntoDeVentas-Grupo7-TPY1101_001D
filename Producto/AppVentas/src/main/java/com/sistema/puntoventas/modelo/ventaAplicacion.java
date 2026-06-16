package com.sistema.puntoventas.modelo;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;

import java.util.List;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;

public class ventaAplicacion {
     
    private venta venta;
    private List<Producto> detalleVentas;
    private List<Platillo> detallePlatillos;

    public ventaAplicacion() {
    }

    public ventaAplicacion(venta venta, List<Producto> detalleVentas, List<Platillo> detallePlatillos) {
        this.venta = venta;
        this.detalleVentas = detalleVentas;
        this.detallePlatillos = detallePlatillos;
    }

    public venta getVenta() {
        return venta;
    }

    public void setVenta(venta venta) {
        this.venta = venta;
    }

    public List<Producto> getDetalleVentas() {
        return detalleVentas;
    }

    public List<Platillo> getDetallePlatillos() {
        return detallePlatillos;
    }

    public void setDetallePlatillos(List<Platillo> detallePlatillos) {
        this.detallePlatillos = detallePlatillos;
    }

    /**
     * Obtiene el nombre concatenado de todos los productos y platillos en la venta.
     */
    public String getNombreProducto(){
        StringBuilder sb = new StringBuilder();
        
        if (detalleVentas != null) {
            for (Producto producto : detalleVentas) {
                sb.append(producto.getNombre()).append("- ");
            }
        }
        
        if (detallePlatillos != null) {
            for (Platillo platillo : detallePlatillos) {
                sb.append(platillo.getNombre()).append("- ");
            }
        }
        
        return sb.toString();
    }

    public void setDetalleVentas(List<Producto> detalleVentas) {
        this.detalleVentas = detalleVentas;
    }
    @Override
    public String toString() {
        return "ventaAplicacion{" +
                "venta=" + venta +
                ", detalleVentas=" + detalleVentas +
                ", detallePlatillos=" + detallePlatillos +
                '}';
    }




}
