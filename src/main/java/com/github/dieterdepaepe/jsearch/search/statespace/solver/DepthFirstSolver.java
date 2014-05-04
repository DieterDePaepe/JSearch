package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;
import com.google.common.collect.Lists;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * A {@link com.github.dieterdepaepe.jsearch.search.statespace.Solver} implementation that will examine the state space
 * in a depth first way. It may find multiple solutions along the way and will return an optimal solution when the state
 * space has been completely explored.
 * <p/>
 * Nodes will be expanded in the order they are provided by the generator. Because of the limited number of nodes
 * in scope during search, this solver has a very low memory footprint. The depth first expansion makes this solver
 * unsuited for for infinite depth tree like state spaces or graph-like state space, because the solver could endlessly
 * wander around the state space without encountering a solution. This problem can be somewhat remedied by defining a
 * maximum depth in the generator, but this is only possible if a maximum solution depth is known beforehand.
 * <p/>
 * This solver will not use the {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#getSearchSpaceState()}
 * information.
 * <p/>
 * This implementation is stateless and therefor thread-safe.
 * @author Dieter De Paepe
 */
public class DepthFirstSolver implements Solver<SearchNode, Object> {
    @Override
    public <S extends SearchNode, E> void solve(InformedSearchNode<S> startNode,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        Deque<InformedSearchNode<S>> nodesStack = new ArrayDeque<>();

        S bestGoalNode = null;
        double bestGoalNodeCost = Double.POSITIVE_INFINITY;

        nodesStack.addFirst(startNode);
        while (!nodesStack.isEmpty()) {
            if (!manager.continueSearch())
                return;

            InformedSearchNode<S> informedNodeToExpand = nodesStack.removeFirst();

            // Don't expand node if it surpasses the cost boundary
            if (informedNodeToExpand.getEstimatedTotalCost() > manager.getCostBound())
                continue;

            S searchNode = informedNodeToExpand.getSearchNode();
            if (searchNode.isGoal()) {
                if (searchNode.getCost() < bestGoalNodeCost || bestGoalNode == null) {
                    bestGoalNode = searchNode;
                    bestGoalNodeCost = searchNode.getCost();
                }
                manager.registerSolution(new BasicSolution<>(searchNode, false));
            } else {
                // Add generated successor nodes in reverse order to the stack, so they will be evaluated according
                // to the order defined by the generator.
                List<InformedSearchNode<S>> successors = Lists.newArrayList(searchNodeGenerator.generateSuccessorNodes(searchNode, environment, heuristic));
                for (int i = successors.size() - 1; i >= 0; i--)
                    nodesStack.addFirst(successors.get(i));
            }
        }

        if (bestGoalNode != null)
            manager.registerSolution(new BasicSolution<>(bestGoalNode, true));
    }
}
