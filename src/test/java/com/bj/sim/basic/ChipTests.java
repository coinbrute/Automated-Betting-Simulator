package com.bj.sim.basic;

import com.bj.sim.enums.Chip;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class ChipTests {

    @Test
    void runChipTests() {
        System.out.println("Running Tests on Chips...");
        Assert.isTrue(getChip_ByName("ONE") && getChip_ByAmount(1), "Chip Tests Pass..");
    }

    // write test to get the suits by name
    private static boolean getChip_ByName(String name) {
        System.out.println("==========================================================\n" +
                "Testing Get Chip By Name...INPUT --> " + name + "...OUTPUT --> "+ Chip.getChipByName(name));
        System.out.println("==========================================================\n");
        return name.equals(Chip.getChipByName(name).name());
    }

    private static boolean getChip_ByAmount(int amount) {
        System.out.println("==========================================================\n" +
                "Testing Get Chip By Amount...INPUT --> " + amount + "...OUTPUT --> "+Chip.getChipByAmount(amount).get());
        System.out.println("==========================================================\n");
        return amount == Chip.getAmount(Chip.getChipByAmount(amount).get());
    }
}
