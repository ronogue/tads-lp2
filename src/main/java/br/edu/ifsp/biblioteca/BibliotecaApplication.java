package br.edu.ifsp.biblioteca;

import br.edu.ifsp.biblioteca.cli.CatalogoRunner;
import br.edu.ifsp.biblioteca.domain.Livro;
import br.edu.ifsp.biblioteca.repository.ILivroRepository;
import br.edu.ifsp.biblioteca.repository.LivroRepositoryEmMemoria;

import java.util.List;
import java.util.Optional;


public class BibliotecaApplication {
    public static void main(String[] args) {

        CatalogoRunner appExecutor = new CatalogoRunner();
        appExecutor.run();
    }
}
