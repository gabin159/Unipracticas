package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Tutor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    /*
     * Busca un tutor por su correo.
     * Útil para validar duplicados y posteriormente
     * para el inicio de sesión.
     */
    Optional<Tutor> findByCorreo(String correo);

    /*
     * Busca tutores por nombre completo.
     */
    List<Tutor> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);

    /*
     * Busca tutores por facultad.
     */
    List<Tutor> findByFacultadContainingIgnoreCase(String facultad);

    /*
     * Busca tutores por cargo.
     */
    List<Tutor> findByCargoContainingIgnoreCase(String cargo);

    /*
     * Busca tutores por departamento.
     */
    List<Tutor> findByDepartamentoContainingIgnoreCase(String departamento);
}