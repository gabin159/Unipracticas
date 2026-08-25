package com.unipracticas.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del archivo o documento
    @Column(nullable = false, length = 150)
    private String nombreArchivo;

    // Tipo de documento:
    // RUT, CAMARA_COMERCIO, CERTIFICADO_EXISTENCIA,
    // POLIZA, CARTA_INTENCION, HOJA_DE_VIDA, etc.
    @Column(nullable = false, length = 50)
    private String tipoDocumento;

    // Ubicación donde se encuentra almacenado el archivo
    @Column(nullable = false, length = 300)
    private String rutaArchivo;

    // Fecha en la que se cargó el documento
    @Column(nullable = false)
    private LocalDate fechaSubida;

    // Documento perteneciente a una empresa
    // Una empresa puede tener varios documentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Documento perteneciente a un estudiante
    // Un estudiante puede tener varios documentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;
}