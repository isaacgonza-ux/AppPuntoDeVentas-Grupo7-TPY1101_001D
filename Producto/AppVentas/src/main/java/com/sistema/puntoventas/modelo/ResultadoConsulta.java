package com.sistema.puntoventas.modelo;

import java.util.List;

public class ResultadoConsulta {

    private final List<String> columnas;
    private final List<List<String>> filas;
    private final String sqlGenerado;

    public ResultadoConsulta(List<String> columnas, List<List<String>> filas) {
        this.columnas = columnas;
        this.filas = filas;
        this.sqlGenerado = "";
    }

    public ResultadoConsulta(List<String> columnas, List<List<String>> filas, String sqlGenerado) {
        this.columnas = columnas;
        this.filas = filas;
        this.sqlGenerado = sqlGenerado;
    }

    public List<String> getColumnas() {
        return columnas;
    }

    public List<List<String>> getFilas() {
        return filas;
    }

    public String getSqlGenerado() {
        return sqlGenerado;
    }
}
