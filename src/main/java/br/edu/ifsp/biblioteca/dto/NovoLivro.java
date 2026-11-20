package br.edu.ifsp.biblioteca.dto;

import br.edu.ifsp.biblioteca.domain.Autor;
import br.edu.ifsp.biblioteca.domain.Livro;

import java.util.List;

public record NovoLivro(
    String isbn,
    String titulo,
    Integer anoPublicacao,
    List<String> autores
) {
    public Livro toEntity() {
        Livro l = new Livro(
            this.isbn,
            this.titulo,
            this.anoPublicacao()
        );

        if (this.autores != null) {
            for (String nomeAutor : this.autores) {
                l.adicionarAutor(new Autor(nomeAutor));
            }
        }

        return l;
    }
}
