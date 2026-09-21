package com.kramp;

import java.time.LocalDate;

public record Availability(String productId, Integer stockLevel, String warehouseLocation, String expectedDelivery) {
}
