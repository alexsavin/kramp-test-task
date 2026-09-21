package com.kramp.service;

import com.kramp.MarketCode;
import com.kramp.Product;
import com.kramp.ProductAggregated;
import com.kramp.clients.*;
import com.kramp.datasource.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Service
@Slf4j
public class ProductInfoAggregatorService {

    private final ProductCatalogClient productCatalogClient;
    private final ProductPriceClient productPriceClient;
    private final AvailabilityClient availabilityClient;
    private final CustomerContextClient customerContextClient;
    private final long timeoutMs;

    public ProductInfoAggregatorService(ProductCatalogClient productCatalogClient,
                                        ProductPriceClient productPriceClient,
                                        AvailabilityClient availabilityClient,
                                        CustomerContextClient customerContextClient,
                                        @Value("${product-info-aggregator.timeout-ms:200}") long timeoutMs) {
        this.productCatalogClient = productCatalogClient;
        this.productPriceClient = productPriceClient;
        this.availabilityClient = availabilityClient;
        this.customerContextClient = customerContextClient;
        this.timeoutMs = timeoutMs;
    }

    public ProductAggregated aggregate(String productId, String marketName, Optional<String> customerId) {
        log.info("Aggregate info for product ID = {}, market = {}, customer ID = {}", productId, marketName, customerId.orElse("null"));

        MarketCode marketCode = Arrays.stream(MarketCode.values()).filter(c -> c.getName().equals(marketName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown market code " + marketName));

        ProductAggregated productAggregated = new ProductAggregated();

        Product product = fetchRequiredProduct(productId);
        productAggregated.setProduct(Optional.of(product));

        List<CompletableFuture<?>> futureList = new ArrayList<>(List.of(
                buildOptionalFuture(productPriceClient, new ProductPriceParams(productId, marketCode), productAggregated::setPrice),
                buildOptionalFuture(availabilityClient, productId, productAggregated::setAvailability)
        ));

        customerId.ifPresent(clientId -> futureList.add(buildOptionalFuture(customerContextClient, clientId, productAggregated::setCustomerContext)));

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture<?>[0])).join();

        return productAggregated;
    }

    private Product fetchRequiredProduct(String productId) {
        try {
            return CompletableFuture.supplyAsync(() -> productCatalogClient.fetch(productId))
                    .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .join()
                    .orElseThrow(() -> new ProductCatalogUnavailableException(productId));
        } catch (CompletionException exception) {
            throw new ProductCatalogUnavailableException(productId);
        }
    }

    private <T, P> CompletableFuture<?> buildOptionalFuture(DataSource<T, P> dataSource, P param, Consumer<? super Optional<T>> action) {
        return CompletableFuture.supplyAsync(() -> dataSource.fetch(param))
                .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .exceptionally(exception -> {
                    Throwable cause = unwrapCompletionException(exception);

                    if (cause instanceof ServiceUnavailableException) {
                        log.error("Service not available");
                        return Optional.empty();
                    }

                    if (cause instanceof TimeoutException) {
                        log.error("Service timeout");
                        return Optional.empty();
                    }

                    throw new CompletionException(cause);
                })
                .thenAccept(action);
    }

    private Throwable unwrapCompletionException(Throwable exception) {
        if (exception instanceof CompletionException && exception.getCause() != null) {
            return exception.getCause();
        }

        return exception;
    }
}
