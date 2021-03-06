package com.github.dieterdepaepe.jsearch.search.constructive;

/**
 * A solution found by a {@link Solver} working on a specific problem. A {@code Solution} is a simple wrapper around
 * a goal {@link SearchNode} which contains additional information provided by the used {@code Solver}.
 *
 * @param <T> the type of state stored within the solution
 * @author Dieter De Paepe
 */
public interface Solution<T extends SearchNode> {
    /**
     * Gets the goal node that was reached.
     * @return the node
     */
    public T getNode();

    /**
     * Gets whether or not the solver which found the solution believes this solution to be optimal.
     * @return true if the solver believed this solution to be optimal
     */
    public boolean isOptimal();
}
