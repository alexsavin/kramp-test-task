package com.kramp;

import lombok.Getter;

@Getter
public enum MarketCode {

    NL("nl-NL"),
    DE("de-DE"),
    PL("pl-PL");

    private final String name;

    MarketCode(String name) {
        this.name = name;
    }
}
