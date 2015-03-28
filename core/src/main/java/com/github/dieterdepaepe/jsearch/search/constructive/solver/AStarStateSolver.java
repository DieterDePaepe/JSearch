package com.github.dieterdepaepe.jsearch.search.constructive.solver;

import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeap;
import com.github.dieterdepaepe.jsearch.search.constructive.*;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicSolution;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * An <a href="http://en.wikipedia.org/wiki/A*">A*</a> implementation of a {@link Solver}. This solver will expand all
 * nodes whose total estimated cost is lower or equal to cost of the best solution. This solver is guaranteed to
 * find the optimal {@link Solution}, presuming that a solution is reachable.
 *
 * <p>During search, nodes are expanded on a best-first basis: each time, the node with the lowest total estimated cost
 * will be expanded. The {@link com.github.dieterdepaepe.jsearch.search.constructive.StateSearchNode#getSearchSpaceState()
 * searchSpaceState} of each node is tracked and used as an additional pruning criteria. The speed and memory
 * requirements of this solver are greatly depended on the accuracy of the used {@link Heuristic} and the amount of
 * unique search space states.</p>
 *
 * <p>This solver assumes an admissible {@code Heuristic}. Should this assumption be violated, and the heuristic
 * overestimates the remaining cost by a factor of {@code e (> 0)}, the found solution is still guaranteed to be at most
 * <tt>(1 + e)</tt> times more expensive than the actual optimal solution. This technique may be used to speed up
 * the search and decrease memory usage by decreasing the number of visited nodes. Note however that the found
 * solution will still indicate optimality, since the solver assumes an admissible heuristic.</p>
 *
 * <p>This implementation is stateless and therefor thread-safe.</p>
 *
 * @see com.github.dieterdepaepe.jsearch.search.constructive.solver.AStarSolver
 * @author Dieter De Paepe
 */
public class AStarStateSolver implements Solver<StateSearchNode, Object> {
    @Override
    public <S extends StateSearchNode, E> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                     E environment,
                                                     Heuristic<? super S, ? super E> heuristic,
                                                     SearchNodeGenerator<S, E> searchNodeGenerator,
                                                     Manager<? super S> manager) {
        FibonacciHeap<Cost, InformedSearchNode<S>> heap = FibonacciHeap.create();
        Map<Object, Cost> bestEncounteredCostPerState = Maps.newHashMap();
        Cost costBound = manager.getCostBound();

        for (InformedSearchNode<S> startNode : startNodes) {
            if (startNode.getEstimatedTotalCost().compareTo(costBound) > 0)
                continue;
            Object searchSpaceState = startNode.getSearchNode().getSearchSpaceState();
            Cost stateCost = startNode.getSearchNode().getCost();
            Cost equalStateCost = bestEncounteredCostPerState.get(searchSpaceState);
            if (equalStateCost == null || stateCost.compareTo(equalStateCost) < 0) {
                heap.insert(startNode.getEstimatedTotalCost(), startNode);
                bestEncounteredCostPerState.put(searchSpaceState, stateCost);
            }
        }

        while (!heap.isEmpty() && manager.continueSearch()) {
            InformedSearchNode<S> informedNodeToExpand = heap.deleteMinimum().getValue();
            costBound = manager.getCostBound();

            // The cost bound might have been lowered since this state was added to the queue, we need to check it again.
            if (informedNodeToExpand.getEstimatedTotalCost().compareTo(costBound) > 0)
                return;

            S nodeToExpand = informedNodeToExpand.getSearchNode();

            Object searchSpaceState = nodeToExpand.getSearchSpaceState();
            if (bestEncounteredCostPerState.get(searchSpaceState).compareTo(nodeToExpand.getCost()) < 0)
                continue;

            if (nodeToExpand.isGoal()) {
                manager.registerSolution(new BasicSolution<>(nodeToExpand, true));
            }

            for (InformedSearchNode<S> successor : searchNodeGenerator.generateSuccessorNodes(nodeToExpand, environment, heuristic)) {
                Cost estimatedTotalCost = successor.getEstimatedTotalCost();
                // Since A* can be very memory expensive, we do a premature purging of search nodes.
                if (estimatedTotalCost.compareTo(costBound) > 0)
                    continue;
                Object successorSearchSpaceState = successor.getSearchNode().getSearchSpaceState();
                Cost stateCost = successor.getSearchNode().getCost();
                Cost equalStateCost = bestEncounteredCostPerState.get(successorSearchSpaceState);
                if (equalStateCost == null || stateCost.compareTo(equalStateCost) < 0) {
                    heap.insert(estimatedTotalCost, successor);
                    bestEncounteredCostPerState.put(successorSearchSpaceState, stateCost);
                }
            }
        }
    }
}
