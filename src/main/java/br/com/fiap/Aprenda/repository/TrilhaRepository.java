package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.Trilha;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para entidade Trilha
 */
@Repository
public interface TrilhaRepository extends JpaRepository<Trilha, Long> {

    @EntityGraph(attributePaths = { "cursos", "desafios" })
    Page<Trilha> findByArea(String area, Pageable pageable);

    @EntityGraph(attributePaths = { "cursos", "desafios" })
    Page<Trilha> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "cursos", "desafios" })
    Optional<Trilha> findById(Long id);
}
