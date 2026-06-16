

package com.sistema.puntoventas.repository;

import java.util.List;
import java.util.Observable;

import com.sistema.puntoventas.modelo.venta;

import javafx.collections.ObservableList;

public interface IventaRepository {
    public Boolean registrarVentaCompleta(venta venta, List<Integer> idProducto, List<Integer> idPlatillo);
    public boolean actualizarVenta(venta venta);
    public venta obtenerVentaPorId(int id);
    public List<venta> obtenerVentas();
    public void anularVenta(int id);
    public void activarVenta(int id);
    public List<String> obtenerTodasLasFechas();
}
