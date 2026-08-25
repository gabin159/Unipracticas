package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la empresa
    @Column(nullable = false, length = 150)
    private String nombreEmpresa;

    // NIT de la empresa
    @Column(nullable = false, unique = true, length = 20)
    private String nit;

    // Dirección de la empresa
    @Column(length = 200)
    private String direccion;

    // Teléfono de la empresa
    @Column(length = 20)
    private String telefono;

    // Correo electrónico de la empresa
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    // Sector económico de la empresa
    @Column(length = 100)
    private String sector;

    // Nombre de la persona de contacto
    // Ejemplo: Ana Sofía Giraldo
    @Column(length = 150)
    private String nombreContacto;

    // Contraseña para iniciar sesión
    @Column(nullable = false, length = 255)
    private String password;

    // Logo de la empresa
    @Column(length = 500)
    private String logo;

    // Una empresa puede tener varios estudiantes
    @OneToMany(mappedBy = "empresa")
    private List<Estudiante> estudiantes;

    // Una empresa puede publicar varias prácticas
    @OneToMany(
        mappedBy = "empresa",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Practica> practicas;

    // Una empresa puede realizar evaluaciones a varios estudiantes
    @OneToMany(mappedBy = "empresa")
    private List<Evaluacion> evaluaciones;
}