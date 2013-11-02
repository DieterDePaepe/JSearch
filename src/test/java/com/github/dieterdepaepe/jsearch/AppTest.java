package com.github.dieterdepaepe.jsearch;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private int meaningOfLife;

    @BeforeClass
    public void setup() {
        meaningOfLife = 42;
    }

    @Test
    public void sampleTest() {
        assertEquals(42, meaningOfLife, "Incorrect meaning of life!");
    }
}
