package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Estudiante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    /*
     * Busca un estudiante por su correo.
     */
    Optional<Estudiante> findByCorreo(String correo);

    /*
     * Busca estudiantes por nombre completo.
     */
    List<Estudiante> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);

    /*
     * Busca estudiantes por programa académico.
     */
    List<Estudiante> findByProgramaAcademicoContainingIgnoreCase(String programaAcademico);

    /*
     * Lista los estudiantes asignados a un tutor específico.
     */
    List<Estudiante> findByTutorId(Long tutorId);

    /*
     * Lista los estudiantes pertenecientes a una empresa específica.
     */
    List<Estudiante> findByEmpresaId(Long empresaId);
}