package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.modelo.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import com.sistema.puntoventas.modelo.moduloProducto.RankingProductosDTO;
import com.sistema.puntoventas.repository.IEstadisticasRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.impl.EstadisticasRepositoryImpl;
import com.sistema.puntoventas.repository.impl.ProductoRepositoryImpl;
import com.sistema.puntoventas.service.EstadisticaService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.application.Platform;

public class PanelPrincipalEstadisticasController implements Initializable {

    @FXML
    private Label lblIngresosTotales;

    @FXML
    private Label lblUtilidadNeta;

    @FXML
    private Label lblPerdidasTotales;

    @FXML
    private Label lblTicketPromedio;

    @FXML
    private TableView<RankingProductosDTO> tableRankingProductos;

    @FXML
    private TableColumn<RankingProductosDTO, String> colRankingProducto;

    @FXML
    private TableColumn<RankingProductosDTO, Integer> colRankingCantidad;

    @FXML
    private VBox vboxVentasUsuarios;

    @FXML
    private TableView<String> tableActividadReciente;

    @FXML
    private TableColumn<String, String> colActividadReciente;

    @FXML
    private TableView<PrediccionStockDTO> tablePrediccionStock;

    @FXML
    private TableColumn<PrediccionStockDTO, String> colNombre;

    @FXML
    private TableColumn<PrediccionStockDTO, Integer> colStockActual;

    @FXML
    private TableColumn<PrediccionStockDTO, Integer> colDiasRestantes;

    @FXML
    private TableColumn<PrediccionStockDTO, Double> colIndiceRiesgo;

    @FXML
    private TableColumn<PrediccionStockDTO, Integer> colSugerenciaCompra;

    private EstadisticaService estadisticaService;
    private Timeline actualizacionAutomatica;
    private static final int INTERVALO_ACTUALIZACION = 30; // segundos

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IEstadisticasRepository estadisticasRepository = new EstadisticasRepositoryImpl();
        IProductoRepository productoRepository = new ProductoRepositoryImpl();
        this.estadisticaService = new EstadisticaService(estadisticasRepository, productoRepository);

        // Configurar columnas de la tabla
        configurarColumnasTabla();

        // Cargar datos iniciales
        cargarReporteFinanciero();
        cargarPrediccionStock();
        cargarRankingProductos();
        cargarVentasUsuarios();
        cargarHistorialActividad();

        // Iniciar actualización automática en tiempo real
        iniciarActualizacionAutomatica();
    }

    private void iniciarActualizacionAutomatica() {
        // Crear Timeline que se ejecuta cada 30 segundos
        actualizacionAutomatica = new Timeline(new KeyFrame(Duration.seconds(INTERVALO_ACTUALIZACION), event -> {
            cargarReporteFinanciero();
            cargarPrediccionStock();
            cargarRankingProductos();
            cargarVentasUsuarios();
            cargarHistorialActividad();
        }));
        
        // El Timeline se repite indefinidamente
        actualizacionAutomatica.setCycleCount(Timeline.INDEFINITE);
        actualizacionAutomatica.play();
    }

    public void detenerActualizacion() {
        if (actualizacionAutomatica != null) {
            actualizacionAutomatica.stop();
        }
    }

    private void configurarColumnasTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colDiasRestantes.setCellValueFactory(new PropertyValueFactory<>("diasParaAgotarse"));
        colIndiceRiesgo.setCellValueFactory(new PropertyValueFactory<>("indiceRiesgo"));
        colIndiceRiesgo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    if (value >= 0.7) setText("ALTO");
                    else if (value >= 0.4) setText("MEDIO");
                    else setText("BAJO");
                }
            }
        });

        colSugerenciaCompra.setCellValueFactory(new PropertyValueFactory<>("cantidadSugerida"));
        colSugerenciaCompra.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                setText((empty || value == null) ? null : "Comprar " + value + " u.");
            }
        });

        if (colRankingProducto != null && colRankingCantidad != null) {
            colRankingProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
            colRankingCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
        }

        if (tableRankingProductos != null) {
            tableRankingProductos.setPlaceholder(new Label("No hay ventas registradas aún."));
        }

        if (tableActividadReciente != null && colActividadReciente != null) {
            colActividadReciente.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
            tableActividadReciente.setPlaceholder(new Label("No hay actividad reciente registrada."));
        }
    }

    private void cargarReporteFinanciero() {
        String periodo = LocalDate.now().toString().substring(0, 7);

        BalanceFinancieroDTO reporte = estadisticaService.obtenerBalance(periodo);

        if (lblIngresosTotales != null && lblUtilidadNeta != null && lblPerdidasTotales != null && lblTicketPromedio != null) {
            lblIngresosTotales.setText(String.format("$ %.1f", reporte.getIngresosTotales()));
            lblPerdidasTotales.setText(String.format("$ %.1f", reporte.getPerdidasTotales()));
            lblUtilidadNeta.setText(String.format("$ %.1f", reporte.getUtilidadNeta()));

            // Calcular ticket promedio: ingresos / cantidad de transacciones (estimado)
            double ticketPromedio = reporte.getIngresosTotales() > 0 ? reporte.getIngresosTotales() / 20.0 : 0;
            lblTicketPromedio.setText(String.format("$ %.2f", ticketPromedio));
        }
    }

    private void cargarPrediccionStock() {
        // Ejecutar la predicción en background para no bloquear el hilo de la UI
        Task<List<PrediccionStockDTO>> task = new Task<>() {
            @Override
            protected List<PrediccionStockDTO> call() throws Exception {
                return estadisticaService.ejecutarPrediccionStock();
            }
        };

        task.setOnSucceeded(evt -> {
            List<PrediccionStockDTO> predicciones = task.getValue();
            Platform.runLater(() -> {
                if (predicciones != null && !predicciones.isEmpty()) {
                    tablePrediccionStock.getItems().setAll(predicciones);
                } else {
                    tablePrediccionStock.getItems().clear();
                }
            });
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            System.err.println("Error al cargar predicción de stock: " + (ex != null ? ex.getMessage() : "unknown"));
        });

        Thread th = new Thread(task, "Prediccion-Worker");
        th.setDaemon(true);
        th.start();
    }

    private void cargarVentasUsuarios() {
        vboxVentasUsuarios.getChildren().clear();

        List<RankingVendedoresDTO> rankingVendedores = estadisticaService.obtenerRankingVendedores(5);

        if(rankingVendedores.isEmpty()){
            vboxVentasUsuarios.getChildren().add(new Label("No hay datos de ventas por usuario para mostrar."));
            return;
        }

        for (RankingVendedoresDTO vendedor : rankingVendedores){
            Label lbl = new Label(vendedor.getNombreVendedor() + " — " + vendedor.getCantidadVentas() + " ventas");
            vboxVentasUsuarios.getChildren().add(lbl);
        }

    }

    private void cargarRankingProductos() {
        List<RankingProductosDTO> topProductos = estadisticaService.obtenerRankingProductos(5);

        if (tableRankingProductos == null) {
            return;
        }

        if (topProductos.isEmpty()) {
            tableRankingProductos.getItems().clear();
            return;
        }

        tableRankingProductos.getItems().setAll(topProductos);
    }

    private void cargarHistorialActividad() {
        List<String> actividades = estadisticaService.obtenerUltimasActividades(20);

        if (tableActividadReciente == null) {
            return;
        }

        if (actividades == null || actividades.isEmpty()) {
            tableActividadReciente.getItems().clear();
            return;
        }

        tableActividadReciente.setItems(FXCollections.observableArrayList(actividades));
    }
}
