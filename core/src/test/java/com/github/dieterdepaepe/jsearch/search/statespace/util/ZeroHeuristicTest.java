package com.github.dieterdepaepe.jsearch.search.statespace.util;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.statespace.util.ZeroHeuristic}.
 * @author Dieter De Paepe
 */
public class ZeroHeuristicTest {
    @Test
    public void testHeuristicValue() {
        Heuristic<SearchNode, Object> heuristic = new ZeroHeuristic();
        DummySearchNode searchNode = new DummySearchNode("", 1, 2, false);

        assertEquals(heuristic.estimateRemainingCost(searchNode, null), 0.);
    }
}