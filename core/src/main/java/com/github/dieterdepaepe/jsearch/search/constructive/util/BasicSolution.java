package com.github.dieterdepaepe.jsearch.search.constructive.util;

import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.Solution;

/**
 * Basic {@link Solution} implementation.
 *
 * @param <T> The type of the search node held by this solution.
 * @author Dieter De Paepe
 */
public class BasicSolution<T extends SearchNode> implements Solution<T> {
    private T searchNode;
    private boolean isOptimal;

    public BasicSolution(T searchNode, boolean optimal) {
        this.searchNode = searchNode;
        isOptimal = optimal;
    }

    @Override
    public T getNode() {
        return searchNode;
    }

    @Override
    public boolean isOptimal() {
        return isOptimal;
    }
}
