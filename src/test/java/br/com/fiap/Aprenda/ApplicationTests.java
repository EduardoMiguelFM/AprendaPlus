package br.com.fiap.Aprenda;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.flyway.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
		"spring.ai.openai.chat.enabled=false"
})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
