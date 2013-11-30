package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A class capable of searching solutions for a defined problem.
 * <p>
 * A {@code Solver} bundles all functionality on how to solve a problem, but no specifics of the problem being solved.
 * The advantages and disadvantages of a specific {@code Solver} depend on its implementation.
 *
 * @author Dieter De Paepe
 */
public interface Solver {
    /**
     * Starts a <a href="http://en.wikipedia.org/wiki/State_space_search">state space search</a> for the problem
     * described by the provided parameters.
     * <p>
     * The search will explore the state space defined by the starting state, environment and state generator. Any found
     * goal states will be reported to the {@code Manager}. The provided heuristic may be used to guide or limit the
     * areas of the state space being visited. It is assumed the provided heuristic is admissible unless otherwise
     * mentioned.
     * @param startState the starting state from which the search state space will be constructed
     * @param environment a container for all static data related to the search states (see {@link StateGenerator})
     * @param heuristic an (admissible) heuristic for the remaining cost before reaching a goal state of each search state
     * @param stateGenerator the state space generator
     * @param solutionHandler the manager to collect any results
     * @param <T> the type of search states related to the problem being solved
     * @param <U> the type of the environment related to the problem being solved
     */
    public <T extends SearchState, U> void solve(T startState,
                                                 U environment,
                                                 Heuristic<? super T, ? super U> heuristic,
                                                 StateGenerator<? super T, ? super U> stateGenerator,
                                                 Manager<? super T> solutionHandler);
}
