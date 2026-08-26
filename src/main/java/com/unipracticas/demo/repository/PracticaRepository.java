package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Practica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticaRepository extends JpaRepository<Practica, Long> {

    /*
     * Busca prácticas por título.
     */
    List<Practica> findByTituloContainingIgnoreCase(String titulo);

    /*
     * Busca prácticas según su estado.
     */
    List<Practica> findByEstado(String estado);

    /*
     * Busca las prácticas pertenecientes a una empresa específica.
     */
    List<Practica> findByEmpresaId(Long empresaId);
}