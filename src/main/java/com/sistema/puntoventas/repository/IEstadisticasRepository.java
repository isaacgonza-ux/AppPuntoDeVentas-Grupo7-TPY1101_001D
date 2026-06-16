package com.sistema.puntoventas.repository;

import com.sistema.puntoventas.modelo.RankingVendedoresDTO;
import com.sistema.puntoventas.modelo.moduloProducto.RankingProductosDTO;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;

import java.util.List;
import java.util.Map;

public interface IEstadisticasRepository {
    double obtenerIngresosTotales(String periodo);
    List<RankingProductosDTO>obtenerRankingProductos(int limite);
    int obtenerVentasUsuario(int idUsuario);
    int prepararDatosParaIA();
    int prepararDatosStockParaIA();
    double obtenerPerdidasTotales(String periodo);
    List<RankingVendedoresDTO> obtenerRankingVendedores(int limite);
    List<String> obtenerUltimasActividades(int limite);
    Map<Integer, Producto> obtenerProductosPorIds(List<Integer> ids);
}
