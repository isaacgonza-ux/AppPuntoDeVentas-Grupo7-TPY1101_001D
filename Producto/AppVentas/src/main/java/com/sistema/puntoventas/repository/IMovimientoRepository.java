package com.sistema.puntoventas.repository;

import com.sistema.puntoventas.modelo.MovimientoInventario;
import java.util.List;

public interface IMovimientoRepository {
    // De la imagen verde y Trello
    boolean registrarMovimiento(MovimientoInventario movimiento);
    boolean actualizarStockFisico(int idProducto, int cantidad); // Ojo: cantidad puede ser negativa para restar

    // Alertas y reportes
    void generarAlertaStock(); // o verificarProductosCriticos()
    List<MovimientoInventario> listarMovimientosPorProducto(int idProducto);
    List<MovimientoInventario> obtenerUltimosMovimientos();
}