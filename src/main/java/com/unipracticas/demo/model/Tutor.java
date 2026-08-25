package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Datos personales */
    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 200)
    private String direccion;

    /* Datos institucionales */
    @Column(length = 100)
    private String cargo;

    @Column(length = 150)
    private String departamento;

    @Column(length = 150)
    private String facultad;

    @Column(length = 150)
    private String formacionMaxima;

    /* Credenciales */
    @Column(nullable = false, length = 255)
    private String contrasena;

    /* Relaciones */
    @OneToMany(mappedBy = "tutor")
    private List<Estudiante> estudiantes;

    @OneToMany(mappedBy = "tutor")
    private List<Evaluacion> evaluaciones;
}