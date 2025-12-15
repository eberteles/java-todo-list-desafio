# 🚀 Todo List API - Desafio Stefanini

Esta é uma API RESTful para gerenciamento de tarefas (Todo List), desenvolvida em **Spring Boot 3** e **Java 21**, seguindo os princípios de Domain-Driven Design (DDD) e as boas práticas do mercado (GitFlow, Testes e Dockerização).

## 💡 Stack Tecnológico

| Categoria | Tecnologia | Versão |
| :--- | :--- | :--- |
| **Linguagem** | Java | 21 |
| **Framework** | Spring Boot | 3.x |
| **Banco de Dados** | SQL Server (via Docker) | 2019 |
| **Migração DB** | Flyway | - |
| **Mapeamento DTO** | MapStruct | - |
| **Documentação** | Springdoc-OpenAPI (Swagger UI) | - |
| **Contêineres** | Docker & Docker Compose | - |
| **Cloud & DevOps** | Google Cloud Platform (GCP) & GitHub Actions | - |

---

## ✨ Padrões de Desenvolvimento Utilizados

A arquitetura do projeto foi estruturada com foco em manutenibilidade, escalabilidade e separação de responsabilidades (SOLID).

| Padrão | Objetivo no Projeto |
| :--- | :--- |
| **DTOs & Records** | Padronizar a entrada e saída de dados da API, garantindo que apenas os dados necessários sejam trafegados. O uso de **Records** em Java 17+ garante imutabilidade e concisão. |
| **Mappers (MapStruct)** | Isolar a lógica de conversão entre DTOs, Domain Models e Entidades JPA, mantendo o código do `Service` limpo de código repetitivo de mapeamento. |
| **Flyway** | Gerenciar o **versionamento do banco de dados**, garantindo que as alterações de schema (DDL) sejam aplicadas de forma controlada e previsível em todos os ambientes (Local, Teste, Docker). |
| **Controller Advice** | Padronizar o tratamento de erros globais (Ex: `TaskNotFoundException`, Validações), garantindo que a API retorne respostas coerentes (JSON) e códigos de status HTTP corretos (400, 404). |
| **OpenAPI/Swagger** | Documentação automática dos endpoints da API, permitindo que qualquer consumidor entenda a estrutura de requisições, respostas e códigos de status. |
| **Basic Auth** | Implementação de autenticação simples e robusta (Spring Security) para proteger os endpoints da API contra acesso não autorizado. |
| **DDD (Domain Model)** | Manter uma separação clara entre as regras de negócio (`Task.java`) e os detalhes técnicos (persistência e API), aumentando a manutenibilidade do código. |
| **CI/CD** | Pipeline de integração e entrega contínua configurado. Todo Pull Request para a branch `main` dispara automaticamente o deploy da aplicação no ambiente Google Cloud. |

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

*   **Docker & Docker Compose** (Essencial para rodar o banco de dados e a aplicação containerizada)
*   **Java JDK 21** (Apenas se desejar rodar/debugar a aplicação localmente fora do Docker)

---

## 🐳 Guia de Execução

### 1. Modo Recomendado (Docker Compose)

Esta é a forma mais simples de rodar a aplicação, pois sobe tanto o banco de dados quanto a API em containers configurados.

Na raiz do projeto, execute:

```bash
docker-compose up --build -d
```

A API estará disponível em: `http://localhost:8080`

### 2. Modo Desenvolvimento (Local)

Caso queira rodar a aplicação via IDE (IntelliJ/Eclipse) ou Gradle para desenvolvimento:

1.  Suba apenas o banco de dados SQL Server via Docker:
    ```bash
    docker-compose up -d sqlserver
    ```
2.  Execute a aplicação via Gradle:
    ```bash
    ./gradlew bootRun
    ```

---

## 📖 Acesso à Documentação

Após iniciar a aplicação, você pode acessar a documentação interativa da API e testar os endpoints diretamente pelo navegador.

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Para facilitar ainda mais os testes, uma coleção do Postman foi disponibilizada com todos os endpoints pré-configurados.

- **Coleção Postman**: [Acessar Coleção](https://www.postman.com/gold-water-273200/workspace/java-todo-list-desafio/collection/17958433-76690ed1-2a81-4d44-aa2f-3d61582f0c2f?action=share&creator=17958433)

### 🔐 Credenciais de Acesso

Para utilizar os endpoints protegidos (via Swagger ou Postman), utilize as seguintes credenciais de Basic Auth:

| Username | Password |
| :--- | :--- |
| `stefuser` | `stef123` |

---

## 🧪 Estrutura de Testes

O projeto possui uma cobertura de testes robusta, validando o fluxo vertical completo da aplicação (Unitários, Mapeamento, Repositório e Integração Full).

Para executar todos os testes (Unitários e de Integração), use o comando:

```bash
./gradlew clean test
```

---

## ☁️ Deploy Automatizado

O projeto conta com um pipeline de CI/CD configurado via **GitHub Actions**.

Sempre que um Pull Request é aprovado e mergeado na branch `main`, o pipeline é acionado automaticamente para realizar o deploy da nova versão da API no ambiente **Google Cloud Platform (GCP)**.

| Serviço | Detalhe |
| :--- | :--- |
| **Plataforma** | Google Cloud Run |
| **Banco de Dados** | Cloud SQL for SQL Server Express |
| **Pipeline CI/CD** | GitHub Actions |
| **URL de Acesso** | [Acesse a API aqui](https://todo-list-api-service-540271331730.us-central1.run.app) |
| **Credenciais de Acesso** | HTTP Basic Auth (Usuário: `stefuser`, Senha: `stef123`) |