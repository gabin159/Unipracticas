package com.unipracticas.demo.service;

import com.unipracticas.demo.model.Empresa;
import com.unipracticas.demo.repository.EmpresaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    /*
     * Crea una nueva empresa o actualiza una existente.
     */
    public Empresa guardar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    /*
     * Retorna todas las empresas registradas.
     */
    public List<Empresa> listarTodos() {
        return empresaRepository.findAll();
    }

    /*
     * Busca una empresa por su ID.
     * Retorna null si no existe.
     */
    public Empresa buscarPorId(Long id) {
        return empresaRepository.findById(id).orElse(null);
    }

    /*
     * Elimina una empresa por su ID.
     */
    public void eliminar(Long id) {
        empresaRepository.deleteById(id);
    }

    /*
     * Busca empresas por nombre.
     * Si el texto está vacío, retorna todas las empresas.
     */
    public List<Empresa> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        return empresaRepository.findByNombreEmpresaContainingIgnoreCase(texto);
    }

    /*
     * Busca empresas por sector económico.
     * Si el sector está vacío, retorna todas las empresas.
     */
    public List<Empresa> buscarPorSector(String sector) {
        if (sector == null || sector.trim().isEmpty()) {
            return listarTodos();
        }

        return empresaRepository.findBySectorContainingIgnoreCase(sector);
    }

    /*
     * Busca una empresa por su correo.
     */
    public Empresa buscarPorCorreo(String correo) {
        return empresaRepository.findByCorreo(correo).orElse(null);
    }

    /*
     * Busca una empresa por su NIT.
     */
    public Empresa buscarPorNit(String nit) {
        return empresaRepository.findByNit(nit).orElse(null);
    }
}