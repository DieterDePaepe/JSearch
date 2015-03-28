package com.github.dieterdepaepe.jsearch.search.constructive;

/**
 * A node, encountered in the search graph while solving a problem, that has a search space state identifier.
 *
 * <p>There is a subtle but important difference between a {@code SearchNode} and a search space state. A search space
  * state represents the result from a set of chosen actions. A {@code SearchNode} represents a result, but also
  * information regarding the path found to that search space state (most notably the cost needed to reach that state).
  * While the state space for a certain problem can be either a tree or a graph, the search graph is always a tree.</p>
 *
 * <p>Search nodes with an {@code equal} search space state represent the same (possibly incomplete) solution. This
 * means nodes may be safely dropped by a {@link com.github.dieterdepaepe.jsearch.search.constructive.Solver}
 * when a cheaper search node with an {@code equal} space state is known. This mechanism can be used by a {@code Solver}
 * to reduce the search graph.</p>
 *
 * @author Dieter De Paepe
 */
public interface StateSearchNode extends SearchNode {
    /**
     * Returns a (lightweight) object that represents the search space state that has been reached by this node.
     *
     * <p>When 2 {@code SearchNode}s have an {@code equal} search space state, the most expensive node can be dropped
     * from the search by a solver.</p>
     * @return a non-null object
     */
    public Object getSearchSpaceState();
}
