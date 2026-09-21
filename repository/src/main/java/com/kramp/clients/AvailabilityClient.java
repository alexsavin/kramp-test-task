package com.kramp.clients;

import com.kramp.Availability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AvailabilityClient extends AbstractClient<Availability, String> {

    public AvailabilityClient(@Value("${clients.availability.latency-ms:0}") int latency,
                              @Value("${clients.availability.reliability:1000}") int reliability) {
        super(latency, reliability);
    }

    @Override
    protected Optional<Availability> fetchInternal(String productId) {
        return Optional.of(new Availability(productId, 10, "Poznan", "3 days"));
    }
}
