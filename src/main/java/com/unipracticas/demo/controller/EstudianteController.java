package com.unipracticas.demo.controller;

import com.unipracticas.demo.model.Documento;
import com.unipracticas.demo.model.Estudiante;
import com.unipracticas.demo.model.Evidencia;
import com.unipracticas.demo.model.Postulacion;
import com.unipracticas.demo.model.Practica;
import com.unipracticas.demo.model.Tutor;
import com.unipracticas.demo.service.DocumentoService;
import com.unipracticas.demo.service.EstudianteService;
import com.unipracticas.demo.service.EvidenciaService;
import com.unipracticas.demo.service.PostulacionService;
import com.unipracticas.demo.service.PracticaService;
import com.unipracticas.demo.service.TutorService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private TutorService tutorService;

    /*
     * Carpeta base donde se almacenarán los documentos y evidencias.
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
     * Inicia sesión validando las credenciales del estudiante.
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            Model model) {

        Estudiante estudiante = estudianteService.listarTodos().stream()
                .filter(e -> e.getCorreo() != null && e.getCorreo().equalsIgnoreCase(correo)
                        && e.getPassword() != null && e.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (estudiante == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "estudiante/login";
        }

        return "redirect:/estudiante/perfil/" + estudiante.getId();
    }

    /*
     * Muestra el perfil del estudiante junto con la lista completa de docentes/tutores.
     */
    @GetMapping("/perfil/{id}")
    public String perfil(
            @PathVariable Long id,
            Model model) {

        Estudiante estudiante = estudianteService.buscarPorId(id);

        if (estudiante == null) {
            return "redirect:/estudiante/login";
        }

        List<Tutor> tutores = tutorService.listarTodos();

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("tutores", tutores);

        return "estudiante/perfil";
    }

    /*
     * Actualiza los datos personales del estudiante desde el panel.
     */
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("id") Long id,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("programaAcademico") String programaAcademico,
            @RequestParam("semestre") Integer semestre) {

        Estudiante estudiante = estudianteService.buscarPorId(id);

        if (estudiante != null) {
            estudiante.setNombreCompleto(nombreCompleto);
            estudiante.setCorreo(correo);
            estudiante.setTelefono(telefono);
            estudiante.setCiudad(ciudad);
            estudiante.setProgramaAcademico(programaAcademico);
            estudiante.setSemestre(semestre);

            estudianteService.guardar(estudiante);
        }

        return "redirect:/estudiante/perfil/" + id;
    }

    /*
     * Lista las prácticas disponibles y evalúa si el estudiante ya posee una vinculación ACEPTADA.
     */
    @GetMapping("/practicas")
    public String practicas(
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "estudianteId", required = false) Long estudianteId,
            Model model) {

        List<Practica> practicas = practicaService.buscar(buscar);
        boolean tienePracticaAceptada = false;

        if (estudianteId != null) {
            List<Postulacion> misPostulaciones = postulacionService.listarPorEstudiante(estudianteId);
            
            List<Long> practicasPostuladasIds = misPostulaciones.stream()
                    .map(p -> p.getPractica().getId())
                    .toList();

            // Evaluar si alguna postulación del estudiante ya fue ACEPTADA
            tienePracticaAceptada = misPostulaciones.stream()
                    .anyMatch(p -> "ACEPTADA".equalsIgnoreCase(p.getEstado()));

            model.addAttribute("practicasPostuladasIds", practicasPostuladasIds);
        }

        model.addAttribute("practicas", practicas);
        model.addAttribute("buscar", buscar);
        model.addAttribute("estudianteId", estudianteId);
        model.addAttribute("tienePracticaAceptada", tienePracticaAceptada);

        return "estudiante/practicas";
    }

    /*
     * Registra la postulación del estudiante a una práctica.
     * Valida que no posea una postulación ACEPTADA previamente ni duplicados a la misma vacante.
     */
    @PostMapping("/postular")
    public String postular(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("practicaId") Long practicaId,
            RedirectAttributes redirectAttributes) {

        List<Postulacion> postulacionesExistentes = postulacionService.listarPorEstudiante(estudianteId);

        // 1. Bloqueo de postulaciones si el estudiante ya fue aceptado por alguna empresa
        boolean tieneAceptada = postulacionesExistentes.stream()
                .anyMatch(p -> "ACEPTADA".equalsIgnoreCase(p.getEstado()));

        if (tieneAceptada) {
            redirectAttributes.addFlashAttribute("error", "Ya te encuentras vinculado a una empresa. No puedes realizar nuevas postulaciones.");
            return "redirect:/estudiante/practicas?estudianteId=" + estudianteId;
        }

        // 2. Validación de duplicidad sobre la misma práctica
        boolean yaPostulado = postulacionesExistentes.stream()
                .anyMatch(p -> p.getPractica() != null && p.getPractica().getId().equals(practicaId));

        if (yaPostulado) {
            redirectAttributes.addFlashAttribute("error", "Ya te encuentras postulado a esta práctica.");
            return "redirect:/estudiante/practicas?estudianteId=" + estudianteId;
        }

        try {
            Estudiante estudiante = estudianteService.buscarPorId(estudianteId);
            Practica practica = practicaService.buscarPorId(practicaId);

            Postulacion postulacion = new Postulacion();
            postulacion.setEstudiante(estudiante);
            postulacion.setPractica(practica);
            postulacion.setEstado("PENDIENTE");
            postulacion.setFechaPostulacion(LocalDate.now());

            postulacionService.guardar(postulacion);
            redirectAttributes.addFlashAttribute("exito", "¡Te has postulado con éxito!");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Ya te encuentras postulado a esta práctica.");
            return "redirect:/estudiante/practicas?estudianteId=" + estudianteId;
        }

        return "redirect:/estudiante/postulaciones/" + estudianteId;
    }

    /*
     * Muestra todas las postulaciones realizadas por el estudiante.
     */
    @GetMapping("/postulaciones/{estudianteId}")
    public String postulaciones(
            @PathVariable Long estudianteId,
            Model model) {

        List<Postulacion> postulaciones = postulacionService.listarPorEstudiante(estudianteId);

        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("estudianteId", estudianteId);

        return "estudiante/postulaciones";
    }

    /*
     * Permite subir documentos (Hoja de Vida, Formato Vinculación, Informes, Reportes de Firmas).
     * Si es HOJA_DE_VIDA o FORMATO_VINCULACION, se elimina automáticamente la versión anterior.
     */
    @PostMapping("/documentos/subir")
    public String subirDocumento(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("archivo") MultipartFile archivo)
            throws IOException {

        Estudiante estudiante = estudianteService.buscarPorId(estudianteId);

        // Si es Hoja de Vida o Formato de Vinculación, eliminar versión anterior
        if ("HOJA_DE_VIDA".equals(tipoDocumento) || "FORMATO_VINCULACION".equals(tipoDocumento)) {
            List<Documento> existentes = documentoService.listarPorEstudiante(estudianteId);
            for (Documento doc : existentes) {
                if (tipoDocumento.equals(doc.getTipoDocumento())) {
                    try {
                        Files.deleteIfExists(Paths.get(doc.getRutaArchivo()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    documentoService.eliminar(doc.getId());
                }
            }
        }

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
     * Permite eliminar un documento guardado.
     */
    @PostMapping("/documentos/eliminar/{id}")
    public String eliminarDocumento(
            @PathVariable Long id,
            @RequestParam("estudianteId") Long estudianteId) {

        Documento documento = documentoService.listarTodos().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (documento != null) {
            try {
                Files.deleteIfExists(Paths.get(documento.getRutaArchivo()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            documentoService.eliminar(id);
        }

        return "redirect:/estudiante/documentos/" + estudianteId;
    }

    /*
     * Muestra todos los documentos del estudiante.
     */
    @GetMapping("/documentos/{estudianteId}")
    public String documentos(
            @PathVariable Long estudianteId,
            Model model) {

        List<Documento> documentos = documentoService.listarPorEstudiante(estudianteId);

        model.addAttribute("documentos", documentos);
        model.addAttribute("estudianteId", estudianteId);

        return "estudiante/documentos";
    }

    /*
     * Endpoint para descargar documentos.
     */
    @GetMapping("/documentos/descargar/{id}")
    @ResponseBody
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        Documento documento = documentoService.listarTodos().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (documento == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(documento.getRutaArchivo());
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNombreArchivo() + "\"")
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    /*
     * Permite subir evidencias asignando 'tipoActividad' para evitar violar restricciones NOT NULL.
     */
    @PostMapping("/evidencias/subir")
    public String subirEvidencia(
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "tipoActividad", required = false, defaultValue = "Avance General") String tipoActividad,
            @RequestParam("archivo") MultipartFile archivo)
            throws IOException {

        Estudiante estudiante = estudianteService.buscarPorId(estudianteId);

        String rutaArchivo = guardarArchivoEnDisco(
                archivo,
                CARPETA_UPLOADS + "evidencias/"
        );

        Evidencia evidencia = new Evidencia();
        evidencia.setEstudiante(estudiante);
        evidencia.setTitulo(titulo);
        evidencia.setDescripcion(descripcion);
        evidencia.setTipoActividad(tipoActividad);
        evidencia.setRutaArchivo(rutaArchivo);
        evidencia.setFechaSubida(LocalDate.now());

        evidenciaService.guardar(evidencia);

        return "redirect:/estudiante/evidencias/" + estudianteId;
    }

    /*
     * Permite actualizar la información de una evidencia guardada previamente.
     */
    @PostMapping("/evidencias/actualizar")
    public String actualizarEvidencia(
            @RequestParam("id") Long id,
            @RequestParam("estudianteId") Long estudianteId,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "tipoActividad", required = false, defaultValue = "Avance General") String tipoActividad) {

        Evidencia evidencia = evidenciaService.listarTodos().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (evidencia != null) {
            evidencia.setTitulo(titulo);
            evidencia.setDescripcion(descripcion);
            evidencia.setTipoActividad(tipoActividad);

            evidenciaService.guardar(evidencia);
        }

        return "redirect:/estudiante/evidencias/" + estudianteId;
    }

    /*
     * Permite eliminar una evidencia guardada.
     */
    @PostMapping("/evidencias/eliminar/{id}")
    public String eliminarEvidencia(
            @PathVariable Long id,
            @RequestParam("estudianteId") Long estudianteId) {

        evidenciaService.eliminar(id);

        return "redirect:/estudiante/evidencias/" + estudianteId;
    }

    /*
     * Muestra todas las evidencias del estudiante.
     */
    @GetMapping("/evidencias/{estudianteId}")
    public String evidencias(
            @PathVariable Long estudianteId,
            Model model) {

        List<Evidencia> evidencias = evidenciaService.listarPorEstudiante(estudianteId);

        model.addAttribute("evidencias", evidencias);
        model.addAttribute("estudianteId", estudianteId);

        return "estudiante/evidencias";
    }

    /*
     * Manejador de excepciones para exceso de tamaño de archivo al subir.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String manejarExcesoTamano(
            MaxUploadSizeExceededException exc,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", "El archivo excede el tamaño máximo permitido (50 MB). Por favor adjunta un archivo más liviano.");
        return "redirect:/estudiante/evidencias/1";
    }

    /*
     * Guarda un archivo en el sistema de archivos y normaliza separadores de ruta.
     */
    private String guardarArchivoEnDisco(
            MultipartFile archivo,
            String carpetaDestino)
            throws IOException {

        File carpeta = new File(carpetaDestino);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreFinal = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
        Path destino = Path.of(carpetaDestino, nombreFinal);

        Files.copy(archivo.getInputStream(), destino);

        return destino.toString().replace("\\", "/");
    }
}