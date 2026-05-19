package com.sistema.puntoventas.service;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.repository.impl.AuditoriaRepositoryImpl;

public class AuditoriaService {

	private final AuditoriaRepositoryImpl auditoriaRepository;

	public AuditoriaService() {
		this.auditoriaRepository = new AuditoriaRepositoryImpl();
	}

	public boolean registrarEvento(AuditoriaEvento evento) {
		if (evento == null) {
			return false;
		}

		try {
			auditoriaRepository.registrarEvento(evento);
			return true;
		} catch (Exception e) {
			System.err.println("Error al registrar evento de auditoría: " + e.getMessage());
			return false;
		}
	}
}
