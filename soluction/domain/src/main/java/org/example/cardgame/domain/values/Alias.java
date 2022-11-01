package org.example.cardgame.domain.values;


import org.example.cardgame.generic.ValueObject;

import java.util.Objects;

public class Alias implements ValueObject<String> {
    private final String value;

    public Alias(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String value() {
        return value;
    }
}
