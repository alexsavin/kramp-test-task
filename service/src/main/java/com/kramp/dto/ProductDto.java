package com.kramp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDto {
    private String name;
    private String description;
    private String specs;
}
