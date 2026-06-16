package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.BalanceFinancieroDTO;
import com.sistema.puntoventas.modelo.PrediccionStockDTO;
import com.sistema.puntoventas.modelo.RankingVendedoresDTO;
import com.sistema.puntoventas.modelo.moduloProducto.RankingProductosDTO;
import com.sistema.puntoventas.repository.IEstadisticasRepository;
import com.sistema.puntoventas.repository.moduloProductos.IProductoRepository;
import com.sistema.puntoventas.repository.impl.AuditoriaRepositoryImpl;
import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.moduloProducto.Producto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticaService {

    private final IEstadisticasRepository estadisticasRepository;
    private final IProductoRepository productoRepository;
    private final AuditoriaRepositoryImpl auditoriaRepository;

    public EstadisticaService(IEstadisticasRepository estadisticasRepository, IProductoRepository productoRepository) {
        this.estadisticasRepository = estadisticasRepository;
        this.productoRepository = productoRepository;
        this.auditoriaRepository = new AuditoriaRepositoryImpl();
    }


    public double obtenerIngresosTotales(String periodo) {
        return estadisticasRepository.obtenerIngresosTotales(periodo);
    }

    public List<RankingProductosDTO> obtenerRankingProductos(int limite){
        if(limite <= 0){
            System.err.println("El límite debe ser mayor cero");
            return new ArrayList<>();
        }

        if(limite > 50){
            limite = 50; //ponemos limite para no colapsar la memoria
        }

        return estadisticasRepository.obtenerRankingProductos(limite);
    }

    public double obtenerPerdidasTotales(String periodo){

        if(estadisticasRepository.obtenerPerdidasTotales(periodo) == 0){
            System.out.println("No se han registrado pérdidas en el periodo " + periodo);
        }

        if(estadisticasRepository.obtenerPerdidasTotales(periodo) < 0){
            System.err.println("Error: Las pérdidas no pueden ser negativas. Revisa los datos.");
        }


        return estadisticasRepository.obtenerPerdidasTotales(periodo);
    }


    public BalanceFinancieroDTO obtenerBalance(String periodo){
        if(periodo == null || periodo.trim().isEmpty()){
            return new BalanceFinancieroDTO(periodo, 0.0, 0.0, 0.0);
        }

       double  perdida = estadisticasRepository.obtenerPerdidasTotales(periodo);
       double ingreso = estadisticasRepository.obtenerIngresosTotales(periodo);
       double balance = (ingreso - perdida) ;


        return new BalanceFinancieroDTO(periodo, ingreso, perdida, balance);
    }



    public int obtenerVentasPorUsuarios(int id){
        if(id == 0 || id < 0){
            System.err.println("ID de usuario no valido");
            return 0;
        }

        int ventas = estadisticasRepository.obtenerVentasUsuario(id);
        return ventas;
    }

    public List<RankingVendedoresDTO> obtenerRankingVendedores(int limite) {
        if (limite <= 0) return new ArrayList<>();
        return estadisticasRepository.obtenerRankingVendedores(limite);
    }

    public List<String> obtenerUltimasActividades(int limite) {
        if (limite <= 0) {
            return new ArrayList<>();
        }
        if (limite > 50) {
            limite = 50;
        }
        // Combinar actividades existentes (ventas + inventario) con auditoría general
        List<String> res = new ArrayList<>();
        try {
            // primero, obtener las actividades ya implementadas
            List<String> actividades = estadisticasRepository.obtenerUltimasActividades(limite);
            if (actividades != null) res.addAll(actividades);

            // luego, obtener eventos de auditoría y convertir a String
            List<AuditoriaEvento> eventos = auditoriaRepository.obtenerUltimosEventos(limite);
            if (eventos != null) {
                for (AuditoriaEvento ev : eventos) {
                    String detalle = String.format("[%s] %s: %s %s", ev.getFecha(), ev.getModulo(), ev.getAccion(), ev.getDetalle() == null ? "" : ev.getDetalle());
                    res.add(detalle);
                }
            }

            // ordenar por fecha descendente si tenemos mezcla
            res.sort((a, b) -> b.compareTo(a));
        } catch (Exception e) {
            System.err.println("Error al combinar actividades y auditoría: " + e.getMessage());
            // fallback a solo actividades existentes
            return estadisticasRepository.obtenerUltimasActividades(limite);
        }

        // Limitar resultado
        if (res.size() > limite) {
            return res.subList(0, limite);
        }
        return res;
    }


    public boolean prepararDatosParaIA(){
        int filasExportadas = estadisticasRepository.prepararDatosParaIA();

        if(filasExportadas <7){
            System.err.println("Datos insuficientes " + filasExportadas + "días. La IA necesita al menos 7 días");
            return false;
        }
        return true;
    }



    public boolean prepararDatosStockParaIA() {

        int filasExportadas = estadisticasRepository.prepararDatosStockParaIA();


        if (filasExportadas < 7) {
            System.err.println("Datos de stock insuficientes (" + filasExportadas + "). Se requieren al menos 7 días de historial.");
            return false;
        }

        System.out.println("Datos de stock preparados con éxito.");
        return true;
    }


    public void ejecutarPrediccionProphet(){
        boolean datosListos = prepararDatosParaIA();

        if(!datosListos){
            System.err.println("No se puede ejecutar la predicción, faltan datos");
        }

        System.out.println("Ejecutando la IA Predictiva");

        try{
            ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/java/com/sistema/puntoventas/modelo_predictivo.py");


            processBuilder.redirectErrorStream(true);

            Process procesoPython = processBuilder.start(); //Iniciamos python

            BufferedReader reader = new BufferedReader(new InputStreamReader(procesoPython.getInputStream()));
            String linea;
            System.out.println("--- Respuesta de la IA ---");
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
            System.out.println("-----------------------------------------------------------------");

            int exitCode = procesoPython.waitFor(); //esperamos a que termine el proceso
            if(exitCode == 0){
                System.out.println("Predicción terminada correctamente");
            }else{
                System.err.println("El script de python fallo, código de error "+exitCode);
            }

        }catch (Exception e){
            System.err.println("Error al ejecutar el modelo predictivo: " + e.getMessage());
        }

    }



    public List<PrediccionStockDTO> ejecutarPrediccionStock() {
        List<PrediccionStockDTO> predicciones = new ArrayList<>();

        if (!prepararDatosStockParaIA()) {
            return predicciones; // retornamos una lista vacía si no hay datos suficientes
        }


        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/java/com/sistema/puntoventas/prediccion_stock.py");
            processBuilder.redirectErrorStream(true);
            Process procesoPython = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(procesoPython.getInputStream()));
            String linea;

            // ===== PASO 1: Recopilar IDs y demandas del script Python =====
            Map<Integer, Double> demandaPorProducto = new HashMap<>();
            while ((linea = reader.readLine()) != null) {
                if(linea.contains("ERROR")) continue;

                String[] partes = linea.split(",");
                if(partes.length == 2) {
                    try {
                        int idProducto = Integer.parseInt(partes[0]);
                        double demandaDiaria = Double.parseDouble(partes[1]);
                        demandaPorProducto.put(idProducto, demandaDiaria);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parseando línea Python: " + linea);
                    }
                }
            }
            procesoPython.waitFor();

            // ===== PASO 2: Obtener TODOS los productos en UNA sola consulta batch =====
            Map<Integer, Producto> productosMap = estadisticasRepository.obtenerProductosPorIds(
                new ArrayList<>(demandaPorProducto.keySet())
            );

            // ===== PASO 3: Construir DTOs sin más consultas por producto =====
            for (Map.Entry<Integer, Double> entry : demandaPorProducto.entrySet()) {
                int idProducto = entry.getKey();
                double demandaDiaria = entry.getValue();

                Producto producto = productosMap.get(idProducto);
                if (producto == null) {
                    System.err.println("Advertencia: No se encontraron datos para producto ID " + idProducto);
                    continue;
                }

                int stockActual = producto.getStockActual();
                String nombreProducto = (producto.getNombre() != null && !producto.getNombre().trim().isEmpty())
                        ? producto.getNombre()
                        : "Producto #" + idProducto;
                int stockMinimo = producto.getStockMinimo();

                // Calculamos los días restantes
                int diasParaAgotarse = 0;
                if(demandaDiaria>0){
                    diasParaAgotarse = (int) (stockActual / demandaDiaria);
                }else {
                    diasParaAgotarse = 999;
                }

                // Calculamos riesgo 
                double indiceRiesgo;

                if (stockActual <= 0) {
                    indiceRiesgo = 1.0; // CRÍTICO: Ya no hay stock
                } else if (stockActual <= stockMinimo) {
                    indiceRiesgo = 0.9; // ALTO: Ya tocaste el límite mínimo del dueño
                } else if (diasParaAgotarse <= 3) {
                    indiceRiesgo = 0.8; // ALTO: Se agota en menos de 3 días
                } else if (diasParaAgotarse <= 7) {
                    indiceRiesgo = 0.5; // MEDIO: Te queda una semana
                } else if (diasParaAgotarse <= 14) {
                    indiceRiesgo = 0.3; // PREVENTIVO: Te quedan 2 semanas
                } else {
                    indiceRiesgo = 0.1; // BAJO: Hay buen stock
                }

                //Sugerencia de compra inteligente
                int cantidadSugerida = 0;
                if (indiceRiesgo >= 0.5) { // Solo sugerimos comprar si hay riesgo medio o mayor
                    // Rellenar hasta superar el mínimo + lo necesario para 7 días extra
                    cantidadSugerida = (int) Math.ceil((stockMinimo - stockActual) + (demandaDiaria * 7));
                    if (cantidadSugerida < 1) {
                        cantidadSugerida = 1; // Siempre sugerir al menos 1
                    }
                }


                // Llenamos el DTO
                PrediccionStockDTO ps = new PrediccionStockDTO();
                ps.setIdProducto(idProducto);
                ps.setNombreProducto(nombreProducto);
                ps.setStockActual(stockActual);
                ps.setDiasParaAgotarse(diasParaAgotarse);
                ps.setCantidadSugerida(cantidadSugerida);
                ps.setIndiceRiesgo(indiceRiesgo);

                predicciones.add(ps);
            }

            System.out.println("DEBUG PREDICCION: " + predicciones.size() + " predicciones generadas con consulta batch.");

        } catch (Exception e) {
            System.err.println("Error en IA: " + e.getMessage());
        }

        return predicciones;
    }
}
