package com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;

/**
 * Container class holding information about the selection of the {@link SearchNode}s to be expanded in an iteration
 * of a {@link BeamSearchSolver}.
 * @author Dieter De Paepe
 */
public class GenerationSelection<T extends SearchNode> {
    private Iterable<InformedSearchNode<T>> selectedNodes;
    private InformedSearchNode<T> bestPrunedNode;

    /**
     * Creates a new selection
     * @param selectedNodes the chosen nodes that should be expanded for creating the next generation of nodes
     * @param bestPrunedNode the node with the lowest {@link InformedSearchNode#getEstimatedTotalCost()} that was pruned,
     * or {@code null} if no nodes were pruned
     */
    public GenerationSelection(Iterable<InformedSearchNode<T>> selectedNodes, InformedSearchNode<T> bestPrunedNode) {
        this.selectedNodes = selectedNodes;
        this.bestPrunedNode = bestPrunedNode;
    }

    /**
     * Gets the search nodes that were selected for further expansion
     * @return an iterable containing all selected nodes
     */
    public Iterable<InformedSearchNode<T>> getSelectedNodes() {
        return selectedNodes;
    }

    /**
     * Returns the search node with the lowest total estimated cost that was not selected for further expansion.
     * @return null if no search node was excluded from expansion
     */
    public InformedSearchNode<T> getBestPrunedNode() {
        return bestPrunedNode;
    }
}
