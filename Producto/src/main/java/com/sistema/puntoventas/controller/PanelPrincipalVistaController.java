package com.sistema.puntoventas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelPrincipalVistaController {

    @FXML private Button btnDashboard; // <--- NUEVO
    @FXML private Button btnUsuarios;
    @FXML private Button btnProductos;
    @FXML private Button btnVentas;
    @FXML private Button btnInventario;
    @FXML private Button btnPlatillos;
    @FXML private Button btnEstadisticas;
    @FXML private Button btnCategorias;

    @FXML private StackPane contentArea;

        // Variable para no perder el dashboard original al cambiar de pantallas
        private Node vistaDashboardInicial;

        // Cache para almacenar las vistas ya cargadas y preservar su estado (tablas, textos, etc.)
        private final Map<String, Node> vistasCache = new HashMap<>();

        // 2. MÉTODO DE INICIALIZACIÓN
        @FXML
        public void initialize() {
            cargarVistaModulo("DashboardVista.fxml", null);

        // Acción para el nuevo botón Inicio
        btnDashboard.setOnAction(e -> cargarVistaModulo("DashboardVista.fxml", btnDashboard));

        btnUsuarios.setOnAction(e -> cargarVistaModulo("PanelPrincipalUsuarios.fxml", btnUsuarios));
        btnProductos.setOnAction(e -> cargarVistaModulo("PanelPrincipalProductos.fxml", btnProductos));
        btnVentas.setOnAction(e -> cargarVistaModulo("panelVentas.fxml", btnVentas));
        btnInventario.setOnAction(e -> cargarVistaModulo("PanelInventario.fxml", btnInventario));
        btnPlatillos.setOnAction(e -> cargarVistaModulo("PanelPrincipalPlatillosVista.fxml", btnPlatillos));
        btnEstadisticas.setOnAction(e -> cargarVistaModulo("PanelPrincipalEstadisticasVista.fxml", btnEstadisticas));
        btnCategorias.setOnAction(e -> cargarVistaModulo("PanelPrincipalCategorias.fxml", btnCategorias));
    }

        // 3. SISTEMA DE NAVEGACIÓN DINÁMICA
        private void cargarVistaModulo(String archivoFxml, Button botonActivo) {
            try {
                Node vista;

            // Solo aplicamos persistencia (caché) si el archivo es 'panelVentas.fxml'
            // Esto permite que el VentaController y su vista sigan "activos" en memoria
            if (archivoFxml.equals("panelVentas.fxml") && vistasCache.containsKey(archivoFxml)) {
                    vista = vistasCache.get(archivoFxml);
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/" + archivoFxml));
                    vista = loader.load();
                
                if (archivoFxml.equals("panelVentas.fxml")) {
                    vistasCache.put(archivoFxml, vista);
                }
                }

                // Reemplazar el contenido del área central por la vista (nueva o recuperada)
                contentArea.getChildren().setAll(vista);

            // Actualizar estilo de los botones para que el seleccionado se vea azul
            actualizarEstiloBotones(botonActivo);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + archivoFxml);
            e.printStackTrace();
        }
    }

    private void actualizarEstiloBotones(Button botonActivo) {
        // Agregamos btnDashboard a la lista para que también se limpie su estilo
        List<Button> botones = Arrays.asList(
                btnDashboard, btnUsuarios, btnProductos, btnVentas,
                btnInventario, btnPlatillos, btnEstadisticas, btnCategorias
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
        System.out.println("Clic detectado en una tarjeta del Dashboard!");
    }
}