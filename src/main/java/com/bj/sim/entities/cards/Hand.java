package com.bj.sim.entities.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hand implements Serializable {
    private List<Card> theHand = new ArrayList<>();

    public Hand() {
    }

    public Hand(Card c) {
        theHand.add(c);
    }

    // Calculates the total of a hand and also decides whether ace is 1 or 11
    public int calculateTotal() {
        int total =0;
        boolean aceFlag = false;
        for (int i = 0; i < theHand.size(); i++) {
            int value = theHand.get(i).getValue();
            if (value > 10) {
                value = 10;
            } else if ( value == 1) {
                aceFlag = true;
            }
            total += value;
        }
        if (aceFlag && total + 10 <= 21) {
            total += 10;
        }
        return total;
    }

    public String toString(){
        return this.toString(false, false);
    }

    public String toString(boolean isDealer, boolean hideHoleCard){
        String str = "";
        int total =0;
        boolean aceFlag = false;
        String aceString = "";
        for (int i = 0; i < theHand.size(); i++) {
            int value = theHand.get(i).getValue();
            if (isDealer && hideHoleCard && i == 0) {
                str = " showing";
            } else {
                    String valueName;
                    if (value > 10) {
                        valueName = theHand.get(i).getValueName().substring(0, 1);
                    } else if (value == 1) {
                        valueName = "A";
                    } else {
                        valueName = Integer.toString(value);
                    }
                    str += " " + valueName + theHand.get(i).getSuitDesignator();
                    if (value > 10) {
                        value = 10;
                    } else if (value == 1) {
                        aceFlag = true;
                    }
                    total += value;
            }
        }
        if (aceFlag && total + 10 <= 21) {
            aceString = " or "+ (total + 10);
        }
        if ( hideHoleCard) {
            return str;
        } else {
            return str+ " totals "+ total + aceString;
        }
    }

    public static boolean isHandSoft(Hand hand) {
        return hand.getCards().stream().anyMatch(card -> card.getValueName().equals("Ace"));
    }

    public boolean isPair() {
        return (theHand.size() == 2 && theHand.get(0).compareValue(theHand.get(1)));
    }

    public List<Card> getCards() {
        return theHand;
    }

    public Card getCardFromHand(int index) {
        return theHand.get(index);
    }

    public void hit(Card c) {
        theHand.add(c);
    }

}