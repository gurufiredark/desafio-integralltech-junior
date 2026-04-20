# Decisões Técnicas

---

## 1. Separação em camadas (Controller → Service → Repository)

Optei pela arquitetura em três camadas clássica do Spring Boot.

O **Controller** é responsável apenas por receber a requisição HTTP, delegar para o Service e retornar a resposta. Ele não contém nenhuma lógica de negócio.

O **Service** centraliza todas as regras de negócio — validação de status, preenchimento do `dataFechamento`, impedimento de reabertura. Isso facilita testes e manutenção: se uma regra mudar, mexo em um só lugar.

O **Repository** cuida exclusivamente do acesso ao banco via Spring Data JPA, sem lógica extra.

---

## 2. Uso de DTOs

Em vez de expor a entidade `Chamado` diretamente nas respostas, criei DTOs separados:

- `ChamadoRequestDTO` — o que o cliente envia ao criar
- `ChamadoUpdateDTO` — o que o cliente envia ao atualizar (inclui `status`)
- `ChamadoResponseDTO` — o que a API retorna
- `AnaliseResponseDTO` — resposta exclusiva do endpoint de IA

Usei **records** por serem imutáveis, concisos e semanticamente corretos para objetos de transferência — um DTO não precisa de setters.

O pensamento de separar Request do Response foi de que posso controlar exatamente o que entra e o que sai, sem expor campos internos como `dataAbertura` (que é gerada pelo sistema, não pelo cliente).

**Trade-off:** mais arquivos para manter. Mas a clareza e a segurança compensam.

---

## 3. GlobalExceptionHandler com @RestControllerAdvice

Em vez de tratar erros em cada controller, centralizei o tratamento em um único `GlobalExceptionHandler`.

Criei duas exceções customizadas:
- `ChamadoNotFoundException` — para 404 quando o chamado não existe
- `BusinessException` — para 422 quando uma regra de negócio é violada (ex: tentar reabrir um chamado cancelado)

Isso mantém os controllers limpos e garante que os erros sigam sempre o mesmo formato JSON (`{ "erros": [...] }`), independente de onde o erro aconteceu.

---

## 4. DELETE como cancelamento (soft delete)

O requisito pede que `DELETE /api/chamados/{id}` não remova o registro do banco, apenas mude o status para `CANCELADO`.

Essa é uma prática comum em sistemas de suporte: o histórico de chamados tem valor. Se um chamado fosse deletado fisicamente, perderíamos o registro de que ele existiu, quem abriu, quando foi aberto.

Implementei isso no `ChamadoService.cancelar()`, que busca o chamado, valida que ele pode ser cancelado, muda o status e preenche `dataFechamento`.

---

## 5. Integração com IA via Spring AI + Groq

Para a integração com IA, escolhi o **Spring AI** com o provider OpenAI apontando para a **API da Groq**, que é gratuita e compatível com a interface da OpenAI.

O modelo usado é o `llama-3.1-8b-instant`, gratuito, rápido e suficiente para a tarefa de triagem.

**Decisão sobre o prompt:** instruo explicitamente a IA a responder *apenas* em JSON válido, sem markdown e sem texto extra. Mesmo assim, o `parsearResposta()` remove blocos de código (` ```json `) caso a IA desobedeça a instrução — o que acontece às vezes com modelos menores/gratuitos.

**Tratamento de erros da IA:**
- Se a IA retornar um enum inválido (ex: `"URGENTE"` em vez de `"CRITICA"`), o `IllegalArgumentException` é capturado e convertido em `BusinessException` com mensagem clara.
- Se o JSON vier malformado, um `catch (Exception)` genérico retorna uma mensagem amigável.
- O chamado original nunca é alterado pelo endpoint de análise — a IA apenas sugere.

**Configuração da chave:** a `api-key` usa `${GROQ_API_KEY}`, lendo de variável de ambiente. Isso garante que a chave real nunca vá para o repositório, mas a aplicação ainda sobe sem a variável definida.

---

## 6. H2 em memória

Escolhi o H2 pela simplicidade: sem necessidade de instalar nada, o banco sobe junto com a aplicação e o console web está disponível em `/h2-console` para inspecionar os dados durante o desenvolvimento e os testes.

**Trade-off:** os dados são perdidos ao reiniciar a aplicação. Para produção, usaria PostgreSQL com `spring.jpa.hibernate.ddl-auto=validate`.

---

## 7. SpringDoc (Swagger UI)

Adicionei o `springdoc-openapi` para gerar documentação interativa automaticamente. Isso elimina a necessidade de um cliente HTTP separado (como Postman) para testar — qualquer pessoa com acesso à aplicação pode testar os endpoints pelo navegador em `/swagger-ui/index.html`.

---

## 8. Lombok

Usei `@Getter` e `@Setter` do Lombok na entidade `Chamado` para evitar boilerplate. Optei por não usar `@Data` porque ele gera `equals/hashCode` baseado em todos os campos, o que pode causar problemas com entidades JPA em relacionamentos — por isso usei apenas as anotações necessárias.

---

## Dificuldades encontradas

- **Comportamento da IA com JSON:** mesmo instruindo explicitamente a retornar JSON puro, o modelo às vezes envolve a resposta em blocos markdown. A solução foi remover os marcadores no `parsearResposta()` antes de fazer o parse.

- **ChamadoService:** a lógica do `dataFechamento` não estava sendo preenchida ao mudar o status para `RESOLVIDO` via `PUT`, só no `cancelar`. Isso levou à criação do `ChamadoUpdateDTO` separado e à correção do método `atualizar`.

- **IaService:** configurar a integração com o Spring AI foi desafiador. Optei por usar o `ObjectMapper` para fazer o parse do JSON retornado pela IA e tratar separadamente o caso em que ela retorna um enum inválido — como `"URGENTE"` em vez de `"CRITICA"` — capturando o `IllegalArgumentException` e convertendo em `BusinessException` com mensagem clara.
