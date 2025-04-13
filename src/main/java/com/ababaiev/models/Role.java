package com.ababaiev.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER");

    private final String value;
}
