package com.github.dieterdepaepe.jsearch.search.statespace.util;

import com.github.dieterdepaepe.jsearch.search.statespace.Manager;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.Solution;

/**
 * Basic implementation of {@code Manager} meant for a single search by a single solver.
 * It will instruct to continue searching until an optimal solution is found.
 * Any intermediate solutions found will be passed as a cost bound to the solver in order to speed up the search.
 * <p/>
 * This implementation is not thread-safe.
 * @author Dieter De Paepe
 */
public class BasicManager<T extends SearchNode> implements Manager<T> {
    private Solution<? extends T> bestSolutionSoFar;

    public Solution<? extends T> getSolution() {
        return bestSolutionSoFar;
    }

    @Override
    public boolean continueSearch() {
        return bestSolutionSoFar == null || !bestSolutionSoFar.isOptimal();
    }

    @Override
    public void registerSolution(Solution<? extends T> solution) {
        if (bestSolutionSoFar == null || solution.isOptimal() || bestSolutionSoFar.getNode().getCost() > solution.getNode().getCost())
            bestSolutionSoFar = solution;
    }

    @Override
    public double getCostBound() {
        if (bestSolutionSoFar == null)
            return Double.POSITIVE_INFINITY;
        else
            return bestSolutionSoFar.getNode().getCost();
    }
}
