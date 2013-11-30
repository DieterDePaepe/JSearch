package com.github.dieterdepaepe.jsearch.search.statespace;

import java.util.List;

/**
 * A class defining how one {@link SearchState} can transition into other {@code SearchState}s. In other words:
 * it defines how the search <a href="http://en.wikipedia.org/wiki/State_space">state space</a> is formed.
 *
 * <h2>State versus Environment</h2>
 * All data related to certain state can be classified as either:
 * <ul>
 *     <li>static/global data shared between all states encountered when solving a certain type of problem</li>
 *     <li>data not shared between all states encountered when solving a certain type of problem</li>
 * </ul>
 * In order to prevent overhead and improve clarity, we store the first category in a container which we call the
 * <strong>environment</strong>. The environment is shared between all states during a search and should therefor
 * not contain any changing data (this is of course limited to exposed data - the environment is an ideal location
 * to place caches).
 * <p>
 * All other data is limited to one or a limited number of states, and should be stored in the
 * <strong>{@code SearchState}</strong> implementation.
 *
 * <h2>Implementation note:</h2>
 * A state space can be represented as a directed graph. It is advisable to design the {@code SearchState} and
 * corresponding {@code StateGenerator} so that the state space will take on the form of a tree (when starting
 * from a certain state). This will prevent the overhead of encountering search states multiple times.
 *
 * @param <T> the type of the search states used
 * @param <U> the type of the environment
 * @author Dieter De Paepe
 */
public interface StateGenerator<T extends SearchState, U> {
    /**
     * Generate a new state for each valid transition from the provided state.
     * @param state the state for which to find successor states
     * @param environment the problem environment, containing all static data for the problem being solved
     * @return the new states, in no particular order
     */
    public List<T> generateSuccessorStates(T state, U environment);
}
