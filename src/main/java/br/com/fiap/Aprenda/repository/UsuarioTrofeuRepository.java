package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.UsuarioTrofeu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para entidade UsuarioTrofeu
 */
@Repository
public interface UsuarioTrofeuRepository extends JpaRepository<UsuarioTrofeu, Long> {

    List<UsuarioTrofeu> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndTrofeu_Id(Long usuarioId, Long trofeuId);
}
