package com.github.dieterdepaepe.jsearch.search.constructive;

/**
 * A class defining how one {@link SearchNode} can transition into other {@code SearchNode}s. In other words:
 * it defines how the <a href="http://en.wikipedia.org/wiki/Search_space">search space</a> is formed and is
 * responsible for creating the search tree.
 *
 * <h2>{@code SearchNode} versus Environment</h2>
 * All data related to certain state can be classified as either:
 * <ul>
 *     <li>static/global data shared between all {@code SearchNode}s encountered when solving a certain type of
 *     problem</li>
 *     <li>data not shared between all {@code SearchNode}s encountered when solving a certain type of problem</li>
 * </ul>
 * In order to prevent overhead and improve code clarity, it is advised to store data of the first category in a
 * container which we call the <strong>environment</strong>. The environment is shared between all {@code SearchNode}s
 * during a search and should therefor not contain any changing data (this is of course limited to exposed data -
 * the environment is an ideal location to place caches).
 *
 * <p>All other data is limited to one or a limited number of nodes, and should be stored in the
 * <strong>{@code SearchNode}</strong> implementation.</p>
 *
 * <h2>Implementation note:</h2>
 * A <a href="http://en.wikipedia.org/wiki/State_space">state space</a> can be represented
 * as a directed graph. It is advisable to design the {@code SearchNode} and corresponding {@code SearchNodeGenerator}
 * in such a way so that the state space will take on the form of a tree (when starting from a certain state).
 * This will prevent the overhead of exploring {@code SearchNode}s with a shared search
 * space state multiple times, or the need to purge these duplicate search paths. See the
 * {@link com.github.dieterdepaepe.jsearch.search.constructive.StateSearchNode} javadoc for more details.
 *
 * @param <T> the type of the search nodes used
 * @param <U> the type of the environment
 * @author Dieter De Paepe
 */
public interface SearchNodeGenerator<T extends SearchNode, U> {
    /**
     * Generate a new node for each valid transition from the provided state.
     * @param node the node for which to find successor states
     * @param environment the problem environment, containing all static data for the problem being solved
     * @param heuristic the heuristic to use
     * @return the new states, enriched with an estimate of the heuristic, in no particular order
     */
    public Iterable<InformedSearchNode<T>> generateSuccessorNodes(T node, U environment, Heuristic<? super T, ? super U> heuristic);
}
