package com.sistema.puntoventas.repository.impl;

import com.sistema.puntoventas.modelo.ResultadoConsulta;
import com.sistema.puntoventas.repository.IConsultaSQLRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConsultaSQLRepositoryImpl implements IConsultaSQLRepository {

    private static final String URL_BD = "jdbc:sqlite:DBventasInventario.db";

    @Override
    public ResultadoConsulta ejecutarSelect(String sql) {
        List<String> columnas = new ArrayList<>();
        List<List<String>> filas = new ArrayList<>();

        Properties config = new Properties();
        config.setProperty("open_mode", "1");

        try (Connection conn = DriverManager.getConnection(URL_BD, config);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int numCols = meta.getColumnCount();

            for (int i = 1; i <= numCols; i++) {
                columnas.add(meta.getColumnLabel(i));
            }

            while (rs.next()) {
                List<String> fila = new ArrayList<>();
                for (int i = 1; i <= numCols; i++) {
                    String val = rs.getString(i);
                    fila.add(val != null ? val : "");
                }
                filas.add(fila);
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar SQL: " + e.getMessage());
            System.err.println("SQL: " + sql);
        }

        return new ResultadoConsulta(columnas, filas);
    }
}
