package br.edu.ifsp.biblioteca.dto;

import br.edu.ifsp.biblioteca.domain.Usuario;

public record UsuarioDetalhe(Long id, String nome, String email) {

    public static UsuarioDetalhe of(Usuario usuario) {

        return new UsuarioDetalhe(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }
}
