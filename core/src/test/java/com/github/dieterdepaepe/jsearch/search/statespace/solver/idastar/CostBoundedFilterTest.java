package com.github.dieterdepaepe.jsearch.search.statespace.solver.idastar;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.idastar.CostBoundedFilter}.
 * @author Dieter De Paepe
 */
public class CostBoundedFilterTest {
    @Test
    public void testBehavior() {
        CostBoundedFilter filter = new CostBoundedFilter(10);

        assertFalse(filter.hasFilteredAnItem());

        filter.apply(createNode(0));
        filter.apply(createNode(5));
        filter.apply(createNode(-5));
        filter.apply(createNode(10));
        assertFalse(filter.hasFilteredAnItem());

        filter.apply(createNode(20));
        assertTrue(filter.hasFilteredAnItem());
        assertEquals(filter.getMinExceedingCost(), 20.0);

        filter.apply(createNode(15));
        filter.apply(createNode(100));
        filter.apply(createNode(12));
        filter.apply(createNode(16));
        assertTrue(filter.hasFilteredAnItem());
        assertEquals(filter.getMinExceedingCost(), 12.0);
    }

    private InformedSearchNode<DummySearchNode> createNode(double cost) {
        return new InformedSearchNode<>(new DummySearchNode("", cost, 0, false), 0);
    }
}
