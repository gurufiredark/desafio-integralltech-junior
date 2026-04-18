package com.integralltech.chamados.dto;

import com.integralltech.chamados.model.Chamado;
import com.integralltech.chamados.model.Prioridade;
import com.integralltech.chamados.model.Setor;
import com.integralltech.chamados.model.Status;

import java.time.LocalDateTime;

public record ChamadoResponseDTO(
        Long id,
        String titulo,
        String descricao,
        Setor setor,
        Prioridade prioridade,
        Status status,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        String solicitante
) {
    public static ChamadoResponseDTO fromEntity(Chamado chamado) {
        return new ChamadoResponseDTO(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getSetor(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                chamado.getDataAbertura(),
                chamado.getDataFechamento(),
                chamado.getSolicitante()
        );
    }
}