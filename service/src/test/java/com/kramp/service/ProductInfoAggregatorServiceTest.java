package com.kramp.service;

import com.kramp.ProductAggregated;
import com.kramp.clients.AvailabilityClient;
import com.kramp.clients.CustomerContextClient;
import com.kramp.clients.ProductCatalogClient;
import com.kramp.clients.ProductPriceClient;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductInfoAggregatorServiceTest {

    @Test
    void aggregateShouldReturnFullResultWhenAllServicesSucceed() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                200
        );

        ProductAggregated result = service.aggregate("product-1", "nl-NL", Optional.of("customer-1"));

        assertTrue(result.getProduct().isPresent());
        assertTrue(result.getPrice().isPresent());
        assertTrue(result.getAvailability().isPresent());
        assertTrue(result.getCustomerContext().isPresent());

        assertEquals("product-1", result.getProduct().get().productId());
        assertEquals("product-1", result.getPrice().get().productId());
        assertEquals("product-1", result.getAvailability().get().productId());
        assertEquals("customer-1", result.getCustomerContext().get().customerId());
    }

    @Test
    void aggregateShouldFailWhenProductCatalogIsUnavailable() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 0);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                200
        );

        assertThrows(
                ProductCatalogUnavailableException.class,
                () -> service.aggregate("product-1", "nl-NL", Optional.of("customer-1"))
        );
    }

    @Test
    void aggregateShouldFailWhenProductCatalogTimesOut() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(500, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                50
        );

        assertThrows(
                ProductCatalogUnavailableException.class,
                () -> service.aggregate("product-1", "nl-NL", Optional.of("customer-1"))
        );
    }

    @Test
    void aggregateShouldReturnPartialResultWhenPriceClientIsUnavailable() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 0);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                200
        );

        ProductAggregated result = service.aggregate("product-1", "nl-NL", Optional.of("customer-1"));

        assertTrue(result.getProduct().isPresent());
        assertFalse(result.getPrice().isPresent());
        assertTrue(result.getAvailability().isPresent());
        assertTrue(result.getCustomerContext().isPresent());
    }

    @Test
    void aggregateShouldReturnPartialResultWhenAvailabilityClientIsUnavailable() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 0);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                200
        );

        ProductAggregated result = service.aggregate("product-1", "nl-NL", Optional.of("customer-1"));

        assertTrue(result.getProduct().isPresent());
        assertTrue(result.getPrice().isPresent());
        assertFalse(result.getAvailability().isPresent());
        assertTrue(result.getCustomerContext().isPresent());

        assertEquals("product-1", result.getProduct().get().productId());
        assertEquals("product-1", result.getPrice().get().productId());
        assertEquals("customer-1", result.getCustomerContext().get().customerId());
    }

    @Test
    void aggregateShouldReturnPartialResultWhenCustomerContextClientIsUnavailable() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 0);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                200
        );

        ProductAggregated result = service.aggregate("product-1", "nl-NL", Optional.of("customer-1"));

        assertTrue(result.getProduct().isPresent());
        assertTrue(result.getPrice().isPresent());
        assertTrue(result.getAvailability().isPresent());
        assertFalse(result.getCustomerContext().isPresent());

        assertEquals("product-1", result.getProduct().get().productId());
        assertEquals("product-1", result.getPrice().get().productId());
        assertEquals("product-1", result.getAvailability().get().productId());
    }

    @Test
    void aggregateShouldReturnEmptyOptionalForTimedOutClient() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(500, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                50
        );

        ProductAggregated result = service.aggregate("product-1", "nl-NL", Optional.of("customer-1"));

        assertTrue(result.getProduct().isPresent());
        assertTrue(result.getPrice().isPresent());
        assertFalse(result.getAvailability().isPresent());
        assertTrue(result.getCustomerContext().isPresent());
    }

    @Test
    void aggregateShouldReturnStandardResultWhenCustomerIdIsNotProvided() {
        ProductCatalogClient productCatalogClient = new ProductCatalogClient(0, 1000);
        ProductPriceClient productPriceClient = new ProductPriceClient(0, 1000);
        AvailabilityClient availabilityClient = new AvailabilityClient(0, 1000);
        CustomerContextClient customerContextClient = new CustomerContextClient(0, 1000);

        initClients(productCatalogClient, productPriceClient, availabilityClient, customerContextClient);

        ProductInfoAggregatorService service = new ProductInfoAggregatorService(
                productCatalogClient,
                productPriceClient,
                availabilityClient,
                customerContextClient,
                200
        );

        ProductAggregated result = service.aggregate("product-1", "nl-NL", Optional.empty());

        assertTrue(result.getProduct().isPresent());
        assertTrue(result.getPrice().isPresent());
        assertTrue(result.getAvailability().isPresent());
        assertFalse(result.getCustomerContext().isPresent());
    }

    private void initClients(ProductCatalogClient productCatalogClient,
                             ProductPriceClient productPriceClient,
                             AvailabilityClient availabilityClient,
                             CustomerContextClient customerContextClient) {
        productCatalogClient.init();
        productPriceClient.init();
        availabilityClient.init();
        customerContextClient.init();
    }
}
