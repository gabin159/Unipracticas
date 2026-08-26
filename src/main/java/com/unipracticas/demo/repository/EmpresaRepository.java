package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Empresa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /*
     * Busca una empresa por su nombre.
     */
    List<Empresa> findByNombreEmpresaContainingIgnoreCase(String nombreEmpresa);

    /*
     * Busca una empresa por su NIT.
     */
    Optional<Empresa> findByNit(String nit);

    /*
     * Busca una empresa por su correo.
     * Se utiliza para validar duplicados y posteriormente
     * para el inicio de sesión.
     */
    Optional<Empresa> findByCorreo(String correo);

    /*
     * Busca empresas por sector económico.
     */
    List<Empresa> findBySectorContainingIgnoreCase(String sector);
}