# Sistema de Chamados Técnicos

API REST para gerenciamento de chamados de suporte técnico, desenvolvida com Java + Spring Boot.

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
git clone https://github.com/gurufiredark/api-chamados-tecnico-ia.git
cd api-chamados-tecnico-ia
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

### Alternativa: rodando pela IDE

Se preferir usar a IDE em vez do terminal, tanto VS Code quanto IntelliJ têm suporte nativo para Spring Boot.

#### VS Code — Spring Boot Dashboard

1. Instale a extensão **[Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack)** (ela inclui o Spring Boot Dashboard)
2. Com o projeto aberto, clique no ícone do Spring Boot na barra lateral (Spring Boot Dashboard)
3. Seu projeto aparecerá listado — clique no ▶️ ao lado do nome para subir
4. Para passar a variável de ambiente, crie um arquivo `.env` na raiz **ou** configure em `.vscode/launch.json`:

```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot",
      "request": "launch",
      "mainClass": "com.integralltech.chamados.ChamadosApplication",
      "env": {
        "GROQ_API_KEY": "sua_chave_aqui"
      }
    }
  ]
}
```

#### IntelliJ IDEA

1. Abra o projeto normalmente (**File > Open**)
2. O IntelliJ detecta automaticamente a classe `main` anotada com `@SpringBootApplication` e cria uma Run Configuration
3. Para adicionar a variável de ambiente, vá em **Run > Edit Configurations**, selecione a configuração do projeto e preencha o campo **Environment variables**:
```
   GROQ_API_KEY=sua_chave_aqui
```
4. Clique no ▶️ (símbolo de play) no canto superior direito (ou `Shift + F10`) para rodar

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