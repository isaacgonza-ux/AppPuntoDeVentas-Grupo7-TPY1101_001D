module com.sistema.puntoventas {
    // Módulos base de JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Librerías de terceros (UI y utilidades)
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // Conector de base de datos
    requires org.xerial.sqlitejdbc;
    requires java.sql;

    // Otros
    requires jdk.jdi;
    requires net.datafaker;
    requires static lombok;

    // --- CONFIGURACIÓN DE ACCESOS Y PERMISOS ---

    // Permite que el cargador de FXML pueda leer e inyectar datos en tus controladores
    // Sin esto, te da el error "IllegalAccessException"
    opens com.sistema.puntoventas.controller to javafx.fxml;
    opens com.sistema.puntoventas to javafx.fxml;

    // Si tus modelos (como Usuario) se usan en tablas de JavaFX, es bueno abrirlos también
    opens com.sistema.puntoventas.modelo to javafx.base;

    // Hace que tus clases sean visibles para otros módulos de Java
    exports com.sistema.puntoventas;
    exports com.sistema.puntoventas.controller;
    exports com.sistema.puntoventas.service;
    exports com.sistema.puntoventas.modelo;
    exports com.sistema.puntoventas.modelo.moduloProducto;
    opens com.sistema.puntoventas.modelo.moduloProducto to javafx.base;
    exports com.sistema.puntoventas.controller.moduloProductos;
    opens com.sistema.puntoventas.controller.moduloProductos to javafx.fxml;
    exports com.sistema.puntoventas.pruebas;
    opens com.sistema.puntoventas.pruebas to javafx.fxml;


    // Permite a Mockito acceder a tus clases para hacer las pruebas
    opens com.sistema.puntoventas.service to org.mockito;
    opens com.sistema.puntoventas.repository.moduloProductos to org.mockito;
}