package com.github.dieterdepaepe.jsearch.problem.dummy;

import com.github.dieterdepaepe.jsearch.search.statespace.Cost;
import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;

/**
 * {@code Heuristic} implementation for {@code DummySearchNode}s, which simply reads the heuristic value stored within.
 * @author Dieter De Paepe
 */
public class DummyHeuristic implements Heuristic<DummySearchNode, Object> {
    @Override
    public Cost estimateRemainingCost(DummySearchNode node, Object environment) {
        return node.getHeuristicValue();
    }
}
