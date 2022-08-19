package com.bj.sim.config;

/**
 * @param rsa           == re-split aces
 * @param lateSurrender false == no surrender
 */
public record TableRules(DealerRules dealerRuleConfig, Integer numOfDecks, boolean rsa, Integer numOfSplitsOnAces,
        Integer numOfSplitsOnNonAces, Integer numOfDrawsOnSplitAces, boolean lateSurrender, int minBet, int maxBet) {}

