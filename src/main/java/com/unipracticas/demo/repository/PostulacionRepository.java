package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Postulacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    /*
     * Lista las postulaciones realizadas por un estudiante.
     */
    List<Postulacion> findByEstudianteId(Long estudianteId);

    /*
     * Lista las postulaciones recibidas por una práctica.
     */
    List<Postulacion> findByPracticaId(Long practicaId);

    /*
     * Lista las postulaciones según su estado.
     */
    List<Postulacion> findByEstado(String estado);
}