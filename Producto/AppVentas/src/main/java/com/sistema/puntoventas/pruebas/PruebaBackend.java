package com.sistema.puntoventas.pruebas;

import com.sistema.puntoventas.modelo.moduloProducto.*;
import com.sistema.puntoventas.repository.IEstadisticasRepository;
import com.sistema.puntoventas.repository.impl.EstadisticasRepositoryImpl;
import com.sistema.puntoventas.repository.impl.ProductoRepositoryImpl;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.service.*;
import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.detalleVenta;
import com.sistema.puntoventas.modelo.MovimientoInventario;
import com.sistema.puntoventas.modelo.TipoMovimiento;

import com.sistema.puntoventas.repository.impl.MovimientoRepositoryImpl;

import java.util.ArrayList;
import java.util.List;


public class PruebaBackend {
    public static void main(String[] args) throws Exception {
        System.out.println("--- INICIANDO PRUEBA DE BACKEND ---");
        
        
        ProductoService productoService = new ProductoService();
        PlatilloService platilloService = new PlatilloService();
        UsuarioService usuarioService = new UsuarioService();
        VentaService ventaService = new VentaService();
        IEstadisticasRepository iestadisticasRepository = new EstadisticasRepositoryImpl();
        IProductoRepository productoRepository = new ProductoRepositoryImpl();
        EstadisticaService estadisticaService = new EstadisticaService(iestadisticasRepository, productoRepository);
        
        
    /*    Categoria categoria = new Categoria(1, true, "Bebidas calientes y frías", "Bebidas");
        Categoria categoria2 = new Categoria(2, true, "Pasteleria", "Pasteleria");
        Categoria categoria3 = new Categoria(3, true, "Postres ricos", "Postres");
        Categoria categoria4 = new Categoria(4, true, "chatarra", "Comida rapida");
        Categoria categoria5 = new Categoria(5, true, "ingredientes", "Ingredientes");

        productoService.registrarCategoria(categoria);
        productoService.registrarCategoria(categoria2);
        productoService.registrarCategoria(categoria3);
        productoService.registrarCategoria(categoria4);
        productoService.registrarCategoria(categoria5);
        
        /*Producto producto = new Producto(1, "Café Americano", 800, 1500, categoria, "2026-12-31", 50, 10, "cafe_americano.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        

        // 19 productos (DIRECTO y SOLO_INVENTARIO) asociados a categorias existentes
        Producto p1 = new Producto(2, "Latte", 1200, 2200, categoria, "2026-12-31", 40, 8, "latte.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p2 = new Producto(3, "Capuccino", 1300, 2300, categoria, "2026-12-31", 35, 7, "capuccino.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p3 = new Producto(4, "Te Verde", 700, 1400, categoria, "2026-12-31", 60, 12, "te_verde.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p4 = new Producto(5, "Mocha", 1400, 2500, categoria, "2026-12-31", 30, 6, "mocha.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p5 = new Producto(6, "Chocolate Caliente", 1100, 2100, categoria, "2026-12-31", 45, 9, "choco_caliente.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);

        Producto p6 = new Producto(7, "Croissant", 900, 1800, categoria2, "2026-12-31", 25, 5, "croissant.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p7 = new Producto(8, "Muffin Vainilla", 850, 1700, categoria2, "2026-12-31", 28, 6, "muffin_vainilla.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p8 = new Producto(9, "Brownie", 950, 1900, categoria3, "2026-12-31", 22, 5, "brownie.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p9 = new Producto(10, "Torta Chocolate", 2000, 3500, categoria3, "2026-12-31", 15, 4, "torta_choco.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p10 = new Producto(11, "Cheesecake", 2200, 3800, categoria3, "2026-12-31", 12, 3, "cheesecake.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);

        Producto p11 = new Producto(12, "Hamburguesa", 1800, 3200, categoria4, "2026-12-31", 20, 5, "hamburguesa.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p12 = new Producto(13, "Papas Fritas", 700, 1500, categoria4, "2026-12-31", 50, 10, "papas.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);
        Producto p13 = new Producto(14, "Hot Dog", 1200, 2400, categoria4, "2026-12-31", 18, 4, "hotdog.jpg", UnidadMedida.UNIDAD, 1.0, TipoProducto.DIRECTO);

        Producto p14 = new Producto(15, "Cafe en Grano", 6000, 9000, categoria5, "2027-12-31", 80, 20, "cafe_grano.jpg", UnidadMedida.GRAMOS, 1.0, TipoProducto.SOLO_INVENTARIO);
        Producto p15 = new Producto(16, "Leche Entera", 900, 1400, categoria5, "2026-10-31", 100, 25, "leche_entera.jpg", UnidadMedida.MILILITROS, 1.0, TipoProducto.SOLO_INVENTARIO);
        Producto p16 = new Producto(17, "Azucar", 700, 1200, categoria5, "2027-12-31", 120, 30, "azucar.jpg", UnidadMedida.GRAMOS, 1.0, TipoProducto.SOLO_INVENTARIO);
        Producto p17 = new Producto(18, "Cacao en Polvo", 2500, 4000, categoria5, "2027-12-31", 40, 10, "cacao.jpg", UnidadMedida.GRAMOS, 1.0, TipoProducto.SOLO_INVENTARIO);
        Producto p18 = new Producto(19, "Crema", 1500, 2300, categoria5, "2026-08-31", 60, 15, "crema.jpg", UnidadMedida.MILILITROS, 1.0, TipoProducto.SOLO_INVENTARIO);
        Producto p19 = new Producto(20, "Mantequilla", 1800, 2600, categoria5, "2026-09-30", 55, 12, "mantequilla.jpg", UnidadMedida.GRAMOS, 1.0, TipoProducto.SOLO_INVENTARIO);

        productoService.registrarProducto(producto);
        productoService.registrarProducto(p1);
        productoService.registrarProducto(p2);
        productoService.registrarProducto(p3);
        productoService.registrarProducto(p4);
        productoService.registrarProducto(p5);
        productoService.registrarProducto(p6);
        productoService.registrarProducto(p7);
        productoService.registrarProducto(p8);
        productoService.registrarProducto(p9);
        productoService.registrarProducto(p10);
        productoService.registrarProducto(p11);
        productoService.registrarProducto(p12);
        productoService.registrarProducto(p13);
        productoService.registrarProducto(p14);
        productoService.registrarProducto(p15);
        productoService.registrarProducto(p16);
        productoService.registrarProducto(p17);
        productoService.registrarProducto(p18);
        productoService.registrarProducto(p19);



        // Platillos con detalle de ingredientes
        List<DetallePlatillo> detallePlatillo1 = new ArrayList<>();
        Platillo platillo1 = new Platillo(25, 1800, true, 3200, categoria3, detallePlatillo1, "Brownie Especial", 101, TipoProducto.PLATILLO);
        DetallePlatillo dp1a = new DetallePlatillo(1, 80, platillo1, p16, platillo1.getId());
        dp1a.setProducto(p16);
        detallePlatillo1.add(dp1a);
        DetallePlatillo dp1b = new DetallePlatillo(2, 40, platillo1, p17, platillo1.getId());
        dp1b.setProducto(p17);
        detallePlatillo1.add(dp1b);

        List<DetallePlatillo> detallePlatillo2 = new ArrayList<>();
        Platillo platillo2 = new Platillo(20, 2000, true, 3500, categoria3, detallePlatillo2, "Cheesecake Tradicional", 102, TipoProducto.PLATILLO);
        DetallePlatillo dp2a = new DetallePlatillo(3, 60, platillo2, p18, platillo2.getId());
        dp2a.setProducto(p18);
        detallePlatillo2.add(dp2a);
        DetallePlatillo dp2b = new DetallePlatillo(4, 50, platillo2, p19, platillo2.getId());
        dp2b.setProducto(p19);
        detallePlatillo2.add(dp2b);

        List<DetallePlatillo> detallePlatillo3 = new ArrayList<>();
        Platillo platillo3 = new Platillo(30, 1600, true, 3000, categoria3, detallePlatillo3, "Torta Chocolate", 103, TipoProducto.PLATILLO);
        DetallePlatillo dp3a = new DetallePlatillo(5, 90, platillo3, p17, platillo3.getId());
        dp3a.setProducto(p17);
        detallePlatillo3.add(dp3a);
        DetallePlatillo dp3b = new DetallePlatillo(6, 60, platillo3, p19, platillo3.getId());
        dp3b.setProducto(p19);
        detallePlatillo3.add(dp3b);

        List<DetallePlatillo> detallePlatillo4 = new ArrayList<>();
        Platillo platillo4 = new Platillo(18, 2200, true, 3800, categoria3, detallePlatillo4, "Muffin Premium", 104, TipoProducto.PLATILLO);
        DetallePlatillo dp4a = new DetallePlatillo(7, 50, platillo4, p16, platillo4.getId());
        dp4a.setProducto(p16);
        detallePlatillo4.add(dp4a);
        DetallePlatillo dp4b = new DetallePlatillo(8, 30, platillo4, p19, platillo4.getId());
        dp4b.setProducto(p19);
        detallePlatillo4.add(dp4b);

        List<DetallePlatillo> detallePlatillo5 = new ArrayList<>();
        Platillo platillo5 = new Platillo(22, 1900, true, 3300, categoria3, detallePlatillo5, "Torta Vainilla", 105, TipoProducto.PLATILLO);
        DetallePlatillo dp5a = new DetallePlatillo(9, 70, platillo5, p16, platillo5.getId());
        dp5a.setProducto(p16);
        detallePlatillo5.add(dp5a);
        DetallePlatillo dp5b = new DetallePlatillo(10, 40, platillo5, p18, platillo5.getId());
        dp5b.setProducto(p18);
        detallePlatillo5.add(dp5b);

        List<DetallePlatillo> detallePlatillo6 = new ArrayList<>();
        Platillo platillo6 = new Platillo(15, 2600, true, 4500, categoria4, detallePlatillo6, "Hamburguesa Doble", 106, TipoProducto.PLATILLO);
        DetallePlatillo dp6a = new DetallePlatillo(11, 120, platillo6, p15, platillo6.getId());
        dp6a.setProducto(p15);
        detallePlatillo6.add(dp6a);
        DetallePlatillo dp6b = new DetallePlatillo(12, 60, platillo6, p19, platillo6.getId());
        dp6b.setProducto(p19);
        detallePlatillo6.add(dp6b);

        List<DetallePlatillo> detallePlatillo7 = new ArrayList<>();
        Platillo platillo7 = new Platillo(35, 1400, true, 2600, categoria4, detallePlatillo7, "Papas con Salsa", 107, TipoProducto.PLATILLO);
        DetallePlatillo dp7a = new DetallePlatillo(13, 50, platillo7, p15, platillo7.getId());
        dp7a.setProducto(p15);
        detallePlatillo7.add(dp7a);
        DetallePlatillo dp7b = new DetallePlatillo(14, 20, platillo7, p18, platillo7.getId());
        dp7b.setProducto(p18);
        detallePlatillo7.add(dp7b);

        List<DetallePlatillo> detallePlatillo8 = new ArrayList<>();
        Platillo platillo8 = new Platillo(16, 2300, true, 4000, categoria4, detallePlatillo8, "Hot Dog Especial", 108, TipoProducto.PLATILLO);
        DetallePlatillo dp8a = new DetallePlatillo(15, 80, platillo8, p15, platillo8.getId());
        dp8a.setProducto(p15);
        detallePlatillo8.add(dp8a);
        DetallePlatillo dp8b = new DetallePlatillo(16, 30, platillo8, p19, platillo8.getId());
        dp8b.setProducto(p19);
        detallePlatillo8.add(dp8b);

        List<DetallePlatillo> detallePlatillo9 = new ArrayList<>();
        Platillo platillo9 = new Platillo(28, 1700, true, 3100, categoria3, detallePlatillo9, "Torta Mocha", 109, TipoProducto.PLATILLO);
        DetallePlatillo dp9a = new DetallePlatillo(17, 60, platillo9, p14, platillo9.getId());
        dp9a.setProducto(p14);
        detallePlatillo9.add(dp9a);
        DetallePlatillo dp9b = new DetallePlatillo(18, 40, platillo9, p17, platillo9.getId());
        dp9b.setProducto(p17);
        detallePlatillo9.add(dp9b);

        List<DetallePlatillo> detallePlatillo10 = new ArrayList<>();
        Platillo platillo10 = new Platillo(24, 2100, true, 3600, categoria3, detallePlatillo10, "Cafe Helado", 110, TipoProducto.PLATILLO);
        DetallePlatillo dp10a = new DetallePlatillo(19, 50, platillo10, p14, platillo10.getId());
        dp10a.setProducto(p14);
        detallePlatillo10.add(dp10a);
        DetallePlatillo dp10b = new DetallePlatillo(20, 80, platillo10, p15, platillo10.getId());
        dp10b.setProducto(p15);
        detallePlatillo10.add(dp10b);

        // --- Llamadas a persistencia: registrar platillos ---
        List<Platillo> platillos = new ArrayList<>();
        platillos.add(platillo1);
        platillos.add(platillo2);
        platillos.add(platillo3);
        platillos.add(platillo4);
        platillos.add(platillo5);
        platillos.add(platillo6);
        platillos.add(platillo7);
        platillos.add(platillo8);
        platillos.add(platillo9);
        platillos.add(platillo10);

        for (Platillo pl : platillos) {
            try {
                platilloService.registrarPlatillo(pl);
                System.out.println("Platillo registrado: " + pl.getNombre());
            } catch (Exception e) {
                System.err.println("Error al registrar platillo '" + pl.getNombre() + "': " + e.getMessage());
            }
        }


        // 20 ventas con detalles (productos DIRECTO y platillos)
        venta v1 = new venta(1, "2026-05-01 10:00", 1, 5400, "EFECTIVO", "Venta 1");
        venta v2 = new venta(2, "2026-05-01 11:15", 2, 6800, "TARJETA", "Venta 2");
        venta v3 = new venta(3, "2026-05-01 12:30", 1, 3200, "EFECTIVO", "Venta 3");
        venta v4 = new venta(4, "2026-05-01 13:45", 3, 9000, "TARJETA", "Venta 4");
        venta v5 = new venta(5, "2026-05-01 15:00", 2, 2600, "EFECTIVO", "Venta 5");
        venta v6 = new venta(6, "2026-05-02 09:20", 1, 4500, "TARJETA", "Venta 6");
        venta v7 = new venta(7, "2026-05-02 10:40", 2, 7000, "EFECTIVO", "Venta 7");
        venta v8 = new venta(8, "2026-05-02 12:05", 3, 4100, "EFECTIVO", "Venta 8");
        venta v9 = new venta(9, "2026-05-02 13:30", 1, 8200, "TARJETA", "Venta 9");
        venta v10 = new venta(10, "2026-05-02 16:10", 2, 3000, "EFECTIVO", "Venta 10");
        venta v11 = new venta(11, "2026-05-03 09:10", 3, 5600, "TARJETA", "Venta 11");
        venta v12 = new venta(12, "2026-05-03 10:50", 1, 6200, "EFECTIVO", "Venta 12");
        venta v13 = new venta(13, "2026-05-03 12:20", 2, 4700, "TARJETA", "Venta 13");
        venta v14 = new venta(14, "2026-05-03 14:00", 3, 7800, "EFECTIVO", "Venta 14");
        venta v15 = new venta(15, "2026-05-03 16:30", 1, 3600, "TARJETA", "Venta 15");
        venta v16 = new venta(16, "2026-05-04 10:05", 2, 5900, "EFECTIVO", "Venta 16");
        venta v17 = new venta(17, "2026-05-04 11:40", 3, 9100, "TARJETA", "Venta 17");
        venta v18 = new venta(18, "2026-05-04 13:15", 1, 4300, "EFECTIVO", "Venta 18");
        venta v19 = new venta(19, "2026-05-04 15:25", 2, 6600, "TARJETA", "Venta 19");
        venta v20 = new venta(20, "2026-05-04 17:05", 3, 5200, "EFECTIVO", "Venta 20");

        detalleVenta dv1 = new detalleVenta(1, v1.getIdVenta(), p1.getId());
        detalleVenta dv2 = new detalleVenta(2, v1.getIdVenta(), platillo1.getId());
        detalleVenta dv3 = new detalleVenta(3, v2.getIdVenta(), p4.getId());
        detalleVenta dv4 = new detalleVenta(4, v2.getIdVenta(), platillo2.getId());
        detalleVenta dv5 = new detalleVenta(5, v3.getIdVenta(), p7.getId());
        detalleVenta dv6 = new detalleVenta(6, v3.getIdVenta(), platillo3.getId());
        detalleVenta dv7 = new detalleVenta(7, v4.getIdVenta(), p10.getId());
        detalleVenta dv8 = new detalleVenta(8, v4.getIdVenta(), platillo4.getId());
        detalleVenta dv9 = new detalleVenta(9, v5.getIdVenta(), p12.getId());
        detalleVenta dv10 = new detalleVenta(10, v5.getIdVenta(), platillo5.getId());
        detalleVenta dv11 = new detalleVenta(11, v6.getIdVenta(), p2.getId());
        detalleVenta dv12 = new detalleVenta(12, v6.getIdVenta(), platillo6.getId());
        detalleVenta dv13 = new detalleVenta(13, v7.getIdVenta(), p5.getId());
        detalleVenta dv14 = new detalleVenta(14, v7.getIdVenta(), platillo7.getId());
        detalleVenta dv15 = new detalleVenta(15, v8.getIdVenta(), p8.getId());
        detalleVenta dv16 = new detalleVenta(16, v8.getIdVenta(), platillo8.getId());
        detalleVenta dv17 = new detalleVenta(17, v9.getIdVenta(), p11.getId());
        detalleVenta dv18 = new detalleVenta(18, v9.getIdVenta(), platillo9.getId());
        detalleVenta dv19 = new detalleVenta(19, v10.getIdVenta(), p3.getId());
        detalleVenta dv20 = new detalleVenta(20, v10.getIdVenta(), platillo10.getId());

        detalleVenta dv21 = new detalleVenta(21, v11.getIdVenta(), p6.getId());
        detalleVenta dv22 = new detalleVenta(22, v11.getIdVenta(), platillo1.getId());
        detalleVenta dv23 = new detalleVenta(23, v12.getIdVenta(), p9.getId());
        detalleVenta dv24 = new detalleVenta(24, v12.getIdVenta(), platillo2.getId());
        detalleVenta dv25 = new detalleVenta(25, v13.getIdVenta(), p13.getId());
        detalleVenta dv26 = new detalleVenta(26, v13.getIdVenta(), platillo3.getId());
        detalleVenta dv27 = new detalleVenta(27, v14.getIdVenta(), p1.getId());
        detalleVenta dv28 = new detalleVenta(28, v14.getIdVenta(), platillo4.getId());
        detalleVenta dv29 = new detalleVenta(29, v15.getIdVenta(), p2.getId());
        detalleVenta dv30 = new detalleVenta(30, v15.getIdVenta(), platillo5.getId());
        detalleVenta dv31 = new detalleVenta(31, v16.getIdVenta(), p4.getId());
        detalleVenta dv32 = new detalleVenta(32, v16.getIdVenta(), platillo6.getId());
        detalleVenta dv33 = new detalleVenta(33, v17.getIdVenta(), p7.getId());
        detalleVenta dv34 = new detalleVenta(34, v17.getIdVenta(), platillo7.getId());
        detalleVenta dv35 = new detalleVenta(35, v18.getIdVenta(), p10.getId());
        detalleVenta dv36 = new detalleVenta(36, v18.getIdVenta(), platillo8.getId());
        detalleVenta dv37 = new detalleVenta(37, v19.getIdVenta(), p12.getId());
        detalleVenta dv38 = new detalleVenta(38, v19.getIdVenta(), platillo9.getId());
        detalleVenta dv39 = new detalleVenta(39, v20.getIdVenta(), p5.getId());
        detalleVenta dv40 = new detalleVenta(40, v20.getIdVenta(), platillo10.getId());

        List<venta> ventas = new ArrayList<>();
        ventas.add(v1);
        ventas.add(v2);
        ventas.add(v3);
        ventas.add(v4);
        ventas.add(v5);
        ventas.add(v6);
        ventas.add(v7);
        ventas.add(v8);
        ventas.add(v9);
        ventas.add(v10);
        ventas.add(v11);
        ventas.add(v12);
        ventas.add(v13);
        ventas.add(v14);
        ventas.add(v15);
        ventas.add(v16);
        ventas.add(v17);
        ventas.add(v18);
        ventas.add(v19);
        ventas.add(v20);

        List<detalleVenta> detallesVentas = new ArrayList<>();
        detallesVentas.add(dv1);
        detallesVentas.add(dv2);
        detallesVentas.add(dv3);
        detallesVentas.add(dv4);
        detallesVentas.add(dv5);
        detallesVentas.add(dv6);
        detallesVentas.add(dv7);
        detallesVentas.add(dv8);
        detallesVentas.add(dv9);
        detallesVentas.add(dv10);
        detallesVentas.add(dv11);
        detallesVentas.add(dv12);
        detallesVentas.add(dv13);
        detallesVentas.add(dv14);
        detallesVentas.add(dv15);
        detallesVentas.add(dv16);
        detallesVentas.add(dv17);
        detallesVentas.add(dv18);
        detallesVentas.add(dv19);
        detallesVentas.add(dv20);
        detallesVentas.add(dv21);
        detallesVentas.add(dv22);
        detallesVentas.add(dv23);
        detallesVentas.add(dv24);
        detallesVentas.add(dv25);
        detallesVentas.add(dv26);
        detallesVentas.add(dv27);
        detallesVentas.add(dv28);
        detallesVentas.add(dv29);
        detallesVentas.add(dv30);
        detallesVentas.add(dv31);
        detallesVentas.add(dv32);
        detallesVentas.add(dv33);
        detallesVentas.add(dv34);
        detallesVentas.add(dv35);
        detallesVentas.add(dv36);
        detallesVentas.add(dv37);
        detallesVentas.add(dv38);
        detallesVentas.add(dv39);
        detallesVentas.add(dv40);

        // 10 movimientos de inventario asociados a productos existentes
        List<MovimientoInventario> movInventarios = new ArrayList<>();
        MovimientoInventario m1 = new MovimientoInventario(p1.getId(), TipoMovimiento.SALIDA_VENTA, 5, "Venta mostrador", 1);
        MovimientoInventario m2 = new MovimientoInventario(p2.getId(), TipoMovimiento.SALIDA_VENTA, 3, "Venta delivery", 2);
        MovimientoInventario m3 = new MovimientoInventario(p3.getId(), TipoMovimiento.SALIDA_VENTA, 7, "Venta mostrador", 1);
        MovimientoInventario m4 = new MovimientoInventario(p6.getId(), TipoMovimiento.SALIDA_VENTA, 2, "Venta combo", 2);
        MovimientoInventario m5 = new MovimientoInventario(p11.getId(), TipoMovimiento.ENTRADA, 20, "Proveedor X", 3);
        MovimientoInventario m6 = new MovimientoInventario(p14.getId(), TipoMovimiento.ENTRADA, 50, "Recepción lote", 3);
        MovimientoInventario m7 = new MovimientoInventario(p15.getId(), TipoMovimiento.MERMA, 2, "Merma producción", 1);
        MovimientoInventario m8 = new MovimientoInventario(p16.getId(), TipoMovimiento.AJUSTE, 5, "Ajuste inventario", 2);
        MovimientoInventario m9 = new MovimientoInventario(p8.getId(), TipoMovimiento.SALIDA_VENTA, 4, "Venta especial", 1);
        MovimientoInventario m10 = new MovimientoInventario(p10.getId(), TipoMovimiento.SALIDA_VENTA, 1, "Venta rápida", 2);

        movInventarios.add(m1);
        movInventarios.add(m2);
        movInventarios.add(m3);
        movInventarios.add(m4);
        movInventarios.add(m5);
        movInventarios.add(m6);
        movInventarios.add(m7);
        movInventarios.add(m8);
        movInventarios.add(m9);
        movInventarios.add(m10);

        // --- Llamadas a persistencia: registrar ventas completas ---
        for (venta v : ventas) {
            List<Integer> ids = new ArrayList<>();
            for (detalleVenta dv : detallesVentas) {
                try {
                    if (dv.getIdVenta() == v.getIdVenta()) {
                        ids.add(dv.getIdProducto());
                    }
                } catch (Exception e) {
                    // en caso de que no existan los getters, evitar romper la ejecución
                }
            }

            try {
                boolean ok = ventaService.guardarVenta(v, ids);
                System.out.println("Venta id=" + v.getIdVenta() + " registrada? " + ok);
            } catch (Exception e) {
                System.err.println("Error al registrar venta id=" + v.getIdVenta() + ": " + e.getMessage());
            }
        }

        // --- Llamadas a persistencia: registrar movimientos de inventario ---
        MovimientoRepositoryImpl movimientoRepo = new MovimientoRepositoryImpl();
        for (MovimientoInventario m : movInventarios) {
            try {
                boolean ok = movimientoRepo.registrarMovimiento(m);
                System.out.println("Movimiento registrado (productoId=" + m.getIdProducto() + ")? " + ok);
            } catch (Exception e) {
                System.err.println("Error al registrar movimiento para productoId=" + m.getIdProducto() + ": " + e.getMessage());
            }
        }


       System.out.println("Categorías creadas: " + 5);
       System.out.println("Productos creados: " + 19);
       System.out.println("Platillos creados: " + 10);



        System.out.println("Ventas creadas: " + ventas.size());
        for (venta v : ventas) {
            System.out.println(v);
        }

        System.out.println("Movimientos de inventario creados: " + movInventarios.size());
        for (MovimientoInventario m : movInventarios) {
            System.out.println(m);
        }


        */
    }
}
