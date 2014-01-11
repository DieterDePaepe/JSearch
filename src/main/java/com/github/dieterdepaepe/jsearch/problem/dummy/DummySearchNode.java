package com.github.dieterdepaepe.jsearch.problem.dummy;

import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;

/**
 * Implementation of a {@code SearchNode} for an entirely predefined problem.
 * @author Dieter De Paepe
 */
public class DummySearchNode implements SearchNode{
    private boolean isGoal;
    private double cost;
    private double heuristicValue;
    private Object searchStateIdentifier;

    /**
     * Creates a new search node with the specified values.
     * @param cost the cost for this node
     * @param heuristicValue the estimated remaining cost for this node
     * @param isGoal whether or not this node indicates a solution
     * @param searchStateIdentifier the search state identifier to be used, if null, this object will be used as identifier
     */
    public DummySearchNode(double cost, double heuristicValue, boolean isGoal, Object searchStateIdentifier) {
        this.isGoal = isGoal;
        this.cost = cost;
        this.heuristicValue = heuristicValue;
        this.searchStateIdentifier = searchStateIdentifier;
    }

    /**
     * Creates a new search node with the specified values and itself as the search state identifier.
     * @param cost the cost for this node
     * @param heuristicValue the estimated remaining cost for this node
     * @param isGoal whether or not this node indicates a solution
     */
    public DummySearchNode(double cost, double heuristicValue, boolean isGoal) {
        this(cost, heuristicValue, isGoal, null);
    }

    @Override
    public boolean isGoal() {
        return isGoal;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public Object getSearchSpaceState() {
        return searchStateIdentifier == null ? this : searchStateIdentifier;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }
}