package com.kramp;

public enum MarketCode {

    NL("nl-NL"),
    DE("de-DE"),
    PL("pl-PL");

    private final String name;

    public String getName() {
        return name;
    }

    MarketCode(String name) {
        this.name = name;
    }
}
