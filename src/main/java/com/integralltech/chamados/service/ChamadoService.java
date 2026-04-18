package com.integralltech.chamados.service;

import com.integralltech.chamados.dto.ChamadoRequestDTO;
import com.integralltech.chamados.dto.ChamadoResponseDTO;
import com.integralltech.chamados.exception.BusinessException;
import com.integralltech.chamados.exception.ChamadoNotFoundException;
import com.integralltech.chamados.model.Chamado;
import com.integralltech.chamados.model.Setor;
import com.integralltech.chamados.model.Status;
import com.integralltech.chamados.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;

    public ChamadoService(ChamadoRepository repository) {
        this.repository = repository;
    }

    public ChamadoResponseDTO criar(ChamadoRequestDTO dto) {
        Chamado chamado = new Chamado();
        chamado.setTitulo(dto.titulo());
        chamado.setDescricao(dto.descricao());
        chamado.setSetor(dto.setor());
        chamado.setPrioridade(dto.prioridade());
        chamado.setSolicitante(dto.solicitante());
        // status e dataAbertura sao preenchidos automaticamente

        return ChamadoResponseDTO.fromEntity(repository.save(chamado));
    }

    public List<ChamadoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ChamadoResponseDTO::fromEntity)
                .toList();
    }

    public ChamadoResponseDTO buscarPorId(Long id) {
        Chamado chamado = repository.findById(id)
                .orElseThrow(() -> new ChamadoNotFoundException(id));

        return ChamadoResponseDTO.fromEntity(chamado);
    }

    public ChamadoResponseDTO atualizar(Long id, ChamadoRequestDTO dto) {
        Chamado chamado = repository.findById(id)
                .orElseThrow(() -> new ChamadoNotFoundException(id));

        if (chamado.getStatus() == Status.CANCELADO || chamado.getStatus() == Status.RESOLVIDO) {
            throw new BusinessException("Nao e possivel atualizar um chamado " + chamado.getStatus());
        }

        chamado.setTitulo(dto.titulo());
        chamado.setDescricao(dto.descricao());
        chamado.setSetor(dto.setor());
        chamado.setPrioridade(dto.prioridade());
        chamado.setSolicitante(dto.solicitante());

        return ChamadoResponseDTO.fromEntity(repository.save(chamado));
    }

    public ChamadoResponseDTO cancelar(Long id) {
        Chamado chamado = repository.findById(id)
                .orElseThrow(() -> new ChamadoNotFoundException(id));

        if (chamado.getStatus() == Status.CANCELADO) {
            throw new BusinessException("Chamado ja esta cancelado");
        }

        if (chamado.getStatus() == Status.RESOLVIDO) {
            throw new BusinessException("Nao e possivel cancelar um chamado ja resolvido");
        }

        chamado.setStatus(Status.CANCELADO);
        chamado.setDataFechamento(LocalDateTime.now());

        return ChamadoResponseDTO.fromEntity(repository.save(chamado));
    }

    public List<ChamadoResponseDTO> listarPorSetor(Setor setor) {
        return repository.findBySetor(setor)
                .stream()
                .map(ChamadoResponseDTO::fromEntity)
                .toList();
    }
}