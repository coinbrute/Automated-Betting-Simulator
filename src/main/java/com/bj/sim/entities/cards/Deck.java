package com.bj.sim.entities.cards;

import com.bj.sim.enums.Suit;
import com.bj.sim.enums.Value;

import java.util.*;

public class Deck {

    // is a full mix of cards one of every value and suit
    private final List<Card> deck;

    // constructor builds it.
    public Deck() {
        this.deck = instantiateDeck();
    }

    public List<Card> getDeck() {
        return deck;
    }

    private static List<Card> instantiateDeck() {
        // loop over each value in Value and create the four Suits
        List<Card> deck = new ArrayList<>();
        for (int i = 1; i <= 13;) {
            deck.add(new Card('H', i++));
        }
        for (int i = 1; i <= 13;) {
            deck.add(new Card('S', i++));
        }
        for (int i = 1; i <= 13;) {
            deck.add(new Card('C', i++));
        }
        for (int i = 1; i <= 13;) {
            deck.add(new Card('D', i++));
        }
        return shuffle(shuffle(shuffle(shuffle(shuffle(shuffle(shuffle(deck)))))));
    }

    private static List<Card> shuffle(List<Card> deck) {
        for (int i = 0; i < deck.size(); i++) {
            int index = (int) (Math.random() * (i + 1));
            Card c = deck.remove(index);
            deck.add(c);
        }
        return deck;
    }

    // decks can be drawn from NOT returned to
    // first out
    public static Card draw(List<Card> d) {
        return d.remove(0);
    }

    @Override
    public String toString() {
        return "Deck{\n\t\t" + deck.toString() + "}\n\t";
    }
}
