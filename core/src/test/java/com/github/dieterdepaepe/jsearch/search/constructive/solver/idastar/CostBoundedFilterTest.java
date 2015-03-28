package com.github.dieterdepaepe.jsearch.search.constructive.solver.idastar;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.idastar.CostBoundedFilter}.
 * @author Dieter De Paepe
 */
public class CostBoundedFilterTest {
    @Test
    public void testBehavior() {
        CostBoundedFilter filter = new CostBoundedFilter(DoubleCost.valueOf(10));

        assertFalse(filter.hasFilteredAnItem());

        filter.apply(createNode(0));
        filter.apply(createNode(5));
        filter.apply(createNode(-5));
        filter.apply(createNode(10));
        assertFalse(filter.hasFilteredAnItem());

        filter.apply(createNode(20));
        assertTrue(filter.hasFilteredAnItem());
        assertEquals(filter.getMinExceedingCost(), DoubleCost.valueOf(20));

        filter.apply(createNode(15));
        filter.apply(createNode(100));
        filter.apply(createNode(12));
        filter.apply(createNode(16));
        assertTrue(filter.hasFilteredAnItem());
        assertEquals(filter.getMinExceedingCost(), DoubleCost.valueOf(12));
    }

    private InformedSearchNode<DummySearchNode> createNode(double cost) {
        return new InformedSearchNode<>(new DummySearchNode("", cost, 0, false), DoubleCost.valueOf(0));
    }
}
