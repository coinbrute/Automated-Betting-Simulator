package com.bj.sim.enums;

import java.util.*;

public enum Chip {
    ONE(1), FIVE(5), TWENTY_FIVE(25), HUNDRED(100), FIVE_HUNDRED(500), THOUSAND(1000);

    private final int amount;

    Chip(int amount) {
        this.amount = amount;
    }

    public static int getAmount(Chip chip) {
        return chip.amount;
    }

    public static Optional<Chip> getChipByAmount(int amount) {
        return Arrays.stream(Chip.values())
                .filter(c -> c.amount == amount)
                .findFirst();
    }

    public static Chip getChipByName(String name) {
        Chip c;
        switch(name) {
            case "ONE":
                c = Chip.ONE;
                break;
            case "FIVE":
                c = Chip.FIVE;
                break;
            case "TWENTY_FIVE":
                c = Chip.TWENTY_FIVE;
                break;
            case "HUNDRED":
                c = Chip.HUNDRED;
                break;
            case "FIVE_HUNDRED":
                c = Chip.FIVE_HUNDRED;
                break;
            case "THOUSAND":
            default:
                c = Chip.THOUSAND;
                break;
        }
        return c;
    }

    @Override
    public String toString() {
        return "Chip{" +
                "amount=" + amount +
                '}';
    }
}
