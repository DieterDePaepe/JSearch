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
     * The search will explore the search graph defined by the starting state, environment and state generator. Any found
     * goal nodes will be reported to the {@code Manager}. The provided heuristic may be used to guide or limit the
     * areas of the search graph being visited. It is assumed the provided heuristic is admissible unless otherwise
     * mentioned.
     * @param startNode the starting node from which the search state space will be constructed
     * @param environment a container for all static data related to the {@code SearchNode}s (see {@link SearchNodeGenerator})
     * @param heuristic an (admissible) heuristic that estimates the remaining cost for any {@code SearchNode}
     * @param searchNodeGenerator the generator of the search graph
     * @param manager the manager responsible for handling all found solutions
     * @param <T> the type of search nodes related to the problem being solved
     * @param <U> the type of the environment related to the problem being solved
     */
    public <T extends SearchNode, U> void solve(InformedSearchNode<T> startNode,
                                                 U environment,
                                                 Heuristic<? super T, ? super U> heuristic,
                                                 SearchNodeGenerator<T, U> searchNodeGenerator,
                                                 Manager<? super T> manager);
}
