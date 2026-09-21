package com.kramp.clients;

import com.kramp.Availability;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvailabilityClientTest {

    @Test
    void fetchShouldReturnAvailabilityWhenReliabilityIsOneHundredPercent() {
        AvailabilityClient client = new AvailabilityClient(0, 1000);
        client.init();

        Optional<Availability> result = client.fetch("product-1");

        assertTrue(result.isPresent());
        assertEquals("product-1", result.get().productId());
        assertEquals(10, result.get().stockLevel());
        assertEquals("Poznan", result.get().warehouseLocation());
        assertEquals("3 days", result.get().expectedDelivery());
    }

    @Test
    void fetchShouldThrowServiceUnavailableExceptionWhenReliabilityIsZero() {
        AvailabilityClient client = new AvailabilityClient(0, 0);
        client.init();

        assertThrows(ServiceUnavailableException.class, () -> client.fetch("product-1"));
    }

    @Test
    void fetchShouldRespectConfiguredLatencyWhenServiceIsAvailable() {
        int latencyMs = 100;
        AvailabilityClient client = new AvailabilityClient(latencyMs, 1000);
        client.init();

        long startedAt = System.nanoTime();

        Optional<Availability> result = client.fetch("product-1");

        long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;

        assertTrue(result.isPresent());
        assertTrue(elapsedMs >= latencyMs);
    }

    @Test
    void fetchShouldNotApplyLatencyWhenServiceIsUnavailable() {
        int latencyMs = 500;
        AvailabilityClient client = new AvailabilityClient(latencyMs, 0);
        client.init();

        long startedAt = System.nanoTime();

        assertThrows(ServiceUnavailableException.class, () -> client.fetch("product-1"));

        long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;

        assertTrue(elapsedMs < latencyMs);
    }
}
