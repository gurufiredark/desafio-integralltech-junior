package com.integralltech.chamados.repository;

import com.integralltech.chamados.model.Chamado;
import com.integralltech.chamados.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findBySetor(Setor setor);
}