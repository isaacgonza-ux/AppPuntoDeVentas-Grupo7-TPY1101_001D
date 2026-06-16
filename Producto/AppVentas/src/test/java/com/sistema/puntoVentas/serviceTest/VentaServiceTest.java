package com.sistema.puntoVentas.serviceTest;

import com.sistema.puntoventas.controller.LoginController;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.ventaAplicacion;
import com.sistema.puntoventas.modelo.moduloProducto.DetallePlatillo;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.repository.impl.DetalleVentaImpl;
import com.sistema.puntoventas.repository.impl.MovimientoRepositoryImpl;
import com.sistema.puntoventas.repository.impl.VentaRepositoryimpl;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.service.VentaService;

import javafx.collections.ObservableList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    DetalleVentaImpl detalleVentaImpl;
    @Mock
    VentaRepositoryimpl ventaRepositoryimpl;
    @Mock
    ProductoService productoService;
    @Mock
    MovimientoRepositoryImpl movimientoRepo;

    VentaService service;

    @BeforeEach
    void setUp() {
        service = new VentaService(detalleVentaImpl, ventaRepositoryimpl, productoService, movimientoRepo);
    }

    @Nested
    @DisplayName("resolverItemsVenta")
    class ResolverItemsVentaTests {

        @Test
        @DisplayName("mezcla de productos y platillos se asigna a sus listas correctas")
        void mezclaProductosYPlatillos() throws Exception {
            // Verifica que items mixtos (producto + platillo) se asignen
            // a las listas correspondientes en ventaAplicacion
            Producto coca = new Producto();
            coca.setNombre("Coca Cola");
            coca.setPrecioVenta(1500);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setPrecio(5000);

            List<Producto> catalogoProd = List.of(coca);
            List<Platillo> catalogoPlat = List.of(pizza);
            ventaAplicacion ventaApp = new ventaAplicacion();

            service.resolverItemsVenta("Coca Cola, Pizza", catalogoProd, catalogoPlat, ventaApp);

            assertEquals(1, ventaApp.getDetalleVentas().size());
            assertEquals("Coca Cola", ventaApp.getDetalleVentas().get(0).getNombre());
            assertEquals(1, ventaApp.getDetallePlatillos().size());
            assertEquals("Pizza", ventaApp.getDetallePlatillos().get(0).getNombre());
        }

        @Test
        @DisplayName("input nulo lanza excepcion")
        void inputNulo() {
            // Valida que pasar null como input lance la excepcion
            // con el mensaje "no puede estar vacio"
            ventaAplicacion ventaApp = new ventaAplicacion();
            Exception ex = assertThrows(Exception.class,
                    () -> service.resolverItemsVenta(null, List.of(), List.of(), ventaApp));
            assertTrue(ex.getMessage().contains("no puede estar vacío"));
        }

        @Test
        @DisplayName("input vacio lanza excepcion")
        void inputVacio() {
            // Valida que un string vacio como input lance excepcion
            ventaAplicacion ventaApp = new ventaAplicacion();
            assertThrows(Exception.class,
                    () -> service.resolverItemsVenta("", List.of(), List.of(), ventaApp));
        }

        @Test
        @DisplayName("item no encontrado en ningun catalogo lanza excepcion con su nombre")
        void itemNoEncontrado() {
            // Verifica que cuando un item del input no existe en
            // ningun catalogo, la excepcion incluya su nombre
            Producto coca = new Producto();
            coca.setNombre("Coca Cola");

            ventaAplicacion ventaApp = new ventaAplicacion();
            Exception ex = assertThrows(Exception.class,
                    () -> service.resolverItemsVenta("Coca Cola, Inexistente", List.of(coca), List.of(), ventaApp));
            assertTrue(ex.getMessage().contains("Inexistente"));
        }

        @Test
        @DisplayName("catalogos vacios lanza excepcion para cualquier item")
        void catalogosVacios() {
            // Verifica que con catalogos existentes pero sin elementos,
            // cualquier item ingresado lance excepcion por no encontrado
            ventaAplicacion ventaApp = new ventaAplicacion();
            assertThrows(Exception.class,
                    () -> service.resolverItemsVenta("Coca Cola", List.of(), List.of(), ventaApp));
        }
    }

    @Nested
    @DisplayName("calcularTotal")
    class CalcularTotalTests {

        @Test
        @DisplayName("mezcla productos y platillos suma correctamente")
        void mezclaProductosYPlatillos() {
            // Verifica que la suma total incluya precios de productos
            // y platillos: 1500 + 5000 = 6500
            Producto coca = new Producto();
            coca.setNombre("Coca Cola");
            coca.setPrecioVenta(1500);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setPrecio(5000);

            double total = service.calcularTotal("Coca Cola, Pizza", List.of(coca), List.of(pizza));
            assertEquals(6500, total, 0.001);
        }

        @Test
        @DisplayName("input nulo retorna 0.0")
        void inputNulo() {
            // Verifica que input nulo retorne 0.0 sin lanzar NPE
            assertEquals(0.0, service.calcularTotal(null, List.of(), List.of()));
        }

        @Test
        @DisplayName("catalogoProductos null no lanza NPE")
        void catalogoProductosNull() {
            // Verifica que el metodo tolera catalogoProd = null
            // y calcula correctamente solo con platillos
            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setPrecio(5000);

            double total = service.calcularTotal("Pizza", null, List.of(pizza));
            assertEquals(5000, total, 0.001);
        }

        @Test
        @DisplayName("item inexistente en ambos catalogos retorna 0.0")
        void itemInexistente() {
            // Verifica que items sin match en ningun catalogo
            // no suman nada al total
            assertEquals(0.0, service.calcularTotal("Inexistente", List.of(), List.of()));
        }
    }

    @Nested
    @DisplayName("procesarNuevaVentaApp")
    class ProcesarNuevaVentaAppTests {

        @Test
        @DisplayName("total no numerico lanza excepcion")
        void totalNoNumerico() {
            // Verifica que un total con formato invalido lance
            // excepcion con mensaje "formato numerico"
            Exception ex = assertThrows(Exception.class,
                    () -> service.procesarNuevaVentaApp("abc", "2026-06-05", "EFECTIVO", "desc", List.of(), List.of()));
            assertTrue(ex.getMessage().contains("formato numérico"));
        }

        @Test
        @DisplayName("datos validos crea ventaAplicacion correctamente")
        void datosValidos() throws Exception {
            // Verifica que con datos validos se construya un objeto
            // ventaAplicacion con todos los campos correctamente asignados
            List<Producto> productos = new ArrayList<>();
            List<Platillo> platillos = new ArrayList<>();

            Usuario usuario = new Usuario();
            usuario.setId(1);
            LoginController.usuarioLogueado = usuario;

            ventaAplicacion result = service.procesarNuevaVentaApp(
                    "1500", "2026-06-05", "EFECTIVO", "Venta normal", productos, platillos);

            assertNotNull(result);
            assertEquals(1500, result.getVenta().getTotalVenta(), 0.001);
            assertEquals("2026-06-05", result.getVenta().getFechaHora());
            assertEquals("EFECTIVO", result.getVenta().getTipoPago());
            assertEquals("Venta normal", result.getVenta().getDescripcion());
            assertSame(productos, result.getDetalleVentas());
            assertSame(platillos, result.getDetallePlatillos());
        }
    }

    @Nested
    @DisplayName("descontarProductoyPlatillo")
    class DescontarProductoyPlatilloTests {

        private Producto crearProducto(int id, String nombre, int stockActual, UnidadMedida unidadMedida, double cantidad, double cantidadDefault) {
            Producto p = new Producto();
            p.setId(id);
            p.setNombre(nombre);
            p.setStockActual(stockActual);
            p.setUnidadMedida(unidadMedida);
            p.setCantidad(cantidad);
            p.setCantidadDefault(cantidadDefault);
            return p;
        }

        @Test
        @DisplayName("producto con stock suficiente descuenta 1 unidad")
        void productoConStock() {
            // Verifica que un producto vendido con stock > 0
            // descuente exactamente 1 unidad via actualizarStockFisico
            Producto p = crearProducto(1, "Coca Cola", 5, null, 0, 0);
            when(productoService.obtenerProductoPorId(1)).thenReturn(p);

            service.descontarProductoyPlatillo(List.of(p), null);

            verify(movimientoRepo).actualizarStockFisico(1, -1);
        }

        @Test
        @DisplayName("producto sin stock no descuenta")
        void productoSinStock() {
            // Verifica que un producto con stockActual = 0 no
            // ejecute descuento alguno (evita stock negativo)
            Producto p = crearProducto(1, "Coca Cola", 0, null, 0, 0);
            when(productoService.obtenerProductoPorId(1)).thenReturn(p);

            service.descontarProductoyPlatillo(List.of(p), null);

            verify(movimientoRepo, never()).actualizarStockFisico(anyInt(), anyInt());
        }

        @Test
        @DisplayName("ingrediente UNIDAD con stock descuenta cantidad exacta")
        void ingredienteUnidadConStock() {
            // Verifica que un ingrediente tipo UNIDAD descuente
            // la cantidad exacta especificada en la receta (2 unidades)
            Producto harina = crearProducto(5, "Harina", 10, UnidadMedida.UNIDAD, 0, 0);
            DetallePlatillo detalle = new DetallePlatillo();
            detalle.setProducto(harina);
            detalle.setCantidadIngrediente(2);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setIngrediente(List.of(detalle));

            when(productoService.obtenerProductoPorId(5)).thenReturn(harina);

            service.descontarProductoyPlatillo(null, List.of(pizza));

            verify(movimientoRepo).actualizarStockFisico(5, -2);
        }

        @Test
        @DisplayName("ingrediente UNIDAD sin stock no descuenta")
        void ingredienteUnidadSinStock() {
            // Verifica que un ingrediente UNIDAD con stock insuficiente
            // (1 disponible, 2 requeridos) no ejecute descuento
            Producto harina = crearProducto(5, "Harina", 1, UnidadMedida.UNIDAD, 0, 0);
            DetallePlatillo detalle = new DetallePlatillo();
            detalle.setProducto(harina);
            detalle.setCantidadIngrediente(2);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setIngrediente(List.of(detalle));

            when(productoService.obtenerProductoPorId(5)).thenReturn(harina);

            service.descontarProductoyPlatillo(null, List.of(pizza));

            verify(movimientoRepo, never()).actualizarStockFisico(anyInt(), anyInt());
        }

        @Test
        @DisplayName("ingrediente GRAMOS con rollover descuenta stock y ajusta cantidad")
        void ingredienteGramosConRollover() {
            // Verifica la logica de rollover: cuando cantidadIngrediente supera
            // la cantidad disponible, descuenta 1 unidad de stock fisico y
            // ajusta la cantidad a (cantidadDefault - cantidadIngrediente)
            Producto harina = crearProducto(5, "Harina", 3, UnidadMedida.GRAMOS, 0.1, 1.0);
            DetallePlatillo detalle = new DetallePlatillo();
            detalle.setProducto(harina);
            detalle.setCantidadIngrediente(0.2);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setIngrediente(List.of(detalle));

            when(productoService.obtenerProductoPorId(5)).thenReturn(harina);

            service.descontarProductoyPlatillo(null, List.of(pizza));

            verify(movimientoRepo).actualizarStockFisico(5, -1);
            verify(movimientoRepo).actualizarCantidadFisica(5, 0.8);
        }

        @Test
        @DisplayName("ingrediente GRAMOS sin rollover solo ajusta cantidad")
        void ingredienteGramosSinRollover() {
            // Verifica que cuando hay suficiente cantidad disponible,
            // solo se ajusta la fraccion sin tocar el stock fisico
            Producto harina = crearProducto(5, "Harina", 3, UnidadMedida.GRAMOS, 2.0, 1.0);
            DetallePlatillo detalle = new DetallePlatillo();
            detalle.setProducto(harina);
            detalle.setCantidadIngrediente(0.5);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setIngrediente(List.of(detalle));

            when(productoService.obtenerProductoPorId(5)).thenReturn(harina);

            service.descontarProductoyPlatillo(null, List.of(pizza));

            verify(movimientoRepo, never()).actualizarStockFisico(anyInt(), anyInt());
            verify(movimientoRepo).actualizarCantidadFisica(5, -0.5);
        }

        @Test
        @DisplayName("ingrediente con producto nulo no lanza excepcion")
        void ingredienteProductoNulo() {
            // Verifica que un DetallePlatillo con producto=null se
            // ignore sin lanzar NPE ni interactuar con los repositorios
            DetallePlatillo detalle = new DetallePlatillo();
            detalle.setProducto(null);

            Platillo pizza = new Platillo();
            pizza.setNombre("Pizza");
            pizza.setIngrediente(List.of(detalle));

            service.descontarProductoyPlatillo(null, List.of(pizza));

            verify(productoService, never()).obtenerProductoPorId(anyInt());
            verifyNoInteractions(movimientoRepo);
        }
    }



    @Test
    void platilloSinIngredientes() {
        Platillo pizza = new Platillo();
        pizza.setNombre("Pizza");
        pizza.setIngrediente(null);

        service.descontarProductoyPlatillo(null, List.of(pizza));

        verifyNoInteractions(movimientoRepo);
    }


    /*
    *
    *test escenciales para el funcionamiento del crud de ventas
    * 
    */

    @Test
    @DisplayName("delega correctamente al repositorio y retorna su resultado")
    void retornaListaDelRepo() {
        // Verifica que el metodo simplemente delega a detalleVentaImpl y devuelve lo que recibe
        List<ventaAplicacion> esperado = List.of(new ventaAplicacion());
        when(detalleVentaImpl.obtenerTodasLasVentas()).thenReturn(esperado);

        List<ventaAplicacion> resultado = service.traerTodasLasVentas();

        assertSame(esperado, resultado);
        verify(detalleVentaImpl).obtenerTodasLasVentas();
    }

    @Test
    @DisplayName("repositorio retorna lista vacia, el servicio la propaga")
    void listaVacia() {
        // Verifica que una lista vacia del repo se propague sin transformacion
        when(detalleVentaImpl.obtenerTodasLasVentas()).thenReturn(List.of());

        List<ventaAplicacion> resultado = service.traerTodasLasVentas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("fechas duplicadas quedan como una sola en el resultado")
    void deduplicaFechas() {
        // Verifica que dos registros con la misma fecha (distinta hora) producen un solo elemento
        when(ventaRepositoryimpl.obtenerTodasLasFechas())
            .thenReturn(List.of("2026-06-01 09:00:00", "2026-06-01 15:30:00"));

        ObservableList<String> resultado = service.obtenerFechasTableView();

        assertEquals(1, resultado.size());
        assertEquals("2026-06-01", resultado.get(0));
    }

    @Test
    @DisplayName("fechas distintas se ordenan de mayor a menor")
    void ordenDescendente() {
        // Verifica el orden descendente: la fecha mas reciente aparece primero
        when(ventaRepositoryimpl.obtenerTodasLasFechas())
            .thenReturn(List.of("2026-05-01 08:00:00", "2026-06-01 08:00:00"));

        ObservableList<String> resultado = service.obtenerFechasTableView();

        assertEquals("2026-06-01", resultado.get(0));
        assertEquals("2026-05-01", resultado.get(1));
    }

    @Test
    @DisplayName("excepcion en el repo retorna null sin propagar")
    void repoLanzaExcepcion() {
        // Verifica que si el repositorio falla, el metodo captura el error y retorna null
        when(ventaRepositoryimpl.obtenerTodasLasFechas()).thenThrow(new RuntimeException("DB error"));

        ObservableList<String> resultado = service.obtenerFechasTableView();

        assertNull(resultado);
    }

    @Test
    @DisplayName("la fecha se formatea con %% antes de llamar al repo")
    void formateaFechaConLike() {
        // Verifica que el string de fecha se envuelve en %...% para el LIKE de SQL
        when(detalleVentaImpl.obtenerTodasLasVentasporFecha("%2026-06-01%"))
            .thenReturn(List.of());

        service.obtenerVentasporFecha("2026-06-01");

        verify(detalleVentaImpl).obtenerTodasLasVentasporFecha("%2026-06-01%");
    }

    @Test
    @DisplayName("retorna las ventas del repo como ObservableList")
    void retornaObservableList() {
        // Verifica que el resultado sea un ObservableList con los datos del repositorio
        ventaAplicacion va = new ventaAplicacion();
        when(detalleVentaImpl.obtenerTodasLasVentasporFecha(anyString()))
            .thenReturn(List.of(va));

        ObservableList<ventaAplicacion> resultado = service.obtenerVentasporFecha("2026-06-01");

        assertEquals(1, resultado.size());
        assertSame(va, resultado.get(0));
    }

    @Test
    @DisplayName("excepcion en el repo retorna null sin propagar")
    void repoLanzaExcepcionFechas() {
        // Verifica que si el repositorio falla, el metodo captura el error y retorna null
        when(detalleVentaImpl.obtenerTodasLasVentasporFecha(anyString()))
            .thenThrow(new RuntimeException("DB error"));

        ObservableList<ventaAplicacion> resultado = service.obtenerVentasporFecha("2026-06-01");

        assertNull(resultado);
    }


    @Test
    @DisplayName("delega al repositorio y retorna true si este retorna true")
    void retornaTrue() {
        // Verifica que el metodo propague el resultado true del repositorio sin modificarlo
        ArrayList<ventaAplicacion> tabla = new ArrayList<>();
        when(ventaRepositoryimpl.registrarTabladeVentaCompleta(tabla)).thenReturn(true);

        assertTrue(service.subirTablaBD(tabla));
    }

    @Test
    @DisplayName("retorna false si el repositorio retorna false")
    void retornaFalse() {
        // Verifica que un fallo en el repositorio se propague como false
        when(ventaRepositoryimpl.registrarTabladeVentaCompleta(any())).thenReturn(false);

        assertFalse(service.subirTablaBD(new ArrayList<>()));
    }

    @Test
@DisplayName("siempre pasa lista vacia de platillos al repositorio")
void pasaListaVaciadePlatillos() {
    // Verifica que guardarVenta nunca pasa platillos al repositorio (comportamiento legacy)
    venta v = new venta();
    List<Integer> ids = List.of(1, 2);
    when(ventaRepositoryimpl.registrarVentaCompleta(eq(v), eq(ids), eq(List.of())))
        .thenReturn(true);

    Boolean result = service.guardarVenta(v, ids);

    assertTrue(result);
    verify(ventaRepositoryimpl).registrarVentaCompleta(v, ids, new ArrayList<>());
}

    @Test
    @DisplayName("retorna false si el repositorio retorna false")
    void retornaFalseDelRepo() {
        // Verifica que un fallo en el repositorio se propague correctamente
        when(ventaRepositoryimpl.registrarVentaCompleta(any(), any(), any())).thenReturn(false);
        //para venta individual
        assertFalse(service.guardarVenta(new venta(), List.of()));
    }


    @Test
    @DisplayName("verifica que service delega al repositorio y retorna true si este retorna true")
    void retornaTrueYVerificaPasoAlRepository() {
        ArrayList<ventaAplicacion> tabla = new ArrayList<>();
        when(ventaRepositoryimpl.registrarTabladeVentaCompleta(tabla)).thenReturn(true);

        assertTrue(service.subirTablaBD(tabla));

        // Verifica que el servicio si delego la llamada al repositorio (que conecta con la bd)
        verify(ventaRepositoryimpl).registrarTabladeVentaCompleta(tabla);
    }

}
