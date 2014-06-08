package com.github.dieterdepaepe.jsearch.search.statespace.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeap;
import com.github.dieterdepaepe.jsearch.datastructure.priority.FibonacciHeapEntry;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link BeamSearchSolver.ParentSelector} which selects the {@code n} best (lowest total estimated cost), <b>unique
 * state-space</b> search nodes
 * for each iteration of beam search. This method is sometimes referred to as <i>local beam search</i>,
 * <i>fixed width beam search</i> or, if {@code n == 1}, <i>greedy local search</i>.
 * <p/>
 * This selector uses the {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#getSearchSpaceState()}
 * information so that in each generation, there will be no 2 nodes having the same search space state.
 * <p/>
 * This class is thread-safe.
 * @author Dieter De Paepe
 * @see com.github.dieterdepaepe.jsearch.search.statespace.solver.beamsearch.SelectNBest
 */
public class SelectUniqueNBest implements BeamSearchSolver.ParentSelector<SearchNode, Object> {
    private int n;

    /**
     * Creates a new instance that will select the {@code n} best nodes of each generation as parents for the next
     * generation, where each node has a unique state space.
     * @param n the number of nodes to select
     * @throws java.lang.IllegalArgumentException if n <= 0
     */
    public SelectUniqueNBest(int n) {
        checkArgument(n >= 1, "n should be >= 1");

        this.n = n;
    }

    @Override
    public <S extends SearchNode> GenerationSelection<S> selectNodesToExpand(Iterable<InformedSearchNode<S>> nodesToChooseFrom, Object environment) {
        FibonacciHeap<InformedSearchNode<S>> heap = new FibonacciHeap<>();
        Map<Object, FibonacciHeapEntry<InformedSearchNode<S>>> uniqueStates = new HashMap<>();
        InformedSearchNode<S> bestPrunedNode = null;

        for (InformedSearchNode<S> searchNode : nodesToChooseFrom) {
            // We negate the cost, so the highest costs have the highest priority in the heap.
            double heapKeyValue = -searchNode.getEstimatedTotalCost();

            FibonacciHeapEntry<InformedSearchNode<S>> sameStateEntry = uniqueStates.get(searchNode.getSearchNode().getSearchSpaceState());
            if (sameStateEntry != null) {
                if (sameStateEntry.getValue().getEstimatedTotalCost() > searchNode.getEstimatedTotalCost()) {
                    heap.delete(sameStateEntry);
                    FibonacciHeapEntry<InformedSearchNode<S>> newEntry = heap.insert(searchNode, heapKeyValue);
                    uniqueStates.put(searchNode.getSearchNode().getSearchSpaceState(), newEntry);
                }
                continue;
            }

            if (heap.size() < n) {
                FibonacciHeapEntry<InformedSearchNode<S>> newEntry = heap.insert(searchNode, heapKeyValue);
                uniqueStates.put(searchNode.getSearchNode().getSearchSpaceState(), newEntry);
            } else {
                FibonacciHeapEntry<InformedSearchNode<S>> mostExpensiveEntryInHeap = heap.findMinimum();
                if (searchNode.getEstimatedTotalCost() >= mostExpensiveEntryInHeap.getValue().getEstimatedTotalCost())
                    continue;

                FibonacciHeapEntry<InformedSearchNode<S>> removedEntry = heap.deleteMinimum();
                uniqueStates.remove(removedEntry.getValue().getSearchNode().getSearchSpaceState());
                FibonacciHeapEntry<InformedSearchNode<S>> newEntry = heap.insert(searchNode, heapKeyValue);
                uniqueStates.put(searchNode.getSearchNode().getSearchSpaceState(), newEntry);

                if (bestPrunedNode == null || bestPrunedNode.getEstimatedTotalCost() > removedEntry.getValue().getEstimatedTotalCost())
                    bestPrunedNode = removedEntry.getValue();
            }
        }

        List<InformedSearchNode<S>> selectedNodes = new ArrayList<>(heap.size());
        while (!heap.isEmpty())
            selectedNodes.add(heap.deleteMinimum().getValue());

        return new GenerationSelection<>(selectedNodes, bestPrunedNode);
    }

    @Override
    public String toString() {
        return "SelectUniqueNBest{" +
                "n=" + n +
                '}';
    }
}
