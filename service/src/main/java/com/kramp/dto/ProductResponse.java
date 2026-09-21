package com.kramp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private String productId;

    private ProductDto product;
    private PriceDto price;
    private AvailabilityDto availability;
    private CustomerContextDto customerContext;
}
