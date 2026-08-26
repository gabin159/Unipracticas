package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Documento;
import com.unipracticas.demo.repository.DocumentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    /*
     * Crea un nuevo documento o actualiza uno existente.
     */
    public Documento guardar(Documento documento) {
        return documentoRepository.save(documento);
    }

    /*
     * Retorna todos los documentos registrados.
     */
    public List<Documento> listarTodos() {
        return documentoRepository.findAll();
    }

    /*
     * Busca un documento por su ID.
     * Retorna null si no existe.
     */
    public Documento buscarPorId(Long id) {
        return documentoRepository.findById(id).orElse(null);
    }

    /*
     * Elimina un documento por su ID.
     */
    public void eliminar(Long id) {
        documentoRepository.deleteById(id);
    }

    /*
     * Lista los documentos subidos por un estudiante.
     */
    public List<Documento> listarPorEstudiante(Long estudianteId) {
        return documentoRepository.findByEstudianteId(estudianteId);
    }

    /*
     * Filtra los documentos según su tipo.
     * Si el tipo está vacío, retorna todos los documentos.
     */
    public List<Documento> listarPorTipo(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            return listarTodos();
        }

        return documentoRepository.findByTipoDocumento(tipoDocumento);
    }
}