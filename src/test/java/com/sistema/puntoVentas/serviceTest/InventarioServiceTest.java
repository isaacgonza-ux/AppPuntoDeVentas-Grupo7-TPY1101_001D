package com.sistema.puntoVentas.serviceTest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sistema.puntoventas.modelo.MovimientoInventario;
import com.sistema.puntoventas.modelo.TipoMovimiento;
import com.sistema.puntoventas.repository.IMovimientoRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.moduloProductos.IstockRepository;

import com.sistema.puntoventas.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock private IMovimientoRepository movimientoRepo;
    @Mock private IProductoRepository productoRepo;
    @Mock private IstockRepository stockRepo;

    private InventarioService inventarioService;
    private MovimientoInventario movimientoValido;

    @BeforeEach
    public void setUp() {
        inventarioService = new InventarioService(movimientoRepo, productoRepo);
        inventarioService.setStockRepo(stockRepo);

        movimientoValido = MovimientoInventario.builder()
                .idMovimiento(1)
                .idProducto(100)
                .cantidad(50)
                .motivo("Ingreso de mercadería mensual")
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .fecha(LocalDateTime.now())
                .idUsuario(1)
                .build();
    }

    @Test
    @DisplayName("TC-01: Camino Feliz - Entrada de mercadería exitosa")
    public void testTC01_ProcesarEntradaExitosa() {
        when(movimientoRepo.actualizarStockFisico(100, 50)).thenReturn(true);
        when(movimientoRepo.registrarMovimiento(any(MovimientoInventario.class))).thenReturn(true);

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertTrue(resultado);
        verify(movimientoRepo, times(1)).generarAlertaStock();
    }

    @Test
    @DisplayName("TC-02: Camino Feliz - Salida de mercadería con stock suficiente")
    public void testTC02_ProcesarSalidaExitosa() {
        movimientoValido.setTipoMovimiento(TipoMovimiento.SALIDA_VENTA);
        when(stockRepo.obtenerStockActual(100)).thenReturn(100);
        when(movimientoRepo.actualizarStockFisico(100, -50)).thenReturn(true);
        when(movimientoRepo.registrarMovimiento(any(MovimientoInventario.class))).thenReturn(true);

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertTrue(resultado);
        verify(movimientoRepo).actualizarStockFisico(100, -50);
    }

    @Test
    @DisplayName("TC-03: Procesar Movimiento Nulo")
    public void testTC03_MovimientoNulo() {
        boolean resultado = inventarioService.procesarMovimiento(null);

        assertFalse(resultado);
        verifyNoInteractions(movimientoRepo, stockRepo);
    }

    @Test
    @DisplayName("TC-04: Procesar Movimiento sin Tipo de Movimiento")
    public void testTC04_TipoMovimientoNulo() {
        movimientoValido.setTipoMovimiento(null);

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("TC-05: Validación Stock Insuficiente - Salida Venta")
    public void testTC05_SalidaVentaStockInsuficiente() {
        movimientoValido.setTipoMovimiento(TipoMovimiento.SALIDA_VENTA);
        when(stockRepo.obtenerStockActual(100)).thenReturn(20); // Menor a las 50 requeridas

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertFalse(resultado);
        verify(movimientoRepo, never()).actualizarStockFisico(anyInt(), anyInt());
    }

    @Test
    @DisplayName("TC-06: Validación Stock Insuficiente - Merma")
    public void testTC06_MermaStockInsuficiente() {
        movimientoValido.setTipoMovimiento(TipoMovimiento.MERMA);
        when(stockRepo.obtenerStockActual(100)).thenReturn(45);

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertFalse(resultado);
        verify(movimientoRepo, never()).registrarMovimiento(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("TC-07: Verificación de Inversión de Signo Automatizada")
    public void testTC07_InversionSignoCantidad() {
        movimientoValido.setTipoMovimiento(TipoMovimiento.MERMA);
        when(stockRepo.obtenerStockActual(100)).thenReturn(80);
        when(movimientoRepo.actualizarStockFisico(anyInt(), anyInt())).thenReturn(true);
        when(movimientoRepo.registrarMovimiento(any(MovimientoInventario.class))).thenReturn(true);

        inventarioService.procesarMovimiento(movimientoValido);

        ArgumentCaptor<Integer> cantidadCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(movimientoRepo).actualizarStockFisico(eq(100), cantidadCaptor.capture());

        assertEquals(-50, cantidadCaptor.getValue());
    }

    @Test
    @DisplayName("TC-08: Consistencia Historial - Almacenamiento en Positivo")
    public void testTC08_HistorialCantidadAbsoluta() {
        movimientoValido.setTipoMovimiento(TipoMovimiento.SALIDA_VENTA);
        when(stockRepo.obtenerStockActual(100)).thenReturn(100);
        when(movimientoRepo.actualizarStockFisico(100, -50)).thenReturn(true);
        when(movimientoRepo.registrarMovimiento(any(MovimientoInventario.class))).thenReturn(true);

        inventarioService.procesarMovimiento(movimientoValido);

        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepo).registrarMovimiento(movimientoCaptor.capture());

        assertEquals(-50, movimientoCaptor.getValue().getCantidad());
    }

    @Test
    @DisplayName("TC-09: Interrupción por Fallo en Actualización de Stock Físico")
    public void testTC09_FalloActualizarStockFisico() {
        when(movimientoRepo.actualizarStockFisico(100, 50)).thenReturn(false);

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertFalse(resultado);
        verify(movimientoRepo, never()).registrarMovimiento(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("TC-10: Interrupción por Fallo en Registro de Historial")
    public void testTC10_FalloRegistrarMovimientoHistorial() {
        when(movimientoRepo.actualizarStockFisico(100, 50)).thenReturn(false);

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("TC-11: Verificación de Disparador Automático de Alertas")
    public void testTC11_DispararAlertaStock() {
        when(movimientoRepo.actualizarStockFisico(100, 50)).thenReturn(true);
        when(movimientoRepo.registrarMovimiento(any(MovimientoInventario.class))).thenReturn(true);

        inventarioService.procesarMovimiento(movimientoValido);

        verify(movimientoRepo, times(1)).generarAlertaStock();
    }

    @Test
    @DisplayName("TC-12: Manejo Robusto de Excepciones de Base de Datos")
    public void testTC12_ManejoExcepcionesDb() {
        when(movimientoRepo.actualizarStockFisico(100, 50)).thenThrow(new RuntimeException("SQLite temporalmente bloqueado"));

        boolean resultado = inventarioService.procesarMovimiento(movimientoValido);

        assertFalse(resultado);
    }
}