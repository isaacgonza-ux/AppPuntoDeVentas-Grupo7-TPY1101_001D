package com.sistema.puntoventas.repository.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;

import java.util.List;

public interface ICategoriaRepository {

    boolean registrarCategoria(Categoria categoria);
    boolean existeCategoria(String nombre);
    boolean actualizarCategoria(Categoria categoria);
    boolean eliminarCategoria(int id);
    List<Categoria> obtenerCategorias();
}
