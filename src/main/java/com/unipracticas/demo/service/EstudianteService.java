package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Estudiante;
import com.unipracticas.demo.repository.EstudianteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    /*
     * Crea un nuevo estudiante o actualiza uno existente.
     */
    public Estudiante guardar(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    /*
     * Retorna todos los estudiantes registrados.
     */
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    /*
     * Busca un estudiante por su ID.
     * Retorna null si no existe.
     */
    public Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id).orElse(null);
    }

    /*
     * Elimina un estudiante por su ID.
     */
    public void eliminar(Long id) {
        estudianteRepository.deleteById(id);
    }

    /*
     * Busca estudiantes por nombre completo.
     * Si el texto está vacío, retorna todos.
     */
    public List<Estudiante> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        return estudianteRepository.findByNombreCompletoContainingIgnoreCase(texto);
    }

    /*
     * Busca estudiantes por programa académico.
     * Si el texto está vacío, retorna todos.
     */
    public List<Estudiante> buscarPorProgramaAcademico(String programaAcademico) {
        if (programaAcademico == null || programaAcademico.trim().isEmpty()) {
            return listarTodos();
        }

        return estudianteRepository.findByProgramaAcademicoContainingIgnoreCase(programaAcademico);
    }

    /*
     * Lista los estudiantes asignados a un tutor.
     */
    public List<Estudiante> listarPorTutor(Long tutorId) {
        return estudianteRepository.findByTutorId(tutorId);
    }

    /*
     * Lista los estudiantes pertenecientes a una empresa.
     */
    public List<Estudiante> listarPorEmpresa(Long empresaId) {
        return estudianteRepository.findByEmpresaId(empresaId);
    }
}