package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.UsuarioCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade UsuarioCurso
 */
@Repository
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCurso, Long> {

    List<UsuarioCurso> findByUsuario_Id(Long usuarioId);

    List<UsuarioCurso> findByUsuario_IdAndStatus(Long usuarioId, String status);

    Optional<UsuarioCurso> findByUsuario_IdAndCurso_Id(Long usuarioId, Long cursoId);

    boolean existsByUsuario_IdAndCurso_Id(Long usuarioId, Long cursoId);
}
