package com.sistema.puntoventas.controller.moduloProductos;



import com.sistema.puntoventas.modelo.moduloProducto.DetallePlatillo;
import com.sistema.puntoventas.modelo.moduloProducto.MetricasDTO;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.util.MensajesAlerta;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.sistema.puntoventas.util.MensajesAlerta.mostrarConfirmacion;
import static com.sistema.puntoventas.util.MensajesAlerta.mostrarMensaje;

public class PanelPrincipalProductosController {

    @FXML
    private Label lblProductosActivos;

    @FXML
    private Label lblCategoriasActivas;

    @FXML
    private Label lblPlatillosActivos;

    @FXML
    private Label lblBajoStock;

    @FXML
    private Pane CardActivos;

    @FXML
    private Pane CardCategorias;

    @FXML
    private Pane CardProductos;

    @FXML
    private Pane CardBajoStock;

    @FXML
    private Button btnAgregarProducto;

    @FXML
    private Button btnEditarProducto;

    @FXML
    private Button btnEliminarProducto;

    @FXML
    private Button btnVerPlatillos;

    @FXML
    private TableView<Producto> tableProductos;

    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, Double> colPrecioCompra;
    @FXML
    private TableColumn<Producto, Double> colPrecioVenta;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, String> colFechaVenc;
    @FXML
    private TableColumn<Producto, Integer> colStockActual;
    @FXML
    private TableColumn<Producto, Integer> colStockMin;
    @FXML
    private TableColumn<Producto, String> colUnidadMedida;

    @FXML
    private TableColumn<Producto, Double> colCantidad;
    @FXML
    private TableColumn<Producto, String> colTipoProducto;



