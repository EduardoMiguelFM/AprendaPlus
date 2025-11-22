package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.PerguntaDesafio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para entidade PerguntaDesafio
 */
@Repository
public interface PerguntaDesafioRepository extends JpaRepository<PerguntaDesafio, Long> {

    List<PerguntaDesafio> findByDesafio_Id(Long desafioId);
}
