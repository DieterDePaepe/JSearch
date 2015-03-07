package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A node encountered in the search tree while solving a problem. It represents a solution or an intermediate
 * node in the path to a possible solution.
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
}
