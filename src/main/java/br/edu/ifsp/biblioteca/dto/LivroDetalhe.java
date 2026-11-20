package br.edu.ifsp.biblioteca.dto;

import br.edu.ifsp.biblioteca.domain.Autor;
import br.edu.ifsp.biblioteca.domain.Livro;

import java.util.List;

public record LivroDetalhe(
    Long id,
    String isbn,
    String titulo,
    Integer anoPublicacao,
    List<String> autores,
    long exemplaresDisponiveis,
    int qtdExemplares
) {
    public static LivroDetalhe of(Livro livro) {
        return new LivroDetalhe(
            livro.getId(),
            livro.getIsbn(),
            livro.getTitulo(),
            livro.getAnoPublicacao(),
            livro.getAutores().stream().map(Autor::getNome).toList(),
            livro.quantidadeDisponivel(),
            livro.getExemplares().size()
        );
    }
}
