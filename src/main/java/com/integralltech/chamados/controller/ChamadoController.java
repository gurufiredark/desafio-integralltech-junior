package com.integralltech.chamados.controller;

import com.integralltech.chamados.dto.ChamadoRequestDTO;
import com.integralltech.chamados.dto.ChamadoResponseDTO;
import com.integralltech.chamados.dto.ChamadoUpdateDTO;
import com.integralltech.chamados.dto.AnaliseIaResponseDTO;
import com.integralltech.chamados.model.Setor;
import com.integralltech.chamados.service.ChamadoService;
import com.integralltech.chamados.service.IaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    private final ChamadoService service;
    private final IaService iaService;

    public ChamadoController(ChamadoService service, IaService iaService) {
        this.service = service;
        this.iaService = iaService;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponseDTO> criar(@RequestBody @Valid ChamadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ChamadoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ChamadoUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @GetMapping("/setor/{setor}")
    public ResponseEntity<List<ChamadoResponseDTO>> listarPorSetor(@PathVariable Setor setor) {
        return ResponseEntity.ok(service.listarPorSetor(setor));
    }

    @PostMapping("/{id}/analisar")
    public ResponseEntity<AnaliseIaResponseDTO> analisar(@PathVariable Long id) {
        return ResponseEntity.ok(iaService.analisar(id));
    }
}