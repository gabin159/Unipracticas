package com.unipracticas.demo.controller;

import com.unipracticas.demo.model.Documento;
import com.unipracticas.demo.model.Estudiante;
import com.unipracticas.demo.model.Evidencia;
import com.unipracticas.demo.model.Postulacion;
import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.service.DocumentoService;
import com.unipracticas.demo.service.EstudianteService;
import com.unipracticas.demo.service.EvidenciaService;
import com.unipracticas.demo.service.PostulacionService;
import com.unipracticas.demo.service.PracticaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private PracticaService practicaService;

    @Autowired
    private PostulacionService postulacionService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private EvidenciaService evidenciaService;

    /*
     * Carpeta donde se almacenarán los documentos y evidencias
     * que suban los estudiantes.
     */
    private final String CARPETA_UPLOADS = "uploads/estudiantes/";

    /*
     * Muestra el formulario de registro del estudiante.
     */
    @GetMapping("/registro")
    public String formularioRegistro(Model model) {

        model.addAttribute("estudiante", new Estudiante());

        return "estudiante/registro";
    }

    /*
     * Registra un nuevo estudiante en la base de datos.
     */
    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("estudiante") Estudiante estudiante,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return "estudiante/registro";
        }

        estudianteService.guardar(estudiante);

        return "redirect:/estudiante/login";
    }

    /*
     * Muestra el formulario de inicio de sesión.
     */
    @GetMapping("/login")
    public String formularioLogin() {

        return "estudiante/login";
    }

    /*
     * Inicia sesión validando el correo y la contraseña
     * registrados por el estudiante.
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            Model model) {

        Estudiante estudiante = estudianteService.listarTodos().stream()
                .filter(e ->
                        e.getCorreo().equalsIgnoreCase(correo)
                                && e.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (estudiante == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "estudiante/login";
        }

        return "redirect:/estudiante/perfil/" + estudiante.getId();
    }

    /*
     * Muestra el perfil del estudiante.
     */
    @GetMapping("/perfil/{id}")
    public String perfil(
            @PathVariable Long id,
            Model model) {

        Estudiante estudiante = estudianteService.buscarPorId(id);

        model.addAttribute("estudiante", estudiante);

        return "estudiante/perfil";
    }

    /*
     * Lista las prácticas disponibles y permite buscarlas.
     */
    @GetMapping("/practicas")
    public String practicas(
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Practica> practicas =
                practicaService.buscar(buscar);

        model.addAttribute("practicas", practicas);
        model.addAttribute("buscar", buscar);

        return "estudiante/practicas";
    }

    /*
     * Registra la postulación de un estudiante a una práctica.
     * La postulación queda inicialmente en estado PENDIENTE.
     */
    @PostMapping("/postular")
    public String postular(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("practicaId") Long practicaId) {

        Estudiante estudiante =
                estudianteService.buscarPorId(estudianteId);

        Practica practica =
                practicaService.buscarPorId(practicaId);

        Postulacion postulacion = new Postulacion();

        postulacion.setEstudiante(estudiante);
        postulacion.setPractica(practica);
        postulacion.setEstado("PENDIENTE");
        postulacion.setFechaPostulacion(LocalDate.now());

        postulacionService.guardar(postulacion);

        return "redirect:/estudiante/postulaciones/" + estudianteId;
    }

    /*
     * Muestra todas las postulaciones realizadas por el estudiante.
     */
    @GetMapping("/postulaciones/{estudianteId}")
    public String postulaciones(
            @PathVariable Long estudianteId,
            Model model) {

        List<Postulacion> postulaciones =
                postulacionService.listarPorEstudiante(estudianteId);

        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("estudianteId", estudianteId);

        return "estudiante/postulaciones";
    }

    /*
     * Permite subir documentos como hoja de vida,
     * formatos o informes.
     */
    @PostMapping("/documentos/subir")
    public String subirDocumento(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("archivo") MultipartFile archivo)
            throws IOException {

        Estudiante estudiante =
                estudianteService.buscarPorId(estudianteId);

        String rutaArchivo = guardarArchivoEnDisco(
                archivo,
                CARPETA_UPLOADS + "documentos/"
        );

        Documento documento = new Documento();

        documento.setEstudiante(estudiante);
        documento.setTipoDocumento(tipoDocumento);
        documento.setNombreArchivo(archivo.getOriginalFilename());
        documento.setRutaArchivo(rutaArchivo);
        documento.setFechaSubida(LocalDate.now());

        documentoService.guardar(documento);

        return "redirect:/estudiante/documentos/" + estudianteId;
    }

    /*
     * Muestra todos los documentos del estudiante.
     */
    @GetMapping("/documentos/{estudianteId}")
    public String documentos(
            @PathVariable Long estudianteId,
            Model model) {

        List<Documento> documentos =
                documentoService.listarPorEstudiante(estudianteId);

        model.addAttribute("documentos", documentos);
        model.addAttribute("estudianteId", estudianteId);

        return "estudiante/documentos";
    }

    /*
     * Permite subir evidencias de la práctica,
     * como fotografías y comentarios.
     */
    @PostMapping("/evidencias/subir")
    public String subirEvidencia(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("archivo") MultipartFile archivo)
            throws IOException {

        Estudiante estudiante =
                estudianteService.buscarPorId(estudianteId);

        String rutaArchivo = guardarArchivoEnDisco(
                archivo,
                CARPETA_UPLOADS + "evidencias/"
        );

        Evidencia evidencia = new Evidencia();

        evidencia.setEstudiante(estudiante);
        evidencia.setTitulo(titulo);
        evidencia.setDescripcion(descripcion);
        evidencia.setRutaArchivo(rutaArchivo);
        evidencia.setFechaSubida(LocalDate.now());

        evidenciaService.guardar(evidencia);

        return "redirect:/estudiante/evidencias/" + estudianteId;
    }

    /*
     * Muestra todas las evidencias del estudiante.
     */
    @GetMapping("/evidencias/{estudianteId}")
    public String evidencias(
            @PathVariable Long estudianteId,
            Model model) {

        List<Evidencia> evidencias =
                evidenciaService.listarPorEstudiante(estudianteId);

        model.addAttribute("evidencias", evidencias);
        model.addAttribute("estudianteId", estudianteId);

        return "estudiante/evidencias";
    }

    /*
     * Guarda un archivo en la carpeta correspondiente
     * y devuelve la ruta donde quedó almacenado.
     */
    private String guardarArchivoEnDisco(
            MultipartFile archivo,
            String carpetaDestino)
            throws IOException {

        File carpeta = new File(carpetaDestino);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreFinal =
                System.currentTimeMillis()
                        + "_"
                        + archivo.getOriginalFilename();

        Path destino =
                Path.of(carpetaDestino, nombreFinal);

        Files.copy(archivo.getInputStream(), destino);

        return destino.toString();
    }
}
