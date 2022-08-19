package com.bj.sim.entities.strategy;

import com.bj.sim.config.TableRules;
import com.bj.sim.entities.cards.Hand;
import com.bj.sim.entities.floor.Player;

import java.util.Arrays;

public class BettingStrategy {

    private int winCount; // track wins to progress through betting tiers for win tiers
    private int lossCount; // track losses to progress through betting tiers for loss tiers
    private int maxWins; // for logging out at end of play
    private int maxLosses; // for logging out at end of play
    private int maxWinBet; // for logging out at end of play
    private int maxLossBet; // for logging out at end of play
    private int lastWinBet; // used to jump back in to win tier after a win from loss tier
    private int lastLossBet; // used to continue on loss tier after continued losing streak
    private int tableMin; // to set the table minimum bet
    private int tableMax; // to set the table maximum bet
    private int[] winBetTiers; // betting levels for wins
    private boolean inWinTier;
    private int winTierLoopCount; // count loops through win tiers without losing
    private int[] lossBetTiers; // betting levels for losses
    private int winAddBet; // amount to add on a win
    private int winBetLevel; // current level within the betting tiers for wins
    private int lossBetLevel; // current level within the betting tiers for losses
    private int currentBet; // used to get the bet in play

    public BettingStrategy(TableRules rules, Player player) {
        this.tableMin = rules != null ? rules.minBet() : 15;
        this.tableMax = rules != null ? rules.maxBet() : 5000;
        configureInitialStrategy();
    }

    private void configureInitialStrategy() {
        this.inWinTier = true;
        this.winBetTiers = new int[6];
        this.winAddBet = this.tableMin == 15 ? 10 : this.tableMin == 25 ? 15 : 25;
        for(int i = 0; i < 6; i++) {
            this.winBetTiers[i] = i == 0 ? this.tableMin : this.winBetTiers[i-1] + this.winAddBet;
        }
        this.winBetLevel = this.winBetTiers[0];
        this.winCount = 0;
        this.lastWinBet = 0;
        this.maxWinBet = this.lastWinBet;
        this.maxWins = 0;
        this.winTierLoopCount = 0;

        this.lossBetTiers = new int[3];
        this.lossCount = 0;
        this.lossBetLevel = this.lossBetTiers[0];
        this.lastLossBet = 0;
        this.maxLossBet = this.lastLossBet;
        this.maxLosses = this.lossBetLevel;

        this.currentBet = this.lossCount == 0 && this.winCount < this.winBetTiers.length ? this.winBetTiers[0] : this.lossBetTiers[0];
    }

    // STRATEGY ON MOVE DECISION METHODS
        // these methods will make betting decisions for hands and will bet for each hand independently and not make bulk betting decisions for all player hands as one.
            // i.e. if play has split one hand into 2 and one hand wins and one hand loses then the strategy does not dictate to consider those as one lost hand.
            // it is considered two individually bet hands at individual tiers so if the split was started at tier 2 and one was lost and one was won
            // the loser doubles starting at loss tier 1 and the winner increases to win tier 3 and each are tracked separately.

    public String onLoss() {
        // double the bet for next hand
        // UNLESS
        // we are at the limit of the tiers
        // OR
        // we have reached 10% of original bankroll
        return "";
    }

    public String onWin() {
        // up the tier and associated bet amount for next hand
        return "";
    }

