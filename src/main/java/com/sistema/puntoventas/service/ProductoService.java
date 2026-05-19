package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.MetricasDTO;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.repository.moduloProductos.ICategoriaRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.moduloProductos.IstockRepository;
import com.sistema.puntoventas.repository.impl.ProductoRepositoryImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductoService {

    private IProductoRepository productoRepository;
    private ICategoriaRepository categoriaRepository;
    private IstockRepository stockRepository;
    private AuditoriaService auditoriaService;


    public ProductoService() {
        this.productoRepository = new ProductoRepositoryImpl();
        this.categoriaRepository = new ProductoRepositoryImpl();
        this.stockRepository = new ProductoRepositoryImpl();
        this.auditoriaService = new AuditoriaService();
    }

    //-----------------------------------------------------------------------------------------------------------------

    public void registrarProducto(Producto producto) throws Exception{
        if (producto == null){
            throw new Exception("El producto no puede ser nulo");
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es obligatorio.");
        }

        if(producto.getPrecioCompra() <= 0){
            throw new Exception("El precio de compra debe ser mayor a cero") ;
        }

        List<Producto> nombreproducto = productoRepository.obtenerProductoPorNombre(producto.getNombre().trim());
        if (!nombreproducto.isEmpty()) {
            for (Producto p : nombreproducto) {
                if (p.getNombre().equalsIgnoreCase(producto.getNombre().trim())) {
                    throw new Exception("Validación fallida: Ya existe un registro con el nombre '" + producto.getNombre() + "'.");
                }
            }
        }



        /*if (producto.getTipoProducto() == TipoProducto.PLATILLO) {
            // Para platillos, el costo viene de los ingredientes. Se valida que se venda a un precio válido.
            if (producto.getPrecioVenta() <= 0) {
                throw new Exception("El platillo debe tener un precio de venta mayor a cero.");
            }
            // Aquí en el futuro llamarías a PlatilloService para calcular el costo de los ingredientes
        }*/

        // Validación de margen (Ejemplo: Mínimo 10% de ganancia)
        double precioMinimoVenta = producto.getPrecioCompra() * 1.10;

        if(producto.getTipoProducto() == TipoProducto.SOLO_INVENTARIO){
            producto.setPrecioVenta(0.0) ; // Forzamos a que el precio de venta sea 0 para productos que solo son de inventario
        } else if (producto.getPrecioVenta() < precioMinimoVenta) {
            throw new Exception("Protección de Margen: El precio de venta debe ser al menos un 10% mayor al precio de compra.");

        }




        if (producto.getUnidadMedida() == null) {
            throw new Exception("Debe asignar una Unidad de Medida al producto.");
        }



        boolean guardado = productoRepository.registrarProducto(producto);
        System.out.println("Producto registrado "+producto.getNombre());

        if (!guardado) {
            throw new Exception("Error interno: No se pudo guardar el producto en la base de datos.");
        }

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("PRODUCTOS");
        evento.setEntidad("Producto");
        evento.setAccion("NUEVO INGRESO");
        evento.setDetalle("Se agregó el producto: " + producto.getNombre());
        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("El producto se registro, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el producto: " + producto.getNombre());

    }


    //--------------------------------------------------------------------------------------------------------------------


    public List<Producto> obtenerProductos() {
        return productoRepository.obtenerProductos();
    }

    //--------------------------------------------------------------------------------------------------------------------

    public List<Producto> buscarPorTipoProducto(TipoProducto tipoProducto) throws Exception {
        if (tipoProducto == null) {
            throw new Exception("Debe seleccionar un tipo de producto.");
        }

        return productoRepository.buscarPorTipoProducto(tipoProducto);
    }

    //-----------------------------------------------------------------------------------------------------------------

    public boolean actualizarProducto(Producto producto) throws Exception{
        if (producto == null){
            throw new Exception("El producto no puede ser nulo");
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es obligatorio.");
        }

        if(producto.getPrecioCompra() <= 0){
            return false;
        }

        if (producto.getUnidadMedida() == null) {
            throw new Exception("Debe asignar una Unidad de Medida al producto.");
        }

        boolean actualizar = productoRepository.actualizarProducto(producto);

        if(!actualizar){
            throw new Exception("Error al actualizar el producto. Verifique que el producto exista y que los datos sean correctos.");
        }
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("PRODUCTOS");
        evento.setEntidad("Producto");
        evento.setAccion("ACTUALIZACION");
        evento.setDetalle("Se actualizo el producto: " + producto.getNombre());
        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("El producto se actualizo, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado"+evento.getAccion()+" para el producto: " + producto.getNombre());

        return actualizar;
    }

    //------------------------------------------------------------------------------------------------------------------


    public String  eliminarProducto(int id) throws Exception{

        if (id <= 0) {
            throw new Exception("ID de producto no válido.");
        }

        Producto producto = productoRepository.obtenerProductoPorId(id);
        if(producto == null){
            throw new Exception("El producto no existe");
        }

        boolean dependencia = productoRepository.estaAsociadoVentaOPlatillo(id);
        if(dependencia){
            boolean desactivado = productoRepository.desactivarProducto(id);
            if(!desactivado){
                throw new Exception("Error al desactivar");
            }
            return "Este producto esta asociado a venta o platillo y fue desactivado";

        }else{

            boolean eliminado = productoRepository.eliminarProducto(id);
            if (!eliminado) {
                throw new Exception("Error al intentar eliminar el producto permanentemente.");
            }

            AuditoriaEvento evento = new AuditoriaEvento();
            evento.setModulo("PRODUCTOS");
            evento.setEntidad("Producto");
            evento.setAccion("ELIMINACION");
            evento.setDetalle("Se eliminó el producto: " + producto.getNombre());
            boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
            if (!auditoriaRegistrada) {
                throw new Exception("El producto se elimino, pero no se pudo guardar el evento de auditoria.");
            }
            System.out.println("Evento registrado"+evento.getAccion()+" para el producto: " + producto.getNombre());

            System.out.println("El producto no tenía asociaciones y fue ELIMINADO de la base de datos.");
            return "ELIMINADO";
        }

    }


    //------------------------------------------------------------------------------------------------------------------

    public boolean existeNombre(String nombre, int id){
        return productoRepository.existeNombre(nombre, id);
    }

    public List<Producto>obtenerStockCritico(){
        List<Producto> stockCritico = stockRepository.obtenerStockCritico();

        if(stockCritico == null){
            return new ArrayList<>();

        }

        stockCritico.sort(Comparator.comparingDouble(Producto::getStockActual));
        return stockCritico;
    }


    //------------------------------------------------------------------------------------------------------------------

    public int obtenerStockActual(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("ID de producto no válido.");
        }

        return stockRepository.obtenerStockActual(id);
    }

    /**
     * Obtiene un producto por su ID.
     * @param id El ID del producto a buscar.
     * @return El objeto Producto si se encuentra, o null si no existe.
     */
    public Producto obtenerProductoPorId(int id) {
        return productoRepository.obtenerProductoPorId(id);
    }


    //------------------------------------------------------------------------------------------------------------------

    public boolean existeCategoria(String nombreCategoria) throws Exception {
        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            throw new Exception("No existe categoria");
        }

        return categoriaRepository.existeCategoria(nombreCategoria.trim());
    }

    public void registrarCategoria(Categoria categoria) throws Exception {
        if (categoria == null) {
            throw new Exception("La categoría no puede ser nula.");
        }

        String nombre = categoria.getNombreCategoria();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre de la categoría es obligatorio.");
        }

        categoria.setNombreCategoria(nombre.trim());
        if (categoria.getDescripcion() != null) {
            categoria.setDescripcion(categoria.getDescripcion().trim());
        }

        if (categoriaRepository.existeCategoria(categoria.getNombreCategoria())) {
            throw new Exception("La categoría ya existe.");
        }

        boolean registrada = categoriaRepository.registrarCategoria(categoria);
        System.out.println("Categoría registrada: " + categoria.getNombreCategoria());
        if (!registrada) {
            throw new Exception("No se pudo registrar la categoría.");
        }

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("CATEGORIAS");
        evento.setEntidad("Categoria");
        evento.setAccion("NUEVO INGRESO");
        evento.setDetalle("Se ingreso la categoria: " + categoria.getNombreCategoria());

        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("La categoria se registro, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado " + evento.getAccion() + " para la categoria: " + categoria.getNombreCategoria());
    }


    //-----------------------------------------------------------------------------------------------------------------


    public boolean actualizarCategoria(Categoria categoria) throws Exception {
        if (categoria == null || categoria.getId() <= 0) {
            throw new Exception("Categoria no valida.");
        }

        String nombre = categoria.getNombreCategoria();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre de la categoria es obligatorio.");
        }

        categoria.setNombreCategoria(nombre.trim());
        if (categoria.getDescripcion() != null) {
            categoria.setDescripcion(categoria.getDescripcion().trim());
        }

        boolean actualizada = categoriaRepository.actualizarCategoria(categoria);
        if (!actualizada) {
            throw new Exception("No se pudo actualizar la categoria.");
        }

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("CATEGORIAS");
        evento.setEntidad("Categoria");
        evento.setAccion("ACTUALIZACION");
        evento.setDetalle("Se actualizo la categoria: " + categoria.getNombreCategoria());
        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("La categoria se actualizo, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado " + evento.getAccion() + " para la categoria: " + categoria.getNombreCategoria());

        return true;
    }

    public List<Categoria> obtenerCategorias() throws Exception {
        List<Categoria> categorias = categoriaRepository.obtenerCategorias();


        if(categorias == null){
            System.out.println("No hay categorias");
            return new ArrayList<>();

        }
        return categorias;
    }

    public boolean eliminarCategoria(int id) throws Exception {
        String idCategoria = "ID " + id;
        if (id <= 0) {
            throw new Exception("ID de categoría no válido.");
        }

        boolean eliminada = categoriaRepository.eliminarCategoria(id);
        System.out.println("Intentando eliminar categoria: " + idCategoria);

        if (!eliminada) {
            throw new Exception("No se pudo eliminar la categoría porque está asociada a un producto o platillo, no existe o hubo un error.");
        }
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo("CATEGORIAS");
        evento.setEntidad("Categoria");
        evento.setAccion("ELIMINACION");
        evento.setDetalle("Se elimino la categoria: " + idCategoria);
        boolean auditoriaRegistrada = auditoriaService.registrarEvento(evento);
        if (!auditoriaRegistrada) {
            throw new Exception("La categoria se elimino, pero no se pudo guardar el evento de auditoria.");
        }
        System.out.println("Evento registrado " + evento.getAccion() + " para la categoria: " + idCategoria);




        return true;
    }


    public MetricasDTO calcularMetricas() throws Exception {
        List<Producto> lista = obtenerProductos();

        return new MetricasDTO(
                lista.size(),
                lista.stream().filter(p -> p.getStockActual() <= p.getStockMinimo()).count(),
                lista.stream().filter(p -> p.getTipoProducto() != null && (
                        p.getTipoProducto() == TipoProducto.DIRECTO ||
                                p.getTipoProducto() == TipoProducto.PLATILLO ||
                                p.getTipoProducto() == TipoProducto.SOLO_INVENTARIO
                )).count(),
                lista.stream().filter(p -> p.getCategoria() != null).map(p -> p.getCategoria().getId()).distinct().count()
        );
    }
}
