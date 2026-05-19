package com.sistema.puntoventas.repository;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import java.util.List;

public interface IAuditoriaRepository {
    List<AuditoriaEvento> obtenerUltimosEventos(int limite);
    public void registrarEvento(AuditoriaEvento auditoriaEvento);
}

