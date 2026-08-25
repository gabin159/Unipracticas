package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "practicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Practica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Información básica de la práctica */
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String requisitos;

    /* Detalles de la práctica */
    @Column(length = 100)
    private String area;

    @Column(length = 50)
    private String modalidad;

    @Column(length = 50)
    private String duracion;

    @Column(nullable = false)
    private Integer vacantes;

    @Column(length = 150)
    private String ciudadUbicacion;

    /* Estado de la práctica */
    @Column(length = 30)
    private String estado;

    /* Una práctica pertenece a una sola empresa */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    /* Una práctica puede tener varios estudiantes asignados,
       respetando el número de vacantes */
    @OneToMany(mappedBy = "practica")
    private List<Estudiante> estudiantes;

    /* Una práctica puede recibir muchas postulaciones */
    @OneToMany(
        mappedBy = "practica",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Postulacion> postulaciones;

    /* Una práctica puede tener varias evaluaciones */
    @OneToMany(mappedBy = "practica")
    private List<Evaluacion> evaluaciones;
}