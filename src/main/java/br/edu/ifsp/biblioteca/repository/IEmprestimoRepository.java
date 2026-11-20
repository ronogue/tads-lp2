package br.edu.ifsp.biblioteca.repository;

import br.edu.ifsp.biblioteca.domain.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IEmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    /**
     * Em sql seria algo como:
     *  SELECT * FROM emprestimo WHERE usuario_id = ? AND data_devolucao IS NULL
     */
    List<Emprestimo> findByUsuarioIdAndDataDevolucaoIsNull(Long usuarioId);

    long countByUsuarioIdAndDataDevolucaoIsNull(Long usuarioId);

    List<Emprestimo> findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate data);

    List<Emprestimo> findByExemplarIdAndDataDevolucaoIsNull(Long exemplarId);

    // JPQL
    @Query(
        "SELECT e FROM Emprestimo e " +
        "WHERE e.usuario.id = :usuarioId AND e.dataDevolucao IS NULL"
    )
    List<Emprestimo> listarEmprestimosAtivos(@Param("usuarioId") Long usuarioId);


    @Query(
        "SELECT e FROM Emprestimo e " +
        "JOIN FETCH e.usuario " +
        "JOIN FETCH e.exemplar ex " +
        "JOIN FETCH ex.livro " +
        "WHERE e.dataDevolucao IS NULL " +
        "ORDER BY e.dataPrevistaDevolucao "
    )
    List<Emprestimo> listarAtivosComDetalhes();

    @Query(
        "SELECT e FROM Emprestimo e " +
        "JOIN FETCH e.usuario " +
        "JOIN FETCH e.exemplar ex " +
        "JOIN FETCH ex.livro " +
        "WHERE e.dataDevolucao IS NULL AND e.usuario.id = :usuarioId " +
        "ORDER BY e.dataPrevistaDevolucao "
    )
    List<Emprestimo> listarAtivosDoUsuarioComDetalhes(@Param("usuarioId") Long usuarioId);


    @Query(
        "SELECT e FROM Emprestimo e " +
        "JOIN FETCH e.usuario " +
        "JOIN FETCH e.exemplar ex " +
        "JOIN FETCH ex.livro " +
        "WHERE e.dataDevolucao IS NULL AND e.dataPrevistaDevolucao < :hoje " +
        "ORDER BY e.dataPrevistaDevolucao "
    )
    List<Emprestimo> listarAtrasadosComDetalhes(@Param("hoje") LocalDate hoje);
}
