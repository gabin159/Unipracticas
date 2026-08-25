package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "evaluaciones",
    uniqueConstraints = {
        // Evita que un estudiante tenga más de una evaluación
        // para la misma práctica
        @UniqueConstraint(
            columnNames = {"estudiante_id", "practica_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluacion {

    // Identificador único de la evaluación
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha en la que la empresa realizó la evaluación
    @Column(nullable = false)
    private LocalDate fecha;

    // Calificación obtenida por el estudiante en la evaluación
    private Double calificacion;

    // Comentarios adicionales realizados por la empresa
    @Column(columnDefinition = "TEXT")
    private String comentarios;

    // Estudiante que está siendo evaluado
    // Un estudiante puede tener una evaluación por cada práctica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    // Empresa que realiza la evaluación del estudiante
    // Una empresa puede realizar evaluaciones a varios estudiantes
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Tutor universitario que recibe y revisa la evaluación
    // Un tutor puede recibir evaluaciones de varios estudiantes
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    // Práctica a la que pertenece la evaluación
    // Una práctica puede tener varias evaluaciones,
    // una por cada estudiante seleccionado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;
}