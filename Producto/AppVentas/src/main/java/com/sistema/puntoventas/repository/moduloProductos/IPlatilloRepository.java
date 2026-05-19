package com.sistema.puntoventas.repository.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Platillo;

import java.util.List;

public interface IPlatilloRepository {
    boolean registrarPlatillo(Platillo platillo);
    List<Platillo> obtenerPlatillos();
    List<Platillo> obtenerPlatilloPorNombre(String nombre);
    boolean actualizarPlatillo(Platillo platillo);
    boolean eliminarPlatillo(int id);
    boolean desactivarPlatillo(int id);
    boolean existeNombre(String nombre, int id);
    Platillo obtenerPlatilloPorId(int id);
    boolean estaAsociadoVenta(int id);
    List<Platillo> obtenerPlatillosConRecetaCompleta();
}
