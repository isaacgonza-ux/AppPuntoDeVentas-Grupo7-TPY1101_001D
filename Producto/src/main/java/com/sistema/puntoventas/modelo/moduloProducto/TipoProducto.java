package com.sistema.puntoventas.modelo.moduloProducto;

public enum TipoProducto {
    PLATILLO,        // Productos elaborados (ej. Torta)
    DIRECTO,         // Se compran y venden igual (ej. Bebida en lata)
    SOLO_INVENTARIO  // Insumos que no se venden solos (ej. Bolsa de Café, Harina)
}
