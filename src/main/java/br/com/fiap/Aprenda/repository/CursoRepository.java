package br.com.fiap.Aprenda.repository;

import br.com.fiap.Aprenda.model.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para entidade Curso
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Page<Curso> findByArea(String area, Pageable pageable);

    Page<Curso> findByNivel(String nivel, Pageable pageable);

    Page<Curso> findByAreaAndNivel(String area, String nivel, Pageable pageable);

    boolean existsByAreaAndNivel(String area, String nivel);

    @Query("SELECT c FROM Curso c WHERE c.area IN :areas ORDER BY c.criadoEm DESC")
    List<Curso> findByAreas(@Param("areas") List<String> areas, Pageable pageable);
}
