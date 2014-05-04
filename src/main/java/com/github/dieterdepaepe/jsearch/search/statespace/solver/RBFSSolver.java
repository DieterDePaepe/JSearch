package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;
import com.google.common.collect.Iterables;

import java.util.*;

/**
 * Recursive best-first search: An adaptation of the <a href="http://en.wikipedia.org/wiki/A*">A*</a> algorithm which
 * uses linear memory. This {@link com.github.dieterdepaepe.jsearch.search.statespace.Solver} will always find a
 * single solution if one is reachable, and it will be guaranteed to be optimal.
 * <p/>
 * Recursive best-first search expands nodes in a best-first order, but uses recursion rather than a priority heap
 * to track the nodes to be expanded. This means nodes will be visited and expanded multiple times to reconstruct
 * paths to previously visited search nodes. Specifically: each time a new node is to be expanded for the first time,
 * the search tree will be pruned up to the children of the common ancestor of the current node and the node to be
 * expanded. Then the search tree starting at the proper child will be reconstructed until the next best-cost node is
 * encountered. Because of this, performance depends greatly on the structure of the search tree.
 * <p/>
 * This solver does not use the {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#getSearchSpaceState()}
 * information.
 * <p/>
 * This solver assumes an admissible {@code Heuristic}. Should this assumption be violated, and the heuristic
 * overestimates the remaining cost by a factor of {@code e (> 0)}, the found solution is still guaranteed to be at most
 * <tt>(1 + e)</tt> times more expensive than the actual optimal solution. This technique may be used to speed up
 * the search by decreasing the number of visited nodes. Note however that the found
 * solution will still indicate optimality, since the solver assumes an admissible heuristic.
 * <p/>
 * This implementation is stateless and therefor thread-safe.
 * @author Dieter De Paepe
 */
public class RBFSSolver implements Solver<SearchNode, Object> {
    /*
    Implementation note:
    This class is an iterative version of the recursive algorithm described in Artificial Intelligence: A modern
     approach (3th edition). The iterative and recursive version showed equal performance when tested using the NPuzzle
     problem. The iterative version was chosen to prevent potential issues when solving problems with very
     deep search trees.
     */

    @Override
    public <S extends SearchNode, E> void solve(InformedSearchNode<S> startNode,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        Deque<SearchTreeLevel<S>> levelStack = new ArrayDeque<>();
        levelStack.addFirst(new SearchTreeLevel<>(manager.getCostBound(), Arrays.asList(new RBFSSearchNode<>(startNode.getSearchNode(), startNode.getEstimatedTotalCost()))));

        while (manager.continueSearch()) {
            SearchTreeLevel<S> currentLevel = levelStack.peekFirst();
            List<RBFSSearchNode<S>> searchNodes = currentLevel.nodes;
            Collections.sort(searchNodes);
            RBFSSearchNode<S> bestCostNode = searchNodes.get(0);

            currentLevel.cutoffCost = Math.min(currentLevel.cutoffCost, manager.getCostBound());
            if (bestCostNode.minimumSolutionCost > currentLevel.cutoffCost) {
                if (levelStack.size() > 1) {
                    // Move up the search tree to look somewhere else, updating the minimum cost of the parent node
                    levelStack.removeFirst();
                    levelStack.peekFirst().nodes.get(0).minimumSolutionCost = bestCostNode.minimumSolutionCost;
                    continue;
                } else {
                    // No solution can be found
                    return;
                }
            }

            if (bestCostNode.searchNode.isGoal()) {
                manager.registerSolution(new BasicSolution<>(bestCostNode.searchNode, true));
                return;
            }

            Iterable<InformedSearchNode<S>> successors = searchNodeGenerator.generateSuccessorNodes(bestCostNode.searchNode, environment, heuristic);
            if (Iterables.isEmpty(successors)) {
                bestCostNode.minimumSolutionCost = Double.POSITIVE_INFINITY;
                continue;
            }

            List<RBFSSearchNode<S>> rbfsSuccessors = new ArrayList<>();
            for (InformedSearchNode<S> successor : successors) {
                // By taking the minimum solution cost of the parent node into account, we can prevent unneeded backtracking
                // caused by using the minimum solution cost as the cutoff cost for a next iteration.
                rbfsSuccessors.add(new RBFSSearchNode<>(successor.getSearchNode(), Math.max(successor.getEstimatedTotalCost(), bestCostNode.minimumSolutionCost)));
            }

            double newCutoffCost = currentLevel.cutoffCost;
            if (currentLevel.nodes.size() >= 2)
                newCutoffCost = Math.min(newCutoffCost, currentLevel.nodes.get(1).minimumSolutionCost);

            levelStack.addFirst(new SearchTreeLevel<>(newCutoffCost, rbfsSuccessors));
        }
    }

    /**
     * Container holding a level of the search tree, along with a cost boundary for searching inside the level.
     * @param <T> the type of the search nodes contained
     */
    private static class SearchTreeLevel<T extends SearchNode> {
        private double cutoffCost;
        private List<RBFSSearchNode<T>> nodes;

        private SearchTreeLevel(double cutoffCost, List<RBFSSearchNode<T>> nodes) {
            this.cutoffCost = cutoffCost;
            this.nodes = nodes;
        }
    }

    /**
     * A wrapper around a search node that can hold an adjustable lower bound for a solution that has the search
     * node as ancestor.
     * @param <T> the type of the search node wrapped
     */
    private static class RBFSSearchNode<T extends SearchNode> implements Comparable<RBFSSearchNode<T>> {
        private T searchNode;
        private double minimumSolutionCost;

        private RBFSSearchNode(T searchNode, double minimumSolutionCost) {
            this.searchNode = searchNode;
            this.minimumSolutionCost = minimumSolutionCost;
        }

        @Override
        public int compareTo(RBFSSearchNode<T> o) {
            return Double.compare(minimumSolutionCost, o.minimumSolutionCost);
        }
    }
}