    private ProductoService productoService;
    private java.util.List<String> nombresCriticos;

    
    
    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colFechaVenc.setCellValueFactory(new PropertyValueFactory<>("fechaVenc"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colUnidadMedida.setCellValueFactory(new PropertyValueFactory<>("unidadMedida"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipoProducto.setCellValueFactory(new PropertyValueFactory<>("tipoProducto"));

        configurarRowFactory();
        obtenerProductos();
        actualizarMetricas();

        btnAgregarProducto.setOnAction(e -> cargarVistaAgregarProducto("PanelRegistrarProductosvista.fxml"));
        btnEditarProducto.setOnAction(e -> actualizarProductos());
        btnEliminarProducto.setOnAction(this::eliminarProducto);

        tableProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)->{
            if(newSelection != null){
                System.out.println("RECETA");
             //   btnVerReceta.setDisable(!newSelection.getTipoProducto().name().equals("PLATILLO"));
            }
        });
    }

    private void configurarRowFactory() {
        tableProductos.setRowFactory(tv -> new TableRow<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty || nombresCriticos == null) {
                    setStyle("");
                } else if (nombresCriticos.contains(item.getNombre())) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void configurarColumnasTabla() {
        // Configuración de Responsividad (Anchos Proporcionales)
        colId.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.05));
        colNombre.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.15));
        colPrecioCompra.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.10));
        colPrecioVenta.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.10));
        colCategoria.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.10));
        colFechaVenc.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.10));
        colStockActual.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.08));
        colStockMin.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.08));
        colUnidadMedida.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.08));
        colCantidad.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.08));
        colTipoProducto.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.08));

        // Mapeo de Celdas con Lambdas
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecioCompra.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioCompra()));
        colPrecioVenta.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioVenta()));
        colCategoria.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategoria() != null ? cellData.getValue().getCategoria().getNombreCategoria() : "N/A"));
        colFechaVenc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaVenc()));
        colStockActual.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStockActual()));
        colStockMin.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStockMinimo()));
        colUnidadMedida.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUnidadMedida() != null ? cellData.getValue().getUnidadMedida().name() : "N/A"));
        colCantidad.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCantidad()));
        colTipoProducto.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipoProducto() != null ? cellData.getValue().getTipoProducto().name() : "N/A"));
    }

    private void cargarVistaAgregarProducto(String fxml) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/" + fxml));
            Parent root = loader.load();

            // Abrir la vista en una nueva ventana modal
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Registrar producto");
            stage.setScene(new Scene(root,900,600));
            stage.showAndWait();
            obtenerProductos();
            actualizarMetricas();

        } catch (Exception e) {
            MensajesAlerta.mostrarMensaje("ERROR","No se pudo cargar la vista: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    private void cargarVistaActualizarProductos(Producto productoSeleccionado){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistema/puntoventas/PanelRegistrarProductosvista.fxml"));
            Parent root = loader.load();

            ProductoController controller = loader.getController();


            if (controller != null && productoSeleccionado != null) {
                controller.ActualizarProducto(productoSeleccionado);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Actualizar Producto");
            stage.setScene(new Scene(root,1200,768));
            stage.showAndWait();



            obtenerProductos();
            actualizarMetricas();


        } catch (Exception e) {
            MensajesAlerta.mostrarMensaje("ERROR", "Error al abrir vista: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void obtenerProductos(){

        try{
            
            if (productoService == null) {
                productoService = new ProductoService();
            }

            nombresCriticos = productoService.obtenerNombreStockCritico();


            java.util.List<Producto> productos = productoService.obtenerProductos();

            // Si no hay productos, limpiamos la tabla y mostramos aviso
            if (productos == null || productos.isEmpty()){
                if (tableProductos != null) {
                    tableProductos.getItems().clear();
                }
                MensajesAlerta.mostrarMensaje("AVISO","No hay productos para mostrar", Alert.AlertType.INFORMATION);
                return;
            }

            // Poblamos la tabla 
            if (tableProductos != null) {
                
                @SuppressWarnings("unchecked")
                javafx.collections.ObservableList<Producto> items = (javafx.collections.ObservableList<Producto>) tableProductos.getItems();
                items.setAll(productos);
            }

            //MensajesAlerta.mostrarMensaje("ÉXITO","Productos cargados correctamente: " + productos.size(), Alert.AlertType.INFORMATION);
        }catch (Exception e){
            MensajesAlerta.mostrarMensaje("ERROR","Error al obtener productos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void actualizarProductos (){
       Producto productoSeleccionado  =tableProductos.getSelectionModel().getSelectedItem();

       if (productoSeleccionado == null ){
           System.out.println("Producto no seleccionado");
           MensajesAlerta.mostrarMensaje("AVISO","Seleccione un producto para editar", Alert.AlertType.WARNING);
           return;
       }

         cargarVistaActualizarProductos(productoSeleccionado);
    }




    @FXML
    public void eliminarProducto(javafx.event.ActionEvent event){
        Producto productoSeleccionado  =tableProductos.getSelectionModel().getSelectedItem();



        if(productoSeleccionado == null){
            mostrarMensaje("AVISO","Por favor seleccione un producto para eliminar", Alert.AlertType.WARNING);
            return;
        }

        String nombreAeliminar = productoSeleccionado.getNombre();
        boolean existeNombre = productoService.existeNombre(nombreAeliminar,0);
        if(!existeNombre){
            mostrarMensaje("AVISO","El producto seleccionado no existe ", Alert.AlertType.WARNING);
            return;
        }

        boolean respuesta = MensajesAlerta.mostrarConfirmacion("Confirmación","¿Está seguro que desea eliminar el producto seleccionado?", Alert.AlertType.CONFIRMATION);
        if(respuesta ){
            try {
                if (productoService == null) {
                    productoService = new ProductoService();
                }
                String eliminado = productoService.eliminarProducto(productoSeleccionado.getId());
                if (eliminado.equalsIgnoreCase("ELIMINADO")) {
                    mostrarMensaje("ÉXITO", "Producto eliminado correctamente", Alert.AlertType.INFORMATION);
                    obtenerProductos();
                    actualizarMetricas();
                } else {
                    mostrarMensaje("AVISO", "El producto tiene asociaciones, Solo se desactivara " + eliminado, Alert.AlertType.WARNING);
                    obtenerProductos();
                    actualizarMetricas();
                }
            } catch (Exception e) {
                MensajesAlerta.mostrarMensaje("ERROR", e.getMessage(), Alert.AlertType.ERROR);
                System.err.println("Error al eliminar producto: " + e.getMessage());
            }
        }

    }

    private void actualizarMetricas(){
        try{
            if(productoService == null){
                productoService = new ProductoService();
            }

            MetricasDTO metricas = productoService.calcularMetricas();

            lblProductosActivos.setText(String.valueOf(metricas.getTotalPlatillos()));
            lblPlatillosActivos.setText(String.valueOf(metricas.getPlatillosActivos()));
            lblCategoriasActivas.setText(String.valueOf(metricas.getCategoriasActivas()));
            lblBajoStock.setText(metricas.getBajoStock() > 0 ? String.valueOf(metricas.getBajoStock()) : "No hay");

        } catch (Exception e) {
            System.err.println("Error al actualizar metricas: " + e.getMessage());
        }
    }
}
