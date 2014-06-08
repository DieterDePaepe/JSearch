package com.github.dieterdepaepe.jsearch.problem.dummy;

import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNodeGenerator;
import com.google.common.collect.ListMultimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator for a dummy problem in which every {@code SearchNode} has been manually predefined.
 * This class is aimed at testing and experimentation.
 * @author Dieter De Paepe
 */
public class DummyGenerator<T extends SearchNode> implements SearchNodeGenerator<T, Object> {
    private ListMultimap<T, T> successorMapping;

    public DummyGenerator(ListMultimap<T, T> successorMapping) {
        this.successorMapping = successorMapping;
    }

    @Override
    public List<InformedSearchNode<T>> generateSuccessorNodes(T node, Object environment, Heuristic<? super T, ? super Object> heuristic) {
        List<T> successors = successorMapping.get(node);

        List<InformedSearchNode<T>> result = new ArrayList<>(successors.size());
        for (T successor : successors)
            result.add(new InformedSearchNode<>(successor, heuristic.estimateRemainingCost(successor, environment)));

        return result;
    }
}
