package com.sistema.puntoventas.controller.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.service.ProductoService;
import com.sistema.puntoventas.util.AlertaCamposVacios;
import com.sistema.puntoventas.util.MensajesAlerta;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.List;

import static com.sistema.puntoventas.util.MensajesAlerta.mostrarMensaje;

public class ProductoController {




        @FXML
        private Button btnRegistrar;

        @FXML
        private Label lblEstado;

        @FXML
        private ComboBox<Categoria> cmbCategoria;

        @FXML
        private TextField txtFechaVenc;

       /* @FXML
        private TextField txtImagen;*/

        @FXML
        private TextField txtNombre;

        @FXML
        private TextField txtPrecioCompra;

        @FXML
        private TextField txtPrecioVenta;

        @FXML
        private TextField txtStockActual;

        @FXML
        private TextField txtStockMinimo;

        @FXML
        private ComboBox<UnidadMedida> cmbUnidadMedida;

        @FXML
        private TextField txtCantidad;

        @FXML
        private ComboBox<TipoProducto> cmbTipoProducto;


        private ProductoService productoService;
        private Producto productoAEditar = null;







        @FXML
        public void initialize() throws Exception {
            AlertaCamposVacios.resaltarSiVacio(txtNombre, txtPrecioCompra,txtStockActual,txtStockMinimo);
            AlertaCamposVacios.configurarValidacionAutomatica(txtNombre, txtPrecioCompra,txtStockActual,txtStockMinimo);

            cmbTipoProducto.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == TipoProducto.DIRECTO) { //si es directo resaltar en rojo el textfield precio
                    AlertaCamposVacios.resaltarSiVacio(txtPrecioVenta);
                    AlertaCamposVacios.configurarValidacionAutomatica(txtPrecioVenta);
                }
            });

            cmbUnidadMedida.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != UnidadMedida.UNIDAD) { 
                    AlertaCamposVacios.resaltarSiVacio(txtCantidad);
                    AlertaCamposVacios.configurarValidacionAutomatica(txtCantidad);
                }
            });



            productoService = new ProductoService();
            cmbUnidadMedida.getItems().setAll(UnidadMedida.values());
            cmbTipoProducto.getItems().setAll(

                    TipoProducto.DIRECTO,
                    TipoProducto.SOLO_INVENTARIO
            );

            try {
                List<Categoria>categorias = productoService.obtenerCategorias();
                cmbCategoria.getItems().setAll(categorias);
                if (!cmbCategoria.getItems().isEmpty()) {
                    cmbCategoria.getSelectionModel().selectFirst();
                }
            }catch (Exception e){
                MensajesAlerta.mostrarMensaje("Aviso ","No hay categorias, se recomienda agregar una", Alert.AlertType.INFORMATION);
            }

            cmbUnidadMedida.setConverter(new StringConverter<>() {
                @Override
                public String toString(UnidadMedida unidadMedida) {
                    return formatearUnidadMedida(unidadMedida);
                }

                @Override
                public UnidadMedida fromString(String string) {
                    return null;
                }
            });
            cmbUnidadMedida.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(UnidadMedida item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : formatearUnidadMedida(item));
                }
            });
            cmbUnidadMedida.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(UnidadMedida item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : formatearUnidadMedida(item));
                }
            });

            cmbUnidadMedida.getSelectionModel().selectedItemProperty().addListener((observable, valorViejo, valorNuevo) -> {
                if (valorNuevo != null) {

                    switch (valorNuevo) {

                        case UNIDAD:
                            txtCantidad.setText("1");
                            txtCantidad.setDisable(true); // Bloqueamos el campo para el usuario
                            break;


                        case GRAMOS:
                        case MILILITROS:
                            txtCantidad.clear();
                            txtCantidad.setDisable(false); // Habilitamos el campo
                            break;

                        // Si tienes más opciones, agrégalas aquí...
                    }
                }
            });

                cmbTipoProducto.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(TipoProducto tipoProducto) {
                        return formatearTipoProducto(tipoProducto);
                    }

                    @Override
                    public TipoProducto fromString(String string) {
                        return null;
                    }
                });

                cmbTipoProducto.getSelectionModel().selectedItemProperty().addListener((observable, valorViejo, valorNuevo) ->{
                    if(valorNuevo != null){
                        switch (valorNuevo){
                            case SOLO_INVENTARIO:
                                txtPrecioVenta.setText("0");
                                txtPrecioVenta.setDisable(true);
                                System.out.println("Campo precio venta bloqueado");
                                break;

                            case PLATILLO:
                            case DIRECTO:
                                txtPrecioVenta.clear();
                                txtPrecioVenta.setDisable(false);
                                System.out.println("Campo precio venta desbloqueado");
                                break;
                        }
                        
                    }
                });

                cmbCategoria.setConverter(new StringConverter<Categoria>() {
                    @Override
                    public String toString(Categoria categoria) {
                        return categoria != null ? categoria.getNombreCategoria() : "";
                    }

                    @Override
                    public Categoria fromString(String string) {
                        return null;
                    }
                });



        }

        @FXML
       public  void registrarProducto(ActionEvent event) {
            try{

                String nombreIngresado = txtNombre.getText().trim().toLowerCase();



                if(nombreIngresado.isEmpty()){
                    lblEstado.setText("Error: El nombre del producto no puede estar vacío.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }



                // Si estamos editando, ignoramos su propio ID. Si es nuevo, pasamos 0.
                int idAExcluir = (productoAEditar != null) ? productoAEditar.getId() : 0;

                if (productoService.existeNombre(nombreIngresado, idAExcluir)) {
                    lblEstado.setText("Error: Ya existe otro producto con ese nombre.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }

                if(cmbUnidadMedida.getValue() == null){
                    lblEstado.setText("Error: Debe seleccionar una unidad de medida.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }

                if(cmbTipoProducto.getValue() == null){
                    lblEstado.setText("Error: Debe seleccionar un tipo de producto.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }

                if (txtPrecioCompra.getText().trim().isEmpty() ||
                        txtStockActual.getText().trim().isEmpty() ||
                        txtStockMinimo.getText().trim().isEmpty() ||
                        txtCantidad.getText().trim().isEmpty()) {

                    lblEstado.setText("Error: Los precios, stocks y cantidad son campos obligatorios.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }


                double precioCompra = Double.parseDouble(txtPrecioCompra.getText());
                double precioVenta = Double.parseDouble(txtPrecioVenta.getText().isEmpty() ? "0" : txtPrecioVenta.getText());
                int stockActual = Integer.parseInt(txtStockActual.getText());
                int stockMinimo = Integer.parseInt(txtStockMinimo.getText());


                if(precioCompra <=0){
                    lblEstado.setText("Error: El precio de compra debe ser mayor a 0.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }

                if (cmbTipoProducto.getValue() == TipoProducto.SOLO_INVENTARIO) {
                    precioVenta = 0.0;
                }



                if (cmbTipoProducto.getValue() != TipoProducto.SOLO_INVENTARIO) {
                    double precioMinimoVenta = precioCompra * 1.10; // Costo + 10% mínimo

                    if (precioVenta < precioMinimoVenta) {
                        lblEstado.setText("Protección de Margen: El precio de venta debe ser al menos un 10% mayor al precio de compra.");
                        lblEstado.setTextFill(Color.RED);
                        return;
                    }
                }


                if(stockActual <=0 ){
                    lblEstado.setText("Error: El stock actual debe ser mayor a 0.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }

                if(stockMinimo <=0){
                    lblEstado.setText("Error: El stock mínimo debe ser mayor a 0.");
                    lblEstado.setTextFill(Color.RED);
                    return;
                }



                if(productoAEditar == null) {

                    Producto nuevoProducto = new Producto();
                    nuevoProducto.setNombre(nombreIngresado);
                    nuevoProducto.setPrecioCompra(precioCompra);
                    nuevoProducto.setPrecioVenta(precioVenta);
                    nuevoProducto.setStockActual(stockActual);
                    nuevoProducto.setStockMinimo(stockMinimo);
                    nuevoProducto.setCategoria(cmbCategoria.getValue());
                    nuevoProducto.setFechaVenc(txtFechaVenc.getText());
                    nuevoProducto.setActivo(true);
                    nuevoProducto.setUnidadMedida(cmbUnidadMedida.getValue());
                    nuevoProducto.setCantidad(Double.parseDouble(txtCantidad.getText()));
                    nuevoProducto.setTipoProducto(cmbTipoProducto.getValue());
                    lblEstado.setText("");

                try{
                    //lo enviamos al service para hacer validaciones necesarias
                    productoService.registrarProducto(nuevoProducto);
                    System.err.println("Producto registrado: " + nuevoProducto.getNombre());
                    lblEstado.setText("¡Producto registrado con éxito!");
                    lblEstado.setTextFill(Color.GREEN); // Pintamos el texto de verde
                    limpiarFormulario();
                } catch (RuntimeException e) {
                    System.err.println("Advertencia: El producto se guardó, pero falló el historial: " + e.getMessage());
                    lblEstado.setText("¡Producto registrado con éxito! (Sin historial)");
                    lblEstado.setTextFill(Color.GREEN);
                    limpiarFormulario();
                }
                }else {
                    productoAEditar.setNombre(nombreIngresado);
                    productoAEditar.setPrecioCompra(precioCompra);
                    productoAEditar.setPrecioVenta(precioVenta);
                    productoAEditar.setStockActual(stockActual);
                    productoAEditar.setStockMinimo(stockMinimo);
                    productoAEditar.setCategoria(cmbCategoria.getValue());
                    productoAEditar.setFechaVenc(txtFechaVenc.getText());
                   // productoAEditar.setImagen(txtImagen.getText());
                    productoAEditar.setUnidadMedida(cmbUnidadMedida.getValue());
                    productoAEditar.setCantidad(Double.parseDouble(txtCantidad.getText()));
                    productoAEditar.setTipoProducto(cmbTipoProducto.getValue());


                    productoService.actualizarProducto(productoAEditar); // Llama al UPDATE
                    lblEstado.setText("¡Producto actualizado con éxito!");
                    lblEstado.setTextFill(Color.GREEN);
                    limpiarFormulario();
                    productoAEditar = null;
                }

            } catch (NumberFormatException e) {
                lblEstado.setText("Error: Los precios y el stock deben ser números válidos.");
                lblEstado.setTextFill(Color.RED);
            } catch (Exception e) {

                lblEstado.setText(e.getMessage());
                lblEstado.setTextFill(Color.RED);
            }

        }

    //Solo sirve para cargar los datos en el formulario para actualizar producto
    public void ActualizarProducto(Producto producto) {
        this.productoAEditar = producto;

        cmbCategoria.setValue(producto.getCategoria());
        cmbUnidadMedida.setValue(producto.getUnidadMedida());
        cmbTipoProducto.setValue(producto.getTipoProducto());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));

        txtNombre.setText(producto.getNombre());
        txtPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        txtStockActual.setText(String.valueOf(producto.getStockActual()));
        txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        txtFechaVenc.setText(producto.getFechaVenc());
       // txtImagen.setText(producto.getImagen());


        btnRegistrar.setText("Actualizar Producto");
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtFechaVenc.clear();
        txtStockActual.clear();
        txtStockMinimo.clear();
        //txtImagen.clear();
        cmbUnidadMedida.getSelectionModel().clearSelection();
        cmbTipoProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
    }




    private String formatearUnidadMedida(UnidadMedida unidadMedida) {
        if (unidadMedida == null) {
            return "";
        }

        String texto = unidadMedida.name().toLowerCase();
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private String formatearTipoProducto(TipoProducto tipoProducto){
            if(tipoProducto == null){
                return "";
            }

            String texto = tipoProducto.name().toLowerCase();
            return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }



}
