package com.integralltech.chamados.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integralltech.chamados.dto.AnaliseIaResponseDTO;
import com.integralltech.chamados.exception.BusinessException;
import com.integralltech.chamados.exception.ChamadoNotFoundException;
import com.integralltech.chamados.model.Chamado;
import com.integralltech.chamados.model.Prioridade;
import com.integralltech.chamados.model.Setor;
import com.integralltech.chamados.repository.ChamadoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IaService {

    private final ChamadoRepository repository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public IaService(ChamadoRepository repository,
                     ChatClient.Builder chatClientBuilder,
                     ObjectMapper objectMapper) {
        this.repository   = repository;
        this.chatClient   = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AnaliseIaResponseDTO analisar(Long id) {
        Chamado chamado = repository.findById(id)
                .orElseThrow(() -> new ChamadoNotFoundException(id));

        String prompt = montarPrompt(chamado);

        String resposta = chatClient
                .prompt(prompt)
                .call()
                .content();

        return parsearResposta(id, resposta);
    }

    // Prompt pra IA a responder APENAS em JSON
    private String montarPrompt(Chamado chamado) {
        return """
                Você é um assistente especializado em triagem de chamados de suporte técnico.
                Analise o chamado abaixo e responda SOMENTE com um JSON válido, sem texto extra,
                sem blocos de código markdown, sem explicações.

                Chamado:
                - Título: %s
                - Descrição: %s

                Responda exatamente neste formato JSON:
                {
                  "prioridadeSugerida": "<BAIXA|MEDIA|ALTA|CRITICA>",
                  "setorSugerido": "<TI|MANUTENCAO|RH|FINANCEIRO>",
                  "resumo": "<resumo do problema em até 2 frases>"
                }
                """.formatted(chamado.getTitulo(), chamado.getDescricao());
    }

    // Converte o JSON para o DTO, com tratamento de erros
    private AnaliseIaResponseDTO parsearResposta(Long id, String resposta) {
        try {
            // Remove blocos markdown caso a IA ignore a instrução
            String json = resposta
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode node = objectMapper.readTree(json);

            Prioridade prioridade = Prioridade.valueOf(
                    node.get("prioridadeSugerida").asText().toUpperCase()
            );
            Setor setor = Setor.valueOf(
                    node.get("setorSugerido").asText().toUpperCase()
            );
            String resumo = node.get("resumo").asText();

            return new AnaliseIaResponseDTO(id, prioridade, setor, resumo, LocalDateTime.now());

        } catch (IllegalArgumentException e) {
            // Return de enum inválido (ex: "URGENTE" em vez de "CRITICA")
            throw new BusinessException(
                    "A IA retornou um valor inválido para prioridade ou setor: " + e.getMessage()
            );
        } catch (Exception e) {
            // JSON malformado ou campo ausente
            throw new BusinessException(
                    "Não foi possível interpretar a resposta da IA. Tente novamente."
            );
        }
    }
}