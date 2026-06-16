package com.sistema.puntoventas.controller.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.MetricasDTO;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.service.PlatilloService;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.util.MensajesAlerta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import static com.sistema.puntoventas.util.MensajesAlerta.mostrarMensaje;

public class PanelPrincipalPlatillosController {

    @FXML
    private Label lblProductosActivos;

    @FXML
    private Label lblCategoriasActivas;

    @FXML
    private Label lblPlatillosActivos;

    @FXML
    private Label lblBajoStock;

    @FXML
    private Pane CardProductos;

    @FXML
    private Pane CardCategorias;

    @FXML
    private Pane CardActivos;

    @FXML
    private Pane CardBajoStock;

    @FXML
    private Button btnAgregarPlatillo;

    @FXML
    private Button btnEditarPlatillo;

    @FXML
    private Button btnEliminarPlatillo;

    @FXML
    private Button btnVerPlatillos;

    @FXML
    private TableView<Platillo> tablePlatillos;

    @FXML
    private TableColumn<Platillo, Integer> colId;

    @FXML
    private TableColumn<Platillo, String> colNombre;

    @FXML
    private TableColumn<Platillo, String> colCategoria;

    @FXML
    private TableColumn<Platillo, Double> colPrecio;

    @FXML
    private TableColumn<Platillo, Double> colCostoProduccion;

    @FXML
    private TableColumn<Platillo, Boolean> colEstado;

    @FXML
    private TableColumn<Platillo, Integer> colStockActual;

    private PlatilloService platilloService;
    private ProductoService productoService;
    private ObservableList<Platillo> listaPlatillos;


    public PanelPrincipalPlatillosController() {
        this.platilloService = new PlatilloService();
        this.productoService = new ProductoService();
    }

    public void initialize() {
        configurarTabla();
        cargarPlatillos();
        actualizarMetricas();
        System.out.println("Platillos cargados: " + listaPlatillos.size());

        // Configurar botones
        if (btnAgregarPlatillo != null) {
            btnAgregarPlatillo.setOnAction(event -> abrirFormularioAgregarPlatillo());
        }
        if (btnEditarPlatillo != null) {
            btnEditarPlatillo.setOnAction(this::editarPlatillo);
        }
        if (btnEliminarPlatillo != null) {
            btnEliminarPlatillo.setOnAction(event -> eliminarPlatillo());
        }
        if (btnVerPlatillos != null) {
            btnVerPlatillos.setOnAction(event -> cargarPlatillos());
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Mismo estilo que productos para extraer el nombre de la categoría del objeto Categoria
        colCategoria.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoria() != null
                        ? cellData.getValue().getCategoria().getNombreCategoria()
                        : "Sin Categoría")
        );

        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCostoProduccion.setCellValueFactory(new PropertyValueFactory<>("costoProduccion"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockActual.setText("fabricables");

        colEstado.setCellValueFactory(cellData -> {
            boolean estado = cellData.getValue().isEstado();
            return new javafx.beans.property.SimpleBooleanProperty(estado);
        });

        // Configurar convertidores para mostrar texto en lugar de true/false
        colEstado.setCellFactory(column -> new TableCell<Platillo, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Activo" : "Inactivo");
                }
            }
        });

        // Opcional: Formatear celdas de dinero (Style extra)
        colPrecio.setCellFactory(tc -> new TableCell<Platillo, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("$%.2f", price));
            }
        });
    }

    private void cargarPlatillos() {
        try {

            List<Platillo> platillos = platilloService.obtenerPlatillosConRecetaCompleta();

            for (Platillo p : platillos) {
                try {
                    int fabricables = platilloService.calcularStockDisponibleTiempoReal(p);
                    p.setStockActual(fabricables);
                } catch (Exception e) {
                    // Si un platillo tiene ingredientes mal configurados, le dejamos 0 por seguridad
                    p.setStockActual(0);
                    System.err.println("No se pudo calcular el stock del platillo " + p.getNombre() + ": " + e.getMessage());
                }
            }
            listaPlatillos = FXCollections.observableArrayList(platillos);
            tablePlatillos.setItems(listaPlatillos);
            //MensajesAlerta.mostrarMensaje("Éxito", "Platillos cargados correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("Error al cargar platillos: " + e.getMessage());
            MensajesAlerta.mostrarMensaje("Error", "No se pudieron cargar los platillos", Alert.AlertType.ERROR);
        }
    }

    private void abrirFormularioAgregarPlatillo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/PanelRegistroPlatillos.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Agregar Platillo");
            stage.setScene(new Scene(root,1000,600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarPlatillos();
            actualizarMetricas();
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al abrir formulario: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editarPlatillo(javafx.event.ActionEvent event) {
        Platillo seleccionado = tablePlatillos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Advertencia", "Selecciona un platillo para editar", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/PanelRegistroPlatillos.fxml"));
            Parent root = loader.load();

            PanelRegistroPlatillosController controller = loader.getController();
            if (controller != null) {
                controller.cargarDatosParaEdicion(seleccionado);
            }

            Stage stage = new Stage();
            stage.setTitle("Editar Platillo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPlatillos();
            actualizarMetricas();
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al abrir formulario: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void eliminarPlatillo() {
        Platillo seleccionado = tablePlatillos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Advertencia", "Selecciona un platillo para eliminar", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmar = MensajesAlerta.mostrarConfirmacion("Confirmación", "¿Estás seguro de que deseas eliminar este platillo?", Alert.AlertType.CONFIRMATION);
        if (confirmar) {
            try {
                String resultado = platilloService.eliminarPlatillo(seleccionado.getId());

                if ("ELIMINADO".equalsIgnoreCase(resultado)) {
                    mostrarMensaje("Éxito", "Platillo eliminado correctamente", Alert.AlertType.INFORMATION);
                } else {
                    mostrarMensaje("Aviso", resultado, Alert.AlertType.WARNING);
                }

                cargarPlatillos();
                actualizarMetricas();
            } catch (Exception e) {
                mostrarMensaje("Error", "Error al eliminar platillo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    private void actualizarMetricas() {
        try {
            MetricasDTO metricas = productoService.calcularMetricas();


            lblPlatillosActivos.setText(String.valueOf(metricas.getTotalPlatillos()));
            lblCategoriasActivas.setText(String.valueOf(metricas.getCategoriasActivas()));
            lblProductosActivos.setText(String.valueOf(metricas.getPlatillosActivos()));

            // Reutilizamos tu misma validación visual para el stock
            lblBajoStock.setText(metricas.getBajoStock() > 0 ? String.valueOf(metricas.getBajoStock()) : "No hay");

        } catch (Exception e) {
            System.err.println("Error al actualizar métricas: " + e.getMessage());
        }
    }
}
