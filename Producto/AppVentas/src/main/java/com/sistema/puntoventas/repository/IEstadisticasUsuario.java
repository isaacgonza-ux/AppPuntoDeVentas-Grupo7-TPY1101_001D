package com.sistema.puntoventas.repository;

import com.sistema.puntoventas.modelo.EstadisticasUsuario;

public interface IEstadisticasUsuario {
    EstadisticasUsuario obtenerVentasUsuario(int idUsuario);
}
