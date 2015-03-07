package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A node encountered in the search graph while solving a problem.
 *
 * <p>There is a subtle but important difference between a {@code SearchNode} and a search space state. A search space
 * state represents the result from a set of chosen actions. A {@code SearchNode} represents a result, but also
 * information regarding the path found to that search space state (most notably the cost needed to reach that state).
 * While the state space for a certain problem can be either a tree or a graph, the search graph is always a tree.</p>
 *
 * <h2>Implementation note:</h2>
 * Each implementation will be specific for a certain kind of problem being solved.
 * It is recommended to store only node-specific information, any information that is shared between nodes
 * should be stored in the problem environment (as described in {@link SearchNodeGenerator}).
 *
 * @author Dieter De Paepe
 */
public interface SearchNode {
    /**
     * Indicates whether this node is a solution.
     * @return true if it is a solution
     */
    public boolean isGoal();

    /**
     * Returns the exact cost associated with this search node.
     * @return the cost
     */
    public Cost getCost();

    /**
     * Returns a (lightweight) object that represents the search space state that has been reached by this node.
     *
     * <p>When 2 {@code SearchNode}s have an {@code equal} search space state, the most expensive node can be dropped
     * from the search by a solver.</p>
     * @return a non-null object
     */
    public Object getSearchSpaceState();
}
