package com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar;

import com.github.dieterdepaepe.jsearch.search.constructive.Cost;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.IntegerCost;
import com.google.common.collect.Ordering;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CostUtilTest {
    @Test
    public void testCompare() {
        Ordering<Cost> comp = CostUtil.COST_COMPARATOR;

        Cost cost0 = IntegerCost.valueOf(0);
        Cost cost1 = IntegerCost.valueOf(1);

        assertEquals(0, comp.compare(CostUtil.MIN_COST, CostUtil.MIN_COST));
        assertEquals(-1, comp.compare(CostUtil.MIN_COST, cost0));
        assertEquals(1, comp.compare(cost0, CostUtil.MIN_COST));
        assertEquals(0, comp.compare(CostUtil.MAX_COST, CostUtil.MAX_COST));
        assertEquals(-1, comp.compare(cost0, CostUtil.MAX_COST));
        assertEquals(1, comp.compare(CostUtil.MAX_COST, cost0));

        assertEquals(0, comp.compare(cost0, cost0));
        assertEquals(1, comp.compare(cost1, cost0));
        assertEquals(-1, comp.compare(cost0, cost1));
    }

}