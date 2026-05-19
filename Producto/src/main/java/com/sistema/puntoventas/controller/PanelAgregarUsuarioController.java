package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.modelo.Role;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.service.UsuarioService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    // --- NUEVAS VARIABLES PARA MODO EDICIÓN ---
    private boolean modoEdicion = false;
    private Usuario usuarioAEditar = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuarioService = new UsuarioService();
        cmbRol.setItems(FXCollections.observableArrayList(Role.values()));
        lblEstado.setText("Listo para registrar");
    }

    // ==============================================================================
    // NUEVO MÉTODO: Esto lo llamará tu PanelPrincipal cuando presiones "Editar"
    // ==============================================================================
    public void cargarDatosUsuario(Usuario usuario) {
        this.modoEdicion = true;
        this.usuarioAEditar = usuario;

        // Rellenamos los campos con los datos del usuario seleccionado
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtRut.setText(usuario.getRut());
        txtContraseña.setText(usuario.getContraseña());
        txtTelefono.setText(usuario.getTelefono());
        cmbRol.setValue(usuario.getRol());

        // Bloqueamos el RUT para que no puedan cambiar su identificador
        txtRut.setEditable(false);
        txtRut.setStyle("-fx-background-color: #e0e0e0;"); // Darle color gris visualmente

        // Cambiamos los textos de la interfaz
        btnRegistrar.setText("Guardar Cambios");
        lblEstado.setText("Editando usuario: " + usuario.getRut());
    }

    @FXML
    void registrarUsuario(ActionEvent event) {
        // 1. Validar campos vacíos
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty() ||
                txtRut.getText().isEmpty() || txtContraseña.getText().isEmpty() ||
                cmbRol.getValue() == null) {

            mostrarAlerta("Campos incompletos", "Faltan datos", "Por favor, completa todos los campos obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (modoEdicion) {
                // ==========================================
                // LÓGICA DE ACTUALIZAR (MODO EDICIÓN)
                // ==========================================
                usuarioAEditar.setNombre(txtNombre.getText().trim());
                usuarioAEditar.setApellido(txtApellido.getText().trim());
                usuarioAEditar.setContraseña(txtContraseña.getText().trim());
                usuarioAEditar.setTelefono(txtTelefono.getText().trim());
                usuarioAEditar.setRol(cmbRol.getValue());

                // NOTA IMPORTANTE: Necesitarás crear este método en tu UsuarioService y Repository si aún no lo tienes.
                // String mensaje = usuarioService.actualizarUsuario(usuarioAEditar);

                // --- Simulación temporal mientras creas el método ---

                String mensaje = usuarioService.actualizarUsuario(usuarioAEditar);
                if (mensaje.equals("Usuario actualizado exitosamente")) {
                    mostrarAlerta("Éxito", "Actualización Completada", mensaje, Alert.AlertType.INFORMATION);
                    // Opcional: Cerrar la ventana al terminar de editar
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar", mensaje, Alert.AlertType.ERROR);
                }

            } else {
                // ==========================================
                // LÓGICA DE REGISTRAR (MODO CREACIÓN)
                // ==========================================
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setNombre(txtNombre.getText().trim());
                nuevoUsuario.setApellido(txtApellido.getText().trim());
                nuevoUsuario.setRut(txtRut.getText().trim());
                nuevoUsuario.setContraseña(txtContraseña.getText().trim());
                nuevoUsuario.setTelefono(txtTelefono.getText().trim());
                nuevoUsuario.setRol(cmbRol.getValue());
                nuevoUsuario.setEstado(true);

                String mensajeRespuesta = usuarioService.registrarNuevoUsuario(nuevoUsuario);

                if (mensajeRespuesta.equals("Usuario registrado exitosamente")) {
                    mostrarAlerta("Éxito", "Registro Completado", mensajeRespuesta, Alert.AlertType.INFORMATION);
                    limpiarCampos();
                } else {
                    mostrarAlerta("Error en el registro", "No se pudo registrar", mensajeRespuesta, Alert.AlertType.ERROR);
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