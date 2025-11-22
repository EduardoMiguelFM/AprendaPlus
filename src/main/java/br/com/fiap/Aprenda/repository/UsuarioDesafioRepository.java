package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.UsuarioDesafio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade UsuarioDesafio
 */
@Repository
public interface UsuarioDesafioRepository extends JpaRepository<UsuarioDesafio, Long> {

    List<UsuarioDesafio> findByUsuario_Id(Long usuarioId);

    Optional<UsuarioDesafio> findByUsuario_IdAndDesafio_Id(Long usuarioId, Long desafioId);

    boolean existsByUsuario_IdAndDesafio_Id(Long usuarioId, Long desafioId);
}
