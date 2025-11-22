# 📱 Mobile Development - Documentação

## 📋 Visão Geral

Este documento descreve a integração mobile com a aplicação Aprenda+.

## 🔗 Integração com Backend

A aplicação mobile consumirá a **API REST** desenvolvida no backend Spring Boot.

### Endpoints Disponíveis

#### Autenticação

```
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh
```

#### Cursos

```
GET    /api/cursos              # Listar cursos (com paginação)
GET    /api/cursos/{id}         # Buscar curso por ID
POST   /api/cursos              # Criar curso (admin)
PUT    /api/cursos/{id}         # Atualizar curso (admin)
DELETE /api/cursos/{id}         # Excluir curso (admin)
```

#### Inscrições

```
GET    /api/inscricoes          # Listar minhas inscrições
POST   /api/inscricoes          # Inscrever-se em curso
DELETE /api/inscricoes/{id}     # Cancelar inscrição
```

#### Usuário

```
GET    /api/usuarios/perfil     # Meu perfil
PUT    /api/usuarios/perfil     # Atualizar perfil
```

## 📱 Tecnologias Sugeridas

### React Native

- Framework multiplataforma (iOS e Android)
- Comunidade ativa
- Boa integração com APIs REST

### Flutter

- Framework multiplataforma
- Performance excelente
- UI nativa

### Android Nativo (Kotlin/Java)

- Desenvolvimento nativo Android
- Máximo controle e performance

### iOS Nativo (Swift)

- Desenvolvimento nativo iOS
- Máximo controle e performance

## 🔐 Autenticação

A API utiliza **HTTP Basic Authentication** para autenticação.

### Fluxo de Autenticação

1. App envia credenciais para `/api/auth/login` (opcional, para validação)
2. App usa HTTP Basic Authentication em todas as requisições:
   ```
   Authorization: Basic {base64(email:senha)}
   ```
3. O servidor valida as credenciais automaticamente em cada requisição

## 📡 Exemplo de Integração

### React Native (Axios)

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: "https://aprenda-plus.azurewebsites.net/api",
});

// Interceptor para adicionar autenticação HTTP Basic
api.interceptors.request.use((config) => {
  const email = AsyncStorage.getItem("email");
  const senha = AsyncStorage.getItem("senha");
  if (email && senha) {
    const credentials = btoa(`${email}:${senha}`);
    config.headers.Authorization = `Basic ${credentials}`;
  }
  return config;
});

// Exemplo: Listar cursos
export const listarCursos = async (page = 0, size = 10) => {
  const response = await api.get("/cursos", {
    params: { page, size },
  });
  return response.data;
};

// Exemplo: Login (opcional - valida credenciais)
export const login = async (email, senha) => {
  const response = await api.post("/auth/login", { email, senha });
  // Armazenar credenciais para HTTP Basic (use Secure Storage em produção)
  await AsyncStorage.setItem("email", email);
  await AsyncStorage.setItem("senha", senha);
  return response.data;
};
```

### Flutter (HTTP)

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ApiService {
  static const String baseUrl = 'https://aprenda-plus.azurewebsites.net/api';
  static String? token;

  static String? email;
  static String? senha;

  static Future<Map<String, dynamic>> login(String emailLogin, String senhaLogin) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': emailLogin, 'senha': senhaLogin}),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      email = emailLogin;
      senha = senhaLogin;
      return data;
    }
    throw Exception('Falha no login');
  }

  static String _getBasicAuth() {
    if (email != null && senha != null) {
      final credentials = base64Encode(utf8.encode('$email:$senha'));
      return 'Basic $credentials';
    }
    return '';
  }

  static Future<List<dynamic>> listarCursos({int page = 0, int size = 10}) async {
    final response = await http.get(
      Uri.parse('$baseUrl/cursos?page=$page&size=$size'),
      headers: {
        'Authorization': _getBasicAuth(),
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body)['content'];
    }
    throw Exception('Falha ao carregar cursos');
  }
}
```

## 🎨 Funcionalidades Sugeridas

### Tela de Login

- Campos: Email e Senha
- Validação de campos
- Feedback visual de erros
- Opção "Lembrar-me"

### Tela de Cursos

- Lista de cursos disponíveis
- Paginação (scroll infinito)
- Busca e filtros
- Detalhes do curso

### Tela de Detalhes do Curso

- Informações completas
- Botão de inscrição
- Lista de módulos/aulas
- Progresso (se inscrito)

### Tela de Minhas Inscrições

- Lista de cursos inscritos
- Progresso de cada curso
- Acesso rápido às aulas

### Tela de Perfil

- Informações do usuário
- Edição de perfil
- Configurações
- Logout

## 🔄 Sincronização Offline

Considere implementar:

- Cache local dos cursos
- Sincronização quando online
- Modo offline com dados em cache

## 📊 Testes Mobile

### Testes Unitários

- Lógica de negócio
- Validações
- Transformações de dados

### Testes de Integração

- Chamadas à API
- Autenticação
- Persistência local

### Testes E2E

- Fluxos completos
- Navegação
- Interações do usuário

## 🚀 Deploy

### Android

- Google Play Store
- APK para distribuição direta

### iOS

- App Store
- TestFlight para testes

## 📝 Checklist de Desenvolvimento

- [ ] Configurar projeto mobile
- [ ] Implementar autenticação
- [ ] Integrar com API REST
- [ ] Implementar telas principais
- [ ] Adicionar tratamento de erros
- [ ] Implementar loading states
- [ ] Adicionar validações
- [ ] Testes unitários
- [ ] Testes de integração
- [ ] Testes E2E
- [ ] Deploy para lojas

## 📚 Recursos Adicionais

- [React Native Documentation](https://reactnative.dev/)
- [Flutter Documentation](https://flutter.dev/)
- [Android Developer Guide](https://developer.android.com/)
- [iOS Developer Guide](https://developer.apple.com/ios/)

## 🔗 Links Úteis

- API Base URL: `https://aprenda-plus.azurewebsites.net/api`
- Documentação da API: [Swagger/OpenAPI quando disponível]
- Repositório Backend: [Link do repositório]
