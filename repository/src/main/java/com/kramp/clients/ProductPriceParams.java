package com.kramp.clients;

import com.kramp.MarketCode;

public record ProductPriceParams(String productId, MarketCode market) {
}
