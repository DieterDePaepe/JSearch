package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A state in the search state space encountered while solving a problem. Each state represents a step
 * towards a certain solution and contains the information required about that solution.
 *
 * <h2>Implementation note:</h2>
 * Each implementation will be specific for a problem being solved.
 * It is recommended to store only state-specific information, any information that is shared between states
 * should be stored in the problem environment (as described in the {@link StateGenerator} javadoc).
 *
 * @author Dieter De Paepe
 */
public interface SearchState {
    /**
     * Indicates whether this search state is a valid solution.
     * @return true if it is a solution
     */
    public boolean isGoalState();

    /**
     * Returns the exact cost associated with this search state.
     * @return the cost
     */
    public double getCost();
}
