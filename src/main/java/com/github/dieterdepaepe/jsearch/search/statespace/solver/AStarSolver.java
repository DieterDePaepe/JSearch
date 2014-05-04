package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeap;
import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;

/**
 * An <a href="http://en.wikipedia.org/wiki/A*">A*</a> implementation of a {@link Solver}. It uses a {@link Heuristic}
 * to minimise the number of expanded nodes. It will always find a single {@link Solution}, which is guaranteed
 * to be optimal (assuming at least one solution state is reachable).
 * <p/>
 * During search, nodes are expanded on a best-first basis: each time, the node with the lowest total estimated cost
 * will be expanded. Because of this, the speed and memory requirements of this solver are greatly depended on
 * the accuracy of the used {@code Heuristic}.
 * <p/>
 * This solver does not use the {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#getSearchSpaceState()}
 * information.
 * <p/>
 * This solver assumes an admissible {@code Heuristic}. Should this assumption be violated, and the heuristic
 * overestimates the remaining cost by a factor of {@code e (> 0)}, the found solution is still guaranteed to be at most
 * <tt>(1 + e)</tt> times more expensive than the actual optimal solution. This technique may be used to speed up
 * the search and decrease memory usage by decreasing the number of visited nodes. Note however that the found
 * solution will still indicate optimality, since the solver assumes an admissible heuristic.
 * <p/>
 * This implementation is stateless and therefor thread-safe.
 * @author Dieter De Paepe
 */
public class AStarSolver implements Solver<SearchNode, Object> {

    @Override
    public <S extends SearchNode, E> void solve(InformedSearchNode<S> startNode,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        FibonacciHeap<InformedSearchNode<S>> heap = new FibonacciHeap<>();
        heap.insert(startNode, startNode.getEstimatedTotalCost());

        while (!heap.isEmpty() && manager.continueSearch()) {
            InformedSearchNode<S> informedNodeToExpand = heap.deleteMinimum().getValue();
            double costBound = manager.getCostBound();

            // The cost bound might have been lowered since this state was added to the queue, we need to check it again.
            if (informedNodeToExpand.getEstimatedTotalCost() > costBound)
                return;

            S nodeToExpand = informedNodeToExpand.getSearchNode();
            if (nodeToExpand.isGoal()) {
                manager.registerSolution(new BasicSolution<>(nodeToExpand, true));
                return;
            }

            for (InformedSearchNode<S> successor : searchNodeGenerator.generateSuccessorNodes(nodeToExpand, environment, heuristic)) {
                // Since A* can be very memory expensive, we do a premature purging of search nodes.
                if (successor.getEstimatedTotalCost() <= costBound)
                    heap.insert(successor, successor.getEstimatedTotalCost());
            }
        }
    }
}
