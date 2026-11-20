# Backend: Webservices - Projeto Final (Restaurante)

**Equipe:** Andrey Paviani Brawerman · João Gabriel Esperança · Lucas Girata

**Vídeo de apresentação:** [assistir no YouTube](https://youtu.be/sJUmByZhGsQ)

**Status:** Entrega final. 12 endpoints implementados (CRUD de `Product` e `Order`, associação
`Order` ↔ `Product` e `Category`), autenticação nos `DELETE`, tratamento global de erros e logs.

## Tecnologias

- Kotlin 2.3 e Java 21
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security)
- Banco H2 em memória
- Documentação com springdoc-openapi (Swagger UI)
- Gradle (wrapper incluído)

## Como executar

Requisito: JDK 21.

```bash
./gradlew bootRun
```

No Windows (PowerShell):

```powershell
.\gradlew bootRun
```

| Recurso | Endereço |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Console do H2 | http://localhost:8080/h2-console |

Console do H2: JDBC URL `jdbc:h2:mem:restaurant`, usuário `sa`, senha vazia.
O banco é em memória, então os dados são apagados toda vez que a aplicação reinicia.

## Autenticação

Todo `DELETE` exige autenticação HTTP Basic. No Swagger, use o botão **Authorize**.

- Usuário: `admin`
- Senha: `admin123`

As credenciais ficam em `app.security.*` no `src/main/resources/application.yaml`.
Os demais endpoints não exigem autenticação.

## Modelo de dados

| Entidade | Descrição |
|---|---|
| `Category` | Categoria de produtos. O nome é único. |
| `Product` | Produto com nome, preço e categoria (opcional). |
| `Order` | Pedido com nome do cliente e status. |
| `OrderItem` | Tabela do meio entre pedido e produto, com a quantidade. |

Relacionamentos:

- **One-to-many:** `Category` → `Product`.
- **Many-to-many com tabela do meio:** `Order` ↔ `OrderItem` ↔ `Product`.

Status do pedido: `ABERTO`, `EM_PREPARO`, `ENTREGUE` e `CANCELADO`.

## Endpoints

| # | Método e rota | Autenticação |
|---|---|---|
| 1 | `POST /products` | não |
| 2 | `GET /products` (filtros e ordenação) | não |
| 3 | `GET /products/{id}` | não |
| 4 | `PUT /products/{id}` | não |
| 5 | `DELETE /products/{id}` | **sim** |
| 6 | `POST /orders` | não |
| 7 | `GET /orders` (filtros e ordenação) | não |
| 8 | `GET /orders/{id}` | não |
| 9 | `PUT /orders/{id}` | não |
| 10 | `DELETE /orders/{id}` | **sim** |
| 11 | `POST /orders/{orderId}/products/{productId}?quantity=1` | não |
| 12 | `DELETE /orders/{orderId}/products/{productId}` | **sim** |
| + | `POST /categories` e `GET /categories` | não |

### Filtros e ordenação

Produtos (`GET /products`):

| Parâmetro | Descrição |
|---|---|
| `name` | Texto contido no nome, sem diferenciar maiúsculas |
| `categoryId` | Filtra por categoria |
| `minPrice` e `maxPrice` | Faixa de preço |
| `sortBy` | `id` (padrão), `name` ou `price` |
| `direction` | `asc` (padrão) ou `desc` |

Pedidos (`GET /orders`): `customerName`, `status`, `sortBy` e `direction`.

Exemplos:

```
GET /products?name=burger&minPrice=10&maxPrice=50&sortBy=price&direction=desc
GET /products?categoryId=1&sortBy=name
GET /orders?customerName=mesa&status=ABERTO&sortBy=customerName&direction=asc
```

## Tratamento de erros

Um `@RestControllerAdvice` global devolve um formato padrão de erro:

| Código | Quando acontece |
|---|---|
| 400 | Dados inválidos (validação) ou parâmetro de ordenação inválido |
| 401 | `DELETE` sem autenticação |
| 404 | Recurso não encontrado |
| 409 | Conflito, como categoria com nome repetido ou produto que está em um pedido |
| 500 | Erro interno inesperado |

## Estrutura do projeto

```
src/main/kotlin/br/pucpr/restaurant/
├── config/         SecurityConfig, OpenApiConfig
├── controllers/    Category, Product e Order
├── dtos/           Requests e responses
├── exceptions/     Exceções, ApiError e GlobalExceptionHandler
├── models/         Category, Product, Order, OrderItem e OrderStatus
├── repositories/   Repositórios Spring Data JPA
├── services/       Regras de negócio, logs e SortSupport
└── RestaurantApplication.kt
```

## Requisitos do trabalho

| Requisito | Onde está |
|---|---|
| Pelo menos 4 classes | `Product`, `Order`, `OrderItem`, `Category` |
| Pelo menos 2 tipos de relação | many-to-many com tabela do meio e one-to-many |
| CRUD completo de pelo menos 2 classes | `Product` e `Order` |
| Autenticação em ações destrutivas | `SecurityConfig` (todo `DELETE`) |
| Endpoints de associação | `POST` e `DELETE /orders/{orderId}/products/{productId}` |
| Consulta com filtro e ordenação | `GET /products` e `GET /orders` |
| Logs | SLF4J nos services e no handler de exceções |
| Exceções | `GlobalExceptionHandler` e exceções customizadas |
| Segurança (pelo menos 2 serviços) | Spring Security (Basic Auth) e validação de entrada com DTOs e Bean Validation |

## Divisão do trabalho

| Integrante | Responsabilidade |
|---|---|
| Lucas | Infraestrutura: segurança, tratamento de exceções, configuração do Swagger e ordenação |
| João | Pedidos: `Order`, `OrderItem`, CRUD e associação com produtos |
| Andrey | Produtos e categorias: `Product`, `Category`, CRUD, filtros e ordenação |
