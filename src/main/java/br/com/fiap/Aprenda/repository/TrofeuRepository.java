package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.Trofeu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para entidade Trofeu
 */
@Repository
public interface TrofeuRepository extends JpaRepository<Trofeu, Long> {
    Optional<Trofeu> findByNome(String nome);
}
