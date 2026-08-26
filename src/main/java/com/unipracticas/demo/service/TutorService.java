package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Tutor;
import com.unipracticas.demo.repository.TutorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    /*
     * Crea un nuevo tutor o actualiza uno existente.
     */
    public Tutor guardar(Tutor tutor) {
        return tutorRepository.save(tutor);
    }

    /*
     * Retorna todos los tutores registrados.
     */
    public List<Tutor> listarTodos() {
        return tutorRepository.findAll();
    }

    /*
     * Busca un tutor por su ID.
     * Retorna null si no existe.
     */
    public Tutor buscarPorId(Long id) {
        return tutorRepository.findById(id).orElse(null);
    }

    /*
     * Elimina un tutor por su ID.
     */
    public void eliminar(Long id) {
        tutorRepository.deleteById(id);
    }

    /*
     * Busca tutores por nombre.
     * Si el texto está vacío, retorna todos los tutores.
     */
    public List<Tutor> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        return tutorRepository.findByNombreCompletoContainingIgnoreCase(texto);
    }

    /*
     * Busca tutores por facultad.
     * Si la facultad está vacía, retorna todos los tutores.
     */
    public List<Tutor> buscarPorFacultad(String facultad) {
        if (facultad == null || facultad.trim().isEmpty()) {
            return listarTodos();
        }

        return tutorRepository.findByFacultadContainingIgnoreCase(facultad);
    }

    /*
     * Busca tutores por cargo.
     * Si el cargo está vacío, retorna todos los tutores.
     */
    public List<Tutor> buscarPorCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            return listarTodos();
        }

        return tutorRepository.findByCargoContainingIgnoreCase(cargo);
    }

    /*
     * Busca tutores por departamento.
     * Si el departamento está vacío, retorna todos los tutores.
     */
    public List<Tutor> buscarPorDepartamento(String departamento) {
        if (departamento == null || departamento.trim().isEmpty()) {
            return listarTodos();
        }

        return tutorRepository.findByDepartamentoContainingIgnoreCase(departamento);
    }

    /*
     * Busca un tutor por su correo.
     * Retorna null si no existe.
     */
    public Tutor buscarPorCorreo(String correo) {
        return tutorRepository.findByCorreo(correo).orElse(null);
    }
}