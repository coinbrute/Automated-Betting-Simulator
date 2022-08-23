package com.bj.sim.entities.strategy.betting;

import com.bj.sim.config.TableRules;
import com.bj.sim.entities.floor.Player;

public class ModifiedTimmyStrategy {
    private int maxWins; // for logging out at end of play tracks maximum wins in a row
    private int maxLosses; // for logging out at end of play tracks maximum losses in a row
    private int maxLossBet; // for logging out at end of play tracks mamximum lost on a hand
    private int lastLossBet; // used to continue on loss tier after continued losing streak
    private int tableMin; // to set the table minimum bet
    private int tableMax; // to set the table maximum bet
    private boolean onWinTrack; // on winning streak
    private int[] lossBetTiers; // betting levels for losses
    private int lossBetLevel; // current level within the betting tiers for losses
    private int currentBet; // used to get the bet in play

    public ModifiedTimmyStrategy(TableRules rules) {
        this.tableMin = rules != null ? rules.minBet() : 15;
        this.tableMax = rules != null ? rules.maxBet() : 5000;
        configureInitialStrategy();
    }

    private void configureInitialStrategy() {
        this.maxWins = 0;
        this.lossBetTiers = new int[4];
        this.lossBetTiers[0] = 15;
        this.lossBetTiers[1] = 15;
        this.lossBetTiers[2] = 90;
        this.lossBetTiers[3] = 180;
        this.lossBetLevel = 0;
        this.lastLossBet = 0;
        this.maxLossBet = this.lastLossBet;
        this.maxLosses = 0;
        this.onWinTrack = true;
        this.currentBet = this.lastLossBet == 0 && this.onWinTrack ? 15 : this.lossBetTiers[0];
    }

    public void runBettingStrategyAnalysis(String handOutcome, Player player) {
        System.out.println("In betting strategy analysis...");
        // if win track is true we had won the previous had coming into the currently bet hand
        // so its to assume if we lose we will be flipping from onWinTrack=true to false
        if(this.onWinTrack == true) {
            if(handOutcome.equals("LOST")) {
                System.out.println("Lost hand...On win track? " + this.onWinTrack + "...Next hand will be on Loss track at index 0");
                // subtract the current bet from the player bankroll
                player.subFromBankroll(this.currentBet);
                // set win track to false
                this.onWinTrack = false;
                // set loss bet level to loss bet tier 0
                this.lossBetLevel = 0;
                // set last loss bet to loss bet level
                this.lastLossBet = this.lossBetTiers[this.lossBetLevel];
                // set max losses to 1 i.e. the track index count of the current index in the tiers or rather the first index
                this.maxLosses = 1;
                // set current bet to loss bet at current level in tier
                this.currentBet = this.lastLossBet;
            }
            else if(handOutcome.equals("PUSH")) {
                // do nothing and continue just log that fact
                System.out.println("Player pushed and will push the original bet taking back any doubles or splits from the table. Still on win track");
            }
            else {
                System.out.println("Won hand...On win track? " + this.onWinTrack + "...Next hand will be on Win track");
                // pay the 1:1 or 3:2 for BJ win to the player
                if(handOutcome.equals("BJ")) player.addToBankroll(this.currentBet*1.5);
                else player.addToBankroll(this.currentBet);
                // set win track to true
                this.onWinTrack = true;
                // increase win count
                this.maxWins++;
                // reset the loss tier bet level
                this.lossBetLevel = this.lossBetTiers[0];
                // set current bet to 15
                this.currentBet = 15;
            }
        }
        // else we are coming from a losing streak and need to think about this as such
        else {
            if(handOutcome.equals("LOST")) {
                System.out.println("Lost hand...On win track? " + this.onWinTrack + "...Next hand will be on Loss track at index: " + (this.lossBetLevel + 1));
                // subtract the current bet from the player bankroll
                player.subFromBankroll(this.currentBet);
                // still on loss track so keep win track false
                this.onWinTrack = false;
                // increment the loss bet level up until the max tier then reset
                this.lossBetLevel = this.lossBetLevel + 1 < this.lossBetTiers.length ? this.lossBetLevel + 1 : this.lossBetLevel - this.lossBetLevel;
                // set the new last loss bet
                this.lastLossBet = this.lossBetTiers[this.lossBetLevel];
                // increment max losses
                this.maxLosses++;
                // set the current bet
                this.currentBet = this.lastLossBet;
            }
            else if(handOutcome.equals("PUSH")) {
                // do nothing and continue just log that fact
                System.out.println("Player pushed and will push the original bet taking back any doubles or splits from the table. Still on loss track");
            }
            else {
                // pay the 1:1 or 3:2 for BJ win to the player
                if(handOutcome.equals("BJ")) player.addToBankroll(this.currentBet*1.5);
                else player.addToBankroll(this.currentBet);
                // set win track to true
                this.onWinTrack = true;
                // set max wins to 1 to reset starting count
                this.maxWins = 1;
                // reset the loss tier level
                this.lossBetLevel = this.lossBetTiers[0];
                // set the current bet to 15
                this.currentBet = 15;
            }
        }

    }

    public int getCurrentBet() {
        return currentBet;
    }
}
