package com.sistema.puntoventas.modelo;

public class PrediccionStockDTO {

    private int idPrediccion;
    private String nombreProducto;
    private int idProducto;
    private String fechaProyectada;
    private String fechaCalculo;
    private int stockActual;
    private int cantidadSugerida;
    private int diasParaAgotarse;
    private double indiceRiesgo;
    private double nivelConfianza;

    public PrediccionStockDTO() {
    }

    public PrediccionStockDTO(int idPrediccion, String nombreProducto, int idProducto, String fechaProyectada, String fechaCalculo, int stockActual, int cantidadSugerida, int diasParaAgotarse, double indiceRiesgo, double nivelConfianza) {
        this.idPrediccion = idPrediccion;
        this.nombreProducto = nombreProducto;
        this.idProducto = idProducto;
        this.fechaProyectada = fechaProyectada;
        this.fechaCalculo = fechaCalculo;
        this.stockActual = stockActual;
        this.cantidadSugerida = cantidadSugerida;
        this.diasParaAgotarse = diasParaAgotarse;
        this.indiceRiesgo = indiceRiesgo;
        this.nivelConfianza = nivelConfianza;
    }

    public int getIdPrediccion() {
        return idPrediccion;
    }

    public void setIdPrediccion(int idPrediccion) {
        this.idPrediccion = idPrediccion;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getFechaProyectada() {
        return fechaProyectada;
    }

    public void setFechaProyectada(String fechaProyectada) {
        this.fechaProyectada = fechaProyectada;
    }

    public String getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(String fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getCantidadSugerida() {
        return cantidadSugerida;
    }

    public void setCantidadSugerida(int cantidadSugerida) {
        this.cantidadSugerida = cantidadSugerida;
    }

    public double getIndiceRiesgo() {
        return indiceRiesgo;
    }

    public void setIndiceRiesgo(double indiceRiesgo) {
        this.indiceRiesgo = indiceRiesgo;
    }

    public double getNivelConfianza() {
        return nivelConfianza;
    }

    public void setNivelConfianza(double nivelConfianza) {
        this.nivelConfianza = nivelConfianza;
    }

    public int getDiasParaAgotarse() {
        return diasParaAgotarse;
    }

    public void setDiasParaAgotarse(int diasParaAgotarse) {
        this.diasParaAgotarse = diasParaAgotarse;
    }
}

