package com.kramp.clients;

import com.kramp.MarketCode;
import com.kramp.Price;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ProductPriceClient extends AbstractClient<Price, ProductPriceParams> {

    public ProductPriceClient(@Value("${clients.product-price.latency-ms:0}") int latency,
                              @Value("${clients.product-price.reliability:1000}") int reliability) {
        super(latency, reliability);
    }

    private final Map<MarketCode, String[]> priceMap = new HashMap<>();

    {
        priceMap.put(MarketCode.NL, new String[]{"100", "5%", "95"});
        priceMap.put(MarketCode.DE, new String[]{"95", "3%", "93"});
        priceMap.put(MarketCode.PL, new String[]{"90", "7%", "83"});
    }

    @Override
    protected Optional<Price> fetchInternal(ProductPriceParams params) {
        String[] price = priceMap.get(params.market());
        return Optional.of(new Price(params.productId(), params.market().getName(), price[0], price[1], price[2]));
    }
}
