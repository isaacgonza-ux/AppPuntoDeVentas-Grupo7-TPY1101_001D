package com.sistema.puntoventas.repository;

import com.sistema.puntoventas.modelo.ResultadoConsulta;

public interface IConsultaSQLRepository {
    ResultadoConsulta ejecutarSelect(String sql);
}
