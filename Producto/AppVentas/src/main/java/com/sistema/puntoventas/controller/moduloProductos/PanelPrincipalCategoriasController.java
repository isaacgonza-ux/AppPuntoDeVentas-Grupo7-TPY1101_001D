package com.sistema.puntoventas.controller.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.MetricasDTO;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.util.MensajesAlerta;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import static com.sistema.puntoventas.util.MensajesAlerta.mostrarConfirmacion;
import static com.sistema.puntoventas.util.MensajesAlerta.mostrarMensaje;

public class PanelPrincipalCategoriasController {

    @FXML
    private Label lblProductosActivos;
    @FXML
    private Label lblCategoriasActivas;
    @FXML
    private Label lblPlatillosActivos;
    @FXML
    private Label lblBajoStock;

    @FXML
    private Button btnAgregarProducto;
    @FXML
    private Button btnEditarProducto;
    @FXML
    private Button btnEliminarProducto;

    @FXML
    private TableView<Categoria> tableProductos;
    @FXML
    private TableColumn<Categoria, Integer> colId;
    @FXML
    private TableColumn<Categoria, String> colNombre;
    @FXML
    private TableColumn<Categoria, String> colDescripcion;
    @FXML
    private TableColumn<Categoria, String> colActiva;

    private ProductoService productoService;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colActiva.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActiva() ? "Activa" : "Inactiva"));

        cargarCategorias();
        actualizarMetricas();

        btnAgregarProducto.setOnAction(e -> abrirRegistroCategoria(null));
        btnEditarProducto.setOnAction(e -> editarCategoria());
        btnEliminarProducto.setOnAction(e -> eliminarCategoria());
    }

    private void cargarCategorias() {
        try {
            if (productoService == null) {
                productoService = new ProductoService();
            }

            List<Categoria> categorias = productoService.obtenerCategorias();
            tableProductos.getItems().setAll(categorias);
        } catch (Exception e) {
            MensajesAlerta.mostrarMensaje("ERROR", "Error al cargar categorias: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirRegistroCategoria(Categoria categoria) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/PanelRegistrarCategoria.fxml"));
            Parent root = loader.load();

            PanelRegistrarCategoriaController controller = loader.getController();
            if (controller != null && categoria != null) {
                controller.cargarCategoria(categoria);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(categoria == null ? "Registrar categoria" : "Actualizar categoria");
            stage.setScene(new Scene(root, 640, 520));
            stage.showAndWait();

            cargarCategorias();
            actualizarMetricas();
        } catch (Exception e) {
            MensajesAlerta.mostrarMensaje("ERROR", "No se pudo abrir el formulario de categoria: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editarCategoria() {
        Categoria seleccionada = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("AVISO", "Seleccione una categoria para editar.", Alert.AlertType.WARNING);
            return;
        }
        abrirRegistroCategoria(seleccionada);
    }

    private void eliminarCategoria() {
        Categoria seleccionada = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("AVISO", "Seleccione una categoria para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmar = mostrarConfirmacion("Confirmacion", "Desea eliminar la categoria seleccionada?", Alert.AlertType.CONFIRMATION);
        if (!confirmar) {
            return;
        }

        try {
            if (productoService == null) {
                productoService = new ProductoService();
            }

            productoService.eliminarCategoria(seleccionada.getId());
            mostrarMensaje("EXITO", "Categoria eliminada correctamente.", Alert.AlertType.INFORMATION);
            cargarCategorias();
            actualizarMetricas();
        } catch (Exception e) {
            mostrarMensaje("ERROR", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarMetricas() {
        try {
            if (productoService == null) {
                productoService = new ProductoService();
            }

            MetricasDTO metricas = productoService.calcularMetricas();
            lblProductosActivos.setText(String.valueOf(metricas.getTotalPlatillos()));
            lblPlatillosActivos.setText(String.valueOf(metricas.getPlatillosActivos()));
            lblCategoriasActivas.setText(String.valueOf(metricas.getCategoriasActivas()));
            lblBajoStock.setText(metricas.getBajoStock() > 0 ? String.valueOf(metricas.getBajoStock()) : "No hay");
        } catch (Exception e) {
            System.err.println("Error al actualizar metricas: " + e.getMessage());
        }
    }
}
