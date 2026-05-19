package com.sistema.puntoventas.modelo.moduloProducto;

public class MetricasDTO {
    private int totalPlatillos;
    private long categoriasActivas;
    private long platillosActivos;
    private long bajoStock;

    public MetricasDTO() {
    }

    public MetricasDTO(int totalPlatillos, long bajoStock, long platillosActivos, long categoriasActivas) {
        this.totalPlatillos = totalPlatillos;
        this.bajoStock = bajoStock;
        this.platillosActivos = platillosActivos;
        this.categoriasActivas = categoriasActivas;
    }

    public int getTotalPlatillos() {
        return totalPlatillos;
    }

    public void setTotalPlatillos(int totalPlatillos) {
        this.totalPlatillos = totalPlatillos;
    }

    public long getCategoriasActivas() {
        return categoriasActivas;
    }

    public void setCategoriasActivas(long categoriasActivas) {
        this.categoriasActivas = categoriasActivas;
    }

    public long getBajoStock() {
        return bajoStock;
    }

    public void setBajoStock(long bajoStock) {
        this.bajoStock = bajoStock;
    }

    public long getPlatillosActivos() {
        return platillosActivos;
    }

    public void setPlatillosActivos(long platillosActivos) {
        this.platillosActivos = platillosActivos;
    }

    @Override
    public String toString() {
        return "MetricasDTO{" +
                "totalPlatillos=" + totalPlatillos +
                ", categoriasActivas=" + categoriasActivas +
                ", platillosActivos=" + platillosActivos +
                ", bajoStock=" + bajoStock +
                '}';
    }
}
