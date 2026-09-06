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

    @GetMapping
    public String index(Model model) {

        List<Practica> practicas = practicaService.listarPorEstado("DISPONIBLE");
        List<Empresa> empresas = empresaService.buscar(null); // <- Usamos el método existente en tu servicio

        model.addAttribute("practicas", practicas);
        model.addAttribute("empresas", empresas);

        return "index";
    }

    @GetMapping("/practicas")
    public String practicas(
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Practica> practicas = practicaService.buscar(buscar);

        model.addAttribute("practicas", practicas);
        model.addAttribute("buscar", buscar);

        return "public/practicas";
    }

    @GetMapping("/practicas/{id}")
    public String detallePractica(
            @PathVariable Long id,
            Model model) {

        Practica practica = practicaService.buscarPorId(id);

        model.addAttribute("practica", practica);

        return "public/practica-detalle";
    }

    @GetMapping("/empresas")
    public String empresas(
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Empresa> empresas = empresaService.buscar(buscar);

        model.addAttribute("empresas", empresas);
        model.addAttribute("buscar", buscar);

        return "public/empresas";
    }

    @GetMapping("/empresas/{id}")
    public String detalleEmpresa(
            @PathVariable Long id,
            Model model) {

        Empresa empresa = empresaService.buscarPorId(id);

        model.addAttribute("empresa", empresa);

        return "public/empresa-detalle";
    }

    @GetMapping("/acceso/estudiante")
    public String accesoEstudiante() {
        return "redirect:/estudiante/login";
    }

    @GetMapping("/acceso/empresa")
    public String accesoEmpresa() {
        return "redirect:/empresa/login";
    }

    @GetMapping("/acceso/tutor")
    public String accesoTutor() {
        return "redirect:/tutor/login";
    }
}