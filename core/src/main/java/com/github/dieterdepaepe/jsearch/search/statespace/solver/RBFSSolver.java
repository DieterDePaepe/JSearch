package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.*;

/**
 * Recursive best-first search: An adaptation of the <a href="http://en.wikipedia.org/wiki/A*">A*</a> algorithm which
 * uses linear memory. This {@link com.github.dieterdepaepe.jsearch.search.statespace.Solver} will always find a
 * single solution if one is reachable, and it will be guaranteed to be optimal.
 *
 * <p>Recursive best-first search expands nodes in a best-first order, but uses recursion rather than a priority heap
 * to track the nodes to be expanded. This means nodes will be visited and expanded multiple times to reconstruct
 * paths to previously visited search nodes. Specifically: each time a new node is to be expanded for the first time,
 * the search tree will be pruned up to the children of the common ancestor of the current node and the node to be
 * expanded. Then the search tree starting at the proper child will be reconstructed until the next best-cost node is
 * encountered. Because of this, performance depends greatly on the structure of the search tree.</p>
 *
 * <p>This solver assumes an admissible {@code Heuristic}. Should this assumption be violated, and the heuristic
 * overestimates the remaining cost by a factor of {@code e (> 0)}, the found solution is still guaranteed to be at most
 * <tt>(1 + e)</tt> times more expensive than the actual optimal solution. This technique may be used to speed up
 * the search by decreasing the number of visited nodes. Note however that the found
 * solution will still indicate optimality, since the solver assumes an admissible heuristic.</p>
 *
 * <p>This implementation is stateless and therefor thread-safe.</p>
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
    public <S extends SearchNode, E> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        Deque<SearchTreeLevel<S>> levelStack = new ArrayDeque<>();

        List<RBFSSearchNode<S>> startingSearchNodes = new ArrayList<>();
        Cost costBound = manager.getCostBound();
        for (InformedSearchNode<S> startNode : startNodes) {
            if (startNode.getEstimatedTotalCost().compareTo(costBound) <= 0)
                startingSearchNodes.add(new RBFSSearchNode<>(startNode.getSearchNode(), startNode.getEstimatedTotalCost()));
        }
        if (startingSearchNodes.isEmpty())
            return;

        levelStack.addFirst(new SearchTreeLevel<>(manager.getCostBound(), startingSearchNodes));

        while (manager.continueSearch()) {
            SearchTreeLevel<S> currentLevel = levelStack.peekFirst();
            List<RBFSSearchNode<S>> searchNodes = currentLevel.nodes;
            Collections.sort(searchNodes);
            RBFSSearchNode<S> bestCostNode = searchNodes.get(0);

            currentLevel.cutoffCost = Ordering.natural().min(currentLevel.cutoffCost, manager.getCostBound());
            if (bestCostNode.minimumSolutionCost.compareTo(currentLevel.cutoffCost) > 0 || !bestCostNode.mayLeadToSolution) {
                if (levelStack.size() > 1) {
                    // Move up the search tree to look somewhere else, updating the minimum cost of the parent node
                    levelStack.removeFirst();
                    RBFSSearchNode<S> searchNode = levelStack.peekFirst().nodes.get(0);
                    searchNode.minimumSolutionCost = bestCostNode.minimumSolutionCost;
                    searchNode.mayLeadToSolution = bestCostNode.mayLeadToSolution;
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
                bestCostNode.mayLeadToSolution = false;
                continue;
            }

            List<RBFSSearchNode<S>> rbfsSuccessors = new ArrayList<>();
            for (InformedSearchNode<S> successor : successors) {
                // By taking the minimum solution cost of the parent node into account, we can prevent unneeded backtracking
                // caused by using the minimum solution cost as the cutoff cost for a next iteration.
                rbfsSuccessors.add(new RBFSSearchNode<>(successor.getSearchNode(), Ordering.natural().max(successor.getEstimatedTotalCost(), bestCostNode.minimumSolutionCost)));
            }

            Cost newCutoffCost = currentLevel.cutoffCost;
            if (currentLevel.nodes.size() >= 2 && currentLevel.nodes.get(1).mayLeadToSolution)
                newCutoffCost = Ordering.natural().min(newCutoffCost, currentLevel.nodes.get(1).minimumSolutionCost);

            levelStack.addFirst(new SearchTreeLevel<>(newCutoffCost, rbfsSuccessors));
        }
    }

    /**
     * Container holding a level of the search tree, along with a cost boundary for searching inside the level.
     * @param <T> the type of the search nodes contained
     */
    private static class SearchTreeLevel<T extends SearchNode> {
        private Cost cutoffCost;
        private List<RBFSSearchNode<T>> nodes;

        private SearchTreeLevel(Cost cutoffCost, List<RBFSSearchNode<T>> nodes) {
            this.cutoffCost = cutoffCost;
            this.nodes = nodes;
        }
    }

    /**
     * A wrapper around a search node that can hold an adjustable lower bound for a possible solution which has the search
     * node as ancestor.
     * @param <T> the type of the search node wrapped
     */
    private static class RBFSSearchNode<T extends SearchNode> implements Comparable<RBFSSearchNode<T>> {
        private T searchNode;
        private Cost minimumSolutionCost;
        private boolean mayLeadToSolution;

        private RBFSSearchNode(T searchNode, Cost minimumSolutionCost) {
            this.searchNode = searchNode;
            this.minimumSolutionCost = minimumSolutionCost;
            this.mayLeadToSolution = true;
        }

        @Override
        public int compareTo(RBFSSearchNode<T> o) {
            return ComparisonChain.start().
                    compareTrueFirst(mayLeadToSolution, o.mayLeadToSolution).
                    compare(minimumSolutionCost, o.minimumSolutionCost).
                    result();
        }
    }
}
