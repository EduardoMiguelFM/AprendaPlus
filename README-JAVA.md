# 📖 Java Advanced - Documentação Completa

## 📋 Requisitos Atendidos

### ✅ Anotações Spring para Beans e Injeção de Dependências

- Utilização de `@Service`, `@Repository`, `@Controller`, `@RestController`
- Injeção via `@Autowired` e construtores
- Configurações com `@Configuration` e `@Bean`

### ✅ Camada Model/DTO com Métodos de Acesso

- Entidades JPA com getters/setters ou Lombok
- DTOs para transferência de dados
- Validação com Bean Validation

### ✅ Persistência com Spring Data JPA

- Repositories estendendo `JpaRepository`
- Queries customizadas com `@Query`
- Paginação e ordenação

### ✅ Validação com Bean Validation

- Anotações `@NotNull`, `@Size`, `@Email`, etc.
- Validação customizada quando necessário
- Tratamento de erros de validação

### ✅ Caching para Performance

- Configuração com Caffeine Cache
- Anotações `@Cacheable`, `@CacheEvict`, `@CachePut`
- Cache de consultas frequentes

### ✅ Internacionalização (i18n)

- Suporte a Português (pt_BR) e Inglês (en)
- Arquivos `messages.properties` e `messages_en.properties`
- Resolução de locale via sessão

### ✅ Paginação

- Configuração padrão: 10 itens por página
- Máximo: 100 itens por página
- Uso de `Pageable` em repositories e controllers

### ✅ Spring Security

- Autenticação e autorização
- Configuração de rotas públicas e protegidas
- Integração com Thymeleaf

### ✅ Tratamento de Erros e Exceptions

- `@RestControllerAdvice` para tratamento global
- Exceções customizadas (`ResourceNotFoundException`)
- Respostas HTTP adequadas

### ✅ Mensageria com Filas Assíncronas

- RabbitMQ configurado
- Producers e Consumers
- Processamento assíncrono

### ✅ Spring AI - Inteligência Artificial Generativa

- Integração com OpenAI
- Configuração via `application.properties`
- Uso de IA generativa na aplicação

### ✅ API REST

- Verbos HTTP adequados (GET, POST, PUT, DELETE)
- Códigos de status HTTP corretos
- Documentação de endpoints

## 🏗️ Estrutura de Camadas

```
controller/     → Controllers REST e MVC
service/        → Lógica de negócio
repository/     → Acesso a dados (Spring Data JPA)
model/          → Entidades JPA
dto/            → Data Transfer Objects
config/         → Configurações (Security, Cache, i18n, RabbitMQ)
exception/      → Tratamento de exceções
message/        → Consumidores de mensageria
```

## 📝 Exemplo de Uso

### Controller REST

```java
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<Page<CursoDTO>> listar(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(cursoService.listar(pageable));
    }

    @PostMapping
    public ResponseEntity<CursoDTO> criar(@Valid @RequestBody CursoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(cursoService.criar(dto));
    }
}
```

### Service com Cache

```java
@Service
public class CursoService {

    @Cacheable(value = "cursos", key = "#id")
    public CursoDTO buscarPorId(Long id) {
        // Lógica de busca
    }

    @CacheEvict(value = "cursos", key = "#dto.id")
    public CursoDTO atualizar(CursoDTO dto) {
        // Lógica de atualização
    }
}
```

### Repository com Paginação

```java
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Page<Curso> findByAtivoTrue(Pageable pageable);

    @Query("SELECT c FROM Curso c WHERE c.nome LIKE %:nome%")
    Page<Curso> buscarPorNome(@Param("nome") String nome, Pageable pageable);
}
```

## 🔧 Configurações

### application.properties

```properties
# Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/aprenda_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Paginação
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=100

# Cache
spring.cache.type=caffeine

# i18n
spring.web.locale=pt_BR
spring.web.locale-resolver=session

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Spring AI
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

## 🧪 Testes

```bash
# Executar todos os testes
./gradlew test

# Executar testes com cobertura
./gradlew test jacocoTestReport
```

## 📚 Recursos Adicionais

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring AI](https://spring.io/projects/spring-ai)








