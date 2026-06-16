package com.sistema.puntoventas.pruebas;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.service.ProductoService;

public class PruebaBackendProductos {
    public static void main(String[] args) {

        System.out.println("--- INICIANDO PRUEBA DE BASE DE DATOS ---");



        ProductoService productoService = new ProductoService();

        Categoria categoria1  =new Categoria(1,true, "Bebidas calientes y frías", "Bebidas");
        Categoria categoria2  =new Categoria(2,true, "pasteles", "pasteleria");
        Categoria categoria3 = new Categoria(3, true, "Pancitos", "Sandwich");
        Categoria categoria4 = new Categoria(4, true, "Postres y pasteles dulces", "Postres");
        Categoria categoria5 = new Categoria(5, true, "Sándwiches y preparaciones saladas", "Comida Rápida");
        Categoria categoria6 = new Categoria(6, true, "Productos envasados y snacks", "Snacks");
        Categoria categoria7 = new Categoria(7, true, "Insumos y materias primas", "Insumos");

        Producto producto1 = new Producto(1, "pie de limon", 1000, 1500, categoria2, "2026-10-01", 100, 1, "cafe_americano.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.PLATILLO);
        Producto producto2 = new Producto(2, "coca cola", 1000, 1500, categoria2, "2026-10-01", 100, 1, "coca-cola.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto producto3 = new Producto(3, "Café Americano", 800, 1500, categoria1, "2026-12-31", 50, 10, "cafe_americano.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.PLATILLO);
        Producto producto4 = new Producto(4, "Coca Cola 500ml", 700, 1200, categoria1, "2026-10-01", 120, 20, "coca_cola.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto producto5 = new Producto(5, "Jugo de Naranja Natural", 500, 1800, categoria1, "2026-04-25", 30, 5, "jugo_naranja.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.PLATILLO);
        Producto producto6 = new Producto(6, "Agua Mineral sin gas", 400, 900, categoria1, "2027-01-15", 80, 15, "agua_mineral.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto producto7 = new Producto(7, "Porción Torta Tres Leches", 1200, 2800, categoria2, "2026-04-27", 10, 2, "torta_tres_leches.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.PLATILLO);
        Producto producto8 = new Producto(8, "Medialuna", 300, 700, categoria2, "2026-04-26", 40, 10, "medialuna.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto producto9 = new Producto(9, "Sándwich Ave Palta", 1500, 3500, categoria3, "2026-04-25", 20, 5, "ave_palta.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.PLATILLO);
        Producto producto10 = new Producto(10, "Empanada de Queso", 600, 1500, categoria3, "2026-04-26", 35, 10, "empanada_queso.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.PLATILLO);
        Producto producto11 = new Producto(11, "Papas Fritas Lays 100g", 600, 1100, categoria4, "2026-08-20", 50, 10, "lays.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto producto12 = new Producto(12, "Galletas Tritón", 450, 800, categoria4, "2026-09-10", 60, 15, "triton.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        // 3. Intentamos guardarlo usando el Service
        try {
            System.out.println("Enviando producto al servicio...");

           /* productoService.eliminarProducto(1);
            productoService.eliminarCategoria(1);
            productoService.eliminarCategoria(2);*/

            // Primero guardamos las categorías, porque los productos dependen de ellas
           productoService.registrarCategoria(categoria1);
           productoService.registrarCategoria(categoria2);
           productoService.registrarCategoria(categoria3);
           productoService.registrarCategoria(categoria4);
           productoService.registrarCategoria(categoria5);
           productoService.registrarCategoria(categoria6);
           productoService.registrarCategoria(categoria7);

           productoService.registrarProducto(producto1);
           productoService.registrarProducto(producto2);
           productoService.registrarProducto(producto3);
           productoService.registrarProducto(producto4);
           productoService.registrarProducto(producto5);
           productoService.registrarProducto(producto6);
           productoService.registrarProducto(producto7);
           productoService.registrarProducto(producto8);
           productoService.registrarProducto(producto9);
           productoService.registrarProducto(producto10);
           productoService.registrarProducto(producto11);
           productoService.registrarProducto(producto12);



            System.out.println("¡ÉXITO! La categoria '" + categoria1.getNombreCategoria() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! La categoria '" + categoria2.getNombreCategoria() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! La categoria '" + categoria3.getNombreCategoria() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! La categoria '" + categoria4.getNombreCategoria() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! La categoria '" + categoria5.getNombreCategoria() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! La categoria '" + categoria6.getNombreCategoria() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! La categoria '" + categoria7.getNombreCategoria() + "' fue guardado en la BD.");

            System.out.println("¡ÉXITO! El producto '" + producto1.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto2.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto3.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto4.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto5.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto6.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto7.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto8.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto9.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto10.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto11.getNombre() + "' fue guardado en la BD.");
            System.out.println("¡ÉXITO! El producto '" + producto12.getNombre() + "' fue guardado en la BD.");

        } catch (Exception e) {
            // Si alguna validación falla (ej. precio <= 0, o nombre duplicado), caerá aquí
            System.err.println(" ERROR: " + e.getMessage());
        }

        System.out.println("--- FIN DE LA PRUEBA ---");
    }
}
