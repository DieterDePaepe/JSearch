package com.github.dieterdepaepe.jsearch.search.constructive.solver.iterativedeepening;

import com.github.dieterdepaepe.jsearch.search.constructive.Heuristic;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNodeGenerator;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Wrapper around a {@code SearchNodeGenerator} which filters out states whose depth is greater than a specified
 * value. Also tracks whether or not any states were filtered since the creation of this generator.
 * @param <S> the type of search nodes being used
 * @param <E> the type of problem environment being used
 */
class DepthLimitedGenerator<S extends DepthSearchNode, E> implements SearchNodeGenerator<S, E> {
    private SearchNodeGenerator<S, E> innerGenerator;
    private Predicate<InformedSearchNode<S>> depthPredicate;
    private boolean filteredAnyResult;

    DepthLimitedGenerator(SearchNodeGenerator<S, E> innerGenerator, final int maxDepth) {
        this.innerGenerator = innerGenerator;
        this.depthPredicate = new Predicate<InformedSearchNode<S>>() {
            @Override
            public boolean apply(InformedSearchNode<S> input) {
                if (input.getSearchNode().getDepth() <= maxDepth) {
                    return true;
                } else {
                    filteredAnyResult = true;
                    return false;
                }
            }
        };
    }

    @Override
    public Iterable<InformedSearchNode<S>> generateSuccessorNodes(S node, E environment, Heuristic<? super S, ? super E> heuristic) {
        return Iterables.filter(innerGenerator.generateSuccessorNodes(node, environment, heuristic), depthPredicate);
    }

    public boolean filteredAnyResult() {
        return filteredAnyResult;
    }
}
