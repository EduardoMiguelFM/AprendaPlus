# 📚 Flyway - Migrações de Banco de Dados

## O que é Flyway?

O **Flyway** é uma ferramenta de migração de banco de dados que permite versionar e controlar mudanças no schema do banco de forma organizada e automatizada.

## 🎯 Vantagens

- ✅ **Versionamento**: Cada mudança no banco é versionada
- ✅ **Histórico**: Mantém histórico de todas as migrações executadas
- ✅ **Automático**: Executa migrações automaticamente ao iniciar a aplicação
- ✅ **Seguro**: Não executa migrações já aplicadas
- ✅ **Controle**: Facilita trabalho em equipe e deploy

## 📁 Estrutura de Arquivos

As migrações devem seguir a convenção de nomenclatura:

```
V{versão}__{descrição}.sql
```

Exemplo:

- `V1__Criar_schema_inicial.sql`
- `V2__Corrigir_sequence_usuarios.sql`
- `V3__Adicionar_campo_novo.sql`

## 🔧 Como Funciona

1. **Ao iniciar a aplicação**, o Flyway verifica quais migrações já foram executadas
2. **Executa automaticamente** as migrações pendentes na ordem de versão
3. **Registra** no banco de dados (tabela `flyway_schema_history`) quais migrações foram aplicadas
4. **Valida** que as migrações não foram alteradas após serem executadas

## 📝 Criando uma Nova Migração

1. Crie um novo arquivo SQL seguindo a convenção:

   ```
   V{próximo_numero}__{descrição}.sql
   ```

2. Exemplo: `V3__Adicionar_tabela_comentarios.sql`

3. Escreva o SQL da migração:

   ```sql
   CREATE TABLE comentarios (
       id BIGSERIAL PRIMARY KEY,
       texto TEXT NOT NULL,
       criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

4. Ao reiniciar a aplicação, a migração será executada automaticamente

## ⚠️ Regras Importantes

1. **NUNCA altere** uma migração que já foi executada em produção
2. **Sempre crie uma nova migração** para fazer alterações
3. **Use IF NOT EXISTS** para evitar erros se a migração for executada novamente
4. **Teste** as migrações em ambiente de desenvolvimento primeiro

## 🔍 Verificar Status das Migrações

O Flyway cria uma tabela `flyway_schema_history` no banco de dados que contém:

- Versão da migração
- Descrição
- Tipo (SQL, Java, etc.)
- Data de execução
- Status (Success, Failed, etc.)

## 🚀 Comandos Úteis

### Ver migrações pendentes

O Flyway mostra no log da aplicação quais migrações foram executadas.

### Desabilitar Flyway temporariamente

No `application.properties`:

```properties
spring.flyway.enabled=false
```

### Limpar banco (CUIDADO!)

```properties
spring.flyway.clean-disabled=false
```

**ATENÇÃO**: Isso apagará todos os dados! Use apenas em desenvolvimento.

## 📚 Documentação Oficial

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot + Flyway](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

