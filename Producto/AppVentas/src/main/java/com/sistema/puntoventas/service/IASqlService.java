package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.ResultadoConsulta;
import com.sistema.puntoventas.repository.IConsultaSQLRepository;
import com.sistema.puntoventas.repository.ILLMRepository;
import com.sistema.puntoventas.repository.impl.ConsultaSQLRepositoryImpl;
import com.sistema.puntoventas.repository.impl.LLMRepositoryImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IASqlService {

    private static final String RUTA_IA_SQL = "src/main/java/com/sistema/puntoventas/util/ia_sql";

    private final ILLMRepository llmRepository;
    private final IConsultaSQLRepository consultaRepository;

    public IASqlService() {
        this.llmRepository = new LLMRepositoryImpl();
        this.consultaRepository = new ConsultaSQLRepositoryImpl();
    }

    public IASqlService(ILLMRepository llmRepository, IConsultaSQLRepository consultaRepository) {
        this.llmRepository = llmRepository;
        this.consultaRepository = consultaRepository;
    }

    public ResultadoConsulta ejecutarConsultaNatural(String consulta) throws Exception {
        if (consulta == null || consulta.isBlank()) {
            return new ResultadoConsulta(List.of(), List.of());
        }

        String apiKey = llmRepository.obtenerApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("No hay una API Key configurada. Haz clic en ⚙️ para ingresarla.");
        }

        escribirInputJson(apiKey, consulta);

        String respuestaIA = ejecutarPython();

        String sql = limpiarRespuesta(respuestaIA); 

        if (sql.toUpperCase().startsWith("SELECT")) {
            ResultadoConsulta resultado = consultaRepository.ejecutarSelect(sql);
            return new ResultadoConsulta(resultado.getColumnas(), resultado.getFilas(), sql);
        }

        return new ResultadoConsulta(List.of(), List.of(), respuestaIA);
    }

    private void escribirInputJson(String apiKey, String prompt) throws IOException {
        File dir = new File(RUTA_IA_SQL);
        dir.mkdirs();

        String json = String.format("{\"api_key\": \"%s\", \"prompt\": \"%s\"}",
                apiKey.replace("\"", "\\\""),
                prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        try (FileWriter fw = new FileWriter(new File(dir, "input.json"))) {
            fw.write(json);
        }
    }

    private String obtenerPythonPath() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return "python";
        }
        return System.getProperty("user.dir") + "/venv/bin/python";
    }

    private String ejecutarPython() throws Exception {
        File dir = new File(RUTA_IA_SQL);

        ProcessBuilder pb = new ProcessBuilder(obtenerPythonPath(), "main.py");
        pb.directory(dir);
        pb.redirectErrorStream(true);

        Process proceso = pb.start();
        proceso.waitFor();

        File respuestaFile = new File(dir, "respuesta.json");
        if (!respuestaFile.exists()) {
            throw new RuntimeException("No se recibió respuesta de la IA.");
        }

        String raw;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(respuestaFile), StandardCharsets.UTF_8))) {
            raw = reader.readLine();
        }

        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Respuesta vacía de la IA.");
        }

        // raw = {"respuesta": "CONTENIDO"}
        // start apunta al 1er char del contenido, saltando la " de apertura del valor
        int start = raw.indexOf("\"respuesta\": \"") + 14;
        if (start < 14) {
            start = raw.indexOf("\"respuesta\":\"") + 13;
        }
        if (start < 13) {
            throw new RuntimeException("Formato de respuesta inválido.");
        }

        int end = raw.lastIndexOf("\"");
        if (end <= start) {
            throw new RuntimeException("Formato de respuesta inválido.");
        }

        // Extrae el contenido entre comillas y revierte escapes JSON
        return raw.substring(start, end)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .trim();
    }

    private String limpiarRespuesta(String raw) {
        return raw.replaceAll("```sql|```|`", "").trim();
    }
}
