package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Evidencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    /*
     * Lista todas las evidencias subidas por un estudiante.
     */
    List<Evidencia> findByEstudianteId(Long estudianteId);

    /*
     * Busca evidencias por título ignorando mayúsculas y minúsculas.
     */
    List<Evidencia> findByTituloContainingIgnoreCase(String titulo);
}