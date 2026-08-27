package com.unipracticas.demo.controller;

import com.unipracticas.demo.model.Empresa;
import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.service.EmpresaService;
import com.unipracticas.demo.service.PracticaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class PublicController {

    @Autowired
    private PracticaService practicaService;

    @Autowired
    private EmpresaService empresaService;

    /*
     * Página de inicio pública.
     * Muestra las prácticas que se encuentran disponibles.
     */
    @GetMapping
    public String index(Model model) {

        List<Practica> practicas =
                practicaService.listarPorEstado("DISPONIBLE");

        model.addAttribute("practicas", practicas);

        return "index";
    }

    /*
     * Lista todas las prácticas y permite buscar por título.
     */
    @GetMapping("/practicas")
    public String practicas(
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Practica> practicas =
                practicaService.buscar(buscar);

        model.addAttribute("practicas", practicas);
        model.addAttribute("buscar", buscar);

        return "public/practicas";
    }

    /*
     * Muestra el detalle de una práctica específica.
     */
    @GetMapping("/practicas/{id}")
    public String detallePractica(
            @PathVariable Long id,
            Model model) {

        Practica practica =
                practicaService.buscarPorId(id);

        model.addAttribute("practica", practica);

        return "public/practica-detalle";
    }

    /*
     * Lista las empresas registradas.
     * Más adelante se podrá filtrar por convenio
     * cuando se implemente esta característica.
     */
    @GetMapping("/empresas")
    public String empresas(
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Empresa> empresas =
                empresaService.buscar(buscar);

        model.addAttribute("empresas", empresas);
        model.addAttribute("buscar", buscar);

        return "public/empresas";
    }

    /*
     * Muestra el detalle de una empresa específica.
     */
    @GetMapping("/empresas/{id}")
    public String detalleEmpresa(
            @PathVariable Long id,
            Model model) {

        Empresa empresa =
                empresaService.buscarPorId(id);

        model.addAttribute("empresa", empresa);

        return "public/empresa-detalle";
    }

    /*
     * Redirige al inicio de sesión del estudiante.
     */
    @GetMapping("/acceso/estudiante")
    public String accesoEstudiante() {

        return "redirect:/estudiante/login";
    }

    /*
     * Redirige al inicio de sesión de la empresa.
     */
    @GetMapping("/acceso/empresa")
    public String accesoEmpresa() {

        return "redirect:/empresa/login";
    }

    /*
     * Redirige al inicio de sesión del tutor.
     */
    @GetMapping("/acceso/tutor")
    public String accesoTutor() {

        return "redirect:/tutor/login";
    }
}