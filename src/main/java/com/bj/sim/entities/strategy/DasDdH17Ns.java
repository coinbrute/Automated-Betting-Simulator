package com.bj.sim.entities.strategy;

import com.bj.sim.entities.cards.Hand;
import com.bj.sim.entities.floor.Dealer;
import com.bj.sim.entities.floor.Player;

public class DasDdH17Ns {

    public DasDdH17Ns() {
        super();
    }

    public String decideMove(Player p, Dealer d, Hand hand) {
        if(p.hasBJ(0)) return "STAND"; // stand on bj will handle losing/winning/payouts after decision stage
        else if(p.getHandTotal(hand) > 21) return "STAND"; // stand on bust will handle losing/winning/payouts etc after decision
        else return runStrategy(p,d,hand);
    }

    private String runStrategy(Player p, Dealer d, Hand hand) {

        String move = "";
        Hand pHand = hand;
        int pHandTotal = p.getHandTotal(pHand);
        String pCard1 = pHand.getCardFromHand(0).getValueName();
        String pCard2;
        int dCard = d.getDealerShowCard().getValue();

        // dealer ace card and ten/face card value assignment
        if(dCard == 1) dCard = 11;
        else if(dCard > 10) dCard = 10;

        if(pHand.isPair()) { // SPLIT PAIR CHECK
            if(pCard1.equals("Ace") || pCard1.equals("Eight")) {
                move = "SPLIT";
            } else if(pCard1.equals("Two") || pCard1.equals("Three")) {
                if(dCard <= 7) move = "SPLIT";
                else move = "HIT";
            } else if(pCard1.equals("Four")) {
                if(dCard <= 4)move = "HIT";
                else if(dCard >= 7) move = "HIT";
                else move = "SPLIT/HIT";
            } else if(pCard1.equals("Six")) {
                if(dCard <= 6) move = "SPLIT";
                else if(dCard == 7) move = "SPLIT/HIT";
                else move = "HIT";
            } else if(pCard1.equals("Seven")) {
                if(dCard <= 7) move = "SPLIT";
                else if(dCard == 8) move = "SPLIT/HIT";
                else move = "HIT";
            } else if(pCard1.equals("Nine")) {
                if(dCard <= 6) move = "SPLIT";
                else if(dCard == 8) move = "SPLIT";
                else if(dCard == 9) move = "SPLIT";
                else move = "STAND";
            } else move = "STAND";
        } // SOFT HAND CHECK
        else if(Hand.isHandSoft(pHand)) {
            if(pHandTotal == 20) move = "STAND";
            else if(pHandTotal == 21) move = "STAND";
            else if(pHandTotal == 19) {
                if(dCard <= 5) move = "STAND";
                else if(dCard == 6) move = "DOUBLE/STAND";
                else move = "STAND";
            } else if(pHandTotal == 18) {
                if(dCard <= 6) move = "DOUBLE/STAND";
                else if(dCard == 7) move = "STAND";
                else if(dCard == 8) move = "STAND";
                else move = "HIT";
            } else if(pHandTotal == 17) {
                if(dCard >= 3 && dCard <= 6) move = "DOUBLE/HIT";
                else move = "HIT";
            } else if(pHandTotal == 14) {
                if(dCard == 4) move = "DOUBLE/HIT";
                else if(dCard == 5) move = "DOUBLE/HIT";
                else if(dCard == 6) move = "DOUBLE/HIT";
                else move = "HIT";
            } else if(pHandTotal == 15) {
                if(dCard == 4) move = "DOUBLE/HIT";
                else if(dCard == 5) move = "DOUBLE/HIT";
                else if(dCard == 6) move = "DOUBLE/HIT";
                else move = "HIT";
            } else if(pHandTotal == 16) {
                if(dCard == 4) move = "DOUBLE/HIT";
                else if(dCard == 5) move = "DOUBLE/HIT";
                else if(dCard == 6) move = "DOUBLE/HIT";
                else move = "HIT";
            } else if (pHandTotal == 13) {
                if(dCard == 5) move = "DOUBLE/HIT";
                else if(dCard == 6) move = "DOUBLE/HIT";
                else move = "HIT";
            }
        }
        else { // HARD HAND CHECK
            if(pHandTotal <= 8) move = "HIT";
            else if(pHandTotal == 9) {
                if(dCard >= 2 || dCard <= 6) move = "DOUBLE/HIT";
                else move = "HIT";
            } else if(pHandTotal == 10) {
                if(dCard <= 9) move = "DOUBLE/HIT";
                else move = "HIT";
            } else if(pHandTotal == 11) {
                move = "DOUBLE/HIT";
            } else if(pHandTotal == 12) {
                if((dCard == 2 || dCard == 3)) move = "HIT";
                else if(dCard >= 7) move = "HIT";
                else move = "STAND";
            } else if(pHandTotal == 13) {
                if(dCard <= 6) move = "STAND";
                else move = "HIT";
            } else if(pHandTotal == 14) {
                if(dCard <= 6) move = "STAND";
                else move = "HIT";
            } else if(pHandTotal == 15) {
                if(dCard <= 6) move = "STAND";
                else move = "HIT";
            } else if(pHandTotal == 16) {
                if(dCard <= 6) move = "STAND";
                else move = "HIT";
            } else move = "STAND";
        }
        return move;
    }
}
