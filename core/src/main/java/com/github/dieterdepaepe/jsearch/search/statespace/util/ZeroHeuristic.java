package com.github.dieterdepaepe.jsearch.search.statespace.util;

import com.github.dieterdepaepe.jsearch.search.statespace.Cost;
import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;

/**
 * General purpose admissible heuristic which always estimates a specified cost.
 *
 * <p>This heuristic is intended for cases where no heuristic is available, it is to be used
 * with a neutral (zero) cost estimate.</p>
 *
 * <p>This class is stateless and therefor thread-safe.</p>
 * @author Dieter De Paepe
 */
public class ZeroHeuristic implements Heuristic<SearchNode, Object> {
    private Cost zeroCost;

    /**
     * Creates a new instance which will return the given value as a cost estimate for any {@code SearchNode}.
     * @param zeroCost the value to return for each estimate
     */
    public ZeroHeuristic(Cost zeroCost) {
        this.zeroCost = zeroCost;
    }

    @Override
    public Cost estimateRemainingCost(SearchNode node, Object environment) {
        return zeroCost;
    }
}
