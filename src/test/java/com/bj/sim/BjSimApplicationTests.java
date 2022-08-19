package com.bj.sim;

import com.bj.sim.basic.PreSimulationTestRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class BjSimApplicationTests {

    PreSimulationTestRunner preSimulationTestRunner;

    @Test
    void contextLoads() {
        System.out.println("Running all tests...\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
        preSimulationTestRunner = new PreSimulationTestRunner();
        preSimulationTestRunner.runAll();
    }

}
