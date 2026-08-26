package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.repository.PracticaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PracticaService {

    @Autowired
    private PracticaRepository practicaRepository;

    /*
     * Crea una nueva práctica o actualiza una existente.
     */
    public Practica guardar(Practica practica) {
        return practicaRepository.save(practica);
    }

    /*
     * Retorna todas las prácticas registradas.
     */
    public List<Practica> listarTodos() {
        return practicaRepository.findAll();
    }

    /*
     * Busca una práctica por su ID.
     * Retorna null si no existe.
     */
    public Practica buscarPorId(Long id) {
        return practicaRepository.findById(id).orElse(null);
    }

    /*
     * Elimina una práctica por su ID.
     */
    public void eliminar(Long id) {
        practicaRepository.deleteById(id);
    }

    /*
     * Busca prácticas por título.
     * Si el texto está vacío, retorna todas las prácticas.
     */
    public List<Practica> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        return practicaRepository.findByTituloContainingIgnoreCase(texto);
    }

    /*
     * Busca prácticas según su estado.
     */
    public List<Practica> listarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return listarTodos();
        }

        return practicaRepository.findByEstado(estado);
    }

    /*
     * Busca las prácticas pertenecientes a una empresa.
     */
    public List<Practica> listarPorEmpresa(Long empresaId) {
        return practicaRepository.findByEmpresaId(empresaId);
    }
}