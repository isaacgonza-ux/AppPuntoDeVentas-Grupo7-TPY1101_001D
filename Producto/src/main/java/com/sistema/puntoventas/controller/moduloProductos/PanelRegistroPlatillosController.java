package com.sistema.puntoventas.controller.moduloProductos;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.DetallePlatillo;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.service.PlatilloService;
import com.sistema.puntoventas.service.ProductoService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PanelRegistroPlatillosController {

    @FXML
    private Button btnAgregarIngrediente;

    @FXML
    private Button btnRegistrarPlatillo;

    @FXML
    private ComboBox<Producto> cmbIngredientes;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private TableColumn<DetallePlatillo, Integer> colId;

    @FXML
    private TableColumn<DetallePlatillo, String> colNombre;

    @FXML
    private TableColumn<DetallePlatillo, Double> colTipoProducto;

    @FXML
    private TableColumn<DetallePlatillo, Double> colCostoProduccion;

    @FXML
    private TableColumn<DetallePlatillo, String> colUnidadMedida;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblCostoTotal;

    @FXML
    private TableView<DetallePlatillo> tableProductos;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextArea txtdescripcion;
    
    PlatilloService platilloService;
    ProductoService productoService;
    private Platillo platilloAEditar;

    private ObservableList<DetallePlatillo> listaIngredientesTemporal = FXCollections.observableArrayList();

    public void initialize() throws Exception {
        productoService = new ProductoService();
        platilloService = new PlatilloService();
        try {
            List<Categoria> categorias = productoService.obtenerCategorias();
            cmbCategoria.getItems().setAll(categorias);
            
            // Cargar productos para el ComboBox de ingredientes
            cmbIngredientes.getItems().setAll(platilloService.obtenerIngredientes());

            // Configurar el ComboBox para que muestre solo el nombre del ingrediente
            cmbIngredientes.setConverter(new javafx.util.StringConverter<Producto>() {
                @Override
                public String toString(Producto producto) {
                    return producto == null ? "" : producto.getNombre();
                }

                @Override
                public Producto fromString(String string) {
                    return null;
                }
            });

            configurarColumnasTabla();
            tableProductos.setItems(listaIngredientesTemporal);

        } catch (Exception e) {
            System.err.println("Error al cargar los datos: " + e.getMessage());
        }
        
        if (btnAgregarIngrediente != null) {
            btnAgregarIngrediente.setOnAction(this::agregarIngrediente);
        }

        if(btnRegistrarPlatillo != null){
            btnRegistrarPlatillo.setOnAction(this::registrarPlatillo);
        }
    }

    private void configurarColumnasTabla() {
        // Responsividad
        colNombre.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.40));
        colTipoProducto.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.20));
        colUnidadMedida.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.20));
        colCostoProduccion.prefWidthProperty().bind(tableProductos.widthProperty().multiply(0.20));

        // CellValueFactories
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
        
        colTipoProducto.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCantidadIngrediente()).asObject());
        colTipoProducto.setText("Cantidad");
        
        colUnidadMedida.setCellValueFactory(cellData -> {
            var unidad = cellData.getValue().getProducto().getUnidadMedida();
            return new SimpleStringProperty(unidad != null ? unidad.name() : "");
        });
        colUnidadMedida.setText("Unidad Medida");
        
        colCostoProduccion.setCellValueFactory(cellData -> {
            double costoUnitario = cellData.getValue().getProducto().getPrecioCompra();
            double cantidad = cellData.getValue().getCantidadIngrediente();
            return new SimpleDoubleProperty(costoUnitario * cantidad).asObject();
        });
        colCostoProduccion.setText("Costo Total");
    }

    @FXML
    public void agregarIngrediente(ActionEvent event) {
        Producto prodSeleccionado = cmbIngredientes.getValue();
        String cantidadStr = txtCantidad.getText().trim();
        
        if (prodSeleccionado == null || cantidadStr.isEmpty()) {
            lblEstado.setText("Error: Seleccione un ingrediente y digite su cantidad.");
            lblEstado.setTextFill(Color.RED);
            return;
        }
        
        try {
            double cantidad = Double.parseDouble(cantidadStr);
            if (cantidad <= 0) {
                lblEstado.setText("Error: La cantidad debe ser mayor a 0.");
                lblEstado.setTextFill(Color.RED);
                return;
            }
            
            double cantidadConvertida = platilloService.convertirCantidad(prodSeleccionado, cantidad);
            platilloService.validarStockIngredientes(prodSeleccionado, cantidadConvertida, listaIngredientesTemporal);
            
            DetallePlatillo detalle = new DetallePlatillo();
            detalle.setProducto(prodSeleccionado);
            detalle.setCantidadIngrediente(cantidadConvertida);
            
            listaIngredientesTemporal.add(detalle);
            actualizarCostoTotalEnPantalla();
            
            cmbIngredientes.getSelectionModel().clearSelection();
            txtCantidad.clear();
            lblEstado.setText("Ingrediente agregado temporalmente.");
            lblEstado.setTextFill(Color.BLUE);
            
        } catch (NumberFormatException e) {
            lblEstado.setText("Error: Cantidad inválida.");
            lblEstado.setTextFill(Color.RED);
        } catch (Exception e) {
            lblEstado.setText("Error: " + e.getMessage());
            lblEstado.setTextFill(Color.RED);
            System.err.println("Error al agregar ingrediente: " + e.getMessage());
        }
    }

    @FXML
    public void registrarPlatillo(ActionEvent event) {
        try {
            // 1. Validaciones básicas
            if (txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
                lblEstado.setText("Error: El nombre y el precio son obligatorios.");
                lblEstado.setTextFill(Color.RED);
                return;
            }

            if (cmbCategoria.getValue() == null) {
                lblEstado.setText("Error: Debes seleccionar una categoría.");
                lblEstado.setTextFill(Color.RED);
                return;
            }

            // 2. Construir el objeto PLATILLO
            Platillo nuevoPlatillo = new Platillo();
            nuevoPlatillo.setNombre(txtNombre.getText().trim());
            nuevoPlatillo.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            nuevoPlatillo.setCategoria(cmbCategoria.getValue());

            // 3. Asignaciones automáticas por contexto
            nuevoPlatillo.setTipoProducto(TipoProducto.PLATILLO);
            nuevoPlatillo.setCostoProduccion(0.0); // El costo se calculará en base a los ingredientes
            nuevoPlatillo.setStockActual(0); // Los platillos no tienen stock, se preparan en el momento
            nuevoPlatillo.setEstado(true); // Activo por defecto

            // Asignar lista de ingredientes
            if (listaIngredientesTemporal.isEmpty()) {
                lblEstado.setText("Error: El platillo debe tener al menos un ingrediente.");
                lblEstado.setTextFill(Color.RED);
                return;
            }
            nuevoPlatillo.setIngrediente(new ArrayList<>(listaIngredientesTemporal));

            // Calcular y asignar el costo total de producción en el servicio antes de registrar
            platilloService.calcularCostoProduccion(nuevoPlatillo);

            // 4. Enviar al Service (Asumiendo que hiciste un PlatilloService)
            platilloService.registrarPlatillo(nuevoPlatillo);

            // 5. Mensaje de éxito y limpieza
            lblEstado.setText("¡Platillo registrado con éxito!");
            lblEstado.setTextFill(Color.GREEN);
            limpiarFormulario(); // Tu método de limpieza

            // (remove) cierre automatico del modal

        } catch (NumberFormatException e) {
            lblEstado.setText("Error: El precio debe ser un número válido.");
            lblEstado.setTextFill(Color.RED);
        } catch (Exception e) {
            lblEstado.setText("Error: " + e.getMessage());
            lblEstado.setTextFill(Color.RED);
        }
    }

    @FXML
    public void actualizarPlatillo(ActionEvent event) {
        try {
            if (platilloAEditar == null) {
                lblEstado.setText("Error: No hay un platillo seleccionado para actualizar.");
                lblEstado.setTextFill(Color.RED);
                return;
            }

            if (txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
                lblEstado.setText("Error: El nombre y el precio son obligatorios.");
                lblEstado.setTextFill(Color.RED);
                return;
            }

            if (cmbCategoria.getValue() == null) {
                lblEstado.setText("Error: Debes seleccionar una categoría.");
                lblEstado.setTextFill(Color.RED);
                return;
            }

            Platillo platilloActualizado = new Platillo();
            platilloActualizado.setId(platilloAEditar.getId());
            platilloActualizado.setNombre(txtNombre.getText().trim());
            platilloActualizado.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            platilloActualizado.setCategoria(cmbCategoria.getValue());
            platilloActualizado.setTipoProducto(TipoProducto.PLATILLO);
            platilloActualizado.setEstado(platilloAEditar.isEstado());
            platilloActualizado.setStockActual(platilloAEditar.getStockActual());

            if (!listaIngredientesTemporal.isEmpty()) {
                platilloActualizado.setIngrediente(new ArrayList<>(listaIngredientesTemporal));
            } else {
                platilloActualizado.setIngrediente(platilloAEditar.getIngrediente());
                platilloActualizado.setCostoProduccion(platilloAEditar.getCostoProduccion());
            }

            platilloService.actualizarPlatillo(platilloActualizado);

            lblEstado.setText("¡Platillo actualizado con éxito!");
            lblEstado.setTextFill(Color.GREEN);

            // (remove) cierre automatico del modal

        } catch (NumberFormatException e) {
            lblEstado.setText("Error: El precio debe ser un número válido.");
            lblEstado.setTextFill(Color.RED);
        } catch (Exception e) {
            lblEstado.setText("Error: " + e.getMessage());
            lblEstado.setTextFill(Color.RED);
        }
    }

    public void cargarDatosParaEdicion(Platillo platillo) {
        this.platilloAEditar = platillo;
        if (platillo == null) {
            return;
        }

        txtNombre.setText(platillo.getNombre());
        txtPrecio.setText(String.valueOf(platillo.getPrecio()));
        if (platillo.getCategoria() != null) {
            for (Categoria categoria : cmbCategoria.getItems()) {
                if (categoria != null && categoria.getId() == platillo.getCategoria().getId()) {
                    cmbCategoria.getSelectionModel().select(categoria);
                    break;
                }
            }
        }

        listaIngredientesTemporal.clear();
        if (platillo.getIngrediente() != null && !platillo.getIngrediente().isEmpty()) {
            listaIngredientesTemporal.addAll(platillo.getIngrediente());
            actualizarCostoTotalEnPantalla();
        }

        btnRegistrarPlatillo.setText("Actualizar Platillo");
        btnRegistrarPlatillo.setOnAction(this::actualizarPlatillo);
    }


    private void actualizarCostoTotalEnPantalla() {
        double costoTotal = 0.0;

        // Recorremos la lista que está llenando la tabla en pantalla
        for (DetallePlatillo detalle : listaIngredientesTemporal) {
            if (detalle.getProducto() != null) {
                double costoIngrediente = detalle.getProducto().getPrecioCompra();
                double cantidadUtilizada = detalle.getCantidadIngrediente();

                // Sumamos al total general
                costoTotal += (costoIngrediente * cantidadUtilizada);
            }
        }

        // Mostramos el total acumulado en el Label de la pantalla
        if (lblCostoTotal != null) {
            lblCostoTotal.setText(String.format("Costo Producción: $%.2f", costoTotal));
            System.out.println("Costo total actualizado en pantalla: " + costoTotal);
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtdescripcion.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        cmbIngredientes.getSelectionModel().clearSelection();
        txtCantidad.clear();
        listaIngredientesTemporal.clear();
    }

}
