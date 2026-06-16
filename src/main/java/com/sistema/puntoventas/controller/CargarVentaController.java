package com.sistema.puntoventas.controller;

import java.util.List;

import com.sistema.puntoventas.service.VentaService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class CargarVentaController {
    VentaService ventaService = new VentaService();

    @FXML
     
    private TableView<String> idTableViewFechas;

   
    @FXML
    private TableColumn<String, String> columnFecha;

    private String fechaSeleccionada;

    @FXML
    Boolean cargarFechas(ActionEvent event) { //metodo que se ejecuta al apretar el boton cargar
        fechaSeleccionada = idTableViewFechas.getSelectionModel().getSelectedItem();
        
        if (fechaSeleccionada != null) {
            // Cerramos la ventana (Stage) del diálogo actual para volver a la principal
            Stage stage = (Stage) idTableViewFechas.getScene().getWindow();
            stage.close();
            return true;
        } else {
            return false;
        }
    }

    public String getFechaSeleccionada() {
        return fechaSeleccionada;
    }

    private void cargarFechasTableView() {
        List<String> fechas = ventaService.obtenerFechasTableView();
        //ordenar de menor a mayor
        fechas.sort((a, b) -> b.compareTo(a));
            
        columnFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue())
        );

        idTableViewFechas.setItems(FXCollections.observableArrayList(fechas));
    }

    @FXML
    public void initialize() {
        cargarFechasTableView();
    }
}
