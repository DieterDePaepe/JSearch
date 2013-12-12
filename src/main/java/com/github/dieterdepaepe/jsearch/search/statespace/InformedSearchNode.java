package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A container that contains a {@link SearchNode} and an estimate about the remaining cost before that node can
 * reach a solution.
 *
 * @param <T> the type of the {@code SearchNode} contained in this class
 * @author Dieter De Paepe
 */
public class InformedSearchNode<T extends SearchNode> {
    private final T searchNode;
    private final double estimatedRemainingCost;

    /**
     * Creates a new instance.
     * @param searchNode a node
     * @param estimatedRemainingCost an estimate of the remaining cost before the node can reach a solution
     */
    public InformedSearchNode(T searchNode, double estimatedRemainingCost) {
        this.searchNode = searchNode;
        this.estimatedRemainingCost = estimatedRemainingCost;
    }

    public T getSearchNode() {
        return searchNode;
    }

    public double getEstimatedRemainingCost() {
        return estimatedRemainingCost;
    }

    public double getEstimatedTotalCost() {
        return getSearchNode().getCost() + getEstimatedRemainingCost();
    }
}
