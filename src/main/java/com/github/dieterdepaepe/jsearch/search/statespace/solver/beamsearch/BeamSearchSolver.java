package com.github.dieterdepaepe.jsearch.search.statespace.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;
import com.google.common.collect.Iterables;

import java.util.*;

/**
 * Implementation of <a href="http://en.wikipedia.org/wiki/Beam_search">beam search</a>. Beam search can be made to
 * work with limited memory. Beam search may find multiple {@link Solution}s during search, but is not guaranteed to
 * find an optimal (or even any) solution during its search.
 * <p/>
 * Beam search works in iterations. In each iteration, a number of search nodes are selected by a {@link ParentSelector}
 * to generate the search nodes for the next iteration. All generated nodes are examined to see if they are a goal
 * node or not. The search will continue until the search space is exhausted or until instructed by the {@link Manager}.
 * <p/>
 * This solver uses the {@link SearchNode#getSearchSpaceState()} information to filter out equivalent search nodes in
 * each iteration.
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
    public <S extends U, E extends V> void solve(InformedSearchNode<S> startNode,
                                                 E environment,
                                                 Heuristic<? super S, ? super E> heuristic,
                                                 SearchNodeGenerator<S, E> searchNodeGenerator,
                                                 Manager<? super S> manager) {
        // Contains the cheapest estimated solution cost of a search node that was discarded by the parentSelector
        double bestDiscardedNodeCost = Double.POSITIVE_INFINITY;

        // Best solution found so far
        S bestSolution = null;

        Collection<InformedSearchNode<S>> children = Collections.singleton(startNode);

        while (!children.isEmpty()) {
            double costBound = manager.getCostBound();

            children = filterDuplicates(children);

            List<InformedSearchNode<S>> nonGoalChildren = new ArrayList<>(children.size());
            for (InformedSearchNode<S> child : children) {
                if (child.getEstimatedTotalCost() > costBound)
                    continue;
                if (child.getSearchNode().isGoal()) {
                    manager.registerSolution(new BasicSolution<>(child.getSearchNode(), false));
                    if (bestSolution == null || bestSolution.getCost() > child.getSearchNode().getCost())
                        bestSolution = child.getSearchNode();
                } else {
                    nonGoalChildren.add(child);
                }
            }

            if (!manager.continueSearch())
                return;

            GenerationSelection<S> selection = parentSelector.selectNodesToExpand(nonGoalChildren, environment);
            if (selection.getBestPrunedNode() != null)
                bestDiscardedNodeCost = Math.min(bestDiscardedNodeCost, selection.getBestPrunedNode().getEstimatedTotalCost());

            children = new ArrayList<>();
            for (InformedSearchNode<S> parent : selection.getSelectedNodes())
                Iterables.addAll(children, searchNodeGenerator.generateSuccessorNodes(parent.getSearchNode(), environment, heuristic));
        }

        // The search space has been exhausted. We can compare the best solution encountered against the best estimate
        // of a purged search node to decide whether or not we know for sure to have an optimal solution.
        if (bestSolution != null && bestSolution.getCost() <= bestDiscardedNodeCost)
            manager.registerSolution(new BasicSolution<>(bestSolution, true));
    }

    /**
     * Makes a collection containing search nodes that each have a unique state space state.
     * @param nodes the nodes
     * @param <T> the type of search node
     * @return a collection containing all unique search nodes
     */
    private <T extends SearchNode> Collection<InformedSearchNode<T>> filterDuplicates (Collection<InformedSearchNode<T>> nodes) {
        Map<Object, InformedSearchNode<T>> stateToNode = new HashMap<>();
        for (InformedSearchNode<T> node : nodes) {
            InformedSearchNode<T> sameStateNode = stateToNode.get(node.getSearchNode().getSearchSpaceState());
            if (sameStateNode == null || sameStateNode.getEstimatedTotalCost() > node.getEstimatedTotalCost())
                stateToNode.put(node.getSearchNode().getSearchSpaceState(), node);
        }

        return stateToNode.values();
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
        public <S extends U> GenerationSelection<S> selectNodesToExpand(Collection<InformedSearchNode<S>> nodesToChooseFrom, V environment);
    }
}
