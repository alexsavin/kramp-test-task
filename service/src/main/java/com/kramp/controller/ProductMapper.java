package com.kramp.controller;

import com.kramp.Availability;
import com.kramp.CustomerContext;
import com.kramp.Price;
import com.kramp.Product;
import com.kramp.dto.AvailabilityDto;
import com.kramp.dto.CustomerContextDto;
import com.kramp.dto.PriceDto;
import com.kramp.dto.ProductDto;

import java.util.Optional;

public class ProductMapper {

    public static final String AVAILABLE = "AVAILABLE";
    public static final String UNAVAILABLE = "UNAVAILABLE";

    public static ProductDto toProductDto(Optional<Product> product) {
        return product.map(value -> ProductDto.builder()
                        .name(value.name())
                        .description(value.description())
                        .specs(value.specs())
                        .build())
                .orElse(null);
    }

    public static PriceDto toPriceDto(Optional<Price> price) {
        return price.map(value -> PriceDto.builder()
                        .status(AVAILABLE)
                        .market(value.market())
                        .basePrice(value.basePrice())
                        .discount(value.discount())
                        .finalPrice(value.finalPrice())
                        .build())
                .orElseGet(() -> PriceDto.builder()
                        .status(UNAVAILABLE)
                        .build());
    }

    public static AvailabilityDto toAvailabilityDto(Optional<Availability> availability) {
        return availability.map(value -> AvailabilityDto.builder()
                        .status(AVAILABLE)
                        .stockLevel(value.stockLevel())
                        .warehouseLocation(value.warehouseLocation())
                        .expectedDelivery(value.expectedDelivery())
                        .build())
                .orElseGet(() -> AvailabilityDto.builder()
                        .status(UNAVAILABLE)
                        .build());
    }

    public static CustomerContextDto toCustomerContextDto(Optional<CustomerContext> customerContext) {
        return customerContext.map(value -> CustomerContextDto.builder()
                        .status(AVAILABLE)
                        .customerId(value.customerId())
                        .customerSegment(value.customerSegment())
                        .customerPrefs(value.customerPrefs())
                        .build())
                .orElseGet(() -> CustomerContextDto.builder()
                        .status(UNAVAILABLE)
                        .customerId("")
                        .customerSegment("default")
                        .customerPrefs("non-personalized")
                        .build());
    }
}
