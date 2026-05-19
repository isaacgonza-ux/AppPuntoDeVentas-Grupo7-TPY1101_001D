package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.ventaAplicacion;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.modelo.moduloProducto.DetallePlatillo;
import com.sistema.puntoventas.modelo.detalleVenta;


import com.sistema.puntoventas.repository.impl.DetalleVentaImpl;
import com.sistema.puntoventas.repository.impl.MovimientoRepositoryImpl;

import com.sistema.puntoventas.repository.impl.VentaRepositoryimpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VentaService {
    
    DetalleVentaImpl detalleVentaImpl = new DetalleVentaImpl();
    VentaRepositoryimpl ventaRepositoryimpl = new VentaRepositoryimpl();
    ProductoService productoService = new ProductoService();
    MovimientoRepositoryImpl movimientoRepo = new MovimientoRepositoryImpl();

    


    public List<ventaAplicacion> traerTodasLasVentas(){
        return detalleVentaImpl.obtenerTodasLasVentas();
    }
    
        
    public ObservableList<String> obtenerFechasTableView(){
        try {
            List<String> listaFechas = ventaRepositoryimpl.obtenerTodasLasFechas();
            Set<String> fechasUnicas = new HashSet<>();

            for (String fechaCompleta : listaFechas) {
                fechasUnicas.add(fechaCompleta.split(" ")[0]);
            }

            List<String> resultado = new ArrayList<>(fechasUnicas);
            resultado.sort((a, b) -> b.compareTo(a)); // Orden descendente
            return FXCollections.observableArrayList(resultado);
            
        } catch (Exception e) {
            System.err.println("Error al obtener fechas: " + e.getMessage());
            return null;
        }
        

    }

    public ObservableList<ventaAplicacion> obtenerVentasporFecha(String fecha){

        //formatear string fecha para solo obtener la fecha y no la hora ej: 2025-04-01 09:15:00 a 2025-04-01
        
        fecha = "%" + fecha + "%"; //% % para el LIKE del query SQL
        System.out.println("la fecha se formateó a: " + fecha);

        try {
            ObservableList<ventaAplicacion> obsListVentas = FXCollections.observableArrayList(detalleVentaImpl.obtenerTodasLasVentasporFecha(fecha));
            return obsListVentas;

        } catch (Exception e) {
            System.err.println("Error al obtener ventas por fecha: " + e.getMessage());
            return null;
        }
    }
        

    public Boolean subirTablaBD(ArrayList<ventaAplicacion> tablaVentaAplicacion){
        return ventaRepositoryimpl.registrarTabladeVentaCompleta(tablaVentaAplicacion);
    }

    /**
     * Registra una venta individual con su lista de productos asociados.
     * Utilizado principalmente por las suites de pruebas y lógica legacy.
     */
    public Boolean guardarVenta(venta v, List<Integer> idProductos) {
        // Delegamos al repositorio pasando una lista vacía para platillos
        return ventaRepositoryimpl.registrarVentaCompleta(v, idProductos, new ArrayList<>());
    }

    /**
     * Resuelve una cadena de texto buscando coincidencias tanto en el catálogo de productos
     * como en el de platillos.
     */
    public void resolverItemsVenta(String input, List<Producto> catalogoProd, List<Platillo> catalogoPlat, ventaAplicacion ventaApp) throws Exception {
        if (input == null || input.isBlank()) {
            throw new Exception("El campo de productos/platillos no puede estar vacío.");
        }

        String[] nombres = input.split(",");
        List<Producto> listaProductos = new ArrayList<>();
        List<Platillo> listaPlatillos = new ArrayList<>();
        List<String> noEncontrados = new ArrayList<>();

        for (String nombre : nombres) {
            String nombreLimpio = nombre.trim();
            if (nombreLimpio.isEmpty()) continue;

            // Intentar encontrar como producto
            Optional<Producto> prodEncontrado = catalogoProd.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombreLimpio))
                .findFirst();

            if (prodEncontrado.isPresent()) {
                listaProductos.add(prodEncontrado.get());
                continue;
            }

            // Intentar encontrar como platillo
            Optional<Platillo> platEncontrado = catalogoPlat.stream()
                .filter(pl -> pl.getNombre().equalsIgnoreCase(nombreLimpio))
                .findFirst();

            if (platEncontrado.isPresent()) {
                listaPlatillos.add(platEncontrado.get());
                continue;
            }

            noEncontrados.add(nombreLimpio);
        }

        if (!noEncontrados.isEmpty()) {
            throw new Exception("No se encontraron los siguientes ítems: " + String.join(", ", noEncontrados));
        }

        ventaApp.setDetalleVentas(listaProductos);
        ventaApp.setDetallePlatillos(listaPlatillos);
    }

    /**
     * Calcula la suma total de los precios basándose en los nombres de productos o platillos
     * proporcionados en una cadena separada por comas.
     */
    public double calcularTotal(String input, List<Producto> catalogoProd, List<Platillo> catalogoPlat) {
        if (input == null || input.isBlank()) {
            return 0.0;
        }

        String[] nombres = input.split(",");
        double total = 0.0;

        for (String nombre : nombres) {
            final String nombreLimpio = nombre.trim();
            if (nombreLimpio.isEmpty()) continue;

            // Buscar en catálogo de productos
            Optional<Producto> prodEncontrado = (catalogoProd == null) ? Optional.empty() : catalogoProd.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombreLimpio))
                .findFirst();

            if (prodEncontrado.isPresent()) {
                total += prodEncontrado.get().getPrecioVenta();
                continue;
            }

            // Buscar en catálogo de platillos
            Optional<Platillo> platEncontrado = (catalogoPlat == null) ? Optional.empty() : catalogoPlat.stream()
                .filter(pl -> pl.getNombre().equalsIgnoreCase(nombreLimpio))
                .findFirst();

            if (platEncontrado.isPresent()) {
                total += platEncontrado.get().getPrecio();
            }
        }
        return total;
    }

    public ventaAplicacion procesarNuevaVentaApp(String totalStr, String fecha, String pago, String desc, List<Producto> productos, List<Platillo> platillos) throws Exception {
        double total;
        try {
            total = Double.parseDouble(totalStr);
        } catch (NumberFormatException e) {
            throw new Exception("El total de la venta no tiene un formato numérico válido.");
        }

        venta v = new venta();
        v.setTotalVenta(total);
        v.setFechaHora(fecha);
        v.setTipoPago(pago);
        v.setDescripcion(desc);

        ventaAplicacion nuevaVentaApp = new ventaAplicacion();
        nuevaVentaApp.setVenta(v);
        nuevaVentaApp.setDetalleVentas(productos);
        nuevaVentaApp.setDetallePlatillos(platillos);
        return nuevaVentaApp;
    }

    /**
     * Descuenta el stock de productos y los ingredientes de los platillos vendidos.
     * 
     * @param productos Lista de productos vendidos (descuenta 1 unidad).
     * @param platillos Lista de platillos vendidos (descuenta según la receta).
     */
    public void descontarProductoyPlatillo(List<Producto> productos, List<Platillo> platillos) {
        System.out.println("[STOCK] Iniciando proceso de descuento de inventario...");
        if (productos != null) {
            for (Producto p : productos) {
                try {
                    Producto latestProd = productoService.obtenerProductoPorId(p.getId());
                    if (latestProd == null) continue;

                    // descontar, se asume que es unidad por defecto
                    
                    if (latestProd.getStockActual() > 0) { // verifica no descontar negativo

                        System.out.println("[STOCK] Descontando 1 unidad de stockActual: " + p.getNombre());
                        movimientoRepo.actualizarStockFisico(p.getId(), -1);
                    } else {
                        System.err.println("[STOCK] Stock insuficiente para: " + p.getNombre() + ". Omitiendo descuento.");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error al descontar producto: " + e.getMessage());
                }
            }
        }

        if (platillos != null) {
            for (Platillo platillo : platillos) {
                System.out.println("[INVENTARIO] Procesando receta del Platillo: " + platillo.getNombre());
                if (platillo.getIngrediente() != null) {
                    for (DetallePlatillo detalle : platillo.getIngrediente()) {
                        Producto prod = detalle.getProducto();
                        if (prod == null) continue;

                        try {
                            int idProductoIngrediente = prod.getId();
                            Producto latestIng = productoService.obtenerProductoPorId(idProductoIngrediente);
                        
                            if (latestIng == null || latestIng.getUnidadMedida() == null) {
                                System.err.println("[ERROR] Datos de ingrediente incompletos para: " + prod.getNombre());
                                continue;
                            }

                            if (latestIng.getUnidadMedida() == UnidadMedida.UNIDAD) {
                                int cantidadADescontar = (int) detalle.getCantidadIngrediente();
                                if (latestIng.getStockActual() >= cantidadADescontar) {
                                    movimientoRepo.actualizarStockFisico(idProductoIngrediente, -cantidadADescontar);
                                    System.out.println("[STOCK] Descontando " + cantidadADescontar + " unidades de " + prod.getNombre());
                                } else {
                                    System.err.println("[STOCK] Stock insuficiente para ingrediente: " + prod.getNombre());
                                }
                            } else {
                                    double cantidadADescontar = detalle.getCantidadIngrediente();
                                    if (latestIng.getCantidad() - cantidadADescontar <= 0 && latestIng.getStockActual() > 0) {
                                        movimientoRepo.actualizarStockFisico(idProductoIngrediente, -1);
                                        double increment = latestIng.getCantidadDefault() - cantidadADescontar;
                                        movimientoRepo.actualizarCantidadFisica(idProductoIngrediente, increment);
                                        System.out.println("[STOCK] Rollover ingrediente: Nueva unidad abierta para " + prod.getNombre());
                                    } else {
                                    double descuentoReal = Math.min(latestIng.getCantidad(), cantidadADescontar);
                                        movimientoRepo.actualizarCantidadFisica(idProductoIngrediente, -descuentoReal);
                                        System.out.println("[STOCK] Descontando " + descuentoReal + " de " + prod.getNombre());
                                    }
                        }
                        } catch (Exception e) {
                            System.err.println("Error al descontar ingrediente: " + e.getMessage());
                        }
                    }
                }
            }
        }
        System.out.println("[STOCK] Proceso de descuento finalizado.");
    }

}
