package com.bj.sim.simulator;

import com.bj.sim.entities.cards.Card;
import com.bj.sim.entities.cards.Deck;
import com.bj.sim.entities.cards.Hand;
import com.bj.sim.entities.floor.Player;
import com.bj.sim.entities.floor.Table;

import java.util.ArrayList;
import java.util.List;

public class Gameplay {

    public Gameplay() {super();}

    public static Table playRound(int i, String[] args, Table playTable) {
        System.out.println("Playing round... " + i + "");
        System.out.println("Player balance at beginning of round is..." + playTable.getPlayers().get(0).getBankroll() + "...");
        // deal initial cards to player and dealer alternating dealing
        Card p1,d1,p2,d2;
        p1 = Deck.draw(playTable.getShoe());
        d1 = Deck.draw(playTable.getShoe());
        p2 = Deck.draw(playTable.getShoe());
        d2 = Deck.draw(playTable.getShoe());
        Player player1 = playTable.getPlayers().get(0);
        player1.getHands().clear();
        List<Card> dealerHand = playTable.getDealer().showDealerHand().getCards();
        dealerHand.clear();
        player1.viewPlayerHandByIndex(0).hit(p1);
        dealerHand.add(d1);
        player1.viewPlayerHandByIndex(0).hit(p2);
        dealerHand.add(d2);

        // declare and initialize decision and play variables
        String move = "";
        int playPass = 0;
        String splitDecision = "";
        ArrayList<Hand> hands = player1.getHands();
        int handIndex = player1.numberOfSplits()-1;

        // declare and initialize bet variable
        int currentBet = player1.getSpecificBet(handIndex).getCurrentBet(); // amount being bet for this hand

        // check for dealer blackjack with ace show card
        if((playTable.getDealer().showDealerHand().calculateTotal() == 21 &&
                playTable.getDealer().getDealerShowCard().getValueName().equals("Ace"))) {
            move = "STAND";
        }
        // check player blackjack and run associated strategy;
        if(player1.hasBJ(handIndex)) {
            player1.getSpecificBet(handIndex).runBettingStrategyAnalysis("BJ",player1);
            return playTable;
        }

        // perform while loop on the player initially dealt hand and perform perfect basic strategy until the move is stand
        while(!move.equals("STAND")) {

            // first check this is the first pass and dealer does not have 21
            if(playPass < 1 && playTable.getDealer().showDealerHand().calculateTotal() != 21) {
                // make decision on first hand since this is first pass and print out the hands of the player and dealer show card
                move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(0));
//                System.out.println("PLAYER HAND..." + player1.viewPlayerHandByIndex(0).toString());
//                System.out.println("DEALER HAND..." + playTable.getDealer().showDealerHand().toString(true, true));
            }
            // increment the play pass since we have made our first play decision
            playPass++;
            // make hit decisions
            hitMoves(move, playTable, hands, handIndex);
            // if move is split then split
            if(move.equals("SPLIT") || move.equals("SPLIT/HIT") || move.equals("SPLIT/STAND")) { // number of ace splits allowed
                move = makeSplitDecisions(splitDecision, player1, args, move, hands, handIndex, playTable);
            }
            // if move is double down then double down
            // if move is double/hit check for 3 cards if 3 then hit else double
            move = doubleMove(move, player1, hands, handIndex, playTable);
            // if move is stand do nothing
            if(move.equals("STAND")) break;
        }
        // automated dealer play for soft 17 will update this to take into consideration dealerRuleConfig
        automateDealerPlay(playTable);
        finalResults(player1, playTable, currentBet, handIndex);
        return playTable;
    }

    private static void hitMoves(String move, Table playTable, List<Hand> hands, int handIndex) {
        // if move is hit then hit
        if(move.equals("HIT") && hands.size() < 2) {
            Hand onlyHand = Player.hit(playTable, hands.get(handIndex), handIndex);
//            System.out.println("PLAYER RECEIVED HIT CARD FOR A LONELY HAND NOW WITH..." + onlyHand.toString());
        }

        if(move.equals("HIT") && hands.size() >= 2) {
            // check this hand was originally a hand split from an ace pair
            // hitting on split aces is not allowed by most configurations
            // will be an ace split hand if the player has more than one hand and both have aces as the 0 index card
            boolean acePair = false;
            for(int j = 0; j < hands.size(); j++) {
                acePair = hands.get(j).isPair() && hands.get(j).getCards().stream().anyMatch(c -> c.getValue() == 1);
            }
            if(!acePair) {
                Hand splitHand = Player.hit(playTable, hands.get(handIndex), handIndex);
//                System.out.println("PLAYER RECEIVE HIT CARD FOR ONE OF THE HANDS THEY SPLIT ON NOW WITH..." + splitHand.toString());
            }
        }
    }

    private static String doubleMove(String move, Player player1, List<Hand> hands, int handIndex, Table playTable) {
        if(move.equals("DOUBLE") && player1.viewPlayerHandByIndex(handIndex).getCards().size() < 3) {
            Hand doubled = Player.hit(playTable, hands.get(handIndex), handIndex);
//            System.out.println("PLAYER DOUBLED FOR A FINAL HAND OF..." + doubled.toString());
            move = "STAND";
        } else if(move.equals("DOUBLE/HIT") && player1.viewPlayerHandByIndex(handIndex).getCards().size() < 3) {
            Hand dHit = Player.hit(playTable, hands.get(handIndex), handIndex);
//            System.out.println("PLAYER DOUBLED SINCE THEY STILL COULD OTHERWISE THEY WOULD HAVE HIT BUT THEY ENDED UP WITH..." + dHit.toString());
            move = "STAND";
        } else if(move.equals("DOUBLE/HIT") && player1.viewPlayerHandByIndex(handIndex).getCards().size() >= 3) {
            Hand hit = Player.hit(playTable, hands.get(handIndex), handIndex);
//            System.out.println("PLAYER HIT SINCE THEY COULDN'T DOUBLE AGAIN AND NOW HAVE A HAND OF..." + hit.toString());
            move = allOtherAfterSplitDecisions(move, hands, handIndex, player1, playTable);
        } else if(move.equals("DOUBLE/STAND") && player1.viewPlayerHandByIndex(0).getCards().size() < 3) {
            Hand dStd = Player.hit(playTable, hands.get(handIndex), handIndex);
//            System.out.println("PLAYER DOUBLED SINCE THEY STILL COULD AND IT MADE SENSE TO NOT STAND. THEY NOW HAVE A HAND OF..." + dStd.toString());
        } else move = "STAND";
        return move;
    }

    private static void automateDealerPlay(Table playTable) {
        String dealerMove = "";
        boolean hasBusted = false;
        if(playTable.getPlayers().size() < 2) {
            for(int i = 0;i < playTable.getPlayers().get(0).getHands().size();i++) {
                if(playTable.getPlayers().get(0).getHands().get(i).calculateTotal() > 21) {
                    hasBusted = true;
                    break;
                }
            }
        }
        if(!hasBusted) {
            while(playTable.getDealer().showDealerHand().calculateTotal() <= 17 && !dealerMove.equals("STAND")) {
                if(Hand.isHandSoft(playTable.getDealer().showDealerHand())) {
                    if(playTable.getDealer().showDealerHand().calculateTotal() <= 17) {
//                        System.out.println("Dealer is hitting...");
                        playTable.getDealer().hit(playTable);
                    }
                } else if(playTable.getDealer().showDealerHand().calculateTotal() < 17) {
//                    System.out.println("Dealer is hitting...");
                    playTable.getDealer().hit(playTable);
                } else {
//                    System.out.println("Dealer is standing...");
                    dealerMove = "STAND";
                }
            }
        } else {
//            System.out.println("Player has busted so dealer will not be taking cards...");
        }

    }

    private static void finalResults(Player player1, Table playTable, int currentBet, int handIndex) {
        System.out.println("***** Player Finished moves *****");
//        System.out.println("FINAL HAND TALLY...");
//        System.out.println("PLAYER HAND..." + player1.getHands().toString());
//        System.out.println("DEALER HAND..." + playTable.getDealer().showDealerHand().toString(false, false));
//        System.out.println("PLAYER MADE MOVES UNTIL THEY STOOD OR BUSTED.");

        for(Hand hand : player1.getHands()) {
            int playerHandTotal = playTable.getPlayers().get(0).getHandTotal(hand);
            boolean playerSafe = playerHandTotal <= 21;
            int dealerHandTotal = playTable.getDealer().showDealerHand().calculateTotal();
            String outcome = "";
            if(playerSafe){
                if(playerHandTotal == dealerHandTotal) outcome = "PUSH";
                else if(playerHandTotal < dealerHandTotal && dealerHandTotal <= 21) outcome = "LOST";
                else if(playerHandTotal < dealerHandTotal && dealerHandTotal > 21) outcome = "WON";
                else outcome = "WON";
            } else {
                outcome = "LOST";
            }
            System.out.println("OUTCOME FOR PLAYER HAND " + hand + " IS PLAYER " + outcome + " AGAINST A DEALER HAND OF " + playTable.getDealer().showDealerHand().toString());
            handleBetting(outcome,currentBet, handIndex, player1);
        }

        System.out.println("\n===================================================\n");
    }

    private static void handleBetting(String handOutcome, int currentBet, int idx, Player player) {
        System.out.println("Bet for current hand is: " + currentBet + " chips. Bet Outcome is: " + handOutcome);
        // perform the betting strategy algorithm and update player bet for next hand as well as the bankroll
        player.getSpecificBet(0).runBettingStrategyAnalysis(handOutcome, player);
        System.out.println("Strategy dictates bet is to be: " + player.getSpecificBet(idx).getCurrentBet() + " chips for the next hand.");
        System.out.println("Player balance at end of round is..." + player.getBankroll() + "...");
    }

    private static String makeSplitDecisions(String splitDecision, Player player1, String[] args, String move, List<Hand> hands, int startingSplitHand, Table playTable) {
//        System.out.println("In makeSplitDecisions()");
        int handIndex = startingSplitHand;
        // 1. loop through player hands
        for(;handIndex < hands.size();handIndex++) {
//            System.out.println("Looping up hands on hand: " + (handIndex+1));
            // 2. after each iteration reset the split decision for the new hand
            if(handIndex > 0) splitDecision = "";
            // 3. before entering the while loop for the current hand check
            // index is on one of the hands we split into i.e not 0, and that we have not drawn a new card to it yet
            if(hands.get(handIndex).getCards().size() == 1) {
                Player.hit(playTable, hands.get(handIndex), handIndex);
//                System.out.println("Dealing Player original hand its 'hand complete' card and getting new play decision..." + hands.get(handIndex));
                move = splitDecision = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
                continue;
            }
            // 4. continue looping until player hand reaches a stand decision then move onto next hand that was split
            while(!splitDecision.equals("STAND")) {
//                System.out.println("Still in split decision while loop...Current decision is: " + splitDecision);
                // 5. first check that player has fewer hands than ace split and non-ace splits are allowed
                if((player1.numberOfSplits() <= playTable.getRules().numOfSplitsOnAces() || player1.numberOfSplits() <= playTable.getRules().numOfSplitsOnNonAces())) {
                    // do the following if the move is to split
                    if(move.equals("SPLIT")) {
                        // split the last hand in the player hands list
                        Player.hit(playTable, hands.get(handIndex), handIndex);
                        hands = splitAndPrint(player1, handIndex, hands, "Dealing Player original hand new card from split decision and getting new play decision...");
                        move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
                    } else if(move.equals("SPLIT/HIT")) {
                        // split the last hand in the player hands list
                        if((player1.numberOfSplits() < playTable.getRules().numOfSplitsOnAces() || player1.numberOfSplits() < playTable.getRules().numOfSplitsOnNonAces())) {
                            Player.hit(playTable, hands.get(handIndex), handIndex);
                            hands = splitAndPrint(player1, handIndex, hands, "Dealing Player original hand new card from split/hit and getting new play decision...");
                            move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
                        } else {
                            Player.hit(playTable, hands.get(handIndex), handIndex);
//                            System.out.println("Strictly hitting on a split/hit decision since cant hit anymore...");
                            move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
                        }
                    } else if(move.equals("SPLIT/STAND")) {
                        // split the last hand in the player hands list
                        if((player1.numberOfSplits() < playTable.getRules().numOfSplitsOnAces() || player1.numberOfSplits() < playTable.getRules().numOfSplitsOnNonAces())) {
                            Player.hit(playTable, hands.get(handIndex), handIndex);
                            hands = splitAndPrint(player1, handIndex, hands, "Dealing Player original hand new card and then standing...");
                            move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
                        } else {
//                            System.out.println("Player unable to make split move per basic strategy so opting to STAND...");
                            move = "STAND";
                        }
                    } else if(move.equals("HIT")) {
                        if((player1.numberOfSplits() < playTable.getRules().numOfSplitsOnAces() || player1.numberOfSplits() < playTable.getRules().numOfSplitsOnNonAces())) {
                            Player.hit(playTable, hands.get(handIndex), handIndex);
//                            System.out.println("Hitting on a Hit decision...");
                            move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
                        } else {
                            Player.hit(playTable, hands.get(handIndex), handIndex);
//                            System.out.println("Standing since can't split on hand or is not advisable to hit anymore");
                            move = "STAND";
                        }
                    } else {
//                        System.out.println("STANDING ON THE SPLITS");
                        move = "STAND";
                    }
                }
                move = splitDecision = allOtherAfterSplitDecisions(move, hands, handIndex, player1, playTable);
            }
        }
        return move;
    }

    private static List<Hand> splitAndPrint(Player player1, int handIndex, List<Hand> hands, String message) {
//        System.out.println("In splitAndPrint()");
        hands = Player.split(player1, handIndex, hands.get(handIndex).getCards().size()-2);
//        System.out.println("Player SPLIT hands into..." + hands.toString());
//        System.out.println(message + hands.get(handIndex).toString());
        return hands;
    }

    private static String allOtherAfterSplitDecisions(String move, List<Hand> hands, int handIndex, Player player1, Table playTable) {
//        System.out.println("In allOtherAfterSplitDecisions()");
        // do the following if the move is to just hit
        if(move.equals("HIT")) {
            if(hands.get(handIndex).getCardFromHand(0).getValueName().equals("Ace")) {
                if(hands.get(handIndex).getCards().size() < 3) {
                    Player.hit(playTable, hands.get(handIndex), handIndex);
//                    System.out.println("Player HIT THEN STAND after splitting aces...");
                } else {
//                    System.out.println("Player STAND after splitting and dealing on aces with hand of..." + hands.get(handIndex).toString());
                }
                move = "STAND";
            } else {
                Player.hit(playTable, hands.get(handIndex), handIndex);
//                System.out.println("Player HIT after splitting non-aces...");
                move = player1.getPlayStrategy().decideMove(player1, playTable.getDealer(), hands.get(handIndex));
            }

        }
        // do the following if the move is to double
        else if(move.equals("DOUBLE")) {
            if(!hands.get(handIndex).getCardFromHand(0).getValueName().equals("Ace")) { // assume double is a hit check for ace here like below
                Player.hit(playTable, hands.get(handIndex), handIndex);
//                System.out.println("Player DOUBLE after splitting hands...");
                move = "HIT";
            } else if(hands.get(handIndex).getCards().size() > 2) {
//                System.out.println("Player IS UNABLE TO DOUBLE PER RULES SO STANDS after splitting hands...");
                move = "STAND"; // cant make any more moves in a hand that's been doubled.
            } else {
//                System.out.println("EDGE DOUBLE CASE FOUND..." + hands.get(handIndex).toString());
                move = "STAND";
            }
        }
        // do the following if the move is to double/stand
        else if(move.equals("DOUBLE/STAND")) {
            if(!hands.get(handIndex).getCardFromHand(0).getValueName().equals("Ace")) {
                Player.hit(playTable, hands.get(handIndex), handIndex);
//                System.out.println("Player DOUBLES then STANDS after splitting hands...");
            } else {
//                System.out.println("Player STANDS on split aces...");
            }
            move = "STAND";
        }
        // do the following if the move is to double/hit
        else if(move.equals("DOUBLE/HIT")) {
            if(!hands.get(handIndex).getCardFromHand(0).getValueName().equals("Ace")) {
                Player.hit(playTable, hands.get(handIndex), handIndex);
//                System.out.println("Player STANDS after DOUBLING on split hands...");
                move = "STAND";
            } else {
                if(!hands.get(handIndex).getCardFromHand(0).getValueName().equals("Ace")) {
                    Player.hit(playTable, hands.get(handIndex), handIndex);
//                    System.out.println("Player HIT after DOUBLING on split hands...");
                    move = "HIT";
                } else {
                    Player.hit(playTable, hands.get(handIndex), handIndex);
//                    System.out.println("Player STANDS after drawing on split aces...");
                    move = "STAND";
                }
            }
        }
        return move;
    }
}
