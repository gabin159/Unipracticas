package com.unipracticas.demo.repository;

import com.unipracticas.demo.model.Documento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    /*
     * Lista todos los documentos subidos por un estudiante.
     */
    List<Documento> findByEstudianteId(Long estudianteId);

    /*
     * Filtra los documentos según su tipo.
     */
    List<Documento> findByTipoDocumento(String tipoDocumento);
}