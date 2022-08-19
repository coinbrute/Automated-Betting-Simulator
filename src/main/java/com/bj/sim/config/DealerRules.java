package com.bj.sim.config;

/**
 * @param bjPayout 1.5 == 3:2 || 1.2 == 6:5
 */
public record DealerRules(boolean s17, Double bjPayout) {}
