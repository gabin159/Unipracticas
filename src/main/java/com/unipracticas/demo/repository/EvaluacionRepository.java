package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Evaluacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    /*
     * Lista todas las evaluaciones de un estudiante.
     */
    List<Evaluacion> findByEstudianteId(Long estudianteId);

    /*
     * Lista todas las evaluaciones asociadas a una empresa.
     */
    List<Evaluacion> findByEmpresaId(Long empresaId);

    /*
     * Lista todas las evaluaciones realizadas por un tutor.
     */
    List<Evaluacion> findByTutorId(Long tutorId);

    /*
     * Lista todas las evaluaciones asociadas a una práctica.
     */
    List<Evaluacion> findByPracticaId(Long practicaId);
}