package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * A {@link com.github.dieterdepaepe.jsearch.search.statespace.Solver} implementation that will examine the state space
 * in a depth first way. It may find multiple solutions along the way and will return an optimal solution when the state
 * space has been completely explored.
 *
 * <p>Nodes will be expanded in the order they are provided by the generator. Because of the limited number of nodes
 * in scope during search, this solver has a very low memory footprint. The depth first expansion makes this solver
 * unsuited for for infinite depth tree or graph-like state space, because the solver could endlessly
 * wander around the state space without encountering a solution. This problem can be somewhat remedied by defining a
 * maximum depth in the generator, but this is only possible if a maximum solution depth is known beforehand.</p>
 *
 * <p>This solver will not use the
 * {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#getSearchSpaceState()} information.</p>
 *
 * <p>This implementation is stateless and therefor thread-safe.</p>
 * @author Dieter De Paepe
 */
public class DepthFirstSolver implements Solver<SearchNode, Object> {
    @Override
    public <S extends SearchNode, E> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        Deque<Iterator<InformedSearchNode<S>>> nodesStack = new ArrayDeque<>();

        S bestGoalNode = null;
        Cost bestGoalNodeCost = manager.getCostBound();

        nodesStack.addFirst(startNodes.iterator());

        while (!nodesStack.isEmpty()) {
            if (!manager.continueSearch())
                return;

            Iterator<InformedSearchNode<S>> activeIterator = nodesStack.peekFirst();
            if (!activeIterator.hasNext()) {
                nodesStack.removeFirst();
                continue;
            }
            InformedSearchNode<S> informedNodeToExpand = activeIterator.next();

            // Don't expand node if it surpasses the cost boundary
            if (informedNodeToExpand.getEstimatedTotalCost().compareTo(manager.getCostBound()) > 0)
                continue;

            S searchNode = informedNodeToExpand.getSearchNode();
            if (searchNode.isGoal()) {
                if (searchNode.getCost().compareTo(bestGoalNodeCost) < 0 || bestGoalNode == null) {
                    bestGoalNode = searchNode;
                    bestGoalNodeCost = searchNode.getCost();
                }
                manager.registerSolution(new BasicSolution<>(searchNode, false));
            }

            // We examine the children even if searchNode was a solution, since it could be we are looking for multiple solutions.
            nodesStack.addFirst(searchNodeGenerator.generateSuccessorNodes(searchNode, environment, heuristic).iterator());
        }

        if (bestGoalNode != null)
            manager.registerSolution(new BasicSolution<>(bestGoalNode, true));
    }
}
