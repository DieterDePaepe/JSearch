package com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.google.common.collect.Ordering;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link BeamSearchSolver.ParentSelector} which selects the {@code n} best (lowest total estimated cost) search nodes
 * for each iteration of beam search. This method is sometimes referred to as <i>local beam search</i>,
 * <i>fixed width beam search</i> or, if {@code n == 1}, <i>greedy local search</i>.
 *
 * <p>This class is thread-safe.</p>
 * @author Dieter De Paepe
 * @see com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch.SelectUniqueNBest
 */
public class SelectNBest implements BeamSearchSolver.ParentSelector<SearchNode, Object> {
    private int n;

    /**
     * Creates a new instance that will select the {@code n} best nodes of each generation as parents for the next
     * generation.
     * @param n the number of nodes to select
     * @throws java.lang.IllegalArgumentException if n <= 0
     */
    public SelectNBest(int n) {
        checkArgument(n >= 1, "n should be >= 1");

        this.n = n;
    }

    @Override
    public <S extends SearchNode> GenerationSelection<S> selectNodesToExpand(Iterable<InformedSearchNode<S>> nodesToChooseFrom, Object environment) {
        List<InformedSearchNode<S>> cheapestNodes = Ordering.natural().leastOf(nodesToChooseFrom, n + 1);
        if (cheapestNodes.size() <= n)
            return new GenerationSelection<>(cheapestNodes, null);
        else
            return new GenerationSelection<>(cheapestNodes.subList(0, n), cheapestNodes.get(n));
    }

    @Override
    public String toString() {
        return "SelectNBest{" +
                "n=" + n +
                '}';
    }
}
