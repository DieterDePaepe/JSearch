package com.github.dieterdepaepe.jsearch.search.statespace.solver.idastar;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.solver.DepthFirstSolver;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Implementation of <a href="http://en.wikipedia.org/wiki/IDA*">Iterative Deepening A* (IDA*)</a>. It is guaranteed
 * to find an optimal solution (if one exists) and uses less memory than normal A*
 * ({@link com.github.dieterdepaepe.jsearch.search.statespace.solver.AStarSolver}).
 *
 * <p>IDA* uses several iterations of depth first search, while limiting the search space to nodes up to a certain cost.
 * In each iteration, the cost bound is increased to the cost of the cheapest excluded search node from the previous
 * iteration. As a result, the number of iterations depends on the amount of search nodes having an equal total
 * estimated cost. Ideally, it is used for searches where the cost is the length of the path in the search tree.</p>
 *
 * <p>IDA* has a very low memory footprint. It only keeps {@code d} nodes in memory, with {@code d} being the depth
 * in the search tree. There is a computational overhead because search nodes are visited multiple times, but
 * this is typically acceptable in practice if each iteration visits a number of unvisited nodes that is a
 * factor higher compared to the previous iteration.</p>
 *
 * <p>This implementation is stateless and therefor thread-safe.</p>
 * @author Dieter De Paepe
 */
public class IDAStarSolver implements Solver<SearchNode, Object> {
    @Override
    public <S extends SearchNode, E> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        boolean searchSpaceExhausted = false;
        Cost costBound = null;

        DepthFirstSolver depthFirstSolver = new DepthFirstSolver();

        while (!searchSpaceExhausted && manager.continueSearch()) {
            CostBoundedFilter filter = new CostBoundedFilter(costBound);
            depthFirstSolver.solve(
                    Iterables.filter(startNodes, filter),
                    environment,
                    heuristic,
                    new FilteringSearchNodeGenerator<>(searchNodeGenerator, filter),
                    manager
            );
            if (filter.hasFilteredAnItem()) {
                costBound = filter.getMinExceedingCost();
                if (costBound.compareTo(manager.getCostBound()) > 0)
                    searchSpaceExhausted = true;
            } else {
                searchSpaceExhausted = true;
            }
        }

    }

    private static class FilteringSearchNodeGenerator<S extends SearchNode, E> implements SearchNodeGenerator<S, E> {
        private SearchNodeGenerator<S, E> innerGenerator;
        private Predicate<InformedSearchNode<?>> filter;

        private FilteringSearchNodeGenerator(SearchNodeGenerator<S, E> innerGenerator, Predicate<InformedSearchNode<?>> filter) {
            this.innerGenerator = innerGenerator;
            this.filter = filter;
        }

        @Override
        public Iterable<InformedSearchNode<S>> generateSuccessorNodes(S node, E environment, Heuristic<? super S, ? super E> heuristic) {
            return Iterables.filter(innerGenerator.generateSuccessorNodes(node, environment, heuristic), filter);
        }
    }
}
