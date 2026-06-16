package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.modelo.Role;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.service.UsuarioService;
import com.sistema.puntoventas.util.AlertaCamposVacios;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelAgregarUsuarioController implements Initializable {

    @FXML private Button btnRegistrar;
    @FXML private ComboBox<Role> cmbRol;
    @FXML private Label lblEstado;
    @FXML private TextField txtApellido;
    @FXML private TextField txtContraseña;
    @FXML private TextField txtNombre;
    @FXML private TextField txtRut;
    @FXML private TextField txtTelefono;

    private UsuarioService usuarioService;
    private boolean modoEdicion = false;
    private Usuario usuarioAEditar = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // El helper resaltarSiVacio acepta TextField... por eso no podemos pasar un ComboBox aquí.
        // Cambio mínimo: solo pasar los TextField. Si se desea resaltar el ComboBox, añadir
        // una sobrecarga en AlertaCamposVacios sería la opción, pero evitamos tocar otras clases.
        AlertaCamposVacios.resaltarSiVacio(txtNombre, txtApellido, txtRut, txtContraseña);
        usuarioService = new UsuarioService();
        cmbRol.setItems(FXCollections.observableArrayList(Role.values()));
        lblEstado.setText("Listo para registrar");

        // ==============================================================================
        // RESTRICCIÓN EN TIEMPO REAL: Limitar caracteres máximos en los campos
        // ==============================================================================

        // El RUT Chileno formateado completo (con puntos y guion) tiene máximo 12 caracteres (ej: 12.345.678-9)
        txtRut.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 12) {
                return null; // Rechaza el cambio si excede los 12 caracteres
            }
            return change;
        }));

        // El número de teléfono celular en Chile consta estrictamente de 9 dígitos (ej: 912345678)
        txtTelefono.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // Permite solo números y restringe a un máximo de 9 caracteres
            if (newText.matches("\\d*") && newText.length() <= 9) {
                return change;
            }
            return null; // Rechaza letras o exceder los 9 dígitos
        }));
    }

    public void setUsuarioAEditar(Usuario usuario) {
        if (usuario != null) {
            this.modoEdicion = true;
            this.usuarioAEditar = usuario;

            txtNombre.setText(usuario.getNombre());
            txtApellido.setText(usuario.getApellido());
            txtRut.setText(usuario.getRut());
            txtRut.setDisable(true); // El RUT no se edita por ser la Clave Primaria de búsqueda
            txtContraseña.setText(usuario.getContraseña());
            txtTelefono.setText(usuario.getTelefono());
            cmbRol.setValue(usuario.getRol());

            btnRegistrar.setText("Actualizar");
            lblEstado.setText("Modo Edición Activo");
        }
    }

    // Método adaptador mínimo para que otras clases (FXML loaders) puedan
    // invocar la carga de datos con el nombre esperado `cargarDatosUsuario`.
    // Internamente reutiliza `setUsuarioAEditar` para aplicar el mismo comportamiento.
    public void cargarDatosUsuario(Usuario usuario) {
        setUsuarioAEditar(usuario);
    }

    @FXML
    void registrarUsuario(ActionEvent event) {
        try {
            String mensajeRespuesta;

            if (modoEdicion) {
                usuarioAEditar.setNombre(txtNombre.getText());
                usuarioAEditar.setApellido(txtApellido.getText());
                usuarioAEditar.setContraseña(txtContraseña.getText());
                usuarioAEditar.setTelefono(txtTelefono.getText());
                usuarioAEditar.setRol(cmbRol.getValue());

                mensajeRespuesta = usuarioService.actualizarUsuario(usuarioAEditar);

                if (mensajeRespuesta.equals("Usuario actualizado exitosamente")) {
                    mostrarAlerta("Éxito", "Actualización Completada", mensajeRespuesta, Alert.AlertType.INFORMATION);
                    limpiarCampos();
                    modoEdicion = false;
                    usuarioAEditar = null;
                    btnRegistrar.setText("Registrar");
                    txtRut.setDisable(false);
                } else {
                    mostrarAlerta("Atención", "Error al actualizar", mensajeRespuesta, Alert.AlertType.WARNING);
                }

            } else {
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setNombre(txtNombre.getText());
                nuevoUsuario.setApellido(txtApellido.getText());
                nuevoUsuario.setRut(txtRut.getText());
                nuevoUsuario.setContraseña(txtContraseña.getText());
                nuevoUsuario.setTelefono(txtTelefono.getText());
                nuevoUsuario.setRol(cmbRol.getValue());
                nuevoUsuario.setEstado(true);

                mensajeRespuesta = usuarioService.registrarNuevoUsuario(nuevoUsuario);

                if (mensajeRespuesta.equals("Usuario registrado exitosamente")) {
                    mostrarAlerta("Éxito", "Registro Completado", mensajeRespuesta, Alert.AlertType.INFORMATION);
                    limpiarCampos();
                } else {
                    mostrarAlerta("Atención", "Error de validación", mensajeRespuesta, Alert.AlertType.WARNING);
                }
            }

        } catch (Exception e) {
            mostrarAlerta("Error fatal", "Excepción inesperada", "Ha ocurrido un error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtRut.clear();
        txtContraseña.clear();
        txtTelefono.clear();
        cmbRol.getSelectionModel().clearSelection();
        lblEstado.setText("Usuario registrado correctamente");
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}