package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.Desafio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para entidade Desafio
 */
@Repository
public interface DesafioRepository extends JpaRepository<Desafio, Long> {

    Page<Desafio> findByArea(String area, Pageable pageable);

    java.util.List<Desafio> findByArea(String area);

    Page<Desafio> findByNivel(String nivel, Pageable pageable);

    Page<Desafio> findByTipo(String tipo, Pageable pageable);

    Page<Desafio> findByAreaAndNivel(String area, String nivel, Pageable pageable);

    java.util.List<Desafio> findByAreaAndNivel(String area, String nivel);
}
