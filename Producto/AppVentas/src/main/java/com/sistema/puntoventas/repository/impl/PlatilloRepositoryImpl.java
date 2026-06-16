package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.DetallePlatillo;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.repository.moduloProductos.IPlatilloRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatilloRepositoryImpl implements IPlatilloRepository {

    private static final String url = "jdbc:sqlite:DBventasInventario.db";

    @Override
    public boolean registrarPlatillo(Platillo platillo) {
        String sqlPlatillo = "INSERT INTO platillo (nombre, precio, idCategoria, estado, costoProduccion, tipoProducto) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_platillo (idPlatillo, idProducto, cantidadIngrediente) VALUES (?, ?, ?)";
        
        try(Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPlatillo = conn.prepareStatement(sqlPlatillo, Statement.RETURN_GENERATED_KEYS)) {
                // 3. Seteamos los datos del Platillo (Calculados previamente por el sistema)
                stmtPlatillo.setString(1, platillo.getNombre());
                stmtPlatillo.setDouble(2, platillo.getPrecio());
                stmtPlatillo.setInt(3, platillo.getCategoria().getId());
                stmtPlatillo.setBoolean(4, platillo.isEstado());
                stmtPlatillo.setDouble(5, platillo.getCostoProduccion());
                stmtPlatillo.setString(6, "PLATILLO");

                int affectedRows = stmtPlatillo.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("No se pudo crear el platillo.");
                }

                int idPlatilloGenerado;
                try (ResultSet generatedKeys = stmtPlatillo.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idPlatilloGenerado = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener el ID del platillo.");
                    }
                }

                // 5. GUARDAR LOS INGREDIENTES: Usamos Batch para mayor eficiencia
                try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                    for (DetallePlatillo item : platillo.getIngrediente()) {
                        stmtDetalle.setInt(1, idPlatilloGenerado);
                        stmtDetalle.setInt(2, item.getProducto().getId());
                        stmtDetalle.setDouble(3, item.getCantidadIngrediente());
                        stmtDetalle.addBatch(); // Se agrega a la cola
                    }
                    stmtDetalle.executeBatch(); // Se ejecutan todos los inserts de una sola vez
                }

                conn.commit();
                return true;

            } catch (SQLException e) {

                conn.rollback();
                System.err.println("Error en la transacción: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }

    }

    @Override
    public List<Platillo> obtenerPlatillos() {
        List<Platillo> listaPlatillos = new ArrayList<>();
        String sql = "SELECT p.*, c.id as catId, c.nombreCategoria FROM platillo p " +
                     "LEFT JOIN categoria c ON p.idCategoria = c.id " +
                     "WHERE p.estado = 1 ORDER BY p.id ASC";
        try(Connection conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Platillo platillo = new Platillo();
                platillo.setId(rs.getInt("id"));
                platillo.setNombre(rs.getString("nombre"));
                platillo.setPrecio(rs.getDouble("precio"));
                platillo.setEstado(rs.getBoolean("estado"));
                platillo.setTipoProducto(TipoProducto.PLATILLO);
                platillo.setStockActual(rs.getInt("fabricables"));
                
                // Cargar la categoría si existe
                int catId = rs.getInt("catId");
                if (catId > 0) {
                    Categoria categoria = new Categoria();
                    categoria.setId(catId);
                    categoria.setNombreCategoria(rs.getString("nombreCategoria"));
                    platillo.setCategoria(categoria);
                } else {
                    platillo.setCategoria(null);
                }
                
                platillo.setCostoProduccion(rs.getDouble("costoProduccion"));
                platillo.setIngrediente(obtenerIngredientesPorPlatillo(conn, platillo.getId()));
                listaPlatillos.add(platillo);
                System.out.println("Platillo encontrados: " + listaPlatillos);
            }

        } catch (Exception e) {

            System.err.println("Error al obtener platillos: " + e.getMessage());
        }

        return listaPlatillos;

    }

    private List<DetallePlatillo> obtenerIngredientesPorPlatillo(Connection conn, int idPlatillo) {
        List<DetallePlatillo> ingredientes = new ArrayList<>();
        String sql = "SELECT d.id, d.idPlatillo, d.idProducto, d.cantidadIngrediente, p.nombre, p.stockActual " +
                "FROM detalle_platillo d " +
                "INNER JOIN producto p ON d.idProducto = p.id " +
                "WHERE d.idPlatillo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPlatillo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetallePlatillo detalle = new DetallePlatillo();
                    detalle.setId(rs.getInt("id"));
                    detalle.setIdPlatillo(rs.getInt("idPlatillo"));
                    detalle.setCantidadIngrediente(rs.getDouble("cantidadIngrediente"));

                    com.sistema.puntoventas.modelo.moduloProducto.Producto producto = new com.sistema.puntoventas.modelo.moduloProducto.Producto();
                    producto.setId(rs.getInt("idProducto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setStockActual(rs.getInt("stockActual"));

                    detalle.setProducto(producto);
                    ingredientes.add(detalle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar ingredientes del platillo: " + e.getMessage());
        }

        return ingredientes;
    }

    @Override
    public List<Platillo> obtenerPlatilloPorNombre(String nombre) {
        List<Platillo> listaPlatillos = new ArrayList<>();
        String sql = "SELECT p.*,c.nombreCategoria FROM platillo p " +
                     "INNER JOIN categoria c ON p.idCategoria = c.id " +
                     " WHERE p.nombre LIKE ? AND p.estado = 1 ORDER BY p.nombre ASC"; // Solo platillos activos
        try(Connection conn = DriverManager.getConnection(url);
            var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Platillo platillo = new Platillo();
                    platillo.setId(rs.getInt("id"));
                    platillo.setNombre(rs.getString("nombre"));
                    platillo.setPrecio(rs.getDouble("precio"));
                    platillo.setCategoria(null);
                    platillo.setEstado(rs.getBoolean("estado"));
                    platillo.setCostoProduccion(rs.getDouble("costoProduccion"));
                    platillo.setStockActual(rs.getInt("fabricables"));
                    listaPlatillos.add(platillo);
                    System.out.println("Platillo encontrado por nombre: " + platillo.getNombre());
                }
            }

        } catch (Exception e) {

            System.err.println("Error al obtener platillos por nombre: " + e.getMessage());
        }

        return listaPlatillos;
    }

    @Override
    public boolean actualizarPlatillo(Platillo platillo) {
        String sqlUpdatePlatillo = "UPDATE platillo SET nombre = ?, precio = ?, idCategoria = ?, estado = ?, costoProduccion = ?, tipoProducto = ? WHERE id = ?";
        String sqlDeleteDetalle = "DELETE FROM detalle_platillo WHERE idPlatillo = ?";
        String sqlInsertDetalle = "INSERT INTO detalle_platillo (idPlatillo, idProducto, cantidadIngrediente) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (PreparedStatement stmtPlatillo = conn.prepareStatement(sqlUpdatePlatillo)) {
                stmtPlatillo.setString(1, platillo.getNombre());
                stmtPlatillo.setDouble(2, platillo.getPrecio());
                stmtPlatillo.setInt(3, platillo.getCategoria().getId());
                stmtPlatillo.setBoolean(4, platillo.isEstado());
                stmtPlatillo.setDouble(5, platillo.getCostoProduccion());
                stmtPlatillo.setString(6, "PLATILLO");
                stmtPlatillo.setInt(7, platillo.getId());

                int affectedRows = stmtPlatillo.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("No se pudo actualizar el platillo, ID no encontrado.");
                }
            }

            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteDetalle)) {
                stmtDelete.setInt(1, platillo.getId());
                stmtDelete.executeUpdate();
            }

            if (platillo.getIngrediente() != null && !platillo.getIngrediente().isEmpty()) {
                try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlInsertDetalle)) {
                    for (DetallePlatillo item : platillo.getIngrediente()) {
                        stmtDetalle.setInt(1, platillo.getId());
                        stmtDetalle.setInt(2, item.getProducto().getId());
                        stmtDetalle.setDouble(3, item.getCantidadIngrediente());
                        stmtDetalle.addBatch();
                    }
                    stmtDetalle.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar platillo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarPlatillo(int id) {
        String sqlDeleteDetalle = "DELETE FROM detalle_platillo WHERE idPlatillo = ?";
        String sqlDeletePlatillo = "DELETE FROM platillo WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDeleteDetalle)) {
                stmtDetalle.setInt(1, id);
                stmtDetalle.executeUpdate();
            }

            try (PreparedStatement stmtPlatillo = conn.prepareStatement(sqlDeletePlatillo)) {
                stmtPlatillo.setInt(1, id);
                int affectedRows = stmtPlatillo.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("No se pudo eliminar el platillo, ID no encontrado.");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar platillo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean desactivarPlatillo(int id) {
        String sql = "UPDATE platillo SET estado = 0 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar platillo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeNombre(String nombre, int id) {
        String sql = "SELECT COUNT(*) FROM platillo WHERE nombre = ? AND id != ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor a 0, el nombre ya existe
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de nombre: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Platillo obtenerPlatilloPorId(int id) {
        String sql = "SELECT * FROM platillo WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Platillo platillo = new Platillo();
                    platillo.setId(rs.getInt("id"));
                    platillo.setNombre(rs.getString("nombre"));
                    platillo.setPrecio(rs.getDouble("precio"));
                    platillo.setStockActual(rs.getInt("fabricables"));
                    // Aquí podrías cargar la categoría y los ingredientes si lo deseas
                    return platillo;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener platillo por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean estaAsociadoVenta(int id) {
        String sql = "SELECT COUNT(*) FROM detalle_venta WHERE idPlatillo = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor a 0, el platillo está asociado a una venta
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar asociación con ventas: " + e.getMessage());
        }
        return false;
    }

    /**
     * Nuevo método basado en obtenerPlatilloPorNombre que trae la receta completa
     * y los datos de productos necesarios para el inventario.
     */
    @Override
    public List<Platillo> obtenerPlatillosConRecetaCompleta() {
        List<Platillo> listaPlatillos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombreCategoria FROM platillo p " +
                     "INNER JOIN categoria c ON p.idCategoria = c.id " +
                     "WHERE p.estado = 1 ORDER BY p.nombre ASC";
        
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Platillo platillo = new Platillo();
                platillo.setId(rs.getInt("id"));
                platillo.setNombre(rs.getString("nombre"));
                platillo.setPrecio(rs.getDouble("precio"));
                platillo.setEstado(rs.getBoolean("estado"));
                platillo.setCostoProduccion(rs.getDouble("costoProduccion"));
                platillo.setStockActual(rs.getInt("fabricables"));
                platillo.setTipoProducto(TipoProducto.PLATILLO);
                
                // Cargamos la categoría correctamente (no null)
                Categoria cat = new Categoria();
                cat.setId(rs.getInt("idCategoria"));
                cat.setNombreCategoria(rs.getString("nombreCategoria"));
                platillo.setCategoria(cat);

                // Cargamos ingredientes con el nuevo método que trae UnidadMedida
                platillo.setIngrediente(obtenerIngredientesConUnidadMedida(conn, platillo.getId()));
                
                listaPlatillos.add(platillo);
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerPlatillosConRecetaCompleta: " + e.getMessage());
        }
        return listaPlatillos;
    }

    private List<DetallePlatillo> obtenerIngredientesConUnidadMedida(Connection conn, int idPlatillo) throws SQLException {
        List<DetallePlatillo> ingredientes = new ArrayList<>();
        String sql = "SELECT d.*, p.nombre, p.stockActual, p.unidadMedida, p.cantidad, p.precioCompra FROM detalle_platillo d " +
                     "INNER JOIN producto p ON d.idProducto = p.id WHERE d.idPlatillo = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPlatillo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetallePlatillo detalle = new DetallePlatillo();
                    detalle.setId(rs.getInt("id"));
                    detalle.setCantidadIngrediente(rs.getDouble("cantidadIngrediente"));

                    Producto prod = new Producto();
                    prod.setId(rs.getInt("idProducto"));
                    prod.setNombre(rs.getString("nombre"));
                    prod.setStockActual(rs.getInt("stockActual"));
                    
                    // EXTRAEMOS LA UNIDAD DE MEDIDA (Lo que necesitas para comparar)
                    String um = rs.getString("unidadMedida");
                    if (um != null) {
                        prod.setUnidadMedida(UnidadMedida.valueOf(um));
                    }
                    prod.setCantidad(rs.getDouble("cantidad"));
                    prod.setPrecioCompra(rs.getDouble("precioCompra"));

                    detalle.setProducto(prod);
                    ingredientes.add(detalle);
                }
            }
        }
        return ingredientes;
    }
}
