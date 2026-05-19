package com.sistema.puntoventas.controller.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.util.MensajesAlerta;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PanelRegistrarCategoriaController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private CheckBox chkActiva;
    @FXML
    private Label lblEstado;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnCancelar;

    private ProductoService productoService;
    private Categoria categoriaEnEdicion;

    public void initialize() {
        if (chkActiva != null) {
            chkActiva.setSelected(true);
        }
    }

    public void cargarCategoria(Categoria categoria) {
        if (categoria == null) {
            return;
        }
        this.categoriaEnEdicion = categoria;
        if (txtNombre != null) {
            txtNombre.setText(categoria.getNombreCategoria());
        }
        if (txtDescripcion != null) {
            txtDescripcion.setText(categoria.getDescripcion());
        }
        if (chkActiva != null) {
            chkActiva.setSelected(categoria.isActiva());
        }
        if (btnRegistrar != null) {
            btnRegistrar.setText("Actualizar");
        }
        if (lblEstado != null) {
            lblEstado.setText("Editando categoria");
        }
    }

    @FXML
    private void registrarCategoria() {
        try {
            if (productoService == null) {
                productoService = new ProductoService();
            }

            String nombre = txtNombre != null ? txtNombre.getText().trim() : "";
            String descripcion = txtDescripcion != null ? txtDescripcion.getText().trim() : "";

            if (nombre.isEmpty()) {
                MensajesAlerta.mostrarMensaje("AVISO", "El nombre es obligatorio.", Alert.AlertType.WARNING);
                return;
            }

            if (categoriaEnEdicion != null) {
                categoriaEnEdicion.setNombreCategoria(nombre);
                categoriaEnEdicion.setDescripcion(descripcion);
                categoriaEnEdicion.setActiva(chkActiva != null && chkActiva.isSelected());

                productoService.actualizarCategoria(categoriaEnEdicion);
                lblEstado.setText("Categoria actualizada correctamente");
                return;
            }

            Categoria categoria = new Categoria();
            categoria.setNombreCategoria(nombre);
            categoria.setDescripcion(descripcion);
            categoria.setActiva(chkActiva != null && chkActiva.isSelected());

            productoService.registrarCategoria(categoria);
            lblEstado.setText("Categoria registrada correctamente");
        } catch (Exception e) {
            MensajesAlerta.mostrarMensaje("ERROR", "No se pudo guardar la categoria: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        if (btnCancelar != null) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        }
    }
}
