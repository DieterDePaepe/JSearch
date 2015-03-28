package com.github.dieterdepaepe.jsearch.search.constructive.solver;

import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeap;
import com.github.dieterdepaepe.jsearch.search.constructive.*;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicSolution;

/**
 * An <a href="http://en.wikipedia.org/wiki/A*">A*</a> implementation of a {@link Solver}. This solver will expand all
 * nodes whose total estimated cost is lower or equal to cost of the best solution. This solver is guaranteed to
 * find the optimal {@link Solution}, presuming that a solution is reachable.
 *
 * <p>During search, nodes are expanded on a best-first basis: each time, the node with the lowest total estimated cost
 * will be expanded. Because of this, the speed and memory requirements of this solver are greatly depended on
 * the accuracy of the used {@link Heuristic}.</p>
 *
 * <p>This solver assumes an admissible {@code Heuristic}. Should this assumption be violated, and the heuristic
 * overestimates the remaining cost by a factor of {@code e (> 0)}, the found solution is still guaranteed to be at most
 * <tt>(1 + e)</tt> times more expensive than the actual optimal solution. This technique may be used to speed up
 * the search and decrease memory usage by decreasing the number of visited nodes. Note however that the found
 * solution will still indicate optimality, since the solver assumes an admissible heuristic.</p>
 *
 * <p>This implementation is stateless and therefor thread-safe.</p>
 *
 * @see com.github.dieterdepaepe.jsearch.search.constructive.solver.AStarStateSolver
 * @author Dieter De Paepe
 */
public class AStarSolver implements Solver<SearchNode, Object> {

    @Override
    public <S extends SearchNode, E> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        FibonacciHeap<Cost, InformedSearchNode<S>> heap = FibonacciHeap.create();
        Cost costBound = manager.getCostBound();

        for (InformedSearchNode<S> startNode : startNodes)
            if (startNode.getEstimatedTotalCost().compareTo(costBound) <= 0)
                heap.insert(startNode.getEstimatedTotalCost(), startNode);

        while (!heap.isEmpty() && manager.continueSearch()) {
            InformedSearchNode<S> informedNodeToExpand = heap.deleteMinimum().getValue();
            costBound = manager.getCostBound();

            // The cost bound might have been lowered since this state was added to the queue, we need to check it again.
            if (informedNodeToExpand.getEstimatedTotalCost().compareTo(costBound) > 0)
                return;

            S nodeToExpand = informedNodeToExpand.getSearchNode();
            if (nodeToExpand.isGoal()) {
                manager.registerSolution(new BasicSolution<>(nodeToExpand, true));
            }

            for (InformedSearchNode<S> successor : searchNodeGenerator.generateSuccessorNodes(nodeToExpand, environment, heuristic)) {
                Cost estimatedTotalCost = successor.getEstimatedTotalCost();
                // Since A* can be very memory expensive, we do a premature purging of search nodes.
                if (estimatedTotalCost.compareTo(costBound) <= 0)
                    heap.insert(estimatedTotalCost, successor);
            }
        }
    }
}
