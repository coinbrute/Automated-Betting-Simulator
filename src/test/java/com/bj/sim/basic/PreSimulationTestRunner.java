package com.bj.sim.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreSimulationTestRunner {

    ChipTests chipTests;

    @BeforeEach
    private void setup() {
        chipTests = new ChipTests();
    }

    @Test
    public void runAll() {
        System.out.println("RUNNING PRE SIMULATION TESTS");
        setup();
        this.chipTests.runChipTests();
    }


}
