package com.sistema.puntoventas;

import com.sistema.puntoventas.conexion.DbManager;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/sistema/puntoventas/panelPrincipalVista.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Eluney");
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);

        // Listener para controlar la posición cuando se restaura la ventana
        stage.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
            if (!isMaximized) {
                // Al restaurar, centrar la ventana en la pantalla
                double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
                double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();

                double x = (screenWidth - stage.getWidth()) / 2;
                double y = (screenHeight - stage.getHeight()) / 2;

                // Asegurar que la ventana no quede fuera de pantalla
                if (y < 0) y = 20;
                if (x < 0) x = 20;

                stage.setX(x);
                stage.setY(y);
            }
        });

        stage.show();

        DbManager dbManager = new DbManager();
        dbManager.conectarBD();
        dbManager.crearTodasLasTablas();
        dbManager.crearUsuarioAdmin();






    }
}       