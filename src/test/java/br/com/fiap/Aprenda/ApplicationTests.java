package br.com.fiap.Aprenda;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.Aprenda.repository.CursoRepository;
import br.com.fiap.Aprenda.repository.DesafioRepository;
import br.com.fiap.Aprenda.repository.PerguntaDesafioRepository;
import br.com.fiap.Aprenda.repository.PreferenciasUsuarioRepository;
import br.com.fiap.Aprenda.repository.TransacaoPontosRepository;
import br.com.fiap.Aprenda.repository.TrilhaRepository;
import br.com.fiap.Aprenda.repository.TrofeuRepository;
import br.com.fiap.Aprenda.repository.UsuarioCursoRepository;
import br.com.fiap.Aprenda.repository.UsuarioDesafioRepository;
import br.com.fiap.Aprenda.repository.UsuarioRepository;
import br.com.fiap.Aprenda.repository.UsuarioTrilhaRepository;
import br.com.fiap.Aprenda.repository.UsuarioTrofeuRepository;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@ActiveProfiles("test")
class ApplicationTests {

	@MockBean
	private UsuarioRepository usuarioRepository;

	@MockBean
	private CursoRepository cursoRepository;

	@MockBean
	private DesafioRepository desafioRepository;

	@MockBean
	private TrilhaRepository trilhaRepository;

	@MockBean
	private UsuarioCursoRepository usuarioCursoRepository;

	@MockBean
	private UsuarioDesafioRepository usuarioDesafioRepository;

	@MockBean
	private UsuarioTrilhaRepository usuarioTrilhaRepository;

	@MockBean
	private PreferenciasUsuarioRepository preferenciasUsuarioRepository;

	@MockBean
	private TrofeuRepository trofeuRepository;

	@MockBean
	private UsuarioTrofeuRepository usuarioTrofeuRepository;

	@MockBean
	private TransacaoPontosRepository transacaoPontosRepository;

	@MockBean
	private PerguntaDesafioRepository perguntaDesafioRepository;

	@Test
	void contextLoads() {
		// Testa se o contexto Spring carrega corretamente
		// Os repositórios são mockados para evitar necessidade de banco de dados
	}

}
