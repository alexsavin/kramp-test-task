package com.kramp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerContextDto {
    private String status;
    private String customerId;
    private String customerSegment;
    private String customerPrefs;
}