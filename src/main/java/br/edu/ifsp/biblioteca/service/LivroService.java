package br.edu.ifsp.biblioteca.service;

import br.edu.ifsp.biblioteca.domain.Autor;
import br.edu.ifsp.biblioteca.domain.Exemplar;
import br.edu.ifsp.biblioteca.domain.Livro;
import br.edu.ifsp.biblioteca.exception.RegraDeNegocioException;
import br.edu.ifsp.biblioteca.repository.ILivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    private final ILivroRepository livroRepository;

    public LivroService(ILivroRepository repository) {
        this.livroRepository = repository;
    }

    public Livro cadastrar(Livro livro) {

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

        return livroCadastrado;
    }

    public Livro adicionarExemplar(Long livroId, String codigoDoExemplar) {

        Livro livro = this.buscarPorId(livroId);
        livro.adicionarExemplar(new Exemplar(codigoDoExemplar, livro));

        return this.livroRepository.save(livro);
    }

    public Livro adicionarAutor(Long livroId, String nomeAutor) {

        Livro livroEncontrado = this.buscarPorId(livroId);
        livroEncontrado.adicionarAutor(new Autor(nomeAutor));

        return this.livroRepository.save(livroEncontrado);
    }

    public Livro buscarPorId(Long id) {

        Optional<Livro> livroOptional = this.livroRepository.findById(id);

        if (livroOptional.isPresent()) {
            return livroOptional.get();
        }

        throw new RegraDeNegocioException("Livro não encontrado: " + id);
    }

    public List<Livro> buscarPorTitulo(String trechoDoTitulo) {
        return this.livroRepository.buscarPorTitulo(trechoDoTitulo);
    }

    public List<Livro> listarTodos() {
        return this.livroRepository.findAll();
    }
}
