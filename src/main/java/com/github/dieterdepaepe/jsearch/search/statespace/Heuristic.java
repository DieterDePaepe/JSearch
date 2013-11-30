package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * A class capable of estimating the remaining cost needed for a search state to reach a goal state. A heuristic
 * is faster than finding the actual remaining cost. The closer the estimated cost is to the actual cost,
 * the better the heuristic.
 * <p>
 * <strong>Ideally, a heuristic is <a href="http://en.wikipedia.org/wiki/Admissible_heuristic">admissible</a>
 * (or optimistic)</strong>: it will never overestimate the remaining cost. A {@link Solver} will still function
 * with non-admissible heuristics, but may fail to find the optimal solution (depending on the implementation).
 * <p>
 * A heuristic can also be <a href="http://en.wikipedia.org/wiki/Consistent_heuristic">consistent (or monotone)</a>,
 * meaning that total goal cost estimation (actual cost + heuristic) will monotonically rise when getting closer to a
 * goal state. More formally, if {@code A} is a state, {@code B} is a successor state of {@code A} and {@code G}
 * is a goal state, the following holds:
 * <ul>
 *     <li>{@code h(A) <= deltaCost(A, B) + h(B)}</li>
 *     <li>{@code h(G) = 0}</li>
 * </ul>
 * Each consistent heuristic is automatically admissible.
 *
 * @param <T> the type of search states usable by this heuristic
 * @param <U> the type of the environment accompanied by the search state
 * @author Dieter De Paepe
 */
public interface Heuristic<T extends SearchState, U> {
    /**
     * Performs a fast estimate about the remaining cost needed for the given search state to reach a solution.
     * @param state the search state
     * @param environment the environment
     * @return an estimate about the remaining cost
     */
    public double estimateRemainingCost(T state, U environment);
}
