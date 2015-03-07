package com.github.dieterdepaepe.jsearch.search.statespace.solver.beamsearch;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.beamsearch.SelectUniqueNBest}.
 * @author Dieter De Paepe
 */
public class SelectUniqueNBestTest {
    @Test
    public void testSelectsCorrectSubset() {
        SelectUniqueNBest nBest = new SelectUniqueNBest(3);

        Object stateIdentifier1 = new Object();
        Object stateIdentifier2 = new Object();
        Object stateIdentifier3 = new Object();

        List<DummySearchNode> list = Arrays.asList(
                new DummySearchNode("a", 5, 0, false, stateIdentifier2), //5
                new DummySearchNode("b", 3, 0, false, stateIdentifier1), //3
                new DummySearchNode("c", 0, 10, false),                  //10
                new DummySearchNode("d", 8, 1, false, stateIdentifier2), //9
                new DummySearchNode("e", 1, 5, false, stateIdentifier1), //6
                new DummySearchNode("f", 5, 3, false),                   //8
                new DummySearchNode("g", 5, 7, false, stateIdentifier3), //12
                new DummySearchNode("h", 8, 3, false, stateIdentifier3)  //11
        );
        GenerationSelection<DummySearchNode> selection = nBest.selectNodesToExpand(toInformedNodes(list), null);

        List<InformedSearchNode<DummySearchNode>> result = Lists.newArrayList(selection.getSelectedNodes());

        Collections.sort(result);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getSearchNode(), list.get(1)); //3
        assertEquals(result.get(1).getSearchNode(), list.get(0)); //5
        assertEquals(result.get(2).getSearchNode(), list.get(5)); //8

        assertEquals(selection.getBestPrunedNode().getSearchNode(), list.get(2)); //10
    }

    @Test
    public void testSelectsAll() {
        SelectUniqueNBest nBest = new SelectUniqueNBest(10);

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

        assertEquals(result.size(), 5);
        assertNull(selection.getBestPrunedNode());
    }

    @Test
    public void testPrunedNodeCorrectness() {
        SelectUniqueNBest nBest = new SelectUniqueNBest(1);

        List<DummySearchNode> list = Arrays.asList(
                new DummySearchNode("a", 1, 0, false),
                new DummySearchNode("b", 2, 0, false),
                new DummySearchNode("c", 3, 0, false)
        );
        GenerationSelection<DummySearchNode> selection = nBest.selectNodesToExpand(toInformedNodes(list), null);
        assertEquals(selection.getBestPrunedNode().getSearchNode(), list.get(1));

        Object stateIdentifier = new Object();
        list = Arrays.asList(
                new DummySearchNode("a", 3, 0, false, stateIdentifier),
                new DummySearchNode("b", 3, 0, false),
                new DummySearchNode("c", 1, 0, false, stateIdentifier)
        );
        selection = nBest.selectNodesToExpand(toInformedNodes(list), null);
        assertEquals(selection.getBestPrunedNode().getSearchNode(), list.get(1));

        list = Arrays.asList(
                new DummySearchNode("a", 3, 0, false),
                new DummySearchNode("b", 3, 0, false, stateIdentifier),
                new DummySearchNode("c", 1, 0, false, stateIdentifier)
        );
        selection = nBest.selectNodesToExpand(toInformedNodes(list), null);
        assertEquals(selection.getBestPrunedNode().getSearchNode(), list.get(0));
    }

    private List<InformedSearchNode<DummySearchNode>> toInformedNodes(List<DummySearchNode> nodes) {
        List<InformedSearchNode<DummySearchNode>> result = new ArrayList<>(nodes.size());
        for (DummySearchNode node : nodes)
            result.add(new InformedSearchNode<>(node, node.getHeuristicValue()));
        return result;
    }
}
