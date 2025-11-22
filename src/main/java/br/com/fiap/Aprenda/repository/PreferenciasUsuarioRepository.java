package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.PreferenciasUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para entidade PreferenciasUsuario
 */
@Repository
public interface PreferenciasUsuarioRepository extends JpaRepository<PreferenciasUsuario, Long> {

    Optional<PreferenciasUsuario> findByUsuario_Id(Long usuarioId);
}
