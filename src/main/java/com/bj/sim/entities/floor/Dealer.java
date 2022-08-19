package com.bj.sim.entities.floor;

import com.bj.sim.entities.cards.Card;
import com.bj.sim.entities.cards.Hand;
import com.bj.sim.enums.Chip;

import java.util.HashMap;

public class Dealer {
    // will only ever have one hand
    // no betting but has the bank
    @SuppressWarnings("FieldMayBeFinal")
    private Hand hand;
    // needs to have a bankroll of some kind
    @SuppressWarnings("FieldMayBeFinal")
    private HashMap<Chip, Integer> vault;

    public Dealer(Player p) {
        setVault();
        this.hand = new Hand(null);
    }

    // used for testing
    public Dealer(Hand startingHand) {
        setVault();
        this.hand = startingHand;
    }

    private void setVault() {
        vault = new HashMap<>();
        vault.put(Chip.ONE, 100); // 100
        vault.put(Chip.FIVE, 100); // 500
        vault.put(Chip.TWENTY_FIVE, 100); // 2,500
        vault.put(Chip.HUNDRED, 100); // 10,000
        vault.put(Chip.FIVE_HUNDRED, 100); // 50,000
        vault.put(Chip.THOUSAND, 100); // 100,000
    }

    // show card is the first card dealt to the dealer i.e. index 0 in the hand
    public Card getDealerShowCard() {
        return this.hand.getCardFromHand(1);
    }

    public Hand showDealerHand() {
        return this.hand;
    }

    public Hand hit(Table t) {
        this.hand.hit(t.getShoe().remove(0));
        return this.hand;
    }

    public HashMap<Chip, Integer> getVault() {
        return vault;
    }

    public Integer getVaultTotal() {
        return (vault.get(Chip.ONE) * 1) +
                (vault.get(Chip.FIVE) * 5) +
                (vault.get(Chip.TWENTY_FIVE) * 25) +
                (vault.get(Chip.HUNDRED) * 100) +
                (vault.get(Chip.FIVE_HUNDRED) * 500) +
                (vault.get(Chip.THOUSAND) * 1000);
    }

    @Override
    public String toString() {
        return "Dealer{\n\t\t" +
                "hand=" + hand +
                ", vault=" + vault +
                "\n\t}";
    }
}
