package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.modelo.MovimientoInventario;
import com.sistema.puntoventas.modelo.TipoMovimiento;
import com.sistema.puntoventas.repository.impl.MovimientoRepositoryImpl;
import com.sistema.puntoventas.service.InventarioService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.LocalDateTime; // IMPORTADO
import java.time.format.DateTimeFormatter; // IMPORTADO
import java.util.List;
import java.util.ResourceBundle;

public class PanelInventarioController implements Initializable {

    // --- ELEMENTOS DE LA INTERFAZ ---
    @FXML private Button BtnRegistrarMovimiento;
    @FXML private ComboBox<TipoMovimiento> CmbComboBox;
    @FXML private TextField TxtCantidad;
    @FXML private TextArea TxtMotivo;
    @FXML private TextField TxtProductoNombre;

    @FXML private TableView<MovimientoInventario> tablaMovimientos;

    // Cambiado de String a LocalDateTime para que coincida con tu Modelo
    @FXML private TableColumn<MovimientoInventario, LocalDateTime> ColFechaHora;

    @FXML private TableColumn<MovimientoInventario, Integer> ColProducto;
    @FXML private TableColumn<MovimientoInventario, String> ColTipo;
    @FXML private TableColumn<MovimientoInventario, Integer> ColCantidad;
    @FXML private TableColumn<MovimientoInventario, String> ColMotivo;
    @FXML private TableColumn<MovimientoInventario, Integer> ColUsuario;

    // Tarjetas y KPIs
    @FXML private Label lblTotalProductos;
    @FXML private Label lblMovimientoHoy;
    @FXML private Label lblAlertaStock;

    // --- VARIABLES DEL BACKEND ---
    private MovimientoRepositoryImpl movimientoRepo;
    private InventarioService inventarioService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Inicializar Repositorios y Servicios
        movimientoRepo = new MovimientoRepositoryImpl();

        // 2. Configurar el ComboBox con los valores del Enum
        CmbComboBox.setItems(FXCollections.observableArrayList(TipoMovimiento.values()));

        // 3. Configurar las columnas de la tabla
        ColFechaHora.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        ColProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        ColTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
        ColCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        ColMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        ColUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));

        // --- BLOQUE AGREGADO: Formato de fecha elegante ---
        ColFechaHora.setCellFactory(columna -> new TableCell<MovimientoInventario, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                if (vacio || fecha == null) {
                    setText(null);
                } else {
                    // Formato: Día/Mes/Año Hora:Minutos
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    setText(fecha.format(formato));
                }
            }
        });

        // 4. Cargar los datos desde la BD
        cargarHistorial();

        // 5. Asignar la acción al botón directamente aquí
        BtnRegistrarMovimiento.setOnAction(this::registrarMovimientoAction);
    }

    // --- MÉTODOS DE LÓGICA ---

    private void cargarHistorial() {
        List<MovimientoInventario> ultimosMovimientos = movimientoRepo.obtenerUltimosMovimientos();
        ObservableList<MovimientoInventario> datosTabla = FXCollections.observableArrayList(ultimosMovimientos);
        tablaMovimientos.setItems(datosTabla);
    }

    private void registrarMovimientoAction(ActionEvent event) {
        try {
            if (TxtProductoNombre.getText().isEmpty() || TxtCantidad.getText().isEmpty() || CmbComboBox.getValue() == null) {
                mostrarAlerta("Error", "Campos incompletos", "Por favor llena el Producto, Cantidad y Tipo de Movimiento.", Alert.AlertType.WARNING);
                return;
            }

            int idProducto = Integer.parseInt(TxtProductoNombre.getText());
            int cantidad = Integer.parseInt(TxtCantidad.getText());
            TipoMovimiento tipo = CmbComboBox.getValue();
            String motivo = TxtMotivo.getText() != null ? TxtMotivo.getText() : "Sin motivo";
            int idUsuarioAdmin = 1;

            MovimientoInventario nuevoMovimiento = new MovimientoInventario(idProducto, tipo, cantidad, motivo, idUsuarioAdmin);

            boolean registrado = movimientoRepo.registrarMovimiento(nuevoMovimiento);

            // Para la actualización de stock: si es entrada suma, si no (salida/merma) resta
            int cambioStock = (tipo == TipoMovimiento.ENTRADA) ? cantidad : -cantidad;
            boolean stockActualizado = movimientoRepo.actualizarStockFisico(idProducto, cambioStock);

            if (registrado && stockActualizado) {
                mostrarAlerta("Éxito", "Movimiento Registrado", "El inventario se ha actualizado correctamente.", Alert.AlertType.INFORMATION);

                TxtProductoNombre.clear();
                TxtCantidad.clear();
                TxtMotivo.clear();
                CmbComboBox.getSelectionModel().clearSelection();

                cargarHistorial();
            } else {
                mostrarAlerta("Error", "Error en Base de Datos", "No se pudo registrar el movimiento.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Datos inválidos", "El Producto y la Cantidad deben ser números enteros.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error fatal", "Excepción", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}