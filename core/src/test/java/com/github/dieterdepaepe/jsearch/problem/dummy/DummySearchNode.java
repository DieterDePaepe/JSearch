package com.github.dieterdepaepe.jsearch.problem.dummy;

import com.github.dieterdepaepe.jsearch.search.statespace.Cost;
import com.github.dieterdepaepe.jsearch.search.statespace.StateSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.cost.DoubleCost;

/**
 * Implementation of a {@code SearchNode} for an entirely predefined problem.
 * @author Dieter De Paepe
 */
public class DummySearchNode implements StateSearchNode {
    private String name;
    private boolean isGoal;
    private Cost cost;
    private Cost heuristicValue;
    private Object searchStateIdentifier;

    /**
     * Creates a new search node with the specified values.
     * @param name the string to return when calling {@code toString()}
     * @param cost the cost for this node
     * @param heuristicValue the estimated remaining cost for this node
     * @param isGoal whether or not this node indicates a solution
     * @param searchStateIdentifier the search state identifier to be used, if null, this object itself will be used as identifier
     */
    public DummySearchNode(String name, double cost, double heuristicValue, boolean isGoal, Object searchStateIdentifier) {
        this.name = name;
        this.isGoal = isGoal;
        this.cost = DoubleCost.valueOf(cost);
        this.heuristicValue = DoubleCost.valueOf(heuristicValue);
        this.searchStateIdentifier = searchStateIdentifier;
    }

    /**
     * Creates a new search node with the specified values and itself as the search state identifier.
     * @param name the string to return when calling {@code toString()}
     * @param cost the cost for this node
     * @param heuristicValue the estimated remaining cost for this node
     * @param isGoal whether or not this node indicates a solution
     */
    public DummySearchNode(String name, double cost, double heuristicValue, boolean isGoal) {
        this(name, cost, heuristicValue, isGoal, null);
    }

    @Override
    public boolean isGoal() {
        return isGoal;
    }

    @Override
    public Cost getCost() {
        return cost;
    }

    @Override
    public Object getSearchSpaceState() {
        return searchStateIdentifier == null ? this : searchStateIdentifier;
    }

    public Cost getHeuristicValue() {
        return heuristicValue;
    }

    @Override
    public String toString() {
        return name;
    }
}
