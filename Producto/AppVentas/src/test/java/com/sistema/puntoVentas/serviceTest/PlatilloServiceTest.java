package com.sistema.puntoVentas.serviceTest;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.moduloProducto.*;
import com.sistema.puntoventas.repository.moduloProductos.IPlatilloRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.service.AuditoriaService;
import com.sistema.puntoventas.service.PlatilloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlatilloServiceTest {

    @Mock
    private IPlatilloRepository platilloRepository;

    @Mock
    private IProductoRepository productoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PlatilloService platilloService;

    // Variables globales para reutilizar en todos los tests
    private Categoria categoriaComida;
    private Producto productoPan;
    private Producto productoCarne;
    private Platillo platilloBase;

    @BeforeEach
    public void setUp() {

        categoriaComida = new Categoria();
        categoriaComida.setId(5);
        categoriaComida.setNombreCategoria("Comida Rápida");


        productoPan = new Producto();
        productoPan.setId(1);
        productoPan.setNombre("Pan de Hamburguesa");
        productoPan.setPrecioCompra(300.0);
        productoPan.setUnidadMedida(UnidadMedida.UNIDAD);
        productoPan.setStockActual(10); // Hay 10 panes en inventario
        productoPan.setCantidad(10.0);

        productoCarne = new Producto();
        productoCarne.setId(2);
        productoCarne.setNombre("Carne Molida");
        productoCarne.setPrecioCompra(8000.0); // El kilo (1000g) cuesta 8000
        productoCarne.setUnidadMedida(UnidadMedida.GRAMOS);
        productoCarne.setCantidad(500.0); // Hay 500 gramos en inventario (alcanza para 2 porciones de 250g)
        productoCarne.setStockActual(1);

        // Armamos la Receta (Detalles de Platillo)
        DetallePlatillo detallePan = new DetallePlatillo();
        detallePan.setProducto(productoPan);
        detallePan.setCantidadIngrediente(2.0); // La receta pide 2 panes

        DetallePlatillo detalleCarne = new DetallePlatillo();
        detalleCarne.setProducto(productoCarne);
        detalleCarne.setCantidadIngrediente(250.0); // La receta pide 250 gramos

        List<DetallePlatillo> receta = new ArrayList<>();
        receta.add(detallePan);
        receta.add(detalleCarne);

        //Creamos el Platillo listo para probar
        platilloBase = new Platillo();
        platilloBase.setId(1);
        platilloBase.setNombre("Hamburguesa Doble");
        platilloBase.setPrecio(4500.0);
        platilloBase.setTipoProducto(TipoProducto.PLATILLO);
        platilloBase.setCategoria(categoriaComida);
        platilloBase.setIngrediente(receta);
    }


    @Test
    @DisplayName("TC-01: Registrar platillo exitoso")
    public void testRegistrarPlatilloExitoso() throws Exception {
        when(platilloRepository.existeNombre("Hamburguesa Doble", 0)).thenReturn(false);
        when(platilloRepository.registrarPlatillo(platilloBase)).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        assertDoesNotThrow(() -> platilloService.registrarPlatillo(platilloBase));

        verify(platilloRepository, times(1)).registrarPlatillo(platilloBase);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-02: Registrar platillo nulo")
    public void testRegistrarPlatilloNulo() {
        Exception exception = assertThrows(Exception.class, () -> {
            platilloService.registrarPlatillo(null);
        });

        assertEquals("El platillo no puede ser nulo", exception.getMessage());
        verifyNoInteractions(platilloRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-03: Registrar platillo con nombre vacío")
    public void testRegistrarPlatilloNombreVacio() {
        platilloBase.setNombre("   ");

        Exception exception = assertThrows(Exception.class, () -> {
            platilloService.registrarPlatillo(platilloBase);
        });

        assertEquals("El nombre del platillo es obligatorio.", exception.getMessage());
        verifyNoInteractions(platilloRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-04: Registrar platillo con precio inválido")
    public void testRegistrarPlatilloPrecioInvalido() {
        platilloBase.setPrecio(0.0);

        Exception exception = assertThrows(Exception.class, () -> {
            platilloService.registrarPlatillo(platilloBase);
        });

        assertEquals("El platillo debe tener un precio de venta mayor a cero.", exception.getMessage());
        verifyNoInteractions(platilloRepository);
        System.out.println("Test exitoso");
    }

    @Test
    @DisplayName("TC-05: Registrar platillo con nombre duplicado")
    public void testRegistrarPlatilloNombreDuplicado() throws Exception {
        when(platilloRepository.existeNombre("Hamburguesa Doble", 0)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            platilloService.registrarPlatillo(platilloBase);
        });

        assertEquals("Ya existe un platillo con el nombre 'Hamburguesa Doble'.", exception.getMessage());
        verify(platilloRepository, never()).registrarPlatillo(any(Platillo.class));
        System.out.println("Test exitoso");
    }


}

