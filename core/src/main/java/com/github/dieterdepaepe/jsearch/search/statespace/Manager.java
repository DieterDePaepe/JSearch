package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A class responsible for handling any (intermediate) solutions found by a {@link Solver}. It provides a search bound
 * and stop criteria for the utilising {@code Solver}.
 *
 * @param <T> the type of search states to be used for this manager
 * @author Dieter De Paepe
 */
public interface Manager<T extends SearchNode> {
    /**
     * Indicates whether or not a {@code Solver} should continue its search.
     * @return true if search should continue
     */
    public boolean continueSearch();

    /**
     * Handles an (intermediate) solution found by a {@code Solver}.
     * @param solution the solution
     */
    public void registerSolution(Solution<? extends T> solution);

    /**
     * Provides a cost bound for a {@code Solver}, indicating it should not look for solutions whose cost is greater
     * than the returned value.
     * @return a cost bound
     */
    public Cost getCostBound();
}