    // RUN STRATEGY
    public void runBettingStrategyAnalysis(String handOutcome, Player player) {
        // ASSUME A STATIC TIER SYSTEM AND DO NOT INCREASE THE TABLE MINIMUM AND UP THE BETTING STRUCTURE ON WIN WALKS
        // check hand outcome for all scenarios until full analysis can be run
        if(winCount == 0 && lossCount == 0) {
            if(handOutcome.equals("LOST")) {
                // set last loss bet to current bet
                this.lastLossBet = this.currentBet;
                // set loss tiers based on current bet
                // set current bet to loss tier at index 1
                for(int i = 0; i < 3; i++) {
                    this.lossBetTiers[i] = i == 0 ? this.currentBet*2 : this.lossBetTiers[i-1] * 2;
                }
                this.lossBetLevel = this.lossBetTiers[0];
                this.currentBet = this.lossBetLevel;
                // compare max loss bet to last loss bet and set max loss bet to larger
                this.maxLossBet = Math.max(this.maxLossBet, this.lastLossBet);
                // set loss count to 1
                this.lossCount = 1;
                // set win count to 0
                this.winCount = 0;
                // set last win bet to 0
                this.lastWinBet = 0;
            } else if(handOutcome.equals("PUSH")) {

            } else {
                // start win tier from bet amount last lost at in the win tier and continue from there
                this.currentBet = this.winBetTiers[this.winTierLoopCount++];
            }
        } else if(winCount < 6 && lossCount == 0) {
            if(handOutcome.equals("LOSS")) {

            } else if(handOutcome.equals("PUSH")) {

            } else {

            }
            // keep progressing through the win tier
            this.currentBet = this.winBetTiers[this.winTierLoopCount];
        }  else if(winCount < 6 && lossCount != 0) {
            // check hand outcome and see if we are still in the loss tier
            if(handOutcome.equals("LOSS")) {
                if(lastLossBet < tableMax && ((player.getTableBankroll())*.9) > ((player.getTableBankroll()) - lastLossBet)) { //
                    // double the lost amount
                    // progress one more into the loss tier
                } else {
                    // leave the shoe as the loss threshold has been met
                }
            } else if(handOutcome.equals("PUSH")) {
                // do nothing and return that status
            } else {
                // check the current tier if in loss move to win else do win stuff
            }
        } else if(winCount == 6 && lossCount == 0) {
            if(handOutcome.equals("LOSS")) {

            } else if(handOutcome.equals("PUSH")) {

            } else {

            }
            // we made it though the win tier
            // up the win tier loop counter
            // start the win tier over again
            // if we are using a progressive win tier-tiered system check for increase scenario here by comparing against bankroll threshold amount
        } else {
            // assume the winning threshold has been reached and leave the shoe
        }
    }



    // GETTERS AND TOSTRING

    public int getCurrentBet() {
        return currentBet;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public int getMaxWins() {
        return maxWins;
    }

    public int getMaxLosses() {
        return maxLosses;
    }

    public int getMaxWinBet() {
        return maxWinBet;
    }

    public int getMaxLossBet() {
        return maxLossBet;
    }

    public int getLastWinBet() {
        return lastWinBet;
    }

    public int getLastLossBet() {
        return lastLossBet;
    }

    public int getTableMin() {
        return tableMin;
    }

    public int getTableMax() {
        return tableMax;
    }

    public int[] getWinBetTiers() {
        return winBetTiers;
    }

    public int getWinTierLoopCount() {
        return winTierLoopCount;
    }

    public int[] getLossBetTiers() {
        return lossBetTiers;
    }

    public int getWinAddBet() {
        return winAddBet;
    }

    public int getWinBetLevel() {
        return winBetLevel;
    }

    public int getLossBetLevel() {
        return lossBetLevel;
    }

    @Override
    public String toString() {
        return "BettingStrategy{" +
                "winCount=" + winCount +
                ", lossCount=" + lossCount +
                ", maxWins=" + maxWins +
                ", maxLosses=" + maxLosses +
                ", maxWinBet=" + maxWinBet +
                ", maxLossBet=" + maxLossBet +
                ", lastWinBet=" + lastWinBet +
                ", lastLossBet=" + lastLossBet +
                ", tableMin=" + tableMin +
                ", tableMax=" + tableMax +
                ", winBetTiers=" + Arrays.toString(winBetTiers) +
                ", winTierLoopCount=" + winTierLoopCount +
                ", lossBetTiers=" + Arrays.toString(lossBetTiers) +
                ", winAddBet=" + winAddBet +
                ", winBetLevel=" + winBetLevel +
                ", lossBetLevel=" + lossBetLevel +
                '}';
    }
}
