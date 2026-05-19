package com.sistema.puntoventas.pruebas;

import com.sistema.puntoventas.modelo.PrediccionStockDTO;
import com.sistema.puntoventas.repository.impl.EstadisticasRepositoryImpl;
import com.sistema.puntoventas.repository.impl.ProductoRepositoryImpl;
import com.sistema.puntoventas.service.EstadisticaService;

import java.util.List;

/**
 * Clase de prueba exclusiva para la IA de Predicción de Stock.
 * Esta clase verifica que la conexión con el script de Python,
 * el procesamiento de datos de inventario y el cálculo de agotamiento funcionen.
 */
public class PruebaPrediccionStock {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   PRUEBA DE IA: PREDICCIÓN DE AGOTAMIENTO STOCK  ");
        System.out.println("==================================================");

        // 1. Inicialización de componentes
        EstadisticasRepositoryImpl repositoryEstadistica = new EstadisticasRepositoryImpl();
        ProductoRepositoryImpl repositoryProducto = new ProductoRepositoryImpl();


        EstadisticaService service = new EstadisticaService(repositoryEstadistica,repositoryProducto);

        System.out.println("[INFO] Iniciando proceso de análisis...");
        System.out.println("[INFO] (Esto generará el archivo 'datos_stock.csv' y ejecutará Python)\n");

        try {
            // 2. Ejecutar la predicción
            // Este método ya incluye internamente la preparación de datos
            List<PrediccionStockDTO> resultados = service.ejecutarPrediccionStock();

            // 3. Mostrar resultados
            if (resultados == null || resultados.isEmpty()) {
                System.out.println("--------------------------------------------------");
                System.err.println("  ATENCIÓN: No se generaron predicciones.");
                System.err.println("  Asegúrate de tener suficientes ventas registradas.");
                System.out.println("--------------------------------------------------");
            } else {
                System.out.println("================ RESULTADOS ENCONTRADOS ==============");
                System.out.printf("%-20s | %-15s | %-18s | %-10s%n",
                        "NOMBRE PRODUCTO", "DÍAS RESTANTES", "SUGERENCIA COMPRA", "RIESGO");
                System.out.println("------------------------------------------------------------------");

                for (PrediccionStockDTO p : resultados) {
                    String riesgoStr;
                    double riesgo = p.getIndiceRiesgo();

                    if (riesgo >= 1.0) {
                        riesgoStr = "CRÍTICO (X_X)";
                    } else if (riesgo >= 0.8) {
                        riesgoStr = "ALTO (!)";
                    } else if (riesgo >= 0.5) {
                        riesgoStr = "MEDIO (-)";
                    } else if (riesgo >= 0.3) {
                        riesgoStr = "PREVENTIVO";
                    } else {
                        riesgoStr = "BAJO (OK)";
                    }

                    System.out.printf("%-25s | %-15d | %-18d | %-10s%n",
                            p.getNombreProducto(),
                            p.getDiasParaAgotarse(),
                            p.getCantidadSugerida(),
                            riesgoStr);
                }
                System.out.println("------------------------------------------------------------------");
                System.out.println("[ÉXITO] Se analizaron " + resultados.size() + " productos.");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Ocurrió un fallo durante la prueba:");
            e.printStackTrace();
        }

        System.out.println("\n================ FIN DE LA PRUEBA ================");
    }
}