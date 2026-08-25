package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "evidencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título de la evidencia
    // Ejemplo: "Reunión de equipo – Semana 1"
    @Column(nullable = false, length = 150)
    private String titulo;

    // Tipo o categoría de la actividad
    // Ejemplos: TRABAJO_EQUIPO, PRESENTACION, REUNION,
    // CAPACITACION, DESARROLLO, INFORME, APRENDIZAJE, EMPRESA
    @Column(nullable = false, length = 50)
    private String tipoActividad;

    // Comentario realizado por el estudiante sobre la evidencia
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Ruta donde se encuentra almacenada la fotografía
    @Column(nullable = false, length = 300)
    private String rutaArchivo;

    // Fecha en la que el estudiante subió la evidencia
    @Column(nullable = false)
    private LocalDate fechaSubida;

    // Estudiante que creó y subió la evidencia
    // Un estudiante puede tener muchas evidencias
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
}