package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Evaluacion;
import com.unipracticas.demo.repository.EvaluacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    /*
     * Crea una nueva evaluación o actualiza una existente.
     */
    public Evaluacion guardar(Evaluacion evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    /*
     * Retorna todas las evaluaciones registradas.
     */
    public List<Evaluacion> listarTodos() {
        return evaluacionRepository.findAll();
    }

    /*
     * Busca una evaluación por su ID.
     * Retorna null si no existe.
     */
    public Evaluacion buscarPorId(Long id) {
        return evaluacionRepository.findById(id).orElse(null);
    }

    /*
     * Elimina una evaluación por su ID.
     */
    public void eliminar(Long id) {
        evaluacionRepository.deleteById(id);
    }

    /*
     * Lista las evaluaciones de un estudiante.
     */
    public List<Evaluacion> listarPorEstudiante(Long estudianteId) {
        return evaluacionRepository.findByEstudianteId(estudianteId);
    }

    /*
     * Lista las evaluaciones asociadas a una empresa.
     */
    public List<Evaluacion> listarPorEmpresa(Long empresaId) {
        return evaluacionRepository.findByEmpresaId(empresaId);
    }

    /*
     * Lista las evaluaciones realizadas por un tutor.
     */
    public List<Evaluacion> listarPorTutor(Long tutorId) {
        return evaluacionRepository.findByTutorId(tutorId);
    }

    /*
     * Lista las evaluaciones asociadas a una práctica.
     */
    public List<Evaluacion> listarPorPractica(Long practicaId) {
        return evaluacionRepository.findByPracticaId(practicaId);
    }
}