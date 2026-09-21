package com.kramp;

public record Availability(String productId, Integer stockLevel, String warehouseLocation, String expectedDelivery) {
}
