# Sistema de Chamados Técnicos — IntegrAllTech

API REST para gerenciamento de chamados de suporte técnico, desenvolvida com Java + Spring Boot como parte do processo seletivo IntegrAllTech 2026.

## Tecnologias

- Java 21
- Spring Boot 4.0
- Spring Data JPA
- Spring Validation
- Spring AI (integração com Groq)
- H2 Database (em memória)
- Lombok
- SpringDoc OpenAPI (Swagger UI)

## Como rodar

### Pré-requisitos

- Java 21 instalado
- Maven (ou usar o `./mvnw` incluso no projeto)
- Chave de API da Groq (gratuita em [console.groq.com](https://console.groq.com))

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/desafio-integralltech-junior.git
cd desafio-integralltech-junior
```

### 2. Configure a chave da IA

A aplicação usa a API da Groq (gratuita) para análise de chamados. Defina a variável de ambiente antes de rodar:

```bash
export GROQ_API_KEY=sua_chave_aqui
```

Ou passe diretamente no comando de execução:

```bash
GROQ_API_KEY=sua_chave_aqui ./mvnw spring-boot:run
```

> Sem a chave, a aplicação sobe normalmente — apenas o endpoint `/analisar` retornará erro ao ser chamado.

### 3. Suba a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

## Endpoints disponíveis

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/chamados` | Criar novo chamado |
| GET | `/api/chamados` | Listar todos os chamados |
| GET | `/api/chamados/{id}` | Buscar chamado por ID |
| PUT | `/api/chamados/{id}` | Atualizar chamado |
| DELETE | `/api/chamados/{id}` | Cancelar chamado (não deleta) |
| GET | `/api/chamados/setor/{setor}` | Filtrar por setor |
| POST | `/api/chamados/{id}/analisar` | Analisar chamado com IA |

### Valores válidos para enums

- **Setor:** `TI`, `MANUTENCAO`, `RH`, `FINANCEIRO`
- **Prioridade:** `BAIXA`, `MEDIA`, `ALTA`, `CRITICA`
- **Status:** `ABERTO`, `EM_ATENDIMENTO`, `RESOLVIDO`, `CANCELADO`

---

## Documentação interativa (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

Todos os endpoints estão documentados e podem ser testados diretamente pelo Swagger.

---

## Estrutura do projeto

```
src/main/java/com/integralltech/chamados/
├── controller/        # Camada de entrada HTTP (ChamadoController)
├── service/           # Regras de negócio (ChamadoService, IaService)
├── repository/        # Acesso ao banco (ChamadoRepository)
├── model/             # Entidade JPA e enums (Chamado, Setor, Prioridade, Status)
├── dto/               # Objetos de transferência de dados (Request, Response, Update, AnaliseResponse)
└── exception/         # Tratamento de erros (GlobalExceptionHandler, exceções customizadas)
```

---

## Regras de negócio implementadas

- Todo chamado nasce com status `ABERTO`
- `DELETE` não remove do banco — apenas muda o status para `CANCELADO` e preenche `dataFechamento`
- Não é possível atualizar ou cancelar um chamado com status `RESOLVIDO` ou `CANCELADO`
- `dataFechamento` é preenchida automaticamente ao mudar o status para `RESOLVIDO` ou `CANCELADO`
- O endpoint `/analisar` retorna a sugestão da IA sem alterar o chamado original