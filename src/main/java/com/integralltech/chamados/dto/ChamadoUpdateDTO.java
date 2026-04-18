package com.integralltech.chamados.dto;

import com.integralltech.chamados.model.Prioridade;
import com.integralltech.chamados.model.Setor;
import com.integralltech.chamados.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChamadoUpdateDTO(

        @NotBlank(message = "titulo: campo obrigatorio")
        @Size(min = 5, message = "titulo: deve ter no minimo 5 caracteres")
        String titulo,

        @NotBlank(message = "descricao: campo obrigatorio")
        String descricao,

        @NotNull(message = "setor: campo obrigatorio")
        Setor setor,

        @NotNull(message = "prioridade: campo obrigatorio")
        Prioridade prioridade,

        @NotNull(message = "status: campo obrigatorio")
        Status status,

        @NotBlank(message = "solicitante: campo obrigatorio")
        String solicitante
) {}