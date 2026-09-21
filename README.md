# Product Info Aggregator Service

This service aggregates product information from multiple downstream data sources and exposes it through a single HTTP API.

It combines data such as:

- product catalog information
- product price
- product availability
- customer context

The project is implemented as a multi-module Maven application using Java 17 and Spring Boot.

## Project structure

```text
kramp-test-task 
├── application # Spring Boot application entry point and runtime configuration 
├── domain # Domain model records and aggregate model 
├── repository # Simulated downstream clients/data sources 
└── service # REST controller, DTOs, validation, and aggregation service
```

## How to run the service

### Prerequisites

- Java 17
- Maven 3.9+

### Build the project

From the project root:

```bash
mvn clean install
```

### Run the application

```bash
mvn spring-boot:run -pl application
```

Alternatively, run the `com.kramp.Application` class from the IDE

### Example request

```bash
curl "http://localhost:8080/api/aggregator/aggregate?productId=123&market=nl-NL&customerId=customer-1"
```

If `customerId` is not provided, the service returns a default non-personalized customer context.

```bash
curl "http://localhost:8080/api/aggregator/aggregate?productId=123&market=nl-NL"
```

## Configuration

Runtime configuration is located in:
```text
application/src/main/resources/application.yaml
```

### Reliability scale

Client reliability is configured on a `0..1000` scale:

| Value | Meaning |
|---:|---|
| `1000` | approximately 100% reliable |
| `900` | approximately 90% reliable |
| `500` | approximately 50% reliable |
| `0` | always unavailable |

### Timeout

The aggregator timeout is configured with:

```yaml
product-info-aggregator: 
  timeout-ms: 200
```
This timeout is applied to each downstream client call. If a client is unavailable or times out, the aggregator continues and returns the available partial data.

## API

### Aggregate product info

```http request
GET /api/aggregator/aggregate
```

### Query parameters

| Parameter | Required | Description |
|---|---:|---|
| `productId` | yes | Product identifier |
| `market` | yes | Market code. Must match a supported market |
| `customerId` | no | Customer identifier used for personalized context |

### Example response

```json 
{ "productId": "123", 
  "product": { "productId": "123", "name": "Some Product", "description": "description", "specs": "specification" }, 
  "price": { "status": "AVAILABLE", "productId": "123", "market": "nl-NL", "basePrice": "100", "discount": "10", "finalPrice": "90" }, 
  "availability": { "status": "AVAILABLE", "productId": "123", "stockLevel": 10, "warehouseLocation": "Poznan", "expectedDelivery": "3 days" }, 
  "customerContext": { "status": "AVAILABLE", "customerId": "customer-1", "customerSegment": "customer segment", "customerPrefs": "prefs" } }
```

If a downstream service is unavailable, the corresponding DTO is returned with an `UNAVAILABLE` status where applicable.

## Key design decisions and trade-offs

### 1. Multi-module structure

The project is split into separate modules:

- `domain` contains core model classes.
- `repository` contains downstream client simulations.
- `service` contains aggregation logic and the HTTP API.
- `application` starts the Spring Boot application.

This keeps responsibilities separated and makes the project easier to evolve.

**Trade-off:** for a small service, multiple modules add some Maven complexity. For a production-like codebase, the separation is useful.

### 2. Parallel downstream calls

The aggregator calls downstream services asynchronously using `CompletableFuture`.

This reduces total response time because price, availability, and customer context can be fetched in parallel instead of sequentially.

Product is fetched sequentially because it is the only mandatory downstream service.

**Trade-off:** asynchronous code needs more careful exception handling because exceptions are wrapped by `CompletableFuture`.

### 3. Partial response strategy

The service is designed to return partial data when optional downstream services fail or time out.

For example, if price is unavailable, the response can still include product and availability information.

**Trade-off:** clients of this API must handle partial data. To make this explicit, DTOs include a `status` field such as `AVAILABLE` or `UNAVAILABLE`.

### 4. Configurable downstream behavior

Each simulated client has independently configurable latency and reliability.

This makes it possible to test different downstream conditions without changing code.

Example:

```yaml 
clients: 
  availability: 
    latency-ms: 100 
    reliability: 980
```

**Trade-off:** the simulator is intentionally simple. It is useful for the task, but not a replacement for real resilience tooling or service virtualization.

### 5. DTOs instead of exposing domain wrappers

The HTTP response uses DTOs instead of exposing repository/domain objects directly.

This keeps the API contract independent from internal domain models.

**Trade-off:** mapping code is required. With more DTOs, using a mapper library such as MapStruct could reduce boilerplate.

### 6. Request validation

The `market` request parameter is validated before calling the service.

This keeps invalid input out of the aggregation logic and returns a client-friendly `400 Bad Request`.

**Trade-off:** custom validation adds a small amount of code, but improves API correctness and error handling.

## What could be done differently with more time

### 1. Use typed configuration properties

Instead of injecting individual values with `@Value`, use `@ConfigurationProperties`.

For example:

```java 
@ConfigurationProperties(prefix = "clients") 
public class ClientsProperties { 
    // productCatalog, productPrice, availability, customerContext... 
}
```

This would improve validation, type safety, and maintainability.

### 2. Add resilience patterns

The service could use Resilience4j or Spring Cloud Circuit Breaker for:

- circuit breakers
- retries
- bulkheads
- rate limiting
- fallback handling
- metrics

This would be more production-ready than manual timeout and exception handling.

### 3. Improve observability

Add structured logging, metrics, and tracing.

Useful metrics would include:

- downstream latency
- timeout count
- unavailable service count
- partial response count
- success/error rate per downstream client

### 4. Add more tests

Additional tests could cover:

- controller validation
- response mapping
- partial aggregation behavior
- timeout behavior
- market validation
- end-to-end API behavior with Spring Boot test context

### 5. Introduce mapper classes

Mapping from domain objects to DTOs could be moved out of the controller.

For example:

```text 
ProductResponseMapper PriceDtoMapper AvailabilityDtoMapper CustomerContextDtoMapper
```
This would make the controller thinner and easier to test.

### 6. Improve error model

The API could return a standardized error response for validation and unexpected errors.

For example:

```json 
{ "code": "VALIDATION_ERROR", "message": "Invalid request", "details": "" }
```

### 7. Make required and optional dependencies explicit

The aggregation service could model downstream dependencies as required or optional.

For example:

- product catalog: required
- price: optional
- availability: optional
- customer context: optional

This would make failure behavior clearer.

## Adding a Related Products service

Question:

> The Assortment team wants to add a "Related Products" service with 200ms latency and 90% reliability. How would your design accommodate this? Should it be required or optional?

### How the design accommodates it

The current design can accommodate a new downstream service by following the same pattern as the existing clients.

At a high level, the following pieces would be added:

1. Add a domain model, for example `RelatedProduct`.
2. Add a repository client, for example `RelatedProductsClient`.
3. Configure its latency and reliability in `application.yaml`.
4. Add an optional field to the aggregated result.
5. Add a DTO for the API response.
6. Add another asynchronous call in the aggregator.
7. Map the aggregated data into the response.

Example configuration:

```yaml 
clients: 
  related-products: 
    latency-ms: 200 
    reliability: 900
```

The new client would inherit the same simulated behavior as the other clients.

Conceptually:

```java 
@Component 
public class RelatedProductsClient extends AbstractClient<List, String> {

    public RelatedProductsClient(
        @Value("${clients.related-products.latency-ms:0}") int latency,
        @Value("${clients.related-products.reliability:1000}") int reliability) {
        super(latency, reliability);
    }

    @Override
    protected Optional<List<RelatedProduct>> fetchInternal(String productId) {
        return Optional.of(List.of(
                new RelatedProduct("related-1", "Related product 1"),
                new RelatedProduct("related-2", "Related product 2")
        ));
    }
}
```

The aggregator would then add another future:

```java 
buildFuture(relatedProductsClient, productId, productAggregated::setRelatedProducts);
```

### Should Related Products be required or optional?

It should be **optional**.

Reasoning:

- It has `200ms` latency, which is relatively high compared to a low-latency aggregation target.
- It has `90%` reliability, meaning it may fail often enough to negatively impact the whole endpoint if treated as required.
- Related products are usually enrichment data, not core data needed to identify or display the requested product.
- Failing the entire product response because recommendations are unavailable would create a poor user experience.

A better approach is to return the main product response and mark related products as unavailable when the service fails or times out.

Example response fragment:

```json 
{ "relatedProducts": { "status": "UNAVAILABLE", "items": [] } }
```

If the business later decides that related products are mandatory for a specific page or flow, this could be controlled by endpoint design or request options rather than making the dependency globally required.

For example:

```http 
GET /api/aggregator/aggregate?productId=123&market=NL&includeRelatedProducts=true
```

Even then, the default behavior should remain resilient and avoid failing the whole aggregation unless the product catalog itself is unavailable.
