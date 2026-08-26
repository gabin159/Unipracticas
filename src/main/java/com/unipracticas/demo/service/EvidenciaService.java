package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Evidencia;
import com.unipracticas.demo.repository.EvidenciaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvidenciaService {

    @Autowired
    private EvidenciaRepository evidenciaRepository;

    /*
     * Crea una nueva evidencia o actualiza una existente.
     */
    public Evidencia guardar(Evidencia evidencia) {
        return evidenciaRepository.save(evidencia);
    }

    /*
     * Retorna todas las evidencias registradas.
     */
    public List<Evidencia> listarTodos() {
        return evidenciaRepository.findAll();
    }

    /*
     * Busca una evidencia por su ID.
     * Retorna null si no existe.
     */
    public Evidencia buscarPorId(Long id) {
        return evidenciaRepository.findById(id).orElse(null);
    }

    /*
     * Elimina una evidencia por su ID.
     */
    public void eliminar(Long id) {
        evidenciaRepository.deleteById(id);
    }

    /*
     * Lista las evidencias pertenecientes a un estudiante.
     */
    public List<Evidencia> listarPorEstudiante(Long estudianteId) {
        return evidenciaRepository.findByEstudianteId(estudianteId);
    }

    /*
     * Busca evidencias por título.
     * Si el texto está vacío, retorna todas las evidencias.
     */
    public List<Evidencia> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        return evidenciaRepository.findByTituloContainingIgnoreCase(texto);
    }
}