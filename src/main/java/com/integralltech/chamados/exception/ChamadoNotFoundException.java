package com.integralltech.chamados.exception;

// Exceção do erro 404 - Not Found
public class ChamadoNotFoundException extends RuntimeException {
    
    public ChamadoNotFoundException(Long id) {
        super("Chamado não encontrado com id " + id);
    }
    
}
