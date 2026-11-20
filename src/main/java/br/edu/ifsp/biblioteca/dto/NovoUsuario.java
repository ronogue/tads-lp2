package br.edu.ifsp.biblioteca.dto;

import br.edu.ifsp.biblioteca.domain.Usuario;

public record NovoUsuario(String nome, String email) {
    public Usuario toEntity() {
        return new Usuario(this.nome, this.email);
    }
}
