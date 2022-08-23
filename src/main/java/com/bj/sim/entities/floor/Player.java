package com.bj.sim.entities.floor;

import com.bj.sim.config.TableRules;
import com.bj.sim.entities.cards.Card;
import com.bj.sim.entities.cards.Hand;
import com.bj.sim.entities.strategy.betting.BalancingActStrategy;
import com.bj.sim.entities.strategy.DasDdH17Ns;
import com.bj.sim.entities.strategy.betting.ModifiedTimmyStrategy;
import com.bj.sim.enums.Chip;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    // can have multiple hands i.e. splitting
    private ArrayList<Hand> hands;
    private ArrayList<ModifiedTimmyStrategy> bets;
    // needs to have a bankroll of some kind
//    private HashMap<Chip, Integer> bankroll;
    private double bankroll;
    private double tableBankroll;
    // play strategy?
    private DasDdH17Ns playStrategy;

    public Player(TableRules rules) {
        resetBankroll();
        this.hands = new ArrayList<>();
        this.bets = new ArrayList<>();
        this.bets.add(new ModifiedTimmyStrategy(rules));
        this.playStrategy = new DasDdH17Ns();
        this.tableBankroll = this.getBankroll()/2;
    }

    // used for testing
    public Player(ArrayList<Hand> startingHand, TableRules rules) {
        resetBankroll();
        this.hands = new ArrayList<>();
        this.hands.addAll(startingHand);
        this.bets = new ArrayList<>();
        this.bets.add(new ModifiedTimmyStrategy(rules));
        this.playStrategy = new DasDdH17Ns();
        this.tableBankroll = this.getBankroll()/2;
    }

//    private void setBankroll() {
//        this.bankroll = new HashMap<>();
//        this.bankroll.put(Chip.FIVE, 50);
//        this.bankroll.put(Chip.TWENTY_FIVE, 10);
//        this.bankroll.put(Chip.HUNDRED, 5);
//    }
    private void resetBankroll() {
        this.bankroll = 1000;
    }

    public double addToBankroll(double amountToAdd) {
        return this.bankroll += amountToAdd;
    }

    public double subFromBankroll(double amountToSub) {
        return this.bankroll -= amountToSub;
    }

    public Hand viewPlayerHandByIndex(int idx) {
        if(this.hands.size() != 0) return this.hands.get(idx);
        else this.hands.add(new Hand());
        return this.hands.get(idx);
    }

    public double getBankroll() {
        return this.bankroll;
    }

//    public HashMap<Chip, Integer> getBankroll() {
//        return this.bankroll;
//    }
//
//    public Integer getBankrollTotal() {
//        return (bankroll.get(Chip.ONE)           == null ? 0 : bankroll.get(Chip.ONE)          * 1) +
//                (bankroll.get(Chip.FIVE)         == null ? 0 : bankroll.get(Chip.FIVE)         * 5) +
//                (bankroll.get(Chip.TWENTY_FIVE)  == null ? 0 : bankroll.get(Chip.TWENTY_FIVE)  * 25) +
//                (bankroll.get(Chip.HUNDRED)      == null ? 0 : bankroll.get(Chip.HUNDRED)      * 100) +
//                (bankroll.get(Chip.FIVE_HUNDRED) == null ? 0 : bankroll.get(Chip.FIVE_HUNDRED) * 500) +
//                (bankroll.get(Chip.THOUSAND)     == null ? 0 : bankroll.get(Chip.THOUSAND)     * 1000);
//    }

    public ModifiedTimmyStrategy getSpecificBet(int bet) {
        return this.bets.get(bet);
    }


    @Override
    public String toString() {
        return "Player{" +
                "hands=" + hands +
                ", bankroll=" + bankroll +
                '}';
    }

    public DasDdH17Ns getPlayStrategy() {
        return playStrategy;
    }

    public int getHandTotal(Hand hand) {
        return hand.calculateTotal();
    }

    public boolean hasBJ(int hand) {
        return this.hands.get(hand).calculateTotal() == 21 && this.hands.get(hand).getCards().size() == 2;
    }

    public int numberOfSplits() {
        return this.hands.size();
    }

    public ArrayList<Hand> getHands() {
        return this.hands;
    }

    public static ArrayList<Hand> split(Player p, int handIdx, int cardIdx) {
        // remove the second card from the second hand.
        Card newHand2 = p.getHands().get(handIdx).getCards().remove(cardIdx);
        Hand splitHand2 = new Hand(newHand2); // make second split hand from other card in hand
        p.getHands().add(splitHand2);
        return p.getHands();
    }

    public static Hand hit(Table t, Hand h, int handIndex) {
        h.hit(t.getShoe().remove(0));
        return t.getPlayers().get(0).getHands().get(handIndex);
    }

    public double getTableBankroll() {
        return tableBankroll;
    }
}
