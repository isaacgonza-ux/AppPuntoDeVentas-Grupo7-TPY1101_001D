package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.moduloProducto.*;
import com.sistema.puntoventas.repository.impl.PlatilloRepositoryImpl;
import com.sistema.puntoventas.repository.impl.ProductoRepositoryImpl;
import com.sistema.puntoventas.repository.moduloProductos.IPlatilloRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;

import java.util.List;

public class PlatilloService {

    private IPlatilloRepository platilloRepository;
    private IProductoRepository productoRepository;
    private AuditoriaService auditoriaService;

    public PlatilloService(){
        this.platilloRepository = new PlatilloRepositoryImpl();
        this.productoRepository = new ProductoRepositoryImpl();
        this.auditoriaService = new AuditoriaService();
    }



    public void registrarPlatillo(Platillo platillo) throws Exception {
        if (platillo == null) {
            throw new Exception("El platillo no puede ser nulo");
        }

        if (platillo.getNombre() == null || platillo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del platillo es obligatorio.");
        }

        if (platillo.getPrecio() <= 0) {
            throw new Exception("El platillo debe tener un precio de venta mayor a cero.");
        }

        // Validar que el nombre del platillo sea único
        if (platilloRepository.existeNombre(platillo.getNombre().trim(), 0)) {
            throw new Exception("Ya existe un platillo con el nombre '" + platillo.getNombre() + "'.");
        }

        // Aquí podrías agregar lógica adicional para validar los ingredientes, etc.

       if(platillo.getTipoProducto() == TipoProducto.PLATILLO) {
           platilloRepository.registrarPlatillo(platillo);
           System.out.println("Platillo registrado exitosamente: " + platillo.getNombre());

           AuditoriaEvento evento = new AuditoriaEvento();
           evento.setModulo("PLATILLOS");
           evento.setEntidad("PRODUCTOS");
           evento.setAccion("NUEVO INGRESO");
           evento.setDetalle("Se agregó el platillo: " + platillo.getNombre());

           boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
           if (!auditoriaRegistrada) {
               throw new Exception("El producto se registro, pero no se pudo guardar el evento de auditoria.");
           }
           System.out.println("Evento registrado"+evento.getAccion()+" para el platillo: " + platillo.getNombre());


       } else {
           throw new Exception("El tipo de producto debe ser PLATILLO para registrar un platillo.");
       }

    }

    public List<Platillo> obtenerPlatillos() throws Exception {

        return platilloRepository.obtenerPlatillos();
    }


    public List<Producto> obtenerIngredientes() throws Exception{
        return productoRepository.buscarPorTipoProducto(TipoProducto.SOLO_INVENTARIO);
    }

      /**
     * Recupera la lista de platillos activos incluyendo su receta completa
     * y los datos detallados de cada ingrediente (Unidad de Medida, etc).
     */
    public List<Platillo> obtenerPlatillosConRecetaCompleta() throws Exception {
        return platilloRepository.obtenerPlatillosConRecetaCompleta();
    }
    

    public void calcularCostoProduccion(Platillo platillo){
        double costoTotal = 0.0;
        
        if(platillo.getIngrediente() != null){
            for (DetallePlatillo detalle : platillo.getIngrediente()){
                if(detalle.getProducto() != null){
                    double costoIngrediente = detalle.getProducto().getPrecioCompra();
                    double cantidadUtilizada = detalle.getCantidadIngrediente();
                    costoTotal += costoIngrediente * cantidadUtilizada;
                    System.out.println(costoTotal);

                }
            }
            costoTotal = Math.round(costoTotal * 10.0)/10.0;
            
            platillo.setCostoProduccion(costoTotal);
        }


    }


    public void validarStockIngredientes(Producto ingrediente, double cantidadNueva, List<DetallePlatillo> ingredientesActuales) throws Exception{
        if(ingrediente == null){
            throw new Exception("Debes seleccionar un ingrediente");
        }

        if(cantidadNueva <= 0){
            throw new Exception("La cantidad del ingrediente debe ser mayor a cero");
        }

        double cantidadAcumulada = 0.0;
        if(ingredientesActuales != null){
            for (DetallePlatillo detalle : ingredientesActuales){
                if(detalle.getProducto() != null && detalle.getProducto().getId() == ingrediente.getId()){
                    cantidadAcumulada += detalle.getCantidadIngrediente();
                }
            }
        }

        double totalRequerido = cantidadAcumulada + cantidadNueva;

        // Determinamos el stock disponible según la unidad de medida
        double stockDisponible = (ingrediente.getUnidadMedida() == UnidadMedida.UNIDAD) 
                                 ? ingrediente.getStockActual() 
                                 : ingrediente.getCantidad();

        if(totalRequerido > stockDisponible){
            throw new Exception("No hay suficiente stock del ingrediente '"
                    + ingrediente.getNombre() + "'. Stock actual: "
                    + stockDisponible + ", cantidad requerida: " + totalRequerido);
        }

    }




    public double convertirCantidad(Producto producto, double cantidadIngresada){
        if(producto.getUnidadMedida() != null){
            switch (producto.getUnidadMedida()){
                case GRAMOS:
                case MILILITROS:
                    //return cantidadIngresada / 1000.0;
                    return cantidadIngresada;
                
                default:
                    return cantidadIngresada;
            }
        }

        return cantidadIngresada;
    }

    public int calcularStockDisponibleTiempoReal(Platillo platillo) throws Exception {
        if (platillo == null) {
            throw new Exception("El platillo no puede ser nulo.");
        }

        List<DetallePlatillo> ingredientes = platillo.getIngrediente();
        if (ingredientes == null || ingredientes.isEmpty()) {
            return 0;
        }

        int stockPosible = Integer.MAX_VALUE;

        for (DetallePlatillo detalle : ingredientes) {
            if (detalle == null || detalle.getProducto() == null) {
                throw new Exception("Hay un ingrediente inválido en la receta del platillo.");
            }

            Producto ingrediente = detalle.getProducto();
            double cantidadRequerida = detalle.getCantidadIngrediente();

            if (cantidadRequerida <= 0) {
                throw new Exception("La cantidad requerida del ingrediente '" + ingrediente.getNombre() + "' debe ser mayor a cero.");
            }

            // Stock real basado en la unidad de medida
            double stockDisponible = (ingrediente.getUnidadMedida() == UnidadMedida.UNIDAD) 
                                     ? ingrediente.getStockActual() 
                                     : ingrediente.getCantidad();

            if (stockDisponible < 0) {
                throw new Exception("El stock del ingrediente '" + ingrediente.getNombre() + "' no puede ser negativo.");
            }

            int stockPorIngrediente = (int) Math.floor(stockDisponible / cantidadRequerida);
            stockPosible = Math.min(stockPosible, stockPorIngrediente);
        }

        return Math.max(stockPosible, 0);
    }

    public boolean actualizarPlatillo(Platillo platillo) throws Exception {
        if (platillo == null) {
            throw new Exception("El platillo no puede ser nulo");
        }

        if (platillo.getId() <= 0) {
            throw new Exception("El ID del platillo no es válido.");
        }

        if (platillo.getNombre() == null || platillo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del platillo es obligatorio.");
        }

        if (platillo.getPrecio() <= 0) {
            throw new Exception("El precio de venta debe ser mayor a cero.");
        }

        if (platillo.getCategoria() == null || platillo.getCategoria().getId() <= 0) {
            throw new Exception("Debe seleccionar una categoría válida.");
        }

        if (platillo.getTipoProducto() != TipoProducto.PLATILLO) {
            throw new Exception("El tipo de producto debe ser PLATILLO para actualizar un platillo.");
        }

        String nombreNormalizado = platillo.getNombre().trim();
        if (platilloRepository.existeNombre(nombreNormalizado, platillo.getId())) {
            throw new Exception("Ya existe un platillo con el nombre '" + nombreNormalizado + "'.");
        }

        platillo.setNombre(nombreNormalizado);
        calcularCostoProduccion(platillo);

        boolean actualizado = platilloRepository.actualizarPlatillo(platillo);
        System.out.println("Platillo actualizado" +platillo.getNombre());

        if (!actualizado) {
            throw new Exception("No se pudo actualizar el platillo en la base de datos.");
        }

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("PLATILLOS");
        evento.setEntidad("Platillo");
        evento.setAccion("ACTUALIZACION");
        evento.setDetalle("Se actualizo el platillo: " + platillo.getNombre());

        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("El producto se registro, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el platillo: " + platillo.getNombre());




        return true;
    }

    public String eliminarPlatillo(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("ID de platillo no válido.");
        }

        Platillo platillo = platilloRepository.obtenerPlatilloPorId(id);
        if (platillo == null) {
            throw new Exception("El platillo no existe.");
        }

        boolean asociadoVenta = platilloRepository.estaAsociadoVenta(id);
        if (asociadoVenta) {
            boolean desactivado = platilloRepository.desactivarPlatillo(id);
            if (!desactivado) {
                throw new Exception("Error al desactivar el platillo asociado a una venta.");
            }
            return "Este platillo está asociado a una venta y fue desactivado.";
        }

        boolean eliminado = platilloRepository.eliminarPlatillo(id);
        System.out.println("Platillo eliminado"+platillo.getNombre());
        if (!eliminado) {
            throw new Exception("Error al eliminar el platillo.");
        }

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("PLATILLOS");
        evento.setEntidad("Platillo");
        evento.setAccion("ELIMINACION");
        evento.setDetalle("Se actualizó el platillo: " + platillo.getNombre());

        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("El producto se registro, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el platillo: " + platillo.getNombre());


        return "ELIMINADO";
    }

    public boolean desactivarPlatillo(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("ID de platillo no válido.");
        }

        Platillo platillo = platilloRepository.obtenerPlatilloPorId(id);
        if (platillo == null) {
            throw new Exception("El platillo no existe.");
        }

        boolean desactivado = platilloRepository.desactivarPlatillo(id);
        if (!desactivado) {
            throw new Exception("No se pudo desactivar el platillo.");
        }

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("PLATILLOS");
        evento.setEntidad("Platillo");
        evento.setAccion("DESACTIVACION");
        evento.setDetalle("Se desactivo el platillo: " + platillo.getNombre());

        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("El platillo se desactivo, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el platillo: " + platillo.getNombre());

        return true;
    }

}
