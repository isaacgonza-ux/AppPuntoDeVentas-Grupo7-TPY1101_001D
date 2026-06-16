package com.sistema.puntoventas.pruebas;

import com.sistema.puntoventas.repository.impl.EstadisticasRepositoryImpl;
import com.sistema.puntoventas.service.EstadisticaService;

public class PruebaIA {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de integración Java-Python...");

        // 1. Instanciamos nuestras clases
        EstadisticasRepositoryImpl repository = new EstadisticasRepositoryImpl();
        EstadisticaService service = new EstadisticaService(repository, null);

        // 2. Ejecutamos el flujo completo
        // Esto creará el CSV y luego ejecutará el script de Python
        service.ejecutarPrediccionProphet();
        service.ejecutarPrediccionStock();

       System.out.println("Prueba finalizada.");
   }
}
