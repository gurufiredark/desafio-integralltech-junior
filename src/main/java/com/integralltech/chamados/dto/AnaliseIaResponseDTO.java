package com.integralltech.chamados.dto;

import com.integralltech.chamados.model.Prioridade;
import com.integralltech.chamados.model.Setor;

import java.time.LocalDateTime;

public record AnaliseIaResponseDTO(
        Long chamadoId,
        Prioridade prioridadeSugerida,
        Setor setorSugerido,
        String resumo,
        LocalDateTime analisadoEm
) {}
