package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.modelo.Role;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.service.UsuarioService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PanelPrincipalUsuariosController implements Initializable {

    // --- ELEMENTOS GRÁFICOS ---
    @FXML private Pane CardUsuariosActivos;
    @FXML private Pane CardUsuariosAdmin;
    @FXML private Pane CardUsuariosVendedor;

    @FXML private Button btnAgregarUsuario;
    @FXML private Button btnEditarUsuario;
    @FXML private Button btnEliminarUsuario;

    @FXML private Label lblUsuariosActivos;
    @FXML private Label lblUsuariosAdmin;
    @FXML private Label lblUsuariosVendedor;

    // --- TABLA Y COLUMNAS ---
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colRut;
    @FXML private TableColumn<Usuario, String> colContraseña;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, Role> colRol;
    @FXML private TableColumn<Usuario, Boolean> colEstado;

    // --- VARIABLES DE LÓGICA ---
    private UsuarioService usuarioService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Inicializar el Servicio
        usuarioService = new UsuarioService();

        // 2. CONFIGURACIÓN DE COLUMNAS

        // === CONFIGURACIÓN DE ID (Numeración automática 1, 2, 3...) ===
        colId.setCellFactory(columna -> new TableCell<Usuario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean vacio) {
                super.updateItem(item, vacio);
                if (vacio) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colRut.setCellValueFactory(new PropertyValueFactory<>("rut"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // === CONFIGURACIÓN DE CONTRASEÑA (OCULTA CON ASTERISCOS) ===
        colContraseña.setCellValueFactory(new PropertyValueFactory<>("contraseña"));
        colContraseña.setCellFactory(columna -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String contraseñaReal, boolean vacio) {
                super.updateItem(contraseñaReal, vacio);
                if (vacio || contraseñaReal == null) {
                    setText(null);
                } else {
                    setText("*".repeat(contraseñaReal.length()));
                }
            }
        });

        // === CONFIGURACIÓN DE ESTADO (Boolean -> Texto) ===
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(columna -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean estado, boolean vacio) {
                super.updateItem(estado, vacio);
                if (vacio || estado == null) {
                    setText(null);
                } else {
                    setText(estado ? "Activo" : "Inactivo");
                }
            }
        });

        // 3. Cargar la Tabla y los KPIs
        cargarUsuarios();

        // 4. Asignar acciones a los botones
        btnEliminarUsuario.setOnAction(this::eliminarUsuarioSeleccionado);
        btnAgregarUsuario.setOnAction(event -> abrirVentanaUsuario(null));
        btnEditarUsuario.setOnAction(event -> {
            Usuario usuarioSeleccionado = tableUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                mostrarAlerta("Atención", "Ningún usuario seleccionado", "Selecciona un usuario de la tabla para editar.", Alert.AlertType.WARNING);
            } else {
                abrirVentanaUsuario(usuarioSeleccionado);
            }
        });

        // ==============================================================================
        // CONTROL DE ACCESOS: Restricciones automáticas para el rol VENDEDOR
        // ==============================================================================
        if (LoginController.usuarioLogueado != null &&
                LoginController.usuarioLogueado.getRol() == Role.VENDEDOR) {

            // Oculta las acciones de modificación de datos y libera espacio en el diseño
            btnAgregarUsuario.setVisible(false);
            btnAgregarUsuario.setManaged(false);

            btnEditarUsuario.setVisible(false);
            btnEditarUsuario.setManaged(false);

            btnEliminarUsuario.setVisible(false);
            btnEliminarUsuario.setManaged(false);

            // Oculta la tarjeta informativa de cantidad de Administradores
            if (CardUsuariosAdmin != null) {
                CardUsuariosAdmin.setVisible(false);
                CardUsuariosAdmin.setManaged(false);
            }
        }
    }

    // --- MÉTODOS DE VENTANAS ---

    private void abrirVentanaUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/PanelAgregarUsuario.fxml"));
            Parent root = loader.load();

            PanelAgregarUsuarioController controlador = loader.getController();

            if (usuario != null) {
                controlador.cargarDatosUsuario(usuario);
            }

            Stage stage = new Stage();
            stage.setTitle(usuario == null ? "Agregar Nuevo Usuario" : "Editar Usuario");
            stage.setScene(new Scene(root, 1200, 768));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refrescar tabla al volver
            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- MÉTODOS DE DATOS ---

    private void cargarUsuarios() {
        List<Usuario> listaUsuarios = usuarioService.obtenerUsuarios();
        ObservableList<Usuario> datosTabla = FXCollections.observableArrayList(listaUsuarios);
        tableUsuarios.setItems(datosTabla);

        // Actualizar KPIs
        int conteoActivos = 0;
        int conteoAdmins = 0;
        int conteoVendedores = 0;

        for (Usuario user : listaUsuarios) {
            if (user.getEstado() != null && user.getEstado()) conteoActivos++;
            if (user.getRol() != null) {
                if (user.getRol().name().equalsIgnoreCase("ADMIN")) conteoAdmins++;
                if (user.getRol().name().equalsIgnoreCase("VENDEDOR")) conteoVendedores++;
            }
        }

        lblUsuariosActivos.setText(String.valueOf(conteoActivos));
        lblUsuariosAdmin.setText(String.valueOf(conteoAdmins));
        lblUsuariosVendedor.setText(String.valueOf(conteoVendedores));
    }

    private void eliminarUsuarioSeleccionado(ActionEvent event) {
        Usuario usuarioSeleccionado = tableUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            mostrarAlerta("Atención", "Ningún usuario seleccionado", "Por favor, selecciona un usuario.", Alert.AlertType.WARNING);
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar");
        alertaConfirmacion.setHeaderText("Eliminar Usuario");
        alertaConfirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + usuarioSeleccionado.getNombre() + "?");

        alertaConfirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Usuario eliminado = usuarioService.eliminarUsuario(usuarioSeleccionado.getRut());
                if (eliminado != null) {
                    mostrarAlerta("Éxito", "Usuario eliminado", "El usuario fue borrado correctamente.", Alert.AlertType.INFORMATION);
                    cargarUsuarios();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar", "El usuario tiene ventas asociadas y no puede ser eliminado.", Alert.AlertType.WARNING);
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}