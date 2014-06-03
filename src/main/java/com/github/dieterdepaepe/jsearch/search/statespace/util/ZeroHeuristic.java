package com.github.dieterdepaepe.jsearch.search.statespace.util;

import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;

/**
 * General purpose admissible heuristic which always estimates a remaining cost of {@code 0}.
 * <p/>
 * This class is stateless and therefor thread-safe.
 * @author Dieter De Paepe
 */
public class ZeroHeuristic implements Heuristic<SearchNode, Object> {
    @Override
    public double estimateRemainingCost(SearchNode node, Object environment) {
        return 0;
    }
}
