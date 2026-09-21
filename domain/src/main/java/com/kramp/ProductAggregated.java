package com.kramp;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ProductAggregated {
    private Optional<Product> product = Optional.empty();
    private Optional<Price> price = Optional.empty();
    private Optional<Availability> availability = Optional.empty();
    private Optional<CustomerContext> customerContext = Optional.empty();
}
