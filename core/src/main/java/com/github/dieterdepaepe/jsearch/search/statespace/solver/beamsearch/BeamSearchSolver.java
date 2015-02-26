package com.github.dieterdepaepe.jsearch.search.statespace.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.List;

/**
 * Implementation of <a href="http://en.wikipedia.org/wiki/Beam_search">beam search</a>. Beam search can be made to
 * work with limited memory. Beam search may find multiple {@link Solution}s during search, but is not guaranteed to
 * find an optimal (or even any) solution during its search.
 * <p/>
 * Beam search works in iterations. In each iteration, a number of search nodes are selected by a {@link ParentSelector}
 * to generate the search nodes for the next iteration. All generated nodes are examined to see if they are a goal
 * node or not. The search will continue until the search space is exhausted or until instructed by the {@link Manager}.
 * <p/>
 * The solver can use the {@link SearchNode#getSearchSpaceState()} information depending on the {@code ParentSelector}
 * chosen.
 * <p/>
 * This implementation is thread-safe if the used {@code ParentSelector} is.
 * @author Dieter De Paepe
 */
public class BeamSearchSolver<U extends SearchNode, V> implements Solver<U, V> {
    private ParentSelector<U, V> parentSelector;

    public BeamSearchSolver(ParentSelector<U, V> parentSelector) {
        this.parentSelector = parentSelector;
    }

    @Override
    public <S extends U, E extends V> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                 E environment,
                                                 Heuristic<? super S, ? super E> heuristic,
                                                 SearchNodeGenerator<S, E> searchNodeGenerator,
                                                 Manager<? super S> manager) {
        // Contains the cheapest estimated solution cost of a search node that was discarded by the parentSelector
        Cost bestDiscardedNodeCost = null;

        // Best solution found so far
        S bestSolution = null;

        Iterable<InformedSearchNode<S>> children = startNodes;

        while (!Iterables.isEmpty(children)) {
            Cost costBound = manager.getCostBound();

            for (InformedSearchNode<S> child : children) {
                if (child.getEstimatedTotalCost().compareTo(costBound) > 0)
                    continue;
                if (child.getSearchNode().isGoal()) {
                    manager.registerSolution(new BasicSolution<>(child.getSearchNode(), false));
                    if (bestSolution == null || bestSolution.getCost().compareTo(child.getSearchNode().getCost()) > 0)
                        bestSolution = child.getSearchNode();
                }
            }

            if (!manager.continueSearch())
                return;

            children = Iterables.filter(children, new IsCheaperThan(manager.getCostBound()));
            GenerationSelection<S> selection = parentSelector.selectNodesToExpand(children, environment);
            if (selection.getBestPrunedNode() != null) {
                if (bestDiscardedNodeCost == null)
                    bestDiscardedNodeCost = selection.getBestPrunedNode().getEstimatedRemainingCost();
                else
                    bestDiscardedNodeCost = Ordering.natural().min(bestDiscardedNodeCost, selection.getBestPrunedNode().getEstimatedTotalCost());
            }

            List<Iterable<InformedSearchNode<S>>> childIterables = Lists.newArrayList();
            for (InformedSearchNode<S> parent : selection.getSelectedNodes())
                childIterables.add(searchNodeGenerator.generateSuccessorNodes(parent.getSearchNode(), environment, heuristic));
            children = Iterables.concat(childIterables);

        }

        // The search space has been exhausted. We can compare the best solution encountered against the best estimate
        // of a purged search node to decide whether or not we know for sure to have an optimal solution.
        if (bestSolution != null && (bestDiscardedNodeCost == null || bestSolution.getCost().compareTo(bestDiscardedNodeCost) <= 0))
            manager.registerSolution(new BasicSolution<>(bestSolution, true));
    }

    /**
     * A selection criteria used in beam search to decide which search nodes of each generation are used to form
     * the next generation.
     * @param <U> the type of search nodes required by this selector
     * @param <V> the type of problem environment required by this selector
     */
    public interface ParentSelector<U extends SearchNode, V> {
        /**
         * Selects the parent nodes for the next generation of search nodes.
         * @param nodesToChooseFrom the current generation of nodes
         * @param <S> the actual type of the search nodes
         * @return information about the selected nodes
         */
        public <S extends U> GenerationSelection<S> selectNodesToExpand(Iterable<InformedSearchNode<S>> nodesToChooseFrom, V environment);
    }

    /**
     * A predicate that checks whether an {@code InformedSearchNode} is strictly cheaper than a specified cost.
     */
    private static class IsCheaperThan implements Predicate<InformedSearchNode<?>> {
        private Cost maxAllowedCost;

        private IsCheaperThan(Cost maxAllowedCost) {
            this.maxAllowedCost = maxAllowedCost;
        }

        @Override
        public boolean apply(InformedSearchNode<?> input) {
            return input.getEstimatedTotalCost().compareTo(maxAllowedCost) < 0;
        }
    }
}
