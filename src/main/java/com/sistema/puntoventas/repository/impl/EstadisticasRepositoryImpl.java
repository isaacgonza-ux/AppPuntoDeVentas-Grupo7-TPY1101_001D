package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.RankingVendedoresDTO;
import com.sistema.puntoventas.modelo.moduloProducto.RankingProductosDTO;
import com.sistema.puntoventas.repository.IEstadisticasRepository;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.Categoria;
import com.sistema.puntoventas.modelo.moduloProducto.UnidadMedida;
import com.sistema.puntoventas.modelo.moduloProducto.TipoProducto;

public class EstadisticasRepositoryImpl implements IEstadisticasRepository {
    private static final String url = "jdbc:sqlite:DBventasInventario.db";

    public double obtenerIngresosTotales(String periodo) {
        if (periodo == null || periodo.trim().isEmpty()) {
            return 0.0;
        }


        String sql = "SELECT COALESCE(SUM(totalVenta), 0) AS total "
                + "FROM venta "
                + "WHERE fechaHora LIKE ? AND estado = 1";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + periodo.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total"); // Retornamos double limpio
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ingresos totales: " + e.getMessage());
        }

        return 0.0;
    }

    @Override
    public List<RankingProductosDTO> obtenerRankingProductos(int limite) {
        List<RankingProductosDTO> ranking = new ArrayList<>();

        String sql = "SELECT " +
                "p.nombre AS nombreProducto, " +
                "COUNT(*) AS totalCantidadVendida " +
                "FROM detalle_venta d " +
                "INNER JOIN venta v ON d.idVenta = v.idVenta " +
                "INNER JOIN producto p ON d.idProducto = p.id " +
                "WHERE v.estado = 1 " +
                "GROUP BY p.id, p.nombre " +
                "ORDER BY totalCantidadVendida DESC " +
                "LIMIT ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            
            System.out.println("DEBUG: Ejecutando query de ranking con límite: " + limite);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString("nombreProducto");
                    int cantidad = rs.getInt("totalCantidadVendida");

                    System.out.println("DEBUG RS: " + nombre + " - " + cantidad);
                    
                    // Construimos el DTO y lo agregamos a la lista
                    RankingProductosDTO productoDTO = new RankingProductosDTO(nombre, cantidad);
                    ranking.add(productoDTO);
                }
                System.out.println("Ranking de productos obtenido: " + ranking.size() + " registros");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ranking de productos: " + e.getMessage());
            System.err.println("SQL: " + sql);
            e.printStackTrace();
        }

        return ranking;
    }

    @Override
    public int obtenerVentasUsuario(int idUsuario) {
        int cantidadVentas = 0;
        String sql = "SELECT COUNT(idVenta) AS cantidad, " +
                     "COALESCE(SUM(totalVenta),0) AS TotalVentas " +
                    "FROM venta " +
                    "WHERE idUsuario = ? AND estado = 1";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);

            try(ResultSet rs = pstmt.executeQuery()){
                cantidadVentas = rs.getInt("TotalVentas");
            }
        }catch (SQLException e) {
            System.err.println("Error al obtener ventas por usuario: " + e.getMessage());
        }

        return cantidadVentas;
    }

    @Override
    public int prepararDatosParaIA() {
        int filasExportadas = 0;
        String rutaArchivo = "datos_ventas.csv";
        String sql = "SELECT DATE(fechaHora) AS ds," +
                      "SUM(totalVenta) AS y " +
                      "FROM venta " +
                      "WHERE estado = 1 " +
                      "GROUP BY DATE(fechaHora) " +
                      "ORDER BY DATE(fechaHora) ASC";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
             FileWriter fw = new FileWriter(rutaArchivo);
             PrintWriter pw = new PrintWriter(fw)){
            pw.println("ds,y");

            while(rs.next()){
                String fecha = rs.getString("ds");
                double total = rs.getInt("y");
                pw.println(fecha + "," + total);
                filasExportadas++;
            }

            System.out.println("Éxito: Archivo CSV generado con " + filasExportadas + " días de historial.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al preparar datos para IA: " + e.getMessage(), e);
        }catch (Exception e){
            throw new RuntimeException("Error al escribir el archivo CSV: " + e.getMessage(), e);
        }
        return filasExportadas;
    }

    public int prepararDatosStockParaIA() {
        int filasExportadas = 0;
        String rutaArchivo = "datos_stock.csv";

        // Extraemos: Fecha, Cantidad Vendida, ID del Producto
        String sql = "SELECT idProducto, ds, SUM(cantidad_total) AS y " +
                "FROM (" +

                "    SELECT " +
                "        dv.idProducto AS idProducto, " +
                "        DATE(v.fechaHora) AS ds, " +
                "        1 AS cantidad_total " +
                "    FROM detalle_venta dv " +
                "    INNER JOIN venta v ON dv.idVenta = v.idVenta " +
                "    WHERE v.estado = 1 AND dv.idProducto IS NOT NULL " +

                "    UNION ALL " +

                // Consumo indirecto
                "    SELECT " +
                "        dp.idProducto AS idProducto, " +
                "        DATE(v.fechaHora) AS ds, " +
                "        dp.cantidadIngrediente AS cantidad_total " +
                "    FROM detalle_venta dv " +
                "    INNER JOIN venta v ON dv.idVenta = v.idVenta " +
                "    INNER JOIN detalle_platillo dp ON dp.idPlatillo = dv.idPlatillo " +
                "    WHERE v.estado = 1 AND dv.idPlatillo IS NOT NULL" +
                ") AS consumo_global " +
                "WHERE idProducto IS NOT NULL " +
                "GROUP BY idProducto, ds " +
                "ORDER BY idProducto, ds ASC";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
             FileWriter fw = new FileWriter(rutaArchivo);
             PrintWriter pw = new PrintWriter(fw)){

            pw.println("id_producto,ds,y"); // Nueva cabecera

            while(rs.next()){
                int idProducto = rs.getInt("idProducto");
                String fecha = rs.getString("ds");
                double cantidad = rs.getDouble("y");

                pw.println(idProducto + "," + fecha + "," + cantidad);
                filasExportadas++;
            }
            System.out.println("Éxito: Archivo de stock generado con " + filasExportadas + " registros.");

        } catch (Exception e) {
            throw new RuntimeException("Error al preparar datos de stock para IA: " + e.getMessage(), e);
        }
        return filasExportadas;
    }

    public double obtenerPerdidasTotales(String periodo) {
        if (periodo == null || periodo.trim().isEmpty()) {
            return 0.0;
        }

        // Unimos historial y producto, y aplicamos el mismo filtro de fecha
        String sql = "SELECT COALESCE(SUM(ABS(h.cantidad) * p.precioCompra), 0) AS total "
                + "FROM historial_inventario h "
                + "INNER JOIN producto p ON p.id = h.idProducto "
                + "WHERE h.tipoMovimiento IN ('MERMA', 'AJUSTE') "
                + "AND h.fecha LIKE ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + periodo.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener perdidas totales: " + e.getMessage());
        }

        return 0.0;
    }


    public List<RankingVendedoresDTO> obtenerRankingVendedores(int limite) {
        List<RankingVendedoresDTO> lista = new ArrayList<>();

        String sql = "SELECT u.nombre || ' ' || u.apellido AS nombreCompleto, COUNT(v.idVenta) AS total " +
                "FROM usuario u " +
                "INNER JOIN venta v ON u.id = v.idUsuario " +
                "WHERE v.estado = 1 " + // Solo ventas válidas
                "GROUP BY u.id " +
                "ORDER BY total DESC " +
                "LIMIT ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new RankingVendedoresDTO(
                        rs.getString("nombreCompleto"),
                        rs.getInt("total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error en ranking vendedores: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<String> obtenerUltimasActividades(int limite) {
        List<String> actividades = new ArrayList<>();

        String sql = "SELECT fechaOrdenada, modulo, detalle FROM ("
                + "SELECT v.fechaHora AS fechaOrdenada, "
                + "'Venta' AS modulo, "
                + "'Venta #" + "' || v.idVenta || ' por ' || printf('$ %.2f', COALESCE(v.totalVenta, 0)) "
                + "|| CASE WHEN u.nombre IS NOT NULL THEN ' - ' || u.nombre || ' ' || u.apellido ELSE '' END AS detalle "
                + "FROM venta v "
                + "LEFT JOIN usuario u ON u.id = v.idUsuario "
                + "WHERE v.estado = 1 "
                + "UNION ALL "
                + "SELECT h.fecha AS fechaOrdenada, "
                + "'Inventario' AS modulo, "
                + "CASE h.tipoMovimiento "
                + "WHEN 'ENTRADA' THEN 'Entrada de inventario' "
                + "WHEN 'SALIDA_VENTA' THEN 'Salida por venta' "
                + "WHEN 'MERMA' THEN 'Merma registrada' "
                + "WHEN 'AJUSTE' THEN 'Ajuste de stock' "
                + "ELSE h.tipoMovimiento END "
                + "|| ' - ' || p.nombre || ' x' || h.cantidad "
                + "|| CASE WHEN h.motivo IS NOT NULL AND trim(h.motivo) <> '' THEN ' (' || h.motivo || ')' ELSE '' END AS detalle "
                + "FROM historial_inventario h "
                + "INNER JOIN producto p ON p.id = h.idProducto) "
                + "ORDER BY datetime(fechaOrdenada) DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String fecha = rs.getString("fechaOrdenada");
                    String modulo = rs.getString("modulo");
                    String detalle = rs.getString("detalle");
                    actividades.add("[" + fecha + "] " + modulo + ": " + detalle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener actividades recientes: " + e.getMessage());
        }

        return actividades;
    }

    /**
     * Obtiene múltiples productos en una sola consulta batch.
     * Utiliza SQL IN para evitar consultas N+1.
     * 
     * @param ids Lista de IDs de productos
     * @return Map<idProducto, Producto> para acceso rápido por ID
     */
    public Map<Integer, Producto> obtenerProductosPorIds(List<Integer> ids) {
        Map<Integer, Producto> resultado = new HashMap<>();

        if (ids == null || ids.isEmpty()) {
            return resultado;
        }

        // Construir cláusula IN (?, ?, ...) dinámicamente
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            placeholders.append("?");
            if (i < ids.size() - 1) placeholders.append(",");
        }

        String sql = "SELECT id, nombre, precioVenta, precioCompra, stockActual, stockMinimo, " +
                     "fechaVenc,activo, unidadMedida, cantidad, tipoProducto " +
                     "FROM producto WHERE id IN (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignar IDs a los placeholders
            for (int i = 0; i < ids.size(); i++) {
                pstmt.setInt(i + 1, ids.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecioVenta(rs.getDouble("precioVenta"));
                    producto.setPrecioCompra(rs.getDouble("precioCompra"));
                    producto.setStockActual(rs.getInt("stockActual"));
                    producto.setStockMinimo(rs.getInt("stockMinimo"));
                    producto.setFechaVenc(rs.getString("fechaVenc"));
                    producto.setActivo(rs.getBoolean("activo"));
                    producto.setUnidadMedida(mapUnidadMedida(rs.getString("unidadMedida")));
                    producto.setCantidad(rs.getDouble("cantidad"));
                    producto.setTipoProducto(mapTipoProducto(rs.getString("tipoProducto")));

                    resultado.put(producto.getId(), producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos por batch: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Mapea string de UnidadMedida a enum
     */
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

    /**
     * Mapea string de TipoProducto a enum
     */
    private TipoProducto mapTipoProducto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return TipoProducto.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
