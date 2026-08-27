package com.unipracticas.demo.controller;

import com.unipracticas.demo.model.Empresa;
import com.unipracticas.demo.model.Estudiante;
import com.unipracticas.demo.model.Evaluacion;
import com.unipracticas.demo.model.Postulacion;
import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.model.Tutor;

import com.unipracticas.demo.service.EmpresaService;
import com.unipracticas.demo.service.EstudianteService;
import com.unipracticas.demo.service.EvaluacionService;
import com.unipracticas.demo.service.PostulacionService;
import com.unipracticas.demo.service.PracticaService;
import com.unipracticas.demo.service.TutorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private PracticaService practicaService;

    @Autowired
    private PostulacionService postulacionService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private TutorService tutorService;


    /*
     * Muestra el formulario de inicio de sesión de la empresa.
     */
    @GetMapping("/login")
    public String formularioLogin() {

        return "empresa/login";
    }


    /*
     * Permite iniciar sesión utilizando el correo y la contraseña
     * registrados en la entidad Empresa.
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            Model model) {

        Empresa empresa = empresaService.listarTodos().stream()
                .filter(e ->
                        e.getCorreo() != null
                                && e.getCorreo().equalsIgnoreCase(correo)
                                && e.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (empresa == null) {

            model.addAttribute(
                    "error",
                    "Correo o contraseña incorrectos."
            );

            return "empresa/login";
        }

        return "redirect:/empresa/perfil/" + empresa.getId();
    }


    /*
     * Muestra el perfil de la empresa.
     */
    @GetMapping("/perfil/{id}")
    public String perfil(
            @PathVariable Long id,
            Model model) {

        Empresa empresa = empresaService.buscarPorId(id);

        model.addAttribute("empresa", empresa);

        return "empresa/perfil";
    }


    /*
     * Lista todas las prácticas publicadas por una empresa.
     */
    @GetMapping("/practicas/{empresaId}")
    public String practicas(
            @PathVariable Long empresaId,
            Model model) {

        List<Practica> practicas =
                practicaService.listarPorEmpresa(empresaId);

        model.addAttribute("practicas", practicas);
        model.addAttribute("empresaId", empresaId);

        return "empresa/practicas";
    }


    /*
     * Muestra el formulario para crear una nueva práctica.
     */
    @GetMapping("/practicas/nueva/{empresaId}")
    public String formularioNuevaPractica(
            @PathVariable Long empresaId,
            Model model) {

        Empresa empresa =
                empresaService.buscarPorId(empresaId);

        Practica practica = new Practica();

        practica.setEmpresa(empresa);

        model.addAttribute("practica", practica);

        return "empresa/practica-form";
    }


    /*
     * Guarda una nueva práctica o actualiza una práctica existente.
     *
     * Cuando se crea una práctica nueva, su estado inicial
     * será DISPONIBLE.
     */
    @PostMapping("/practicas/guardar")
    public String guardarPractica(
            @Valid @ModelAttribute("practica") Practica practica,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return "empresa/practica-form";
        }

        if (practica.getId() == null) {

            practica.setEstado("DISPONIBLE");
        }

        practicaService.guardar(practica);

        return "redirect:/empresa/practicas/"
                + practica.getEmpresa().getId();
    }


    /*
     * Carga una práctica existente para editarla.
     */
    @GetMapping("/practicas/editar/{id}")
    public String editarPractica(
            @PathVariable Long id,
            Model model) {

        Practica practica =
                practicaService.buscarPorId(id);

        model.addAttribute("practica", practica);

        return "empresa/practica-form";
    }


    /*
     * Elimina una práctica publicada por la empresa.
     */
    @GetMapping("/practicas/eliminar/{id}")
    public String eliminarPractica(
            @PathVariable Long id) {

        Practica practica =
                practicaService.buscarPorId(id);

        Long empresaId =
                practica.getEmpresa().getId();

        practicaService.eliminar(id);

        return "redirect:/empresa/practicas/" + empresaId;
    }


    /*
     * Muestra las postulaciones recibidas para una práctica.
     */
    @GetMapping("/postulaciones/{practicaId}")
    public String postulaciones(
            @PathVariable Long practicaId,
            Model model) {

        List<Postulacion> postulaciones =
                postulacionService.listarPorPractica(practicaId);

        model.addAttribute(
                "postulaciones",
                postulaciones
        );

        return "empresa/postulaciones";
    }


    /*
     * Acepta una postulación.
     *
     * Se verifica que todavía existan vacantes disponibles.
     * Al aceptar:
     *
     * 1. La postulación pasa a ACEPTADA.
     * 2. El estudiante queda relacionado con la empresa.
     * 3. El estudiante queda relacionado con la práctica.
     * 4. La práctica cambia a ASIGNADA cuando alcanza sus vacantes.
     */
    @PostMapping("/postulaciones/aceptar/{postulacionId}")
    public String aceptarPostulacion(
            @PathVariable Long postulacionId,
            Model model) {

        Postulacion postulacion =
                postulacionService.buscarPorId(postulacionId);

        if (postulacion == null) {
            return "redirect:/empresa/practicas";
        }

        Estudiante estudiante =
                postulacion.getEstudiante();

        Practica practica =
                postulacion.getPractica();

        List<Estudiante> estudiantesAsignados =
                practica.getEstudiantes();

        int estudiantesActuales =
                estudiantesAsignados == null
                        ? 0
                        : estudiantesAsignados.size();

        /*
         * Verifica que la práctica todavía tenga vacantes.
         */
        if (estudiantesActuales >= practica.getVacantes()) {

            model.addAttribute(
                    "error",
                    "No hay vacantes disponibles para esta práctica."
            );

            return "redirect:/empresa/postulaciones/"
                    + practica.getId();
        }

        /*
         * Cambia el estado de la postulación.
         */
        postulacion.setEstado("ACEPTADA");

        postulacionService.guardar(postulacion);

        /*
         * Relaciona al estudiante con la empresa.
         */
        estudiante.setEmpresa(
                practica.getEmpresa()
        );

        /*
         * Relaciona al estudiante con la práctica.
         */
        estudiante.setPractica(practica);

        estudianteService.guardar(estudiante);

        /*
         * Si la práctica alcanza el número máximo
         * de vacantes, cambia su estado a ASIGNADA.
         */
        if (estudiantesActuales + 1 >= practica.getVacantes()) {

            practica.setEstado("ASIGNADA");

            practicaService.guardar(practica);
        }

        return "redirect:/empresa/postulaciones/"
                + practica.getId();
    }


    /*
     * Rechaza una postulación.
     *
     * Solamente cambia el estado a RECHAZADA.
     */
    @PostMapping("/postulaciones/rechazar/{postulacionId}")
    public String rechazarPostulacion(
            @PathVariable Long postulacionId) {

        Postulacion postulacion =
                postulacionService.buscarPorId(postulacionId);

        if (postulacion == null) {
            return "redirect:/empresa/practicas";
        }

        postulacion.setEstado("RECHAZADA");

        postulacionService.guardar(postulacion);

        return "redirect:/empresa/postulaciones/"
                + postulacion.getPractica().getId();
    }


    /*
     * Muestra el formulario para realizar una evaluación.
     *
     * Se cargan los tutores porque la entidad Evaluacion
     * necesita obligatoriamente un tutor.
     */
    @GetMapping("/evaluaciones/nueva")
    public String formularioEvaluacion(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("empresaId") Long empresaId,
            @RequestParam("practicaId") Long practicaId,
            Model model) {

        List<Tutor> tutores =
                tutorService.listarTodos();

        model.addAttribute(
                "evaluacion",
                new Evaluacion()
        );

        model.addAttribute(
                "tutores",
                tutores
        );

        model.addAttribute(
                "estudianteId",
                estudianteId
        );

        model.addAttribute(
                "empresaId",
                empresaId
        );

        model.addAttribute(
                "practicaId",
                practicaId
        );

        return "empresa/evaluacion-form";
    }


    /*
     * Guarda la evaluación realizada por la empresa.
     *
     * La evaluación queda relacionada con:
     *
     * - Estudiante
     * - Empresa
     * - Práctica
     * - Tutor
     */
    @PostMapping("/evaluaciones/guardar")
    public String guardarEvaluacion(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("empresaId") Long empresaId,
            @RequestParam("practicaId") Long practicaId,
            @RequestParam("tutorId") Long tutorId,
            @RequestParam("calificacion") Double calificacion,
            @RequestParam("comentarios") String comentarios) {

        Evaluacion evaluacion =
                new Evaluacion();

        evaluacion.setEstudiante(
                estudianteService.buscarPorId(estudianteId)
        );

        evaluacion.setEmpresa(
                empresaService.buscarPorId(empresaId)
        );

        evaluacion.setPractica(
                practicaService.buscarPorId(practicaId)
        );

        evaluacion.setTutor(
                tutorService.buscarPorId(tutorId)
        );

        evaluacion.setCalificacion(
                calificacion
        );

        evaluacion.setComentarios(
                comentarios
        );

        evaluacion.setFecha(
                LocalDate.now()
        );

        evaluacionService.guardar(evaluacion);

        return "redirect:/empresa/perfil/"
                + empresaId;
    }
}