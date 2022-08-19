package com.bj.sim.enums;

public enum Suit {
    CLUB("C"),DIAMOND("D"),HEART("H"),SPADE("S");

    Suit(String name) {
    }

    public static Suit getSuitByName(String name) {
        Suit suit;
        switch(name) {
            case "C":
                suit = Suit.CLUB;
                break;
            case "D":
                suit = Suit.DIAMOND;
                break;
            case "H":
                suit = Suit.HEART;
                break;
            case "S":
            default:
                suit = Suit.SPADE;
                break;
        }
        return suit;
    }
}