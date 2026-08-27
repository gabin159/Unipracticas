package com.unipracticas.demo.controller;

import com.unipracticas.demo.model.Empresa;
import com.unipracticas.demo.model.Estudiante;
import com.unipracticas.demo.model.Evaluacion;
import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.model.Tutor;

import com.unipracticas.demo.service.EmpresaService;
import com.unipracticas.demo.service.EstudianteService;
import com.unipracticas.demo.service.EvaluacionService;
import com.unipracticas.demo.service.PracticaService;
import com.unipracticas.demo.service.TutorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tutor")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private PracticaService practicaService;

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private EmpresaService empresaService;


    // REGISTRO


    /* Muestra el formulario de registro del tutor */
    @GetMapping("/registro")
    public String formularioRegistro(Model model) {

        model.addAttribute("tutor", new Tutor());

        return "tutor/registro";
    }


    /* Guarda un nuevo tutor */
    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("tutor") Tutor tutor,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return "tutor/registro";
        }

        tutorService.guardar(tutor);

        return "redirect:/tutor/login";
    }



    // LOGIN


    /* Muestra el formulario de login */
    @GetMapping("/login")
    public String formularioLogin() {

        return "tutor/login";
    }


    @PostMapping("/login")
    public String login(@RequestParam("correo") String correo,
                        @RequestParam("contrasena") String contrasena,
                        Model model) {

        Tutor tutor = tutorService.listarTodos().stream()
                .filter(t -> t.getCorreo() != null &&
                        t.getCorreo().equalsIgnoreCase(correo) &&
                        t.getContrasena() != null &&
                        t.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);

        if (tutor == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "tutor/login";
        }

        return "redirect:/tutor/perfil/" + tutor.getId();
    }



    // PERFIL


    /* Muestra el perfil del tutor */
    @GetMapping("/perfil/{id}")
    public String perfil(
            @PathVariable Long id,
            Model model) {

        Tutor tutor = tutorService.buscarPorId(id);

        if (tutor == null) {
            return "redirect:/tutor/login";
        }

        model.addAttribute("tutor", tutor);

        return "tutor/perfil";
    }


        // ESTUDIANTES ASIGNADOS

    /* Muestra los estudiantes asignados al tutor */
    @GetMapping("/estudiantes/{tutorId}")
    public String estudiantes(
            @PathVariable Long tutorId,
            Model model) {

        List<Estudiante> estudiantes =
                estudianteService.listarPorTutor(tutorId);

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("tutorId", tutorId);

        return "tutor/estudiantes";
    }


        // SEGUIMIENTO DE PRÁCTICAS


    /* Permite al tutor consultar las prácticas */
    @GetMapping("/practicas")
    public String practicas(
            @RequestParam(
                    value = "estado",
                    required = false
            ) String estado,
            Model model) {

        List<Practica> practicas;

        if (estado == null || estado.trim().isEmpty()) {

            practicas = practicaService.listarTodos();

        } else {

            practicas = practicaService.listarPorEstado(estado);
        }

        model.addAttribute("practicas", practicas);
        model.addAttribute("estado", estado);

        return "tutor/practicas";
    }



    // EVALUACIONES


    /*
     * El tutor NO crea la evaluación.
     * La evaluación es realizada por la empresa.
     * El tutor solamente consulta las evaluaciones asociadas a él.
     */

    /* Muestra las evaluaciones recibidas por el tutor */
    @GetMapping("/evaluaciones/{tutorId}")
    public String evaluaciones(
            @PathVariable Long tutorId,
            Model model) {

        List<Evaluacion> evaluaciones =
                evaluacionService.listarPorTutor(tutorId);

        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("tutorId", tutorId);

        return "tutor/evaluaciones";
    }



    // EMPRESAS / CONVENIOS


    /*
     * Como todavía no existe una entidad Convenio,
     * el tutor gestiona las empresas mediante EmpresaService.
     *
     * Registrar una empresa desde aquí permite posteriormente
     * que esa empresa tenga sus propios datos y pueda ingresar
     * al sistema.
     */

    /* Lista las empresas registradas */
    @GetMapping("/empresas")
    public String empresas(Model model) {

        List<Empresa> empresas =
                empresaService.listarTodos();

        model.addAttribute("empresas", empresas);

        return "tutor/empresas";
    }


    /* Muestra el formulario para registrar una empresa */
    @GetMapping("/empresas/nueva")
    public String formularioNuevaEmpresa(Model model) {

        model.addAttribute("empresa", new Empresa());

        return "tutor/empresa-form";
    }


    /* Guarda una empresa registrada por el tutor */
    @PostMapping("/empresas/guardar")
    public String guardarEmpresa(
            @Valid @ModelAttribute("empresa") Empresa empresa,
            BindingResult resultado) {

        if (resultado.hasErrors()) {

            return "tutor/empresa-form";
        }

        empresaService.guardar(empresa);

        return "redirect:/tutor/empresas";
    }
}