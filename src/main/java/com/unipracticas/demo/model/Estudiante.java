package com.unipracticas.demo.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos personales
    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String ciudad;

    // Datos académicos
    @Column(nullable = false, length = 150)
    private String programaAcademico;

    @Column(nullable = false)
    private Integer semestre;

    @Column(length = 150)
    private String facultad;

    // Contraseña para iniciar sesión
    @Column(nullable = false, length = 255)
    private String password;

    // Un estudiante tiene un solo tutor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    // Un estudiante puede pertenecer a una sola empresa
    // cuando su postulación es aceptada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Un estudiante realiza una sola práctica
    @OneToOne(mappedBy = "estudiante")
    private Practica practica;

    // Un estudiante puede realizar varias postulaciones
    @OneToMany(
        mappedBy = "estudiante",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Postulacion> postulaciones;

    // Un estudiante puede subir varios documentos
    @OneToMany(
        mappedBy = "estudiante",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Documento> documentos;

    // Un estudiante puede subir varias evidencias
    @OneToMany(
        mappedBy = "estudiante",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Evidencia> evidencias;

    // Un estudiante puede tener evaluaciones
    @OneToMany(mappedBy = "estudiante")
    private List<Evaluacion> evaluaciones;
}
