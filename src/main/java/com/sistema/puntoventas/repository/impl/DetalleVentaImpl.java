package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.repository.IDetalleVenta;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.detalleVenta;
import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.ventaAplicacion;


public class DetalleVentaImpl implements IDetalleVenta {

    String url = "jdbc:sqlite:DBventasInventario.db";
    
    public List<detalleVenta> obtenerDetalleVentasporIdVenta(int id) {  //id de la venta
        List<detalleVenta> listaDetalleVenta = new ArrayList<>();
        String sql = "SELECT * FROM detalle_venta WHERE idVenta = ?";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    detalleVenta detalleVenta = new detalleVenta();
                    detalleVenta.setIdDetalle(rs.getInt("idDetalle"));
                    detalleVenta.setIdVenta(rs.getInt("idVenta"));
                    detalleVenta.setIdProducto(rs.getInt("idProducto"));
                    detalleVenta.setIdPlatillo(rs.getInt("idPlatillo"));
                    System.out.println("detalleventa encontrado correctamente");
                    listaDetalleVenta.add(detalleVenta);
                    
                }
            }
            
            }catch (Exception e) {
                System.err.println("Error al obtener detalle venta: " + e.getMessage());
                
            }
        return listaDetalleVenta;
    }

    public List<String> obtenerInfoVentaDetalle(int id) { //idVenta

        List<String> listaInfoDetalleVenta = new ArrayList<>();

        String infoDetalleVenta = "";
        String sql = "SELECT dv.idDetalle, dv.idProducto, dv.idPlatillo, dv.idVenta, v.fechaHora, v.totalVenta, " +
                     "p.nombre AS prodNombre, p.precioVenta AS prodPrecio, pl.nombre AS platNombre, pl.precio AS platPrecio " +
                     "FROM detalle_venta dv " +
                     "JOIN venta v ON dv.idVenta = v.idVenta " +
                     "LEFT JOIN producto p ON p.id = dv.idProducto " +
                     "LEFT JOIN platillo pl ON pl.id = dv.idPlatillo " +
                     "WHERE v.idVenta = ?";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { // Changed from if to while to handle multiple results if the query was changed
                    String nombreItem = rs.getString("prodNombre") != null ? rs.getString("prodNombre") : rs.getString("platNombre");
                    double precioItem = rs.getString("prodNombre") != null ? rs.getDouble("prodPrecio") : rs.getDouble("platPrecio");

                    infoDetalleVenta = "ID Detalle: " + rs.getInt("idDetalle") +
                                       ", ID Venta: " + rs.getInt("idVenta") +
                                       ", ID Producto: " + rs.getInt("idProducto") +
                                       ", Nombre Ítem: " + nombreItem +
                                       ", Precio Venta: " + precioItem + 
                                       ", Fecha Venta: " + rs.getString("fechaHora") + 
                                       ", Total Venta: " + rs.getDouble("totalVenta"); 
                    listaInfoDetalleVenta.add(infoDetalleVenta);
                }
            }
           
            }catch (Exception e) {
                System.err.println("Error al obtener información del detalle de venta: " + e.getMessage());
                
            }
        return listaInfoDetalleVenta;

    }

    public List<ventaAplicacion> obtenerTodasLasVentas() {  //trae toda la info de ventas y producto asociados
        List<ventaAplicacion> listaVentas  = new ArrayList<>();
        Map<Integer,ventaAplicacion> mapVentas = new LinkedHashMap<>();
        String sql = "SELECT v.idVenta, v.fechaHora, v.totalVenta, v.tipoPago, v.descripcion, " +
                     "p.nombre AS prodNombre, p.precioVenta AS prodPrecio, " +
                     "pl.nombre AS platNombre, pl.precio AS platPrecio " +
                     "FROM venta v " +
                     "JOIN detalle_venta dv ON v.idVenta = dv.idVenta " +
                     "LEFT JOIN producto p ON dv.idProducto = p.id " +
                     "LEFT JOIN platillo pl ON dv.idPlatillo = pl.id";

        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idVenta = rs.getInt("idVenta");
                    
                    
                    
                    if (!mapVentas.containsKey(idVenta)) { //si no contiene el id crea la venta app en el map
                       venta v = new venta();
                       v.setIdVenta(idVenta);
                       v.setFechaHora(rs.getString("fechaHora"));
                       v.setTotalVenta(rs.getDouble("totalVenta"));
                       v.setTipoPago(rs.getString("tipoPago"));
                       v.setDescripcion(rs.getString("descripcion"));
                        
                        ventaAplicacion ventaApp = new ventaAplicacion();
                        ventaApp.setVenta(v);
                        ventaApp.setDetalleVentas(new ArrayList<>());
                        ventaApp.setDetallePlatillos(new ArrayList<>());
                        mapVentas.put(idVenta, ventaApp);
                    }
                    
                    if (rs.getString("prodNombre") != null) {
                        Producto p = new Producto();
                        p.setNombre(rs.getString("prodNombre"));
                        p.setPrecioVenta(rs.getDouble("prodPrecio"));
                        mapVentas.get(idVenta).getDetalleVentas().add(p);
                    }
                    
                    if (rs.getString("platNombre") != null) {
                        Platillo pl = new Platillo();
                        pl.setNombre(rs.getString("platNombre"));
                        pl.setPrecio(rs.getDouble("platPrecio"));
                        mapVentas.get(idVenta).getDetallePlatillos().add(pl);
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
        }
        listaVentas.addAll(mapVentas.values()); // pasa los valores del map a la lista
        return listaVentas;

    }


    public List<ventaAplicacion> obtenerTodasLasVentasporFecha(String fecha) {  //trae toda la info de ventas y producto asociados
        List<ventaAplicacion> listaVentas  = new ArrayList<>();
        Map<Integer,ventaAplicacion> mapVentas = new LinkedHashMap<>();
        String sql = "SELECT v.idVenta, v.fechaHora, v.totalVenta, v.tipoPago, v.descripcion, " +
                     "p.nombre AS prodNombre, p.precioVenta AS prodPrecio, " +
                     "pl.nombre AS platNombre, pl.precio AS platPrecio " +
                     "FROM venta v " +
                     "JOIN detalle_venta dv ON v.idVenta = dv.idVenta " +
                     "LEFT JOIN producto p ON dv.idProducto = p.id " +
                     "LEFT JOIN platillo pl ON dv.idPlatillo = pl.id " +
                     "WHERE v.fechaHora LIKE ?";

        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            // ASIGNAR EL PARÁMETRO DE LA FECHA
            pstmt.setString(1, fecha);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idVenta = rs.getInt("idVenta");
                    
                    
                    
                    if (!mapVentas.containsKey(idVenta)) { //si no contiene el id crea la venta app en el map
                       venta v = new venta();
                       v.setIdVenta(idVenta);
                       v.setFechaHora(rs.getString("fechaHora"));
                       v.setTotalVenta(rs.getDouble("totalVenta"));
                       v.setTipoPago(rs.getString("tipoPago"));
                       v.setDescripcion(rs.getString("descripcion"));
                        
                        ventaAplicacion ventaApp = new ventaAplicacion();
                        ventaApp.setVenta(v);
                        ventaApp.setDetalleVentas(new ArrayList<>());
                        ventaApp.setDetallePlatillos(new ArrayList<>());
                        mapVentas.put(idVenta, ventaApp);
                    }
                    
                    if (rs.getString("prodNombre") != null) {
                        Producto p = new Producto();
                        p.setNombre(rs.getString("prodNombre"));
                        p.setPrecioVenta(rs.getDouble("prodPrecio"));
                        mapVentas.get(idVenta).getDetalleVentas().add(p);
                    }
                    
                    if (rs.getString("platNombre") != null) {
                        Platillo pl = new Platillo();
                        pl.setNombre(rs.getString("platNombre"));
                        pl.setPrecio(rs.getDouble("platPrecio"));
                        mapVentas.get(idVenta).getDetallePlatillos().add(pl);
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
        }
        listaVentas.addAll(mapVentas.values()); // pasa los valores del map a la lista
        return listaVentas;

    }

        




}
