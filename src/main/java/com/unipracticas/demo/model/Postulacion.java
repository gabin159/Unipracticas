package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "postulaciones",

    // Evita que un mismo estudiante se postule dos veces a la misma práctica
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"estudiante_id", "practica_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha en la que el estudiante realiza la postulación
    @Column(nullable = false)
    private LocalDate fechaPostulacion;

    // Estado de la postulación: PENDIENTE, ACEPTADA o RECHAZADA
    @Column(nullable = false, length = 30)
    private String estado;

    // Una postulación pertenece a un solo estudiante
    // Un estudiante puede realizar varias postulaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    // Una postulación pertenece a una sola práctica
    // Una práctica puede recibir varias postulaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;
}