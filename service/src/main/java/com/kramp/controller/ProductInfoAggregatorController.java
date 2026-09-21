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

import static com.kramp.controller.ProductMapper.*;

@RestController
@Validated
@RequestMapping("/api/aggregator")
public class ProductInfoAggregatorController {


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
}
