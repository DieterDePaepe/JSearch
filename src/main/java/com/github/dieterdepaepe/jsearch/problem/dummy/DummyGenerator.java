package com.github.dieterdepaepe.jsearch.problem.dummy;

import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNodeGenerator;
import com.google.common.collect.ListMultimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator for a dummy problem in which every {@code SearchNode} has been manually predefined.
 * This class is aimed at testing and experimentation.
 * @author Dieter De Paepe
 */
public class DummyGenerator implements SearchNodeGenerator<DummySearchNode, Object> {
    private ListMultimap<DummySearchNode, DummySearchNode> successorMapping;

    public DummyGenerator(ListMultimap<DummySearchNode, DummySearchNode> successorMapping) {
        this.successorMapping = successorMapping;
    }

    @Override
    public List<InformedSearchNode<DummySearchNode>> generateSuccessorNodes(DummySearchNode node, Object environment, Heuristic<? super DummySearchNode, ? super Object> heuristic) {
        List<DummySearchNode> successors = successorMapping.get(node);

        List<InformedSearchNode<DummySearchNode>> result = new ArrayList<>(successors.size());
        for (DummySearchNode successor : successors)
            result.add(new InformedSearchNode<>(successor, heuristic.estimateRemainingCost(successor, environment)));

        return result;
    }
}
