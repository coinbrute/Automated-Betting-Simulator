package com.bj.sim.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Value {
    ACE(1, "ace"),
    TWO(2, "two"),
    THREE(3, "three"),
    FOUR(4, "four"),
    FIVE(5, "five"),
    SIX(6, "six"),
    SEVEN(7, "seven"),
    EIGHT(8, "eight"),
    NINE(9, "nine"),
    TEN(10, "ten"),
    JACK(10, "jack"),
    QUEEN(10, "queen"),
    KING(10, "king");

    private final int value;
    private final String name;

    Value(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    // reverse lookup methods
    public static Optional<Value> getValueByValue(int value) {
        return Arrays.stream(Value.values())
                .filter(v -> v.value == value)
                .findFirst();
    }

    public static Optional<Value> getValueByName(String name) {
        return Arrays.stream(Value.values())
                .filter(v -> v.name.equals(name))
                .findFirst();
    }
}
