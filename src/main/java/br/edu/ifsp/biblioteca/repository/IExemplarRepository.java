package br.edu.ifsp.biblioteca.repository;

import br.edu.ifsp.biblioteca.domain.EStatusExemplar;
import br.edu.ifsp.biblioteca.domain.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IExemplarRepository extends JpaRepository<Exemplar, Long>{

    Optional<Exemplar> findByCodigo(String codigo);

    List<Exemplar> findByLivroIdAndStatus(Long livroId, EStatusExemplar status);

    long countByLivroIdAndStatus(Long livroId, EStatusExemplar status);
}
