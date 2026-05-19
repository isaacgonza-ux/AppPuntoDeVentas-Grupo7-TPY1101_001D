package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.MovimientoInventario;
import com.sistema.puntoventas.modelo.TipoMovimiento;
import com.sistema.puntoventas.repository.IMovimientoRepository;
// Asumiendo que tienes esta interfaz creada
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.moduloProductos.IstockRepository;

public class InventarioService {

    private final IMovimientoRepository movimientoRepo;
    private final IProductoRepository productoRepo;
    private IstockRepository stockRepo;

    public InventarioService(IMovimientoRepository movimientoRepo, IProductoRepository productoRepo) {
        this.movimientoRepo = movimientoRepo;
        this.productoRepo = productoRepo;
    }

    // RN-INV-01: Validación de Disponibilidad (Pre-Check)
    public boolean validarDisponibilidad(int idProducto, int cantidadRequerida) {
        // TODO: Si implementas lógica de "Platillos", aquí deberías verificar el tipo de producto
        // y hacer la "Explosión de Receta" para verificar los insumos.

        // Obtenemos el stock real desde la base de datos
        // (Asegúrate de tener un método similar a 'obtenerStockActual' en tu IProductoRepository)
        int stockActual = stockRepo.obtenerStockActual(idProducto);

        return stockActual >= cantidadRequerida;
    }

    // RN-INV-02 y RN-INV-04: Registro Obligatorio y Gestión de Mermas
    public boolean procesarMovimiento(MovimientoInventario mov) {
        try {
            // 1. Si es salida o merma, validar que no quede stock negativo
            if (mov.getTipoMovimiento() == TipoMovimiento.SALIDA_VENTA ||
                    mov.getTipoMovimiento() == TipoMovimiento.MERMA) {

                if (!validarDisponibilidad(mov.getIdProducto(), mov.getCantidad())) {
                    System.out.println("Error: Stock insuficiente para procesar la salida/merma.");
                    return false; // Evitar stock negativo
                }

                // RN-INV-04: Si necesitas el costo de la merma para reportes,
                // podrías obtenerlo aquí: double costo = productoRepo.obtenerPrecioCosto(mov.getIdProducto());

                // Si es salida, la cantidad a sumar al stock debe ser negativa
                mov.setCantidad(mov.getCantidad() * -1);
            }

            // 2. Actualizar el stock en la tabla productos
            boolean stockActualizado = movimientoRepo.actualizarStockFisico(mov.getIdProducto(), mov.getCantidad());

            // 3. Registrar el movimiento en el historial (historial_inventario)
            if (stockActualizado) {
                movimientoRepo.registrarMovimiento(mov);

                // RN-INV-03: Lógica de Alertas
                // Después de mover el stock, disparamos la verificación
                movimientoRepo.generarAlertaStock();

                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error al procesar el movimiento: " + e.getMessage());
            return false;
        }
    }
}