package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeap;
import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;

/**
 * An <a href="http://en.wikipedia.org/wiki/A*">A*</a> implementation of a {@link Solver}. It uses a {@link Heuristic}
 * to minimise the number of expanded nodes. It will always find a single {@link Solution}, which is guaranteed
 * to be optimal.
 * <p>
 * During search, nodes are expanded on a best-first basis: each time, the node with the lowest total estimated cost
 * will be expanded. Because of this, the speed and memory requirements of this solver are greatly depended on
 * the accuracy of the used {@code Heuristic}.
 * <p>
 * This solver assumes an admissible {@code Heuristic}. Should this assumption be violated, and the heuristic
 * overestimates the remaining cost by a factor of {@code e}, the found solution is still guaranteed to be at most
 * <tt>(1 + e)</tt> times more expensive than the actual optimal solution. This technique may be used to speed up
 * the search and decrease memory usage by decreasing the number of visited nodes. Note however that the found
 * solution will still indicate optimality, since the solver assumes an admissible heuristic.
 * <p>
 * This implementation is stateless and therefor thread-safe.
 * @author Dieter De Paepe
 */
public class AStarSolver implements Solver {

    @Override
    public <T extends SearchNode, U> void solve(InformedSearchNode<T> startNode,
                                                U environment,
                                                Heuristic<? super T, ? super U> heuristic,
                                                SearchNodeGenerator<T, U> searchNodeGenerator,
                                                Manager<? super T> manager) {

        FibonacciHeap<InformedSearchNode<T>> heap = new FibonacciHeap<InformedSearchNode<T>>();
        heap.insert(startNode, startNode.getEstimatedTotalCost());

        while (!heap.isEmpty() && manager.continueSearch()) {
            InformedSearchNode<T> informedNodeToExpand = heap.deleteMinimum().getValue();
            double costBound = manager.getCostBound();

            // The cost bound might have been lowered since this state was added to the queue, we need to check it again.
            if (informedNodeToExpand.getEstimatedTotalCost() > costBound)
                return;

            T nodeToExpand = informedNodeToExpand.getSearchNode();
            if (nodeToExpand.isGoal()) {
                manager.registerSolution(new BasicSolution<T>(nodeToExpand, true));
                return;
            }

            for (InformedSearchNode<T> successor : searchNodeGenerator.generateSuccessorNodes(nodeToExpand, environment, heuristic)) {
                if (successor.getEstimatedTotalCost() <= costBound)
                    heap.insert(successor, successor.getEstimatedTotalCost());
            }
        }
    }
}
