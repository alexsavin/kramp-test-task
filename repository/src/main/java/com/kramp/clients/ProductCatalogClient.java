package com.kramp.clients;

import com.kramp.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductCatalogClient extends AbstractClient<Product, String> {

    public ProductCatalogClient(@Value("${clients.product-catalog.latency-ms:0}") int latency,
                                @Value("${clients.product-catalog.reliability:1000}") int reliability) {
        super(latency, reliability);
    }

    @Override
    protected Optional<Product> fetchInternal(String productId) {
        return Optional.of(new Product(productId, "Some Product", "description", "specification"));
    }
}
