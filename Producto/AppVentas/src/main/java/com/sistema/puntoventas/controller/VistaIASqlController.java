package com.sistema.puntoventas.controller;

import com.sistema.puntoventas.repository.IConsultaSQLRepository;
import com.sistema.puntoventas.repository.ILLMRepository;
import com.sistema.puntoventas.repository.impl.ConsultaSQLRepositoryImpl;
import com.sistema.puntoventas.repository.impl.LLMRepositoryImpl;
import com.sistema.puntoventas.modelo.ResultadoConsulta;
import com.sistema.puntoventas.service.IASqlService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class VistaIASqlController implements Initializable {

    @FXML
    private TextField txtPrompt;

    @FXML
    private ChoiceBox<String> cmbModo;

    @FXML
    private Button btnEjecutar;

    @FXML
    private Button btnConfigKey;

    @FXML
    private Label lblEstadoApi;

    @FXML
    private Label lblSqlGenerado;

    @FXML
    private TableView<ObservableList<String>> tableResultados;

    private IASqlService iaSqlService;
    private ILLMRepository llmRepository;
    private IConsultaSQLRepository consultaRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.llmRepository = new LLMRepositoryImpl();
        this.consultaRepository = new ConsultaSQLRepositoryImpl();
        this.iaSqlService = new IASqlService(llmRepository, consultaRepository);

        cmbModo.getItems().addAll("Gemini", "Local (Qwen2.5-3B)");
        cmbModo.setValue("Local (Qwen2.5-3B)");
        cmbModo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoModo());

        actualizarEstadoModo();
        limpiarSqlLabel();

        tableResultados.setPlaceholder(new Label("Escribe una consulta y presiona \"Consultar\" para ver los resultados."));

        txtPrompt.setOnAction(e -> ejecutarConsulta());
        btnEjecutar.setOnAction(e -> ejecutarConsulta());
        btnConfigKey.setOnAction(e -> configurarApiKey());
    }

    private void actualizarEstadoModo() {
        boolean isGemini = "Gemini".equals(cmbModo.getValue());
        btnConfigKey.setVisible(isGemini);
        if (isGemini) {
            String key = llmRepository.obtenerApiKey();
            if (key != null && !key.isBlank()) {
                lblEstadoApi.setText("Gemini - API Key configurada");
                lblEstadoApi.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46;");
            } else {
                lblEstadoApi.setText("Gemini - API Key no configurada");
                lblEstadoApi.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;");
            }
        } else {
            lblEstadoApi.setText("Modo Local - Qwen2.5-3B (CPU)");
            lblEstadoApi.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af;");
        }
    }

    private void limpiarSqlLabel() {
        lblSqlGenerado.setVisible(false);
        lblSqlGenerado.setManaged(false);
        lblSqlGenerado.setText("");
    }

    private void mostrarSqlLabel(String sql) {
        lblSqlGenerado.setText("SQL generado: " + sql);
        lblSqlGenerado.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-font-family: monospace;");
        lblSqlGenerado.setVisible(true);
        lblSqlGenerado.setManaged(true);
    }

    private void configurarApiKey() {
        String keyActual = llmRepository.obtenerApiKey();
        TextInputDialog dialog = new TextInputDialog(keyActual != null ? keyActual : "");
        dialog.setTitle("Configurar API Key");
        dialog.setHeaderText("API Key de Gemini");
        dialog.setContentText("Ingresa tu API Key de Google Gemini:");

        ButtonType btnEliminar = new ButtonType("Eliminar");
        dialog.getDialogPane().getButtonTypes().add(btnEliminar);

        dialog.setResultConverter(btn -> {
            if (btn == btnEliminar) {
                return "";
            }
            return dialog.getEditor().getText();
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String valor = result.get().trim();
            if (valor.isEmpty()) {
                llmRepository.eliminarApiKey();
            } else {
                llmRepository.guardarApiKey(valor);
            }
            actualizarEstadoModo();
        }
    }

    @FXML
    private void ejecutarConsulta() {
        String consulta = txtPrompt.getText().trim();
        if (consulta.isEmpty()) {
            return;
        }

        boolean isGemini = "Gemini".equals(cmbModo.getValue());

        if (isGemini) {
            String key = llmRepository.obtenerApiKey();
            if (key == null || key.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("API Key requerida");
                alert.setHeaderText(null);
                alert.setContentText("Debes configurar una API Key de Gemini antes de consultar. Haz clic en ⚙️.");
                alert.showAndWait();
                return;
            }
        }

        Platform.runLater(() -> {
            btnEjecutar.setDisable(true);
            btnEjecutar.setText("Consultando...");
        });

        String modo = isGemini ? "gemini" : "local";

        javafx.concurrent.Task<ResultadoConsulta> task = new javafx.concurrent.Task<>() {
            @Override
            protected ResultadoConsulta call() throws Exception {
                return iaSqlService.ejecutarConsultaNatural(consulta, modo);
            }
        };

        task.setOnSucceeded(evt -> {
            ResultadoConsulta resultado = task.getValue();
            if (resultado == null || resultado.getColumnas().isEmpty()) {
                tableResultados.getColumns().clear();
                tableResultados.getItems().clear();
                tableResultados.setPlaceholder(new Label("No se encontraron resultados para tu consulta."));
            } else {
                generarColumnasDinamicas(resultado.getColumnas());
                cargarFilas(resultado.getColumnas().size(), resultado.getFilas());
                autoAjustarColumnas(tableResultados);
            }
            String sqlGen = resultado.getSqlGenerado();
            if (sqlGen != null && !sqlGen.isBlank()) {
                mostrarSqlLabel(sqlGen);
            } else {
                limpiarSqlLabel();
            }
            btnEjecutar.setDisable(false);
            btnEjecutar.setText("Consultar");
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            String msg = (ex != null ? ex.getMessage() : "Error desconocido");
            System.err.println("Error al ejecutar consulta: " + msg);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();

            tableResultados.getColumns().clear();
            tableResultados.getItems().clear();
            tableResultados.setPlaceholder(new Label("Error al ejecutar la consulta."));
            limpiarSqlLabel();
            btnEjecutar.setDisable(false);
            btnEjecutar.setText("Consultar");
        });

        Thread th = new Thread(task, "IASql-Worker");
        th.setDaemon(true);
        th.start();
    }

    private void generarColumnasDinamicas(List<String> nombresColumnas) {
        tableResultados.getColumns().clear();

        for (int i = 0; i < nombresColumnas.size(); i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(nombresColumnas.get(i));
            col.setCellValueFactory(data -> {
                if (colIndex < data.getValue().size()) {
                    return new ReadOnlyStringWrapper(data.getValue().get(colIndex));
                }
                return new ReadOnlyStringWrapper("");
            });
            col.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(String value, boolean empty) {
                    super.updateItem(value, empty);
                    setText((empty || value == null) ? null : value);
                }
            });
            tableResultados.getColumns().add(col);
        }
    }

    private void cargarFilas(int numColumnas, List<List<String>> filas) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (List<String> fila : filas) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < numColumnas; i++) {
                row.add(i < fila.size() ? fila.get(i) : "");
            }
            data.add(row);
        }
        tableResultados.setItems(data);
    }

    private void autoAjustarColumnas(TableView<?> table) {
        for (TableColumn<?, ?> col : table.getColumns()) {
            col.setPrefWidth(calcularAnchoOptimo(col));
        }
    }

    private double calcularAnchoOptimo(TableColumn<?, ?> col) {
        double ancho = 0;

        Text text = new Text(col.getText());
        text.setFont(Font.font("System", FontWeight.BOLD, 13));
        ancho = text.getLayoutBounds().getWidth() + 25;

        for (int i = 0; i < Math.min(col.getTableView().getItems().size(), 100); i++) {
            Object cellData = col.getCellData(i);
            if (cellData != null) {
                text = new Text(cellData.toString());
                text.setFont(Font.font("System", 13));
                double w = text.getLayoutBounds().getWidth() + 25;
                if (w > ancho) {
                    ancho = w;
                }
            }
        }

        return Math.min(ancho, 400);
    }
}
