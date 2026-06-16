package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.repository.moduloProductos.ICategoriaRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.moduloProductos.IstockRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements IProductoRepository, ICategoriaRepository, IstockRepository {


    private static final String SQL_INSERT =
            "INSERT INTO producto (nombre, precioCompra, precioVenta, idcategoria, " +
                    "fechaVenc, stockActual, stockMinimo, imagen, unidadMedida, cantidad, tipoProducto, cantidadDefault) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String url = "jdbc:sqlite:DBventasInventario.db";


    @Override
    public boolean registrarProducto(Producto producto) {
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(SQL_INSERT)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecioCompra());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getCategoria().getId());
            pstmt.setString(5, producto.getFechaVenc());
            pstmt.setInt(6, producto.getStockActual());
            pstmt.setInt(7, producto.getStockMinimo());
            pstmt.setString(8, producto.getImagen());
            pstmt.setString(9, obtenerUnidadMedida(producto));
            pstmt.setDouble(10, producto.getCantidad());
            pstmt.setString(11, producto.getTipoProducto().name());
            pstmt.setDouble(12, producto.getCantidad()); // cantidadDefault toma el valor inicial de cantidad
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar producto: " + e.getMessage());
            return false;
        }
    }



    @Override
    public List<Producto> obtenerProductos( ) {
        List<Producto> listaProductos = new ArrayList<>();

        String sql = "SELECT p.*,c.nombreCategoria " +
                     "FROM producto p " +
                     "INNER JOIN categoria c ON p.idCategoria = c.id " +
                     "ORDER BY nombre ASC";
        try(Connection conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql)){
            // Recorremos los resultados fila por fila
            while (rs.next()) {
                Producto producto = new Producto();

                // Extraemos la información de la base de datos y la metemos en el objeto
                producto.setId(rs.getInt(1));
                producto.setNombre(rs.getString(2));
                producto.setPrecioCompra(rs.getDouble(3));
                producto.setPrecioVenta(rs.getDouble(4));
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("idCategoria"));
                categoria.setNombreCategoria(rs.getString("nombreCategoria"));
                producto.setCategoria(categoria);
                producto.setFechaVenc(rs.getString(6));
                producto.setStockActual(rs.getInt(7));
                producto.setStockMinimo(rs.getInt(8));
                producto.setImagen(rs.getString(9));
                producto.setUnidadMedida(mapUnidadMedida(rs.getString(10)));
                producto.setCantidad(rs.getDouble(11));
                producto.setTipoProducto(TipoProducto.valueOf(rs.getString(12)));
                producto.setCantidadDefault(rs.getDouble(13));
                // Agregamos el producto armado a nuestra lista
                listaProductos.add(producto);
                System.out.println(listaProductos);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());

        }
        return listaProductos;
    }

    @Override
    public List<Producto> obtenerProductoPorNombre(String nombre) {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre LIKE ?";
        try(Connection connect = DriverManager.getConnection(url);
            PreparedStatement ps  = connect.prepareStatement(sql)){
            ps.setString(1,"%" + nombre + "%");
            try(ResultSet rs =  ps.executeQuery()){
                // Recorremos los resultados fila por fila
                while (rs.next()) {
                    Producto producto = new Producto();

                    // Extraemos la información de la base de datos y la metemos en el objeto
                    producto.setId(rs.getInt(1));
                    producto.setNombre(rs.getString(2));
                    producto.setPrecioCompra(rs.getDouble(3));
                    producto.setPrecioVenta(rs.getDouble(4));
                    producto.setCategoria(mapCategoria(rs.getString(5)));
                    producto.setFechaVenc(rs.getString(6));
                    producto.setStockActual(rs.getInt(7));
                    producto.setStockMinimo(rs.getInt(8));
                    producto.setImagen(rs.getString(9));
                    producto.setUnidadMedida(mapUnidadMedida(rs.getString(10)));
                    producto.setCantidad(rs.getDouble(11));
                    producto.setTipoProducto(TipoProducto.valueOf(rs.getString(12)));
                    producto.setCantidadDefault(rs.getDouble(13));

                    // Agregamos el producto armado a nuestra lista
                    listaProductos.add(producto);
                    System.out.println(listaProductos);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());

        }
        return listaProductos;
    }

    @Override
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, precioCompra = ?, precioVenta = ?, idcategoria = ?, " +
                "fechaVenc = ?, stockActual = ?, stockMinimo = ?, imagen = ?, unidadMedida = ?, cantidad = ?, tipoProducto = ?, cantidadDefault = ? WHERE id = ?";

        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecioCompra());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getCategoria().getId());
            pstmt.setString(5, producto.getFechaVenc());
            pstmt.setInt(6, producto.getStockActual());
            pstmt.setInt(7, producto.getStockMinimo());
            pstmt.setString(8, producto.getImagen());
            pstmt.setString(9, obtenerUnidadMedida(producto));
            pstmt.setDouble(10, producto.getCantidad());
            pstmt.setString(11, producto.getTipoProducto().name());
            pstmt.setDouble(12, producto.getCantidadDefault());
            pstmt.setInt(13, producto.getId());
            pstmt.executeUpdate();
            System.out.println("producto actualizado correctamente");


            return true;
        } catch (SQLException e) {

            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            // execute the delete statement
            pstmt.executeUpdate();
            System.out.println("producto eliminado correctamente");
            return true;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean desactivarProducto(int id) {
        String sql = "UPDATE producto SET estado = 'INACTIVO' WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar el producto: " + e.getMessage());
        }

        return false;
    }

    @Override
    public Producto obtenerProductoPorId(int id) {
        Producto producto = null;
        String sql = "SELECT * FROM producto WHERE id = ?";

        try(Connection connect = DriverManager.getConnection(url);
            PreparedStatement ps  = connect.prepareStatement(sql)){
            ps.setInt(1, id);

            try(ResultSet rs =  ps.executeQuery()){
                // Recorremos los resultados fila por fila
                while (rs.next()) {
                    producto  = new Producto();


                    // Extraemos la información de la base de datos y la metemos en el objeto
                    producto.setId(rs.getInt(1));
                    producto.setNombre(rs.getString(2));
                    producto.setPrecioCompra(rs.getDouble(3));
                    producto.setPrecioVenta(rs.getDouble(4));
                    producto.setCategoria(mapCategoria(rs.getString(5)));
                    producto.setFechaVenc(rs.getString(6));
                    producto.setStockActual(rs.getInt(7));
                    producto.setStockMinimo(rs.getInt(8));
                    producto.setImagen(rs.getString(9));
                    producto.setUnidadMedida(mapUnidadMedida(rs.getString(10)));
                    producto.setCantidad(rs.getDouble(11));
                    producto.setTipoProducto(TipoProducto.valueOf(rs.getString(12)));
                    producto.setCantidadDefault(rs.getDouble(13));

                    // Agregamos el producto armado a nuestra lista
                    System.out.println("producto encontrado correctamente");
                    System.out.println(producto);
                    // System.out.println(listaProductos); // This line was likely a debug print and can be removed or commented out.
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());

        }
        return producto;

    }

    @Override
    public boolean existeNombre(String nombre, int id) {

        String sql = "SELECT COUNT(*) FROM producto WHERE nombre = ? AND id != ?";

        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, id);

            var rs = pstmt.executeQuery();
            if (rs.next()) {
                // Si el conteo es mayor a 0, significa que sí existe un duplicado
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al validar el nombre: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Producto> obtenerStockCritico() {
        List<Producto> productosStockCritico = new ArrayList<>();
        String sql = "SELECT p.*, c.nombreCategoria FROM producto p INNER JOIN categoria c ON p.idCategoria = c.id WHERE p.stockActual <= p.stockMinimo";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecioCompra(rs.getDouble("precioCompra"));
                producto.setPrecioVenta(rs.getDouble("precioVenta"));
                
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("idCategoria"));
                categoria.setNombreCategoria(rs.getString("nombreCategoria"));
                producto.setCategoria(categoria);
                
                producto.setFechaVenc(rs.getString("fechaVenc"));
                producto.setStockActual(rs.getInt("stockActual"));
                producto.setStockMinimo(rs.getInt("stockMinimo"));
                producto.setImagen(rs.getString("imagen"));
                producto.setUnidadMedida(mapUnidadMedida(rs.getString("unidadMedida")));
                producto.setCantidad(rs.getDouble("cantidad"));
                producto.setTipoProducto(TipoProducto.valueOf(rs.getString("tipoProducto")));
                producto.setCantidadDefault(rs.getDouble("cantidadDefault"));
                productosStockCritico.add(producto);
            }
            if (productosStockCritico.isEmpty()) {
                System.out.println("No se encontraron productos en stock crítico.");
            }else {
                System.out.println("hay "+ productosStockCritico.size()+" productos con stock critico");

            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos en stock crítico: " + e.getMessage());
        }

        return productosStockCritico;
    }

    @Override
    public boolean registrarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getNombreCategoria() == null || categoria.getNombreCategoria().trim().isEmpty()) {
            return false;
        }

        String nombre = categoria.getNombreCategoria().trim();
        String sqlExiste = "SELECT 1 FROM categoria WHERE nombreCategoria = ? LIMIT 1";
        String sqlInsert = "INSERT INTO categoria (nombreCategoria, descripcion, activa) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmtExiste = conn.prepareStatement(sqlExiste);
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {

            pstmtExiste.setString(1, nombre);
            try (ResultSet rs = pstmtExiste.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }

            pstmtInsert.setString(1, nombre);
            pstmtInsert.setString(2, categoria.getDescripcion());
            pstmtInsert.setBoolean(3, categoria.isActiva());

            return pstmtInsert.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeCategoria(String nombre) {
        String sql = "SELECT 1 FROM categoria WHERE nombreCategoria = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar categoría: " + e.getMessage());
            return false;
        }
    }


    @Override
    public List<Categoria> obtenerCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY nombreCategoria ASC";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombreCategoria(rs.getString("nombreCategoria"));
                categoria.setDescripcion(rs.getString("descripcion"));
                categoria.setActiva(rs.getBoolean("activa"));
                categorias.add(categoria);
            }
            if (categorias.isEmpty()) {
                System.out.println("No se encontraron categorías.");
            }else {
                System.out.println("hay "+ categorias.size()+" categorías");

            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }

        return categorias;
    }

    @Override
    public boolean actualizarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE categoria SET nombreCategoria = ?, descripcion = ?, activa = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria.getNombreCategoria());
            pstmt.setString(2, categoria.getDescripcion());
            pstmt.setBoolean(3, categoria.isActiva());
            pstmt.setInt(4, categoria.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoria: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarCategoria(int id) {
        if (id <= 0) {
            return false;
        }

        String sqlBuscarNombre = "SELECT nombreCategoria FROM categoria WHERE id = ?";
        String sqlAsociada = "SELECT COUNT(*) AS total FROM producto WHERE id = ?";
        String sqlEliminar = "DELETE FROM categoria WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmtBuscar = conn.prepareStatement(sqlBuscarNombre);
             PreparedStatement pstmtAsociada = conn.prepareStatement(sqlAsociada);
             PreparedStatement pstmtEliminar = conn.prepareStatement(sqlEliminar)) {

            pstmtBuscar.setInt(1, id);
            String nombreCategoria;
            try (ResultSet rs = pstmtBuscar.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                nombreCategoria = rs.getString("nombreCategoria");
            }

            pstmtAsociada.setString(1, nombreCategoria);
            try (ResultSet rs = pstmtAsociada.executeQuery()) {
                if (rs.next() && rs.getInt("total") > 0) {
                    return false;
                }
            }

            pstmtEliminar.setInt(1, id);
            return pstmtEliminar.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Producto> buscarPorTipoProducto(TipoProducto tipoProducto) {
        List<Producto> productosPorTipo = new ArrayList<>();
        String sql = "SELECT p.*, c.nombreCategoria FROM producto p INNER JOIN categoria c ON p.idCategoria = c.id WHERE p.tipoProducto = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipoProducto.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecioCompra(rs.getDouble("precioCompra"));
                    producto.setPrecioVenta(rs.getDouble("precioVenta"));
                    
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("idCategoria"));
                    categoria.setNombreCategoria(rs.getString("nombreCategoria"));
                    producto.setCategoria(categoria);
                    
                    producto.setFechaVenc(rs.getString("fechaVenc"));
                    producto.setStockActual(rs.getInt("stockActual"));
                    producto.setStockMinimo(rs.getInt("stockMinimo"));
                    producto.setImagen(rs.getString("imagen"));
                    producto.setUnidadMedida(mapUnidadMedida(rs.getString("unidadMedida")));
                    producto.setCantidad(rs.getDouble("cantidad"));
                    producto.setTipoProducto(TipoProducto.valueOf(rs.getString("tipoProducto")));
                    producto.setCantidadDefault(rs.getDouble("cantidadDefault"));
                    productosPorTipo.add(producto);
                }
            }
            if (productosPorTipo.isEmpty()) {
                System.out.println("No se encontraron productos del tipo " + tipoProducto);
            }else {
                System.out.println("hay "+ productosPorTipo.size()+" productos del tipo " + tipoProducto);

            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por tipo: " + e.getMessage());
        }

        return productosPorTipo;

    }

    @Override
    public boolean estaAsociadoVentaOPlatillo(int id) {
        String sql = "SELECT COUNT(*) AS total FROM detalle_venta WHERE idProducto = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0; // Si hay más de 0, está asociado
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar dependencias del producto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int obtenerStockActual(int id) {
        if (id <= 0) {
            return -1;
        }

        String sql = "SELECT stockActual FROM producto WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stockActual");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener stock actual: " + e.getMessage());
        }

        return -1;
    }


    private String obtenerNombreCategoria(Producto producto) {
        return producto.getCategoria() != null ? producto.getCategoria().getNombreCategoria() : null;
    }

    private String obtenerUnidadMedida(Producto producto) {
        return producto.getUnidadMedida() != null ? producto.getUnidadMedida().name() : null;
    }

    private Categoria mapCategoria(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            return null;
        }

        Categoria categoria = new Categoria();
        categoria.setNombreCategoria(nombreCategoria);
        return categoria;
    }

    private UnidadMedida mapUnidadMedida(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return UnidadMedida.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
