package com.github.dieterdepaepe.jsearch.search.statespace;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Static utility methods pertaining to {@link com.github.dieterdepaepe.jsearch.search.statespace.Solver}s.
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
