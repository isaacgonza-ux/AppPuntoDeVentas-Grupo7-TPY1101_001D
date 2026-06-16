package com.sistema.puntoventas.repository.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;

import java.util.List;

public interface IstockRepository {
    List<Producto> obtenerStockCritico();
    List<String> obtenerNombreStockCritico();
    int obtenerStockActual(int id);
}
