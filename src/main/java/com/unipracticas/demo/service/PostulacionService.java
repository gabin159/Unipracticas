package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Postulacion;
import com.unipracticas.demo.repository.PostulacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostulacionService {

    @Autowired
    private PostulacionRepository postulacionRepository;

    /*
     * Crea una nueva postulación o actualiza una existente.
     */
    public Postulacion guardar(Postulacion postulacion) {
        return postulacionRepository.save(postulacion);
    }

    /*
     * Retorna todas las postulaciones registradas.
     */
    public List<Postulacion> listarTodos() {
        return postulacionRepository.findAll();
    }

    /*
     * Busca una postulación por su ID.
     * Retorna null si no existe.
     */
    public Postulacion buscarPorId(Long id) {
        return postulacionRepository.findById(id).orElse(null);
    }

    /*
     * Elimina una postulación por su ID.
     */
    public void eliminar(Long id) {
        postulacionRepository.deleteById(id);
    }

    /*
     * Lista las postulaciones realizadas por un estudiante.
     */
    public List<Postulacion> listarPorEstudiante(Long estudianteId) {
        return postulacionRepository.findByEstudianteId(estudianteId);
    }

    /*
     * Lista las postulaciones recibidas por una práctica.
     */
    public List<Postulacion> listarPorPractica(Long practicaId) {
        return postulacionRepository.findByPracticaId(practicaId);
    }

    /*
     * Filtra las postulaciones según su estado.
     * Si el estado está vacío, retorna todas las postulaciones.
     */
    public List<Postulacion> listarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return listarTodos();
        }

        return postulacionRepository.findByEstado(estado);
    }
}