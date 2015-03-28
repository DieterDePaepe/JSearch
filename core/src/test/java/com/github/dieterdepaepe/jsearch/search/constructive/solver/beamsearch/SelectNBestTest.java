package com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;


/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.beamsearch.SelectNBest}.
 * @author Dieter De Paepe
 */
public class SelectNBestTest {
    @Test
    public void testSelectsCorrectSubset() {
        SelectNBest nBest = new SelectNBest(4);

        Object stateIdentifier1 = new Object();
        Object stateIdentifier2 = new Object();

        List<DummySearchNode> list = Arrays.asList(
                new DummySearchNode("a", 5, 0, false, stateIdentifier2),
                new DummySearchNode("b", 3, 0, false, stateIdentifier1),
                new DummySearchNode("c", 0, 10, false, stateIdentifier2),
                new DummySearchNode("d", 8, 1, false),
                new DummySearchNode("e", 1, 5, false, stateIdentifier1),
                new DummySearchNode("f", 5, 6, false)
        );
        GenerationSelection<DummySearchNode> selection = nBest.selectNodesToExpand(toInformedNodes(list), null);

        List<InformedSearchNode<DummySearchNode>> result = Lists.newArrayList(selection.getSelectedNodes());

        Collections.sort(result);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0).getSearchNode(), list.get(1));
        assertEquals(result.get(1).getSearchNode(), list.get(0));
        assertEquals(result.get(2).getSearchNode(), list.get(4));
        assertEquals(result.get(3).getSearchNode(), list.get(3));

        assertNotNull(selection.getBestPrunedNode());
        assertEquals(selection.getBestPrunedNode().getSearchNode(), list.get(2));
    }

    @Test
    public void testSelectsAll() {
        SelectNBest nBest = new SelectNBest(10);

        Object stateIdentifier = new Object();

        List<DummySearchNode> list = Arrays.asList(
                new DummySearchNode("a", 5, 0, false),
                new DummySearchNode("b", 3, 0, false, stateIdentifier),
                new DummySearchNode("c", 0, 10, false),
                new DummySearchNode("d", 8, 1, false),
                new DummySearchNode("e", 1, 5, false, stateIdentifier),
                new DummySearchNode("f", 5, 6, false)
        );
        GenerationSelection<DummySearchNode> selection = nBest.selectNodesToExpand(toInformedNodes(list), null);

        List<InformedSearchNode<DummySearchNode>> result = Lists.newArrayList(selection.getSelectedNodes());

        assertEquals(result.size(), list.size());
        assertNull(selection.getBestPrunedNode());
    }

    private List<InformedSearchNode<DummySearchNode>> toInformedNodes(List<DummySearchNode> nodes) {
        List<InformedSearchNode<DummySearchNode>> result = new ArrayList<>(nodes.size());
        for (DummySearchNode node : nodes)
            result.add(new InformedSearchNode<>(node, node.getHeuristicValue()));
        return result;
    }
}
