package com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeap;
import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeapEntry;
import com.github.dieterdepaepe.jsearch.search.constructive.Cost;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.StateSearchNode;
import com.google.common.collect.Ordering;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link BeamSearchSolver.ParentSelector} which selects the {@code n} best (lowest total estimated cost), <b>unique
 * state-space</b> search nodes
 * for each iteration of beam search. This method is sometimes referred to as <i>local beam search</i>,
 * <i>fixed width beam search</i> or, if {@code n == 1}, <i>greedy local search</i>.
 *
 * <p>This selector uses the
 * {@link com.github.dieterdepaepe.jsearch.search.constructive.StateSearchNode#getSearchSpaceState()}
 * information to identify equivalent search nodes. Equivalent nodes represent the same solution, but might have
 * a different cost. Only the cheapest node of each equivalence group is used for the node selection.</p>
 *
 * <p>This class is thread-safe.</p>
 * @author Dieter De Paepe
 * @see com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch.SelectNBest
 */
public class SelectUniqueNBest implements BeamSearchSolver.ParentSelector<StateSearchNode, Object> {
    private int n;

    /**
     * Creates a new instance that will select the {@code n} best nodes of each generation as parents for the next
     * generation, where each node has a unique state space.
     * @param n the number of nodes to select
     * @throws java.lang.IllegalArgumentException if {@code n <= 0}
     */
    public SelectUniqueNBest(int n) {
        checkArgument(n >= 1, "n should be >= 1");

        this.n = n;
    }

    @Override
    public <S extends StateSearchNode> GenerationSelection<S> selectNodesToExpand(Iterable<InformedSearchNode<S>> nodesToChooseFrom, Object environment) {
        // We reverse the order, so the highest costs have the highest priority in the heap.
        FibonacciHeap<Cost, InformedSearchNode<S>> heap = FibonacciHeap.create(Ordering.<Cost>natural().reverse());
        Map<Object, FibonacciHeapEntry<Cost, InformedSearchNode<S>>> uniqueStates = new HashMap<>();
        InformedSearchNode<S> bestPrunedNode = null;

        for (InformedSearchNode<S> searchNode : nodesToChooseFrom) {
            Cost heapKeyValue = searchNode.getEstimatedTotalCost();

            Object searchSpaceState = searchNode.getSearchNode().getSearchSpaceState();
            FibonacciHeapEntry<Cost, InformedSearchNode<S>> sameStateEntry = uniqueStates.get(searchSpaceState);
            if (sameStateEntry != null) {
                if (sameStateEntry.getValue().getEstimatedTotalCost().compareTo(searchNode.getEstimatedTotalCost()) > 0) {
                    heap.delete(sameStateEntry);
                    FibonacciHeapEntry<Cost, InformedSearchNode<S>> newEntry = heap.insert(heapKeyValue, searchNode);
                    uniqueStates.put(searchSpaceState, newEntry);
                }
                continue;
            }

            if (heap.size() < n) {
                FibonacciHeapEntry<Cost, InformedSearchNode<S>> newEntry = heap.insert(heapKeyValue, searchNode);
                uniqueStates.put(searchSpaceState, newEntry);
            } else {
                FibonacciHeapEntry<Cost, InformedSearchNode<S>> mostExpensiveEntryInHeap = heap.findMinimum();
                InformedSearchNode<S> prunedNode;

                if (searchNode.getEstimatedTotalCost().compareTo(mostExpensiveEntryInHeap.getValue().getEstimatedTotalCost()) >= 0) {
                    prunedNode = searchNode;
                } else {
                    prunedNode = heap.deleteMinimum().getValue();
                    uniqueStates.remove(prunedNode.getSearchNode().getSearchSpaceState());
                    FibonacciHeapEntry<Cost, InformedSearchNode<S>> newEntry = heap.insert(heapKeyValue, searchNode);
                    uniqueStates.put(searchSpaceState, newEntry);
                }

                if (bestPrunedNode == null || bestPrunedNode.getEstimatedTotalCost().compareTo(prunedNode.getEstimatedTotalCost()) >= 0)
                    bestPrunedNode = prunedNode;
            }
        }

        return new GenerationSelection<>(heap.asCollection(), bestPrunedNode);
    }

    @Override
    public String toString() {
        return "SelectUniqueNBest{" +
                "n=" + n +
                '}';
    }
}
