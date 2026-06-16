package com.sistema.puntoventas;

import com.sistema.puntoventas.conexion.DbManager;
import com.sistema.puntoventas.controller.LoginController;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.service.SesionService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
//version ev3
//lista

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DbManager dbManager = new DbManager();
        dbManager.conectarBD();
        dbManager.crearTodasLasTablas();
        dbManager.crearUsuarioAdmin();
        dbManager.crearUsuarioVendedor();

        SesionService sesionService = new SesionService();
        Usuario usuario = sesionService.verificarSesionPersistente();

        if (usuario != null) {
            LoginController.usuarioLogueado = usuario;
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/sistema/puntoventas/panelPrincipalVista.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Sistema Punto de Ventas - Panel Principal");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/sistema/puntoventas/LoginVista.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Eluney");
            stage.setWidth(1920);
            stage.setHeight(1080);
            stage.setScene(scene);
            stage.setMaximized(true);

            stage.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
                if (!isMaximized) {
                    double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
                    double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
                    double x = (screenWidth - stage.getWidth()) / 2;
                    double y = (screenHeight - stage.getHeight()) / 2;
                    if (y < 0) y = 20;
                    if (x < 0) x = 20;
                    stage.setX(x);
                    stage.setY(y);
                }
            });

            stage.show();
        }
    }
}