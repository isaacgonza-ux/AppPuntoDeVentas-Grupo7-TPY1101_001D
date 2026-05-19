package com.sistema.puntoventas.repository.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;

import java.util.List;

public interface IProductoRepository {
    boolean registrarProducto(Producto producto);
    List<Producto>obtenerProductos();
    List<Producto>obtenerProductoPorNombre(String nombre);
    boolean actualizarProducto(Producto producto);
    boolean eliminarProducto(int id);
    boolean desactivarProducto(int id);
    boolean existeNombre(String nombre, int id);
    Producto obtenerProductoPorId(int id);
    List<Producto> buscarPorTipoProducto(TipoProducto tipoProducto);
    boolean estaAsociadoVentaOPlatillo(int id);
    int obtenerStockActual(int id);


}
