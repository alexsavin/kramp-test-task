package com.kramp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailabilityDto {
    private String status;
    private Integer stockLevel;
    private String warehouseLocation;
    private String expectedDelivery;
}
