package com.github.dieterdepaepe.jsearch.search.constructive.util;

import com.github.dieterdepaepe.jsearch.search.constructive.Cost;
import com.github.dieterdepaepe.jsearch.search.constructive.Manager;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.Solution;

/**
 * Basic implementation of {@code Manager} meant for a single search by a single solver.
 * It will instruct to continue searching until an optimal solution is found.
 * Any intermediate solutions found will be passed as a cost bound to the solver in order to speed up the search.
 *
 * <p>This implementation is not thread-safe.</p>
 * @author Dieter De Paepe
 */
public class BasicManager<T extends SearchNode> implements Manager<T> {
    private Solution<? extends T> bestSolutionSoFar;
    private Cost costBound;

    public BasicManager(Cost costBound) {
        this.costBound = costBound;
        this.bestSolutionSoFar = null;
    }

    public Solution<? extends T> getSolution() {
        return bestSolutionSoFar;
    }

    @Override
    public boolean continueSearch() {
        return bestSolutionSoFar == null || !bestSolutionSoFar.isOptimal();
    }

    @Override
    public void registerSolution(Solution<? extends T> solution) {
        Cost solutionCost = solution.getNode().getCost();
        int comparison = solutionCost.compareTo(costBound);

        if (comparison > 0)
            return;

        if (comparison < 0
                || bestSolutionSoFar == null
                || (solution.isOptimal() && !bestSolutionSoFar.isOptimal())) {
            bestSolutionSoFar = solution;
            costBound = solution.getNode().getCost();
        }
    }

    @Override
    public Cost getCostBound() {
        return costBound;
    }
}
