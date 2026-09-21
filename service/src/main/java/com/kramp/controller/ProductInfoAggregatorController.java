package com.kramp.controller;

import com.kramp.*;
import com.kramp.dto.*;
import com.kramp.service.ProductInfoAggregatorService;
import com.kramp.validation.ValidMarketCode;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/aggregator")
public class ProductInfoAggregatorController {

    public static final String AVAILABLE = "AVAILABLE";
    public static final String UNAVAILABLE = "UNAVAILABLE";
    private final ProductInfoAggregatorService service;

    public ProductInfoAggregatorController(ProductInfoAggregatorService service) {
        this.service = service;
    }

    @GetMapping("/aggregate")
    public ProductResponse aggregate(@NotBlank @RequestParam String productId,
                                     @NotBlank @ValidMarketCode @RequestParam String market,
                                     @RequestParam Optional<String> customerId) {
        ProductAggregated productAggregated = service.aggregate(productId, market, customerId);

        return ProductResponse.builder()
                .productId(productId)
                .product(toProductDto(productAggregated.getProduct()))
                .price(toPriceDto(productAggregated.getPrice()))
                .availability(toAvailabilityDto(productAggregated.getAvailability()))
                .customerContext(toCustomerContextDto(productAggregated.getCustomerContext()))
                .build();
    }

    private ProductDto toProductDto(Optional<Product> product) {
        return product.map(value -> ProductDto.builder()
                        .name(value.name())
                        .description(value.description())
                        .specs(value.specs())
                        .build())
                .orElse(null);
    }

    private PriceDto toPriceDto(Optional<Price> price) {
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

    private AvailabilityDto toAvailabilityDto(Optional<Availability> availability) {
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

    private CustomerContextDto toCustomerContextDto(Optional<CustomerContext> customerContext) {
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
