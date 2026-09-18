package br.edu.ifsp.biblioteca.repository;

import br.edu.ifsp.biblioteca.domain.Livro;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class LivroRepositoryJdbc implements ILivroRepository {

    private final DataSource dataSource;

    public LivroRepositoryJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Livro salvar(Livro livro) {

        try(Connection conexao = this.dataSource.getConnection())
        {
            conexao.setAutoCommit(false);

            if (livro.getId() == null) {
                this.inserirLivro(conexao, livro);
            } else {
                this.atualizarLivro(conexao, livro);
            }

            this.salvarAutores(conexao, livro);
            this.salvarExemplares(conexao, livro);

        } catch (SQLException erro) {

        }

        return livro;
    }

    private void inserirLivro(Connection conexao, Livro livro) {

        String sql = "INSERT INTO livro(isbn, titulo, ano_publicacao) VALUES (?, ?, ?)";

        

    }

    private void atualizarLivro(Connection conexao, Livro livro) {

    }

    private void salvarAutores(Connection conexao, Livro livro) {

    }

    private void salvarExemplares(Connection conexao, Livro livro) {

    }

    @Override
    public List<Livro> listarTodos() {
        System.out.println("LivroRepositoryJdbc");
        return List.of();
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        System.out.println("LivroRepositoryJdbc");
        return Optional.empty();
    }

    @Override
    public Optional<Livro> buscarPorIsbn(String isbn) {
        System.out.println("LivroRepositoryJdbc");
        return Optional.empty();
    }

    @Override
    public List<Livro> buscarPorTitulo(String titulo) {
        System.out.println("LivroRepositoryJdbc");
        return List.of();
    }
}
