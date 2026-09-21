package com.kramp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PriceDto {
    private String status;
    private String market;
    private String basePrice;
    private String discount;
    private String finalPrice;
}
