
package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.moduloProducto.Producto;
import com.sistema.puntoventas.modelo.moduloProducto.Platillo;
import com.sistema.puntoventas.modelo.venta;
import com.sistema.puntoventas.modelo.ventaAplicacion;
import com.sistema.puntoventas.repository.IventaRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class VentaRepositoryimpl implements IventaRepository {

    private static final String url = "jdbc:sqlite:DBventasInventario.db";
 
    @Override
    public Boolean registrarVentaCompleta(venta venta, List<Integer> idProductos, List<Integer> idPlatillos) {
        String sqlVenta = "INSERT INTO venta (fechaHora, idUsuario, totalVenta, tipoPago, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (idVenta, idProducto, idPlatillo) VALUES (?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
            
                pstmt.setString(1, venta.getFechaHora());
                pstmt.setInt(2, venta.getIdUsuario());
                pstmt.setDouble(3, venta.getTotalVenta());
                pstmt.setString(4, venta.getTipoPago());
                pstmt.setString(5, venta.getDescripcion());
                pstmt.setInt(6, 1);
                pstmt.executeUpdate();

                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int idVenta = keys.getInt(1);
                        for (Integer idProducto : idProductos) {
                            pstmtDetalle.setInt(1, idVenta);
                            pstmtDetalle.setInt(2, idProducto);
                            pstmtDetalle.setNull(3, java.sql.Types.INTEGER);
                            pstmtDetalle.executeUpdate();
                        }
                        for (Integer idPlatillo : idPlatillos) {
                            pstmtDetalle.setInt(1, idVenta);
                            pstmtDetalle.setNull(2, java.sql.Types.INTEGER);
                            pstmtDetalle.setInt(3, idPlatillo);
                            pstmtDetalle.executeUpdate();
                        }
                    }
                }

                conn.commit();
                System.out.println("Venta registrada correctamente");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }catch (SQLException e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarTabladeVentaCompleta(ArrayList<ventaAplicacion> tablaVentaAplicacion) {
        String sqlVenta = "INSERT INTO venta (fechaHora, idUsuario, totalVenta, tipoPago, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (idVenta, idProducto, idPlatillo) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {

                for (ventaAplicacion va : tablaVentaAplicacion) {
                    venta v = va.getVenta();
                    pstmtVenta.setString(1, v.getFechaHora());
                    pstmtVenta.setInt(2, v.getIdUsuario());
                    pstmtVenta.setDouble(3, v.getTotalVenta());
                    pstmtVenta.setString(4, v.getTipoPago());
                    pstmtVenta.setString(5, v.getDescripcion());
                    pstmtVenta.setInt(6, 1); // Estado activa

                    pstmtVenta.executeUpdate();

                    try (ResultSet generatedKeys = pstmtVenta.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idVenta = generatedKeys.getInt(1);
                            for (Producto producto : va.getDetalleVentas()) {
                                pstmtDetalle.setInt(1, idVenta);
                                pstmtDetalle.setInt(2, producto.getId());
                                pstmtDetalle.setNull(3, java.sql.Types.INTEGER);
                                pstmtDetalle.addBatch();
                            }
                            for (Platillo platillo : va.getDetallePlatillos()) {
                                pstmtDetalle.setInt(1, idVenta);
                                pstmtDetalle.setNull(2, java.sql.Types.INTEGER);
                                pstmtDetalle.setInt(3, platillo.getId());
                                pstmtDetalle.addBatch();
                            }
                            pstmtDetalle.executeBatch();
                        }
                    }
                }
                conn.commit();
                System.out.println("Tabla de ventas registrada exitosamente, guardada en BD.");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error en la transacción de ventas: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarVenta(venta venta) {
        String sql = "UPDATE venta SET fechaHora = ?, idUsuario = ?, totalVenta = ? , estado = ? WHERE idVenta = ?";
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, venta.getFechaHora());
            pstmt.setInt(2, venta.getIdUsuario());
            pstmt.setDouble(3, venta.getTotalVenta());
            pstmt.setInt(4, 1); // 1 significa que la venta está activa
            pstmt.setInt(5, venta.getIdVenta());
            pstmt.executeUpdate();
            System.out.println("Venta actualizada correctamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public venta obtenerVentaPorId(int id) {
        venta v = new venta();
        String sql = "SELECT * FROM venta WHERE idVenta = ?";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    v.setIdVenta(rs.getInt("idVenta"));
                    v.setFechaHora(rs.getString("fechaHora"));
                    v.setIdUsuario(rs.getInt("idUsuario"));
                    v.setTotalVenta(rs.getDouble("totalVenta"));
                    v.setTipoPago(rs.getString("tipoPago"));
                    v.setDescripcion(rs.getString("descripcion"));
                    
                    if (rs.getInt("estado") == 1) {
                        v.setEstado(true);
                    } else {
                        v.setEstado(false);
                    }
                    System.out.println("Venta encontrada correctamente");
                    System.out.println(v.toString());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener venta: " + e.getMessage());
        }
        return v;
    }

    @Override
    public List<venta> obtenerVentas() {
        List<venta> listaVentas = new ArrayList<>();
        String sql = "SELECT * FROM venta";
        try (Connection conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                venta v = new venta();
                v.setIdVenta(rs.getInt("idVenta"));
                v.setFechaHora(rs.getString("fechaHora"));
                v.setIdUsuario(rs.getInt("idUsuario"));
                v.setTotalVenta(rs.getDouble("totalVenta"));
                v.setTipoPago(rs.getString("tipoPago"));
                v.setDescripcion(rs.getString("descripcion"));

                if (rs.getInt("estado") == 1) {
                    v.setEstado(true);
                } else {
                    v.setEstado(false);
                }
                listaVentas.add(v);
                System.out.println(listaVentas);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        return listaVentas;
    }

    @Override
    public void anularVenta(int id) {
        String sql = "UPDATE venta SET estado = 0 WHERE idVenta = ?";
        
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Venta anulada correctamente");
        } catch (SQLException e) {
            System.err.println("Error al anular venta: " + e.getMessage());
        }
    }

    @Override
    public void activarVenta(int id) {
        String sql = "UPDATE venta SET estado = 1 WHERE idVenta = ?";
        
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Venta activada correctamente");
        } catch (SQLException e) {
            System.err.println("Error al anular venta: " + e.getMessage());
        }
    }

    @Override
    public List<String> obtenerTodasLasFechas() { 
        List<String> listaFechas = new ArrayList<>();
        String sql = "SELECT fechaHora FROM venta";

        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
                try (var rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        listaFechas.add(rs.getString("fechaHora"));
                    }
                }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las fechas: " + e.getMessage());
        }
        return listaFechas;
    }


}
