package com.github.dieterdepaepe.jsearch.search.constructive.dev;

import com.github.dieterdepaepe.jsearch.search.constructive.Heuristic;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNodeGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for any {@link com.github.dieterdepaepe.jsearch.search.constructive.SearchNodeGenerator} which tracks
 * the order in which {@link com.github.dieterdepaepe.jsearch.search.constructive.SearchNode}s are expanded. This class
 * is intended for use in tests and experiments.
 * @author Dieter De Paepe
 */
public class LoggingGenerator<T extends SearchNode, U> implements SearchNodeGenerator<T, U> {
    private SearchNodeGenerator<T, U> wrappedGenerator;
    private List<T> expandedNodes;

    /**
     * Creates a wrapper for the given generator which will track the order in which nodes are expanded.
     * @param wrappedGenerator the generator to wrap
     */
    public LoggingGenerator(SearchNodeGenerator<T, U> wrappedGenerator) {
        this.wrappedGenerator = wrappedGenerator;
        expandedNodes = new ArrayList<>();
    }

    @Override
    public Iterable<InformedSearchNode<T>> generateSuccessorNodes(T node, U environment, Heuristic<? super T, ? super U> heuristic) {
        expandedNodes.add(node);
        return wrappedGenerator.generateSuccessorNodes(node, environment, heuristic);
    }

    /**
     * Gets the list containing all nodes that have been expanded so far by this generator.
     * @return a modifiable list
     */
    public List<T> getExpandedNodes() {
        return expandedNodes;
    }
}
