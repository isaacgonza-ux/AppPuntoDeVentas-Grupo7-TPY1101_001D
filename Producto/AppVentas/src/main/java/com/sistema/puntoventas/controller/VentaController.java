package com.sistema.puntoventas.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.ventaAplicacion;
import com.sistema.puntoventas.service.PlatilloService;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.service.VentaService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador encargado de la gestión de la vista de ventas.
 * Permite registrar nuevas ventas, gestionar la tabla temporal de transacciones
 * y persistir la información en la base de datos a través de servicios.
 */
public class VentaController {

    // --- Componentes de la Interfaz (FXML) ---
    @FXML private TableView<ventaAplicacion> idTablaVentas;
    @FXML private TableColumn<ventaAplicacion, String> ColFecha;
    @FXML private TableColumn<ventaAplicacion, String> ColProductos;
    @FXML private TableColumn<ventaAplicacion, String> ColTipoPago;
    @FXML private TableColumn<ventaAplicacion, Double> ColTotalVenta;
    @FXML private TableColumn<ventaAplicacion, String> ColDescripcion;

    @FXML private TextField textFieldProducto;
    @FXML private TextField textfieldDescripcion;
    @FXML private TextField textfieldFecha;
    @FXML private TextField textfieldTipoPago;
    @FXML private TextField textfieldTotal;

    @FXML private MenuItem BotonNuevaVenta;

    // --- Servicios y Estado del Controlador ---
    private final VentaService ventaService = new VentaService();
    private ProductoService productoService = new ProductoService();
    private final PlatilloService platilloService = new PlatilloService();

    private final List<Producto> productosDisponibles = new ArrayList<>();
    private final List<Platillo> platillosDisponibles = new ArrayList<>();

    // Listas para almacenar lo que se ingresa manualmente en la sesión actual
    private final List<Producto> productosAgregados = new ArrayList<>();
    private final List<Platillo> platillosAgregados = new ArrayList<>();
    private final ContextMenu contextMenu = new ContextMenu();

    /**
     * Método de inicialización automática de JavaFX.
     * Configura el estado inicial de la vista.
     */
    @FXML
    public void initialize() {
        configurarColumnasTabla();
        inicializarFechaActual();
        cargarCatalogos();
        configurarAutoComplete();
        configurarCalculoTotal();
    }

