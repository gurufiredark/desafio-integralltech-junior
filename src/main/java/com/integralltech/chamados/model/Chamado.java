package com.integralltech.chamados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "chamados")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "titulo: campo obrigatorio")
    @Size(min = 5, message = "titulo: deve ter no minimo 5 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "descricao: campo obrigatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "setor: campo obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Setor setor;

    @NotNull(message = "prioridade: campo obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ABERTO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    @NotBlank(message = "solicitante: campo obrigatorio")
    @Column(nullable = false)
    private String solicitante;
}