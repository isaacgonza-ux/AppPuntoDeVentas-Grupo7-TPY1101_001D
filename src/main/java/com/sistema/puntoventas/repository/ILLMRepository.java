package com.sistema.puntoventas.repository;

public interface ILLMRepository {
    void guardarApiKey(String apiKey);
    void eliminarApiKey();
    String obtenerApiKey();
}
