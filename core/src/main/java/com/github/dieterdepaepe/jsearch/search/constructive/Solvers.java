package com.github.dieterdepaepe.jsearch.search.constructive;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Static utility methods pertaining to {@link com.github.dieterdepaepe.jsearch.search.constructive.Solver}s.
 */
public class Solvers {
    /*
     * Private constructor, do not allow instances of this class.
     */
    private Solvers() {}

    /**
     * Helper method for calling {@link Solver#solve(Iterable, Object, Heuristic, SearchNodeGenerator, Manager)}, which
     * first applies the heuristic to all starting states.
     *
     * @param solver the solver to be called
     * @param manager the manager responsible for handling all found solutions
     * @param searchNodeGenerator the generator of the search graph
     * @param heuristic an (admissible) heuristic that estimates the remaining cost for any {@code SearchNode}
     * @param environment a container for all static data related to the {@code SearchNode}s (see {@link SearchNodeGenerator})
     * @param firstState the starting node from which the search state space will be constructed
     * @param otherStates any additional starting nodes
     * @param <U> the actual type of search nodes related to the problem being solved
     * @param <V> the actual type of the environment related to the problem being solved
     * @see Solver#solve(Iterable, Object, Heuristic, SearchNodeGenerator, Manager)
     */
    @SafeVarargs
    public static <U extends SearchNode, V> void solve(Solver<? super U, ? super V> solver,
                                                       Manager<? super U> manager,
                                                       SearchNodeGenerator<U, V> searchNodeGenerator,
                                                       Heuristic<? super U, ? super V> heuristic,
                                                       V environment,
                                                       U firstState,
                                                       U... otherStates) {
        solve(solver, manager, searchNodeGenerator, heuristic, environment, Lists.asList(firstState, otherStates));
    }

    /**
     * Helper method for calling {@link Solver#solve(Iterable, Object, Heuristic, SearchNodeGenerator, Manager)}, which
     * first applies the heuristic to all starting states.
     *
     * @param solver the solver to be called
     * @param manager the manager responsible for handling all found solutions
     * @param searchNodeGenerator the generator of the search graph
     * @param heuristic an (admissible) heuristic that estimates the remaining cost for any {@code SearchNode}
     * @param environment a container for all static data related to the {@code SearchNode}s (see {@link SearchNodeGenerator})
     * @param startStates the starting nodes from which the search state space will be constructed
     * @param <U> the actual type of search nodes related to the problem being solved
     * @param <V> the actual type of the environment related to the problem being solved
     * @see Solver#solve(Iterable, Object, Heuristic, SearchNodeGenerator, Manager)
     */
    public static <U extends SearchNode, V> void solve(Solver<? super U, ? super V> solver,
                                                       Manager<? super U> manager,
                                                       SearchNodeGenerator<U, V> searchNodeGenerator,
                                                       final Heuristic<? super U, ? super V> heuristic,
                                                       final V environment,
                                                       Iterable<U> startStates) {
        Iterable<InformedSearchNode<U>> informedStartStates = Iterables.transform(startStates, new Function<U, InformedSearchNode<U>>() {
            @Override
            public InformedSearchNode<U> apply(U input) {
                return new InformedSearchNode<>(input, heuristic.estimateRemainingCost(input, environment));
            }
        });

        solver.solve(informedStartStates, environment, heuristic, searchNodeGenerator, manager);
    }
}
