package com.sistema.puntoVentas.serviceTest;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.repository.moduloProductos.ICategoriaRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.moduloProductos.IstockRepository;
import com.sistema.puntoventas.service.AuditoriaService;
import com.sistema.puntoventas.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private IProductoRepository productoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private ICategoriaRepository categoriaRepository;

    @Mock
    private IstockRepository stockRepository;

    @InjectMocks
    private ProductoService productoService;

    @InjectMocks
    private Categoria categoria;
    private Categoria categoria2;
    private Categoria categoria3;

    @BeforeEach
    public void setUp(){
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombreCategoria("Bebidas");
        categoria.setDescripcion("Productos líquidos para consumo");

        categoria2 = new Categoria();
        categoria.setId(2);
        categoria.setNombreCategoria("ingredientes");
        categoria.setDescripcion("Productos para preparar alimentos");

        categoria3 = new Categoria();
        categoria.setId(3);
        categoria.setNombreCategoria("pasteleria");
        categoria.setDescripcion("Productos dulces");
    }

    @Test
    @DisplayName("TC-01:Registrar producto exitoso")
    public void testRegistrarProductoExitoso()throws Exception{
        Producto producto1 = Producto.builder()
                .nombre("coca-cola")
                .precioCompra(1000.0)
                .precioVenta(1500.0) // Cumple el margen (> 10%)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.UNIDAD)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        when(productoRepository.obtenerProductoPorNombre("coca-cola")).thenReturn(new ArrayList<>());
        when(productoRepository.registrarProducto(producto1)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        assertDoesNotThrow(() -> productoService.registrarProducto(producto1));

        // Verificamos que se llamó a guardar en la BD y a la auditoría
        verify(productoRepository, times(1)).registrarProducto(producto1);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));

        System.out.println("Test exitoso");


    }



    //Esperamos a que el guardado falle y lanze una excepción por el margen de ganancia insuficiente,
    // y que NUNCA se intente guardar en la base de datos ni registrar la auditoría
    @Test
    @DisplayName("TC-02:Registrar producto con margen de ganancia insuficiente")
    public void testRegistrarProductoErroneo() throws Exception{
        Producto producto2 = Producto.builder()
                .nombre("donuts")
                .precioCompra(1000.0)
                .precioVenta(1050.0) // No cumple el margen (> 10%)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.UNIDAD)
                .cantidad(1.0)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        when(productoRepository.obtenerProductoPorNombre("donuts")).thenReturn(new ArrayList<>());


        Exception excepcion = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto2);
        });

        assertEquals("Protección de Margen: El precio de venta debe ser al menos un 10% mayor al precio de compra.", excepcion.getMessage());


        // Verificamos que NUNCA se intentó guardar en la base de datos ni registrar la auditoría
        verify(productoRepository, never()).registrarProducto(any(Producto.class));
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-03:Intento de registrar un producto nulo")
    public void testRegistrarProductoNulo() throws Exception{
        Exception excepcion = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(null);
        });


        assertEquals("El producto no puede ser nulo", excepcion.getMessage());


        // Comprobamos y aseguramos que el sistema se protegió y NUNCA tocó tus repositorios ni auditorías
        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-04: Registrar producto con nombre vacío o puros espacios")
    public void testRegistrarProductoNombreVacio() throws Exception{

        Producto productoConNombreVacio = Producto.builder()
                .nombre("     ")
                .precioCompra(1000.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.UNIDAD)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();


        Exception excepcion = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(productoConNombreVacio);
        });


        assertEquals("El nombre del producto es obligatorio.", excepcion.getMessage());


        // Confirmamos que el flujo se cortó inmediatamente y protegió la base de datos
        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-05: Registrar producto con precio compra inválido")
    public void testRegistrarProductoPrecioInvalido()throws Exception{

        Producto producto5 = Producto.builder()
                .nombre("empanada")
                .precioCompra(-300.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.UNIDAD)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto5);
        });

        assertEquals("El precio de compra debe ser mayor a cero", exception.getMessage());


        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-06: Registrar producto con cantidad 0 para unidad de medida")
    public void testRegistrarProductoUnidadMedidaCero() throws Exception{
        Producto producto6 = Producto.builder()
                .nombre("harina")
                .precioCompra(500.0)
                .precioVenta(700.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(0.0) // Cantidad inválida para unidad de medida
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto6);
        });

        assertEquals("La cantidad debe ser mayor a cero para unidades de medida en gramos o mililitros.", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-07: Registrar producto con stock actual negativo")
    public void testRegistrarProductoStockActualInvalido() throws Exception{
        Producto producto7 = Producto.builder()
                .nombre("azucar")
                .precioCompra(300.0)
                .precioVenta(500.0)
                .stockActual(-5) // Stock actual inválido
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto7);
        });

        assertEquals("El stock actual debe ser mayor a cero", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");


    }


    @Test
    @DisplayName("TC-08: Registrar producto con stock mínimo negativo")
    public void testRegistrarProductoStockMinimoInvalido() throws Exception{
        Producto producto8 = Producto.builder()
                .nombre("sal")
                .precioCompra(200.0)
                .precioVenta(400.0)
                .stockActual(50)
                .stockMinimo(-3) // Stock mínimo inválido
                .categoria(categoria)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto8);
        });

        assertEquals("El stock mínimo debe ser mayor a cero", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-09: Registrar producto sin categoría asignada")
    public void testRegistrarProductoSinCategoria() throws Exception{
        Producto producto8 = Producto.builder()
                .nombre("sal")
                .precioCompra(200.0)
                .precioVenta(400.0)
                .stockActual(50)
                .stockMinimo(3)
                .categoria(null) // Sin categoría
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto8);
        });

        assertEquals("El producto debe tener una categoría asignada", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-10: Registrar producto con nombre duplicado")
    public void testRegistrarProductoConNombreDuplicado() throws Exception{
        Producto producto10 = Producto.builder()
                .nombre("coca-cola")
                .precioCompra(1000.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.UNIDAD)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();

        List<Producto> listaDuplicados = new ArrayList<>();
        listaDuplicados.add(producto10);

        when(productoRepository.obtenerProductoPorNombre("coca-cola")).thenReturn(listaDuplicados); // Simulamos que ya existe un producto con ese nombre

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(producto10);
        });

        assertEquals("Ya existe un registro con el nombre '" + producto10.getNombre() , exception.getMessage());

        verify(productoRepository, times(1)).obtenerProductoPorNombre("coca-cola");
        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-11:Debería registrar producto SOLO_INVENTARIO forzando precio de venta a 0.0")
    public void registrarProducto_SoloInventario_ForzarPrecioVentaCero() throws Exception {
        Producto productoInventario = Producto.builder()
                .nombre("Insumo de prueba")
                .precioCompra(5000.0)
                .precioVenta(9999.0)
                .stockActual(10)
                .stockMinimo(2)
                .categoria(categoria2)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.SOLO_INVENTARIO) // Condición clave
                .build();

        when(productoRepository.obtenerProductoPorNombre(anyString())).thenReturn(new ArrayList<>());
        when(productoRepository.registrarProducto(productoInventario)).thenReturn(true);

        assertDoesNotThrow(() -> productoService.registrarProducto(productoInventario));

        // Verificamos que el Service realmente modificó el precio de venta a 0.0
        assertEquals(0.0, productoInventario.getPrecioVenta());
        System.out.println("Precio de venta forzado a: " + productoInventario.getPrecioVenta());
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-12: Error interno cuando el repositorio falla al guardar en SQLite")
    public void testRegistrarProductoErrorAlmacenamiento() throws Exception {

        Producto productoPerfecto = Producto.builder()
                .nombre("Agua Mineral")
                .precioCompra(1000.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria)
                .unidadMedida(UnidadMedida.UNIDAD)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.DIRECTO)
                .build();


        when(productoRepository.obtenerProductoPorNombre("Agua Mineral")).thenReturn(new ArrayList<>());

        // Simulamos que el repositorio falla al intentar guardar el producto, devolviendo 'false'
        when(productoRepository.registrarProducto(productoPerfecto)).thenReturn(false);



        Exception excepcion = assertThrows(Exception.class, () -> {
            productoService.registrarProducto(productoPerfecto);
        });


        assertEquals("Error interno: No se pudo guardar el producto en la base de datos.", excepcion.getMessage());


        verify(productoRepository, times(1)).registrarProducto(productoPerfecto);


        verifyNoInteractions(auditoriaService);

        System.out.println("Test exitoso");
    }

    /*----------------------------------------------------------------------------------
    * ---------------------------------------------------------------------------------*/


    @Test
    @DisplayName("TC-13: Buscar por tipo de producto exitoso")
    public void testBuscarPorTipoProductoExitoso() throws Exception {
        List<Producto> listaSimulada = new ArrayList<>();
        listaSimulada.add(new Producto()); // Agregamos un producto ficticio

        when(productoRepository.buscarPorTipoProducto(TipoProducto.DIRECTO)).thenReturn(listaSimulada);

        List<Producto> resultado = productoService.buscarPorTipoProducto(TipoProducto.DIRECTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).buscarPorTipoProducto(TipoProducto.DIRECTO);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-14: Buscar por tipo de producto nulo")
    public void testBuscarPorTipoProductoNulo() {
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.buscarPorTipoProducto(null);
        });

        assertEquals("Debe seleccionar un tipo de producto.", exception.getMessage());
        verifyNoInteractions(productoRepository);
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-15: Obtener stock crítico exitoso (ordena correctamente)")
    public void testObtenerStockCriticoExitoso() {
        Producto p1 = Producto.builder().nombre("Producto A").stockActual(5).build();
        Producto p2 = Producto.builder().nombre("Producto B").stockActual(2).build();

        List<Producto> listaStockCritico = new ArrayList<>();
        listaStockCritico.add(p1);
        listaStockCritico.add(p2);

        when(stockRepository.obtenerStockCritico()).thenReturn(listaStockCritico);

        List<Producto> resultado = productoService.obtenerStockCritico();

        // Verificamos que devuelva la lista ordenada (el de stock 2 debe estar primero)
        assertEquals(2, resultado.size());
        assertEquals("Producto B", resultado.get(0).getNombre());
        verify(stockRepository, times(1)).obtenerStockCritico();
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-16: Obtener stock crítico retorna null (devuelve lista vacía segura)")
    public void testObtenerStockCriticoNulo() {
        when(stockRepository.obtenerStockCritico()).thenReturn(null);

        List<Producto> resultado = productoService.obtenerStockCritico();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        System.out.println("Test exitoso");
    }

    /*--------------------------------------------------------------------------------------
    * --------------------------------------------------------------------------------------*/

    //Actualizar Producto

    @Test
    @DisplayName("TC-17: Actualizar producto exitoso")
    public void testActualizarProductoExitoso() throws Exception{
        Producto productoModificado = Producto.builder()
                .id(1)
                .nombre("manjar")
                .precioCompra(1000.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria3)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.SOLO_INVENTARIO)
                .build();

        when(productoRepository.existeNombre("manjar", 1)).thenReturn(false);
        when(productoRepository.actualizarProducto(productoModificado)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        boolean resultado = productoService.actualizarProducto(productoModificado);
        assertTrue(resultado);
        verify(productoRepository, times(1)).actualizarProducto(productoModificado);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-18: Actualizar producto nulo")
    public void testActualizarProductoNulo() throws Exception{
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.actualizarProducto(null);
        });

        assertEquals("El producto no puede ser nulo", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-19: Actualizar producto con nombre vacío o puros espacios")
    public void testActualizarProductoNombreVacio() throws Exception{
        Producto productoVacio = Producto.builder()
                .id(1)
                .nombre("     ")
                .precioCompra(1000.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria3)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.SOLO_INVENTARIO)
                .build();

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.actualizarProducto(productoVacio);
        });
        assertEquals("El nombre del producto es obligatorio.", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(auditoriaService);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-20: Actualizar producto con nombre que ya existe en otro producto")
    public void testActualizarProductoConNombreExistente()throws Exception{
        Producto productoNombreDuplicado = Producto.builder()
                .id(1)
                .nombre("manjar")
                .precioCompra(1000.0)
                .precioVenta(1500.0)
                .stockActual(50)
                .stockMinimo(10)
                .categoria(categoria3)
                .unidadMedida(UnidadMedida.GRAMOS)
                .cantidad(1.0)
                .activo(true)
                .tipoProducto(TipoProducto.SOLO_INVENTARIO)
                .build();

        when(productoRepository.existeNombre("manjar", 1)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.actualizarProducto(productoNombreDuplicado);
        });

        assertEquals("Ya existe un producto con el nombre '" + productoNombreDuplicado.getNombre() + "'.", exception.getMessage());


        verifyNoInteractions(auditoriaService);
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-21: Actualizar producto con precio de compra inválido retorna false")
    public void testActualizarProductoPrecioCompraInvalido() throws Exception {
        Producto productoMalo = Producto.builder().id(1).nombre("Galletas").precioCompra(0.0).build();

        when(productoRepository.existeNombre("Galletas", 1)).thenReturn(false);


        boolean resultado = productoService.actualizarProducto(productoMalo);

        assertFalse(resultado);
        verify(productoRepository, never()).actualizarProducto(any(Producto.class));
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-22: Actualizar producto falla en persistencia (BD)")
    public void testActualizarProductoFalloPersistencia() throws Exception {
        Producto productoValido = Producto.builder().id(1).nombre("Jugo").precioCompra(500.0).unidadMedida(UnidadMedida.UNIDAD).build();

        when(productoRepository.existeNombre("Jugo", 1)).thenReturn(false);
        when(productoRepository.actualizarProducto(productoValido)).thenReturn(false); // Simulamos error de BD

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.actualizarProducto(productoValido);
        });

        assertEquals("Error al actualizar el producto. Verifique que el producto exista y que los datos sean correctos.", exception.getMessage());
        verifyNoInteractions(auditoriaService);
        System.out.println("Test exitoso");
    }


    /*----------------------------------------------------------------------------
    * ----------------------------------------------------------------------------*/

    //ELIMINAR PRODUCTO

    @Test
    @DisplayName("TC-23: Eliminar producto con ID inválido")
    public void testEliminarProductoIdInvalido() {
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.eliminarProducto(-1);
        });

        assertEquals("ID de producto no válido.", exception.getMessage());
        verifyNoInteractions(productoRepository);
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-24: Eliminar producto que no existe")
    public void testEliminarProductoInexistente() throws Exception {
        when(productoRepository.obtenerProductoPorId(99)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.eliminarProducto(99);
        });

        assertEquals("El producto no existe", exception.getMessage());
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-25: Eliminar producto asociado a venta o platillo (Borrado Lógico)")
    public void testEliminarProductoConAsociaciones() throws Exception {
        Producto p = Producto.builder().id(1).nombre("Pan").build();

        when(productoRepository.obtenerProductoPorId(1)).thenReturn(p);
        when(productoRepository.estaAsociadoVentaOPlatillo(1)).thenReturn(true);
        when(productoRepository.desactivarProducto(1)).thenReturn(true);

        String resultado = productoService.eliminarProducto(1);

        assertEquals("Este producto esta asociado a venta o platillo y fue desactivado", resultado);
        verify(productoRepository, never()).eliminarProducto(anyInt());
        System.out.println("Test exitoso");
    }


    @Test
    @DisplayName("TC-26: Eliminar producto permanentemente (Exitoso)")
    public void testEliminarProductoExitoso() throws Exception {
        Producto productoEliminar = Producto.builder().id(2).nombre("Leche").build();

        when(productoRepository.obtenerProductoPorId(2)).thenReturn(productoEliminar);
        when(productoRepository.estaAsociadoVentaOPlatillo(2)).thenReturn(false);
        when(productoRepository.eliminarProducto(2)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        String resultado = productoService.eliminarProducto(2);

        assertEquals("ELIMINADO", resultado);
        verify(productoRepository, times(1)).eliminarProducto(2);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
        System.out.println("Test exitoso");
    }


    /*--------------------------------------------------------------------------------------
    * --------------------------------------------------------------------------------------*/


    //REGISTRAR CATEGORIA

    @Test
    @DisplayName("TC-27: Registrar categoría exitoso")
    public void testRegistrarCategoriaExitoso() throws Exception {
        Categoria nuevaCategoria = Categoria.builder()
                .nombreCategoria("Lácteos")
                .descripcion("Productos derivados de la leche")
                .activa(true)
                .build();

        when(categoriaRepository.existeCategoria("Lácteos")).thenReturn(false);
        when(categoriaRepository.registrarCategoria(nuevaCategoria)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        assertDoesNotThrow(() -> productoService.registrarCategoria(nuevaCategoria));

        verify(categoriaRepository, times(1)).registrarCategoria(nuevaCategoria);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-28: Registrar categoría nula")
    public void testRegistrarCategoriaNula() {
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarCategoria(null);
        });

        assertEquals("La categoría no puede ser nula.", exception.getMessage());
        verifyNoInteractions(categoriaRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-29: Registrar categoría con nombre vacío")
    public void testRegistrarCategoriaNombreVacio() {
        Categoria catMala = new Categoria();
        catMala.setNombreCategoria("");

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarCategoria(catMala);
        });

        assertEquals("El nombre de la categoría es obligatorio.", exception.getMessage());
        verifyNoInteractions(categoriaRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-30: Registrar categoría duplicada")
    public void testRegistrarCategoriaDuplicada() throws Exception {
        when(categoriaRepository.existeCategoria(anyString())).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.registrarCategoria(categoria);
        });

        assertEquals("La categoría ya existe.", exception.getMessage());
        verify(categoriaRepository, never()).registrarCategoria(any(Categoria.class));
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-31: Actualizar categoría exitoso")
    public void testActualizarCategoriaExitosa() throws Exception {
        when(categoriaRepository.actualizarCategoria(categoria)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        boolean resultado = productoService.actualizarCategoria(categoria);

        assertTrue(resultado);
        verify(categoriaRepository, times(1)).actualizarCategoria(categoria);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-32: Actualizar categoría inválida (ID cero)")
    public void testActualizarCategoriaIdInvalido() {
        Categoria catMala = new Categoria();
        catMala.setId(0); // ID inválido

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.actualizarCategoria(catMala);
        });

        assertEquals("Categoria no valida.", exception.getMessage());
        verifyNoInteractions(categoriaRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-33: Actualizar categoría con nombre vacío")
    public void testActualizarCategoriaNombreVacio() {
        Categoria catMala = new Categoria();
        catMala.setId(1);
        catMala.setNombreCategoria("   ");

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.actualizarCategoria(catMala);
        });

        assertEquals("El nombre de la categoria es obligatorio.", exception.getMessage());
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-34: Eliminar categoría con ID inválido")
    public void testEliminarCategoriaIdInvalido() {
        Exception exception = assertThrows(Exception.class, () -> {
            productoService.eliminarCategoria(-5);
        });

        assertEquals("ID de categoría no válido.", exception.getMessage());
        verifyNoInteractions(categoriaRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-35: Eliminar categoría en uso")
    public void testEliminarCategoriaEnUso() throws Exception {
        when(categoriaRepository.tieneProductosAsociados(1)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            productoService.eliminarCategoria(1);
        });

        assertEquals("No se puede eliminar la categoria", exception.getMessage());
        verify(categoriaRepository, never()).eliminarCategoria(anyInt());
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-36: Eliminar categoría exitoso")
    public void testEliminarCategoriaExitoso() throws Exception {
        when(categoriaRepository.tieneProductosAsociados(1)).thenReturn(false);
        when(categoriaRepository.eliminarCategoria(1)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        boolean resultado = productoService.eliminarCategoria(1);

        assertTrue(resultado);
        verify(categoriaRepository, times(1)).eliminarCategoria(1);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
        System.out.println("Test exitoso");
    }


}
