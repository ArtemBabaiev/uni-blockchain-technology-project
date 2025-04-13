package com.ababaiev.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UtilityType {
    ELECTRICITY("electricity", 1.25), WATER("water", 1.3), GAS("gas", 1.5);

    private final String value;
    private final double rate;
}
