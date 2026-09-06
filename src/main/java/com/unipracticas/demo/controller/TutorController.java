package com.unipracticas.demo.controller;

import com.unipracticas.demo.model.Empresa;
import com.unipracticas.demo.model.Estudiante;
import com.unipracticas.demo.model.Evaluacion;
import com.unipracticas.demo.model.Evidencia;
import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.model.Tutor;

import com.unipracticas.demo.service.EmpresaService;
import com.unipracticas.demo.service.EstudianteService;
import com.unipracticas.demo.service.EvaluacionService;
import com.unipracticas.demo.service.EvidenciaService;
import com.unipracticas.demo.service.PracticaService;
import com.unipracticas.demo.service.TutorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private EvidenciaService evidenciaService;

    private final String CARPETA_CONVENIOS = "uploads/convenios/";


    // ==========================================
    // REGISTRO Y LOGIN
    // ==========================================

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

    /* Muestra el formulario de login */
    @GetMapping("/login")
    public String formularioLogin() {
        return "tutor/login";
    }

    /* Procesa el inicio de sesión del tutor */
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


    // ==========================================
    // PERFIL Y EDICIÓN
    // ==========================================

    /* Muestra el perfil del tutor pasando la lista global de estudiantes */
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id, Model model) {
        Tutor tutor = tutorService.buscarPorId(id);

        if (tutor == null) {
            return "redirect:/tutor/login";
        }

        List<Estudiante> todosLosEstudiantes = estudianteService.listarTodos();

        model.addAttribute("tutor", tutor);
        model.addAttribute("tutorId", tutor.getId());
        model.addAttribute("estudiantes", todosLosEstudiantes);

        return "tutor/perfil";
    }

    /* Actualiza los datos institucionales del tutor */
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("id") Long id,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            @RequestParam("cargo") String cargo,
            @RequestParam("departamento") String departamento,
            @RequestParam("facultad") String facultad,
            @RequestParam("formacionMaxima") String formacionMaxima) {

        Tutor tutor = tutorService.buscarPorId(id);

        if (tutor != null) {
            tutor.setNombreCompleto(nombreCompleto);
            tutor.setCorreo(correo);
            tutor.setTelefono(telefono);
            tutor.setCargo(cargo);
            tutor.setDepartamento(departamento);
            tutor.setFacultad(facultad);
            tutor.setFormacionMaxima(formacionMaxima);

            tutorService.guardar(tutor);
        }

        return "redirect:/tutor/perfil/" + id;
    }


    // ==========================================
    // ESTUDIANTES ASIGNADOS Y ENTREGABLES
    // ==========================================

    /* Muestra TODOS los estudiantes registrados en la plataforma */
    @GetMapping("/estudiantes/{tutorId}")
    public String estudiantes(@PathVariable Long tutorId, Model model) {
        List<Estudiante> estudiantes = estudianteService.listarTodos();

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("tutorId", tutorId);

        return "tutor/estudiantes";
    }

    /* Permite descargar o visualizar archivos de evidencias cargados por los estudiantes */
    @GetMapping("/evidencias/descargar/{id}")
    @ResponseBody
    public ResponseEntity<Resource> descargarEvidencia(@PathVariable Long id) {
        Evidencia evidencia = evidenciaService.listarTodos().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (evidencia == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(evidencia.getRutaArchivo());
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }


    // ==========================================
    // SEGUIMIENTO DE PRÁCTICAS Y EVALUACIONES
    // ==========================================

    /* Permite al tutor consultar las prácticas */
    @GetMapping("/practicas")
    public String practicas(
            @RequestParam(value = "tutorId", required = false, defaultValue = "1") Long tutorId,
            @RequestParam(value = "estado", required = false) String estado,
            Model model) {

        List<Practica> practicas;

        if (estado == null || estado.trim().isEmpty()) {
            practicas = practicaService.listarTodos();
        } else {
            practicas = practicaService.listarPorEstado(estado);
        }

        model.addAttribute("practicas", practicas);
        model.addAttribute("estado", estado);
        model.addAttribute("tutorId", tutorId);

        return "tutor/practicas";
    }

    /* Muestra las evaluaciones enviadas por las empresas al tutor */
    @GetMapping("/evaluaciones/{tutorId}")
    public String evaluaciones(@PathVariable Long tutorId, Model model) {
        List<Evaluacion> evaluaciones = evaluacionService.listarPorTutor(tutorId);

        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("tutorId", tutorId);

        return "tutor/evaluaciones";
    }


    // ==========================================
    // EMPRESAS Y CONVENIOS
    // ==========================================

    /* Lista las empresas registradas conservando el id del tutor */
    @GetMapping("/empresas")
    public String empresas(
            @RequestParam(value = "tutorId", required = false, defaultValue = "1") Long tutorId,
            Model model) {
        List<Empresa> empresas = empresaService.listarTodos();
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorId", tutorId);
        return "tutor/empresas";
    }

    /* Muestra el formulario para registrar una empresa */
    @GetMapping("/empresas/nueva")
    public String formularioNuevaEmpresa(
            @RequestParam(value = "tutorId", required = false, defaultValue = "1") Long tutorId,
            Model model) {
        model.addAttribute("empresa", new Empresa());
        model.addAttribute("tutorId", tutorId);
        return "tutor/empresa-form";
    }

    /* Guarda una empresa registrada por el tutor */
    @PostMapping("/empresas/guardar")
    public String guardarEmpresa(
            @RequestParam(value = "tutorId", required = false, defaultValue = "1") Long tutorId,
            @RequestParam(value = "archivoConvenio", required = false) MultipartFile archivoConvenio,
            @Valid @ModelAttribute("empresa") Empresa empresa,
            BindingResult resultado,
            Model model) {

        if (resultado.hasErrors()) {
            model.addAttribute("tutorId", tutorId);
            return "tutor/empresa-form";
        }

        // Si se adjuntó un archivo de convenio en PDF
        if (archivoConvenio != null && !archivoConvenio.isEmpty()) {
            try {
                File carpeta = new File(CARPETA_CONVENIOS);
                if (!carpeta.exists()) {
                    carpeta.mkdirs();
                }
                String nombreArchivo = System.currentTimeMillis() + "_" + archivoConvenio.getOriginalFilename();
                Path destino = Path.of(CARPETA_CONVENIOS, nombreArchivo);
                Files.copy(archivoConvenio.getInputStream(), destino);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        empresaService.guardar(empresa);
        return "redirect:/tutor/empresas?tutorId=" + tutorId;
    }
}