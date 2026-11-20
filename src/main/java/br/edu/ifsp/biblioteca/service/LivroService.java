package br.edu.ifsp.biblioteca.service;

import br.edu.ifsp.biblioteca.domain.Autor;
import br.edu.ifsp.biblioteca.domain.Exemplar;
import br.edu.ifsp.biblioteca.domain.Livro;
import br.edu.ifsp.biblioteca.dto.LivroDetalhe;
import br.edu.ifsp.biblioteca.dto.NovoLivro;
import br.edu.ifsp.biblioteca.exception.RegraDeNegocioException;
import br.edu.ifsp.biblioteca.repository.ILivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    private final ILivroRepository livroRepository;

    public LivroService(ILivroRepository repository) {
        this.livroRepository = repository;
    }

    @Transactional
    public LivroDetalhe cadastrar(NovoLivro novoLivro) {

        Livro livro = novoLivro.toEntity();

        if (livro.getTitulo() == null || livro.getTitulo().isEmpty()) {
            throw new RegraDeNegocioException("O Título é Obrigatório!");
        }

        if (livro.getIsbn() == null || livro.getIsbn().isEmpty()) {
            throw new RegraDeNegocioException("ISBN é Obrigatório!");
        }

        if (livro.getAnoPublicacao() <= 0) {
            throw new RegraDeNegocioException("O Ano de Publicação deve ser maior que 0");
        }

        Optional<Livro> livroJaCadastradoOptional = this.livroRepository
            .findByIsbn(livro.getIsbn());

        if (livroJaCadastradoOptional.isPresent()) {
            throw new RegraDeNegocioException(
                "Já existe um livro cadastrado com o ISBN: " + livro.getIsbn()
            );
        }

        Livro livroCadastrado = this.livroRepository.save(livro);

        return LivroDetalhe.of(livroCadastrado);
    }

    @Transactional(readOnly = true)
    public LivroDetalhe adicionarExemplar(Long livroId, String codigoDoExemplar) {

        Livro livro = this.livroRepository.findById(livroId)
            .orElseThrow(
                () -> new RegraDeNegocioException("Livro não encontrado: " + livroId)
            );

        livro.adicionarExemplar(new Exemplar(codigoDoExemplar, livro));

        return LivroDetalhe.of(this.livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public LivroDetalhe adicionarAutor(Long livroId, String nomeAutor) {

        Livro livroEncontrado = this.livroRepository.findById(livroId)
            .orElseThrow(
                () -> new RegraDeNegocioException("Livro não encontrado: " + livroId)
            );

        livroEncontrado.adicionarAutor(new Autor(nomeAutor));

        return LivroDetalhe.of(this.livroRepository.save(livroEncontrado));
    }

    @Transactional(readOnly = true)
    public LivroDetalhe buscarPorId(Long id) {

        Optional<Livro> livroOptional = this.livroRepository.findById(id);

        if (livroOptional.isPresent()) {
            return LivroDetalhe.of(livroOptional.get());
        }

        throw new RegraDeNegocioException("Livro não encontrado: " + id);
    }

    @Transactional(readOnly = true)
    public List<LivroDetalhe> buscarPorTitulo(String trechoDoTitulo) {
        return this.livroRepository.findByTituloContainingIgnoreCase(trechoDoTitulo)
                .stream()
                .map(LivroDetalhe::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LivroDetalhe> listarTodos() {
        return this.livroRepository.findAll()
                .stream()
                .map(LivroDetalhe::of)
                .toList();
    }
}
