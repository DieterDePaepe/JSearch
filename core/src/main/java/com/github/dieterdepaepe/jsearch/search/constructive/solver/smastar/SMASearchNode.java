package com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar;

import com.github.dieterdepaepe.jsearch.search.constructive.Cost;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A {@code SearchNode} wrapper containing additional information used by
 * {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar.SMAStarSolver}.
 * @author Dieter De Paepe
 */
class SMASearchNode<T extends SearchNode> {
    private final T searchNode;
    private final SMASearchNode<T> parent;
    private final int depth;
    private Cost totalEstimatedCost;

    private boolean childHasBeenPrunedInIteration;
    private Cost cheapestPurgedCostInIteration;

    private Iterable<InformedSearchNode<T>> children;
    private Iterator<InformedSearchNode<T>> currentChildIterator;
    private Collection<SMASearchNode<T>> childrenInMemory;

    /**
     * Creates a new node.
     * @param searchNode the search node to wrap
     * @param parent the parent of this node, may be null
     * @param depth the depth of this node
     * @param totalEstimatedCost the estimated cost
     */
    SMASearchNode(T searchNode, SMASearchNode<T> parent, int depth, Cost totalEstimatedCost) {
        this.searchNode = searchNode;
        this.parent = parent;
        this.depth = depth;
        this.totalEstimatedCost = totalEstimatedCost;

        childHasBeenPrunedInIteration = false;
        cheapestPurgedCostInIteration = CostUtil.MAX_COST;

        children = null;
        currentChildIterator = null;
        childrenInMemory = Collections.emptySet();
    }

    /**
     * Returns whether or not this node already knows the {@code Iterable} containing its child nodes.
     * @return true if the children are known
     * @see #initialiseChildren(Iterable)
     */
    public boolean hasInitialisedChildren() {
        return currentChildIterator != null;
    }

    /**
     * Stores the {@code Iterable} containing the children of this node.
     * @param children the children
     */
    public void initialiseChildren(Iterable<InformedSearchNode<T>> children) {
        this.children = children;
        currentChildIterator = children.iterator();
        if (currentChildIterator.hasNext()) {
            childrenInMemory = new HashSet<>();
        }
    }

    /**
     * Creates a new iterator for the children stored in this node. Assumes the children have already been initialised.
     * @see #getCurrentChildIterator()
     */
    public void resetChildIterator() {
        currentChildIterator = children.iterator();
        cheapestPurgedCostInIteration = CostUtil.MAX_COST;
        childHasBeenPrunedInIteration = false;
    }

    /**
     * Removes the child from internally stored collection of children.
     * @param child the child to remove
     */
    public void removeChildFromMemory(SMASearchNode<T> child) {
        childrenInMemory.remove(child);
        childHasBeenPrunedInIteration = true;
        cheapestPurgedCostInIteration = CostUtil.COST_COMPARATOR.min(
                getCheapestPurgedCostInIteration(),
                child.getTotalEstimatedCost()
        );
    }

    /**
     * Returns if this node should be on the frontier (whether it may have any child that is currently not in memory).
     */
    public boolean shouldBeOnFrontier() {
        return currentChildIterator.hasNext() || childHasBeenPrunedInIteration;
    }

    public T getSearchNode() {
        return searchNode;
    }

    public SMASearchNode<T> getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public Cost getTotalEstimatedCost() {
        return totalEstimatedCost;
    }

    public Cost getCheapestPurgedCostInIteration() {
        return cheapestPurgedCostInIteration;
    }

    public boolean getChildHasBeenPrunedInIteration() {
        return childHasBeenPrunedInIteration;
    }

    public Collection<SMASearchNode<T>> getChildrenInMemory() {
        return childrenInMemory;
    }

    public Iterator<InformedSearchNode<T>> getCurrentChildIterator() {
        return currentChildIterator;
    }

    public void setTotalEstimatedCost(Cost totalEstimatedCost) {
        this.totalEstimatedCost = totalEstimatedCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMASearchNode that = (SMASearchNode) o;

        if (!searchNode.equals(that.searchNode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return searchNode.hashCode();
    }

    @Override
    public String toString() {
        return "SMASearchNode{" +
                "searchNode=" + searchNode +
                '}';
    }
}
