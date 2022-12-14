package com.bj.sim;

import com.bj.sim.config.DealerRules;
import com.bj.sim.config.TableRules;
import com.bj.sim.entities.floor.Table;
import com.bj.sim.simulator.Gameplay;

public class BjSimApplication {

    private static Integer rounds = 1;
    private static Gameplay game;
    private static long startTime;
    private static double pnl = 0.0;

    public static void main(String[] args) {
        // run and time the simulation
        startTime = System.currentTimeMillis();
        game = new Gameplay();
        Table playTable = configureSimulation(args);
        for(int i = 0; i < rounds; i++) {

            if(playTable.getPlayers().get(0).getBankroll() > 1000) pnl += playTable.getPlayers().get(0).subFromBankroll(playTable.getPlayers().get(0).getBankroll()-1000);
            else if(playTable.getPlayers().get(0).getBankroll() < 600) {
                System.out.println("Player ended up with a loss of: " + (1000 - playTable.getPlayers().get(0).getBankroll()) + "...\nThere is a total player vault pnl so far of: " + pnl + "...");
                if(pnl > 1000) {
                    System.out.println("Player has accumulated enough pnl to top up loss from last shoe with some left over... Topping back up to $1000 bankroll...");
                    playTable.getPlayers().get(0).addToBankroll(1000-playTable.getPlayers().get(0).getBankroll());
                } else if(pnl <= 1000 && pnl > 0) {
                    System.out.println("Player is using the pnl accumulated to top up loss... Topping up as much as possible");
                    playTable.getPlayers().get(0).addToBankroll(1000-(pnl-playTable.getPlayers().get(0).getBankroll()));
                } else {
                    System.out.println("In order to continue the simulation the pnl for the player has not accumulated enough yet so the player will put more in from the bank themselves...");
                    System.out.println("Topping back up to $1000...");
                    playTable.getPlayers().get(0).addToBankroll(1000-playTable.getPlayers().get(0).getBankroll());
                    pnl -= 1000-playTable.getPlayers().get(0).getBankroll();
                }
            }
            executeSimulation(args, playTable);
            System.out.println("\n******************************************************\n");

        }
        System.out.println("Player balance at end of simulation is..." + playTable.getPlayers().get(0).getBankroll() + "...");
        System.out.println("Simulation evaluated " + rounds + " rounds...");
        System.out.println("\n===================================================\n");
        System.out.println("That took " + (System.currentTimeMillis() - startTime) + " milliseconds");
    }

    private static Table configureSimulation(String[] args) {
        if(args[0].equals("--rounds")) rounds = Integer.valueOf(args[1]);
        return configureTable(args, null, false, false);
    }

    private static Table configureTable(String[]args, Table playTable, boolean inplay, boolean keepPlayer) {
        // declare local fields
        Double bjPayout=0.0;
        boolean s17,rsa,lateSurrender;
        s17=rsa=lateSurrender=false;
        Integer numosa,numosna,shoeCount,nodosa,playerCount,minBet,maxBet;
        numosa=numosna=shoeCount=nodosa=playerCount=minBet=maxBet=1;

        // set command line environment arguments into the configuration values
        if(args[2].equals("--s17")) s17 = Boolean.getBoolean(args[3]);
        if(args[4].equals("--payout")) bjPayout = Double.valueOf(args[5]);
        if(args[6].equals("--shoe-count")) shoeCount = Integer.valueOf(args[7]);
        if(args[8].equals("--rsa")) rsa = Boolean.getBoolean(args[9]);
        if(args[10].equals("--late-surrender")) lateSurrender = Boolean.getBoolean(args[11]);
        if(args[12].equals("--num-of-splits-on-aces")) numosa = Integer.valueOf(args[13]);
        if(args[14].equals("--num-of-splits-on-non-aces")) numosna = Integer.valueOf(args[15]);
        if(args[16].equals("--num-of-draw-on-split-aces")) nodosa = Integer.valueOf(args[17]);
        if(args[18].equals("--player-count")) playerCount = Integer.valueOf(args[19]);
        if(args[20].equals("--min-bet")) minBet = Integer.valueOf(args[21]);
        if(args[22].equals("--max-bet")) maxBet = Integer.valueOf(args[23]);
        DealerRules dr = new DealerRules(s17, bjPayout);
        TableRules tr = new TableRules(dr, shoeCount, rsa, numosa, numosna, nodosa, lateSurrender, minBet, maxBet);

        // set the play table
        if(inplay) return playTable;
        else if(keepPlayer) return new Table(tr, playerCount, playTable.getPlayers());
        else return new Table(tr, playerCount);
    }

    private static void executeSimulation(String[] args, Table playTable) {
        // run the simulation and call configure round to retrieve the table again.
        System.out.println("Running Simulation...");
            for(int i = 0; i < rounds ; i++) {
                if(playTable.getPlayers().get(0).getBankroll() < 600) {
                    System.out.println("Player balance is less than 60% walkaway threshold..." + playTable.getPlayers().get(0).getBankroll() + "...");
                    System.out.println("It took " + i + "/" + rounds + " rounds to reach loss threshold.");
                    return;
                }
                if(playTable.getShoe().size() < 25) {
                    if(playTable.getPlayers().get(0).getBankroll() < 600) {
                        System.out.println("Player balance is less than 60% walkaway threshold..." + playTable.getPlayers().get(0).getBankroll() + "...");
                        System.out.println("It took " + i + "/" + rounds + " rounds to reach loss threshold.");
                        return;

                    }
                    System.out.println("Player balance at end of shoe is..." + playTable.getPlayers().get(0).getBankroll() + "...");
                    playTable = configureTable(args, playTable, false, true);
                    System.out.println("\n\n===================================================\n" +
                            "\n===================================================\n" +
                            "\nRESHUFFLING SHOE...\n" +
                            "\n===================================================\n" +
                            "\n===================================================\n\n");
                }
                game.playRound(i, args, playTable);
                System.out.println(playTable.getPlayers().get(0).getSpecificBet(0).toString());
                configureTable(args, playTable, true, true);
            }

        System.out.println("Player balance at end of shoe is..." + playTable.getPlayers().get(0).getBankroll() + "...");
        System.out.println("Player played " + rounds + " hands...");
    }

}
