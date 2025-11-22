package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.TransacaoPontos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para entidade TransacaoPontos
 */
@Repository
public interface TransacaoPontosRepository extends JpaRepository<TransacaoPontos, Long> {

    Page<TransacaoPontos> findByUsuario_IdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);
}
