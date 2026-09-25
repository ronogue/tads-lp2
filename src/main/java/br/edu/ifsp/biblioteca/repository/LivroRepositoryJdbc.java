package br.edu.ifsp.biblioteca.repository;

import br.edu.ifsp.biblioteca.domain.Autor;
import br.edu.ifsp.biblioteca.domain.EStatusExemplar;
import br.edu.ifsp.biblioteca.domain.Exemplar;
import br.edu.ifsp.biblioteca.domain.Livro;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
            throw new IllegalStateException("Falha ao salvar o livro", erro);
        }

        return livro;
    }

    private void inserirLivro(Connection conexao, Livro livro) throws SQLException {

        String sql = "INSERT INTO livro(isbn, titulo, ano_publicacao) VALUES (?, ?, ?)";

        try (
            PreparedStatement comando = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            )
        ) {

            comando.setString(1, livro.getIsbn());
            comando.setString(2, livro.getTitulo());
            comando.setObject(3, livro.getAnoPublicacao());
            comando.executeUpdate();

            try (ResultSet rs = comando.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setId(rs.getLong(1));
                }
            }
        }
    }

    private void atualizarLivro(Connection conexao, Livro livro) throws SQLException {

        String sql = "UPDATE livro SET isbn = ?, titulo = ?, ano_publicao = ? WHERE id = ?";


        try (PreparedStatement  comando = conexao.prepareStatement(sql)) {

            comando.setString(1, livro.getIsbn());
            comando.setString(2, livro.getTitulo());
            comando.setObject(3, livro.getAnoPublicacao());
            comando.setLong(4, livro.getId());

            comando.executeUpdate();
        }
    }

    private void salvarAutores(Connection conexao, Livro livro) throws SQLException {

        for (Autor autor : livro.getAutores()) {

            if (autor.getId() == null) {
                String sql = "INSERT INTO autor(nome) VALUES(?)";

                try(
                    PreparedStatement comando = conexao.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS
                    )
                ) {
                    comando.setString(1, autor.getNome());
                    comando.executeUpdate();

                    try (ResultSet rs = comando.getGeneratedKeys()) {
                        if (rs.next()) {
                            autor.setId(rs.getLong(1));
                        }
                    }
                }
            }

            String sqlTblIntermediaria =  "INSERT INTO livro_autor " +
                    "(livro_id, autor_id) VALUES(?, ?)";

            try (PreparedStatement comando = conexao.prepareStatement(sqlTblIntermediaria)) {
                comando.setLong(1, livro.getId());
                comando.setLong(2, autor.getId());
                comando.executeUpdate();
            }
        }
    }

    private void salvarExemplares(Connection conexao, Livro livro) throws SQLException {

        for (Exemplar exemplar : livro.getExemplares()) {

            if (exemplar.getId() == null) {

                String sql = "INSERT INTO exemplar(codigo, status, livro_id) VALUES(?, ?, ?)";

                try (PreparedStatement comando =
                         conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {
                    comando.setString(1, exemplar.getCodigo());
                    comando.setString(2, exemplar.getStatus().name());
                    comando.setLong(3, livro.getId());

                    comando.executeUpdate();

                    try (ResultSet rs = comando.getGeneratedKeys()) {
                        if (rs.next()) {
                            exemplar.setId(rs.getLong(1));
                        }
                    }
                }
            } else {

                String sql = "UPDATE exemplar SET status = ? WHERE id = ?";

                try(PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, exemplar.getStatus().name());
                    comando.setLong(2, exemplar.getId());
                    comando.executeUpdate();
                }
            }
        }
    }

    private Optional<Livro> buscarUm(String sql, Object valorParamentro) {

        try (Connection conexao = this.dataSource.getConnection()) {

            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setObject(1, valorParamentro);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {

                    Livro livro = this.mapear(resultado);

                    this.carregarAutores(conexao, livro);
                    this.carregarExemplares(conexao, livro);

                    return Optional.of(livro);
                }

                return Optional.empty();
            }

        } catch (SQLException erro) {
            throw new IllegalStateException("Falha ao consultar livro", erro);
        }
    }

    private void carregarAutores(Connection conexao, Livro livro) throws SQLException {

        String sql = "SELECT a.* FROM autor a "
                + "JOIN livro_autor la ON la.autor_id = a.id "
                + "WHERE la.livro_id = ?";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setLong(1, livro.getId());

            try (ResultSet resultado = comando.executeQuery()) {

                while(resultado.next()) {

                    Autor autor = new Autor(resultado.getString("nome"));
                    autor.setId(resultado.getLong("id"));

                    livro.adicionarAutor(autor);
                }
            }
        }
    }

    private void carregarExemplares(Connection conexao, Livro livro) throws SQLException {

        String sql = "SELECT * FROM exemplar WHERE livro_id = ? ORDER BY codigo";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setLong(1, livro.getId());

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    Exemplar exemplar = new Exemplar(resultado.getString("codigo"), livro);
                    exemplar.setId(resultado.getLong("id"));

                    if (EStatusExemplar.valueOf(resultado.getString("status")) == EStatusExemplar.EMPRESTADO) {
                        exemplar.emprestar();
                    }

                    livro.adicionarExemplar(exemplar);
                }
            }
        }

    }

    private Livro mapear(ResultSet resultado) throws SQLException {

        Livro livro = new Livro(
            resultado.getString("isbn"),
            resultado.getString("titulo"),
            resultado.getInt("ano_publicacao")
        );

        livro.setId(resultado.getLong("id"));

        return livro;
    }

    @Override
    public List<Livro> listarTodos() {
        String sql = "SELECT * FROM livro ORDER BY titulo";

        try (Connection conexao  = this.dataSource.getConnection()) {

            PreparedStatement comando = conexao.prepareStatement(sql);

            return this.listar(conexao, comando);

        } catch (SQLException erro) {
            throw new IllegalStateException("Falha ao listar livros", erro);
        }
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return this.buscarUm("SELECT * FROM livro WHERE id = ?", id);
    }

    @Override
    public Optional<Livro> buscarPorIsbn(String isbn) {
        return this.buscarUm("SELECT * FROM livro WHERE isbn = ?", isbn);
    }

    @Override
    public List<Livro> buscarPorTitulo(String titulo) {

        String sql = "SELECT * FROM livro WHERE LOWER(titulo) LIKE ? ORDER BY titulo";

        try (Connection conexao = this.dataSource.getConnection()) {

            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setString(1, "%" + titulo + "%");

            return this.listar(conexao, comando);

        } catch (SQLException erro) {
            throw new IllegalStateException("Falha ao consultar livros", erro);
        }
    }

    private List<Livro> listar(Connection conexao, PreparedStatement comando) throws SQLException {

        List<Livro> livros = new ArrayList<>();

        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {

                Livro l = this.mapear(resultado);
                this.carregarAutores(conexao, l);
                this.carregarExemplares(conexao, l);

                livros.add(l);
            }
        }

        return livros;
    }
}
