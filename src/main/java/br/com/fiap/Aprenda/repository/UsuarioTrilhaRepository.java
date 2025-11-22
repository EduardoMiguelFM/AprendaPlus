package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.UsuarioTrilha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade UsuarioTrilha
 */
@Repository
public interface UsuarioTrilhaRepository extends JpaRepository<UsuarioTrilha, Long> {

    List<UsuarioTrilha> findByUsuario_Id(Long usuarioId);

    Optional<UsuarioTrilha> findByUsuario_IdAndTrilha_Id(Long usuarioId, Long trilhaId);

    boolean existsByUsuario_IdAndTrilha_Id(Long usuarioId, Long trilhaId);
}
