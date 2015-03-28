package com.github.dieterdepaepe.jsearch.search.constructive.solver.iterativedeepening;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;

/**
 * Dummy implementation of {@code DepthSearchNode} for testing purposes.
 * @author Dieter De Paepe
 */
public class DummyDepthSearchNode extends DummySearchNode implements DepthSearchNode {
    private int depth;

    public DummyDepthSearchNode(String name, double cost, double heuristicValue, boolean isGoal, int depth, Object searchStateIdentifier) {
        super(name, cost, heuristicValue, isGoal, searchStateIdentifier);
        this.depth = depth;
    }

    public DummyDepthSearchNode(String name, double cost, double heuristicValue, boolean isGoal, int depth) {
        this(name, cost, heuristicValue, isGoal, depth, null);
    }

    @Override
    public int getDepth() {
        return depth;
    }
}
