package com.sistema.puntoventas.util;

import javafx.scene.control.TextField;

/**
 * Utilidad para gestionar validaciones visuales en componentes de la interfaz.
 */
public class AlertaCamposVacios {

    /**
     * Verifica si un TextField está vacío y aplica un resaltado rojo si es necesario.
     * admite varios textfields
     * @param campo El TextField a validar.
     * usar en inicialize() para para que resalte por primera vez que se renderiza la pagina
     */
    public static void resaltarSiVacio(TextField... campos ) {
        for (TextField campo : campos) {
            if (campo.getText() == null || campo.getText().trim().isEmpty()) {
                campo.setStyle("-fx-border-color: #ef4444; -fx-border-width: 1px; -fx-border-radius: 5px;");
                
            } else {
                campo.setStyle(""); // Limpia el estilo (vuelve al original de CSS)
                
            }
        }
        
    }







//funcion que verifica los campos vacios con listener
/**
     * Agrega un listener de cambio de texto a múltiples campos para validar el vacío en tiempo real.
     * @param campos Los TextFields a los que se les aplicará la validación automática.
     * sirve para validar constantemente si esta vacio el campo, cada que se haga un cambio
     * usar en inicialize()
     */
    public static void configurarValidacionAutomatica(TextField... campos) {
        for (TextField campo : campos) {
            campo.textProperty().addListener((obs, oldVal, newVal) -> resaltarSiVacio(campo));
        }
    }

}