    /**
     * Configura el mapeo de las celdas y el redimensionamiento de las columnas.
     */
    private void configurarColumnasTabla() {
        ColFecha.prefWidthProperty().bind(idTablaVentas.widthProperty().multiply(0.2));
        ColProductos.prefWidthProperty().bind(idTablaVentas.widthProperty().multiply(0.2));
        ColTipoPago.prefWidthProperty().bind(idTablaVentas.widthProperty().multiply(0.2));
        ColTotalVenta.prefWidthProperty().bind(idTablaVentas.widthProperty().multiply(0.2));
        ColDescripcion.prefWidthProperty().bind(idTablaVentas.widthProperty().multiply(0.2));

        ColFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVenta().getFechaHora()));
        ColTotalVenta.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getVenta().getTotalVenta()));
        ColProductos.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getNombreProducto()));
        ColDescripcion.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVenta().getDescripcion()));
        ColTipoPago.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVenta().getTipoPago()));
    }

    private void inicializarFechaActual() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        textfieldFecha.setText(ahora.format(formatter));
    }

    private void cargarCatalogos() {
        productosDisponibles.clear();
        productosDisponibles.addAll(this.productoService.obtenerProductos());

        try {
            platillosDisponibles.clear();
            platillosDisponibles.addAll(this.platilloService.obtenerPlatillosConRecetaCompleta());
        } catch (Exception e) {
            System.err.println("Error al cargar catálogo de platillos: " + e.getMessage());
        }
    }

    /**
     * Valida la entrada del usuario y agrega una venta a la lista temporal (TableView).
     * Resuelve los productos ingresados mediante comas.
     */
    @FXML
    void agregarVenta(ActionEvent event) {
        if (esFormularioInvalido()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", 
                "Por favor, complete todos los campos requeridos.");
            return;
        }

        try {
            // Usamos un objeto temporal para que el servicio resuelva tanto productos como platillos
            ventaAplicacion tempVA = new ventaAplicacion();
            ventaService.resolverItemsVenta(textFieldProducto.getText(), productosDisponibles, platillosDisponibles, tempVA);
            
            ventaAplicacion nuevaVentaApp = ventaService.procesarNuevaVentaApp(
                textfieldTotal.getText(),
                textfieldFecha.getText(),
                textfieldTipoPago.getText(),
                textfieldDescripcion.getText(),
                tempVA.getDetalleVentas(),
                tempVA.getDetallePlatillos()
            );

            idTablaVentas.getItems().add(nuevaVentaApp);
            
            // Guardamos en las listas de seguimiento manual
            productosAgregados.addAll(nuevaVentaApp.getDetalleVentas());
            platillosAgregados.addAll(nuevaVentaApp.getDetallePlatillos());
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Venta añadida", "Venta agregada exitosamente a la tabla.");
            limpiarCamposVenta();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error de validación", e.getMessage());
        }
    }

    private boolean esFormularioInvalido() {
        return textfieldFecha.getText().isEmpty() || textfieldTotal.getText().isEmpty() ||
               textfieldTipoPago.getText().isEmpty() || textFieldProducto.getText().isEmpty();
    }

    /**
     * Configura el comportamiento de autocompletado para el campo de productos.
     * Permite buscar sugerencias basándose en el último fragmento después de una coma.
     */
    private void configurarAutoComplete() {
        textFieldProducto.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                contextMenu.hide();
                return;
            }

            String[] partes = newVal.split(",");
            String ultimaParte = partes[partes.length - 1].trim();

            if (ultimaParte.isBlank()) {
                contextMenu.hide();
                return;
            }

            List<String> sugerenciasProductos = productosDisponibles.stream()
                .filter(p -> p.getTipoProducto() == TipoProducto.DIRECTO)
                .map(Producto::getNombre)
                .filter(p -> p.toLowerCase().contains(ultimaParte.toLowerCase()))
                .collect(Collectors.toList());

            List<String> sugerenciasPlatillos = platillosDisponibles.stream()
                .map(Platillo::getNombre)
                .filter(pl -> pl.toLowerCase().contains(ultimaParte.toLowerCase()))
                .collect(Collectors.toList());

            List<String> filtrados = new ArrayList<>(sugerenciasProductos);
            filtrados.addAll(sugerenciasPlatillos);

            if (filtrados.isEmpty()) {
                contextMenu.hide();
                return;
            }

            contextMenu.getItems().clear();
            for (String sugerencia : filtrados) {
                MenuItem item = new MenuItem(sugerencia);
                item.setOnAction(e -> {
                    String textoActual = textFieldProducto.getText();
                    int lastSep = textoActual.lastIndexOf(",");
                    String prefijo = lastSep >= 0 ? textoActual.substring(0, lastSep + 1) + " " : "";
                    textFieldProducto.setText(prefijo + sugerencia);
                    textFieldProducto.positionCaret(textFieldProducto.getText().length());
                    contextMenu.hide();
                });
                contextMenu.getItems().add(item);
            }
            contextMenu.show(textFieldProducto, Side.BOTTOM, 0, 0);
        });

        textFieldProducto.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) contextMenu.hide();
        });
    }

    /**
     * Agrega un listener al campo de productos para calcular automáticamente el total
     * basándose en los precios de los productos y platillos ingresados.
     */
    private void configurarCalculoTotal() {
        textFieldProducto.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double total = ventaService.calcularTotal(newVal, productosDisponibles, platillosDisponibles);
                textfieldTotal.setText(String.valueOf((int) total));
            } catch (Exception e) {
                textfieldTotal.setText("0");
            }
        });
    }

    /**
     * Abre el diálogo para cargar ventas históricas filtradas por fecha.
     */
    @FXML
    void cargarVentaAplicacion(ActionEvent event) {
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/panelCargarVenta.fxml"));
            Parent root = loader1.load();
            CargarVentaController cargarController = loader1.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.setTitle("Cargar Venta");
            
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.showAndWait();

            String fechaElegida = cargarController.getFechaSeleccionada();
            if (fechaElegida != null && !fechaElegida.isEmpty()) {
                cargarVentasTableView(fechaElegida);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarVentasTableView(String fecha) {
        List<ventaAplicacion> listaVentas = ventaService.obtenerVentasporFecha(fecha);
        if (listaVentas != null) {
            idTablaVentas.setItems(FXCollections.observableArrayList(listaVentas));
           
        }
    }

    /**
     * Prepara el formulario para una nueva sesión de venta, limpiando la tabla actual.
     */
    @FXML
    void cargarNuevaVenta(ActionEvent event) {
        idTablaVentas.getItems().clear();
        // Limpiamos también el rastro manual al resetear la venta
        productosAgregados.clear();
        platillosAgregados.clear();
    }

    /**
     * Elimina el registro seleccionado y actualiza las listas de items agregados.
     */
    @FXML
    void eliminarVenta(ActionEvent event) {
        ventaAplicacion seleccionada = idTablaVentas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            idTablaVentas.getItems().remove(seleccionada);

            // Sincronizamos las listas manuales eliminando lo que contenía esa fila
            productosAgregados.removeAll(seleccionada.getDetalleVentas());
            platillosAgregados.removeAll(seleccionada.getDetallePlatillos());
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Seleccione un registro para eliminar.");
        }
    }

    /**
     * Envía todos los registros de la tabla actual al servicio de persistencia.
     */
    @FXML
    void guardarTablaEnBD(ActionEvent event) {
        if (idTablaVentas.getItems().isEmpty() || productosAgregados.isEmpty() && platillosAgregados.isEmpty()){
            mostrarAlerta(Alert.AlertType.WARNING, "Tabla vacía", "No hay datos para guardar.");
            return;
        }

        ArrayList<ventaAplicacion> tablaVentaAplicacion = new ArrayList<>(idTablaVentas.getItems());
        if (ventaService.subirTablaBD(tablaVentaAplicacion)) {
            System.out.println("[VENTA] Venta guardada en BD. Procediendo a descontar stock de " 
                               + productosAgregados.size() + " productos y " + platillosAgregados.size() + " platillos.");
            // Descontamos del inventario los productos y ingredientes de platillos procesados
            ventaService.descontarProductoyPlatillo(productosAgregados, platillosAgregados);

            // Limpiar las listas de seguimiento tras el éxito
           
            productosAgregados.clear();
            platillosAgregados.clear();

            System.out.println("[VENTA] Stock actualizado con éxito.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Las ventas se han guardado correctamente.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCamposVenta() {
        textFieldProducto.clear();
        textfieldDescripcion.clear();
        textfieldTipoPago.clear();
        textfieldTotal.clear();
    }



     @FXML
    void recargarSugerenciaProductos(ActionEvent event) { //boton recargar sugerencias
        cargarCatalogos();

    }
}
