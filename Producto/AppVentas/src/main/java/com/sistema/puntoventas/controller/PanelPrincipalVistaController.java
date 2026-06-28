package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.service.SesionService;
import com.sistema.puntoventas.util.MensajesAlerta; // Importación de tu utilidad de alertas
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelPrincipalVistaController {

    @FXML private Button btnDashboard;
    @FXML private Button btnUsuarios;
    @FXML private Button btnProductos;
    @FXML private Button btnVentas;
    @FXML private Button btnInventario;
    @FXML private Button btnPlatillos;
    @FXML private Button btnEstadisticas;
    @FXML private Button btnCategorias;
    @FXML private Button btnIASql;

    // ==============================================================================
    // BOTÓN DE CERRAR SESIÓN
    // ==============================================================================
    @FXML private Button btnCerrarSesion;

    @FXML private StackPane contentArea;

    // Cache para almacenar las vistas ya cargadas y preservar su estado (tablas, textos, etc.)
    private final Map<String, Node> vistasCache = new HashMap<>();

    @FXML
    public void initialize() {
        // Cargar el Dashboard por defecto al iniciar
        cargarVistaModulo("DashboardVista.fxml", null);

        // Configuración de las acciones comunes (permitidas para todos)
        if (btnDashboard != null) {
            btnDashboard.setOnAction(e -> cargarVistaModulo("DashboardVista.fxml", btnDashboard));
        }
        if (btnProductos != null) {
            btnProductos.setOnAction(e -> cargarVistaModulo("PanelPrincipalProductos.fxml", btnProductos));
        }
        if (btnVentas != null) {
            btnVentas.setOnAction(e -> cargarVistaModulo("panelVentas.fxml", btnVentas));
        }
        if (btnPlatillos != null) {
            btnPlatillos.setOnAction(e -> cargarVistaModulo("PanelPrincipalPlatillosVista.fxml", btnPlatillos));
        }
        if (btnCategorias != null) {
            btnCategorias.setOnAction(e -> cargarVistaModulo("PanelPrincipalCategorias.fxml", btnCategorias));
        }
        if (btnIASql != null) {
            btnIASql.setOnAction(e -> cargarVistaModulo("vistaIASql.fxml", btnIASql));
        }

        // ==============================================================================
        // RESTRICCIÓN DE ACCESOS POR ROL: ADMINISTRADOR vs VENDEDOR
        // ==============================================================================
        Usuario usuarioActivo = LoginController.usuarioLogueado;

        if (usuarioActivo != null && "VENDEDOR".equalsIgnoreCase(usuarioActivo.getRol().name())) {

            // 1. Bloqueamos los botones requeridos para el rol Vendedor
            if (btnUsuarios != null) btnUsuarios.setDisable(true);
            if (btnEstadisticas != null) btnEstadisticas.setDisable(true);
            if (btnInventario != null) btnInventario.setDisable(true);
            if (btnIASql != null) btnIASql.setDisable(true);

            System.out.println("Seguridad: Se han bloqueado los accesos de Usuarios, Estadísticas e Inventario para el Vendedor.");

        } else {
            // 2. Si es Administrador (o cualquier otro rol), habilitamos los botones y asignamos sus funciones
            if (btnUsuarios != null) {
                btnUsuarios.setDisable(false);
                btnUsuarios.setOnAction(e -> cargarVistaModulo("PanelPrincipalUsuarios.fxml", btnUsuarios));
            }
            if (btnEstadisticas != null) {
                btnEstadisticas.setDisable(false);
                btnEstadisticas.setOnAction(e -> cargarVistaModulo("PanelPrincipalEstadisticasVista.fxml", btnEstadisticas));
            }
            if (btnInventario != null) {
                btnInventario.setDisable(false);
                btnInventario.setOnAction(e -> cargarVistaModulo("PanelInventario.fxml", btnInventario));
            }

            System.out.println("Seguridad: Acceso total concedido al Administrador.");
        }

        // Asignar acción al botón cerrar sesión (Siempre activo para todos)
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnAction(e -> cerrarSesion());
        }
    }

    /**
     * Lógica de Cierre de Sesión con Ventana de Confirmación
     */
    private void cerrarSesion() {
        // 1. Desplegamos la alerta de confirmación usando tu clase utilitaria MensajesAlerta
        boolean confirmar = MensajesAlerta.mostrarConfirmacion(
                "Cerrar Sesión",
                "¿Estás seguro de que deseas salir del sistema?",
                Alert.AlertType.CONFIRMATION
        );

        // 2. Si el usuario presiona "OK", procedemos con la desconexión segura
        if (confirmar) {
            LoginController.usuarioLogueado = null;
            new SesionService().cerrarSesionPersistente();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/LoginVista.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stageActual = (Stage) contentArea.getScene().getWindow();
                stageActual.setTitle("Eluney");
                stageActual.setWidth(1920);
                stageActual.setHeight(1080);
                stageActual.setScene(scene);
                stageActual.setMaximized(true);

                stageActual.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
                    if (!isMaximized) {
                        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
                        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
                        double x = (screenWidth - stageActual.getWidth()) / 2;
                        double y = (screenHeight - stageActual.getHeight()) / 2;
                        if (y < 0) y = 20;
                        if (x < 0) x = 20;
                        stageActual.setX(x);
                        stageActual.setY(y);
                    }
                });

                stageActual.show();
            } catch (IOException e) {
                System.err.println("ERROR: No se pudo cargar LoginVista.fxml al cerrar sesión.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Cierre de sesión cancelado de forma voluntaria.");
        }
    }

    private void cargarVistaModulo(String archivoFxml, Button botonActivo) {
        try {
            Node vista;

            if (archivoFxml.equals("DashboardVista.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/" + archivoFxml));
                vista = loader.load();
            } else if (archivoFxml.equals("panelVentas.fxml") && vistasCache.containsKey(archivoFxml)) {
                vista = vistasCache.get(archivoFxml);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/" + archivoFxml));
                vista = loader.load();

                if (archivoFxml.equals("panelVentas.fxml")) {
                    vistasCache.put(archivoFxml, vista);
                }
            }

            contentArea.getChildren().setAll(vista);
            actualizarEstiloBotones(botonActivo);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista interna: " + archivoFxml);
            e.printStackTrace();
        }
    }

    private void actualizarEstiloBotones(Button botonActivo) {
        List<Button> botones = Arrays.asList(
                btnDashboard, btnUsuarios, btnProductos, btnVentas,
                btnInventario, btnPlatillos, btnEstadisticas, btnCategorias,
                btnIASql, btnCerrarSesion
        );

        for (Button btn : botones) {
            if (btn != null) {
                btn.getStyleClass().remove("menu-button-active");
            }
        }

        if (botonActivo != null) {
            botonActivo.getStyleClass().add("menu-button-active");
        }
    }

    @FXML
    public void handleNavegacion(MouseEvent event) {
        // Reservado para clicks auxiliares sobre componentes del Dashboard central
    }
}