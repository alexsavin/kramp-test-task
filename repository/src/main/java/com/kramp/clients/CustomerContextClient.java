package com.kramp.clients;

import com.kramp.CustomerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerContextClient extends AbstractClient<CustomerContext, String> {

    public CustomerContextClient(@Value("${clients.customer-context.latency-ms:0}") int latency,
                                 @Value("${clients.customer-context.reliability:1000}") int reliability) {
        super(latency, reliability);
    }

    @Override
    protected Optional<CustomerContext> fetchInternal(String customerId) {
        return Optional.of(new CustomerContext(customerId, "customer segment", "prefs"));
    }
}
