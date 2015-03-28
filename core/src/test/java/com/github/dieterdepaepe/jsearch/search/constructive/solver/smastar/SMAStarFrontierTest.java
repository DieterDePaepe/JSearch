package com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar;

import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * Testing class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar.SMAStarFrontier}.
 * @author Dieter De Paepe
 */
public class SMAStarFrontierTest {

    @Test
    public void testGetDeepestLeastCostNode() {
        SMAStarFrontier<DummySearchNode> frontier = new SMAStarFrontier<>();
        SMASearchNode<DummySearchNode> depth1Cost5 = createNode(1, 5);
        frontier.addNode(depth1Cost5);
        assertEquals(frontier.getDeepestLeastCostNode(), depth1Cost5);

        SMASearchNode<DummySearchNode> depth1Cost3 = createNode(1, 3);
        SMASearchNode<DummySearchNode> depth1Cost7 = createNode(1, 7);
        frontier.addNode(depth1Cost3);
        frontier.addNode(depth1Cost7);
        assertEquals(frontier.getDeepestLeastCostNode(), depth1Cost3);

        SMASearchNode<DummySearchNode> depth3Cost3 = createNode(3, 3);
        SMASearchNode<DummySearchNode> depth2Cost3 = createNode(2, 3);
        SMASearchNode<DummySearchNode> depth0Cost3 = createNode(0, 3);
        frontier.addNode(depth3Cost3);
        frontier.addNode(depth2Cost3);
        frontier.addNode(depth0Cost3);
        assertEquals(frontier.getDeepestLeastCostNode(), depth3Cost3);
    }

    @Test
    public void testRemoveShallowestHighestCostLeafNode() {
        SMAStarFrontier<DummySearchNode> frontier = new SMAStarFrontier<>();

        SMASearchNode<DummySearchNode> root = createNode(0, 1);
        SMASearchNode<DummySearchNode> depth1Cost4a = createNodeWithParent(1, 4, root);
        SMASearchNode<DummySearchNode> depth1Cost6  = createNodeWithParent(1, 6, root);
        SMASearchNode<DummySearchNode> depth1Cost4b = createNodeWithParent(1, 4, root);
        SMASearchNode<DummySearchNode> depth1Cost3  = createNodeWithParent(1, 3, root);
        SMASearchNode<DummySearchNode> depth1Cost5  = createNodeWithParent(1, 5, root);
        SMASearchNode<DummySearchNode> depth2Cost4  = createNodeWithParent(2, 4, depth1Cost6);
        SMASearchNode<DummySearchNode> depth2Cost5  = createNodeWithParent(2, 5, depth1Cost6);
        SMASearchNode<DummySearchNode> depth3Cost6  = createNodeWithParent(3, 6, depth2Cost5);

        for (SMASearchNode<DummySearchNode> node : Arrays.asList(depth1Cost4a, depth1Cost4b, depth1Cost3, depth1Cost5, depth2Cost4))
            frontier.addNode(node);

        assertEquals(frontier.removeShallowestHighestCostLeafNode(), depth1Cost5);
        assertEquals(frontier.removeShallowestHighestCostLeafNode(), depth1Cost4a);
        assertEquals(frontier.removeShallowestHighestCostLeafNode(), depth1Cost4b);
        assertEquals(frontier.removeShallowestHighestCostLeafNode(), depth2Cost4);
        assertEquals(frontier.removeShallowestHighestCostLeafNode(), depth1Cost3);

        boolean failedToRemoveLeafNode = false;
        try {
            frontier.removeShallowestHighestCostLeafNode();
        } catch (IllegalStateException e) {
            failedToRemoveLeafNode = true;
        }
        assertTrue(failedToRemoveLeafNode);
    }

    @Test
    public void testRemoveNode() {
        SMAStarFrontier<DummySearchNode> frontier = new SMAStarFrontier<>();

        SMASearchNode<DummySearchNode> depth1Cost5 = createNode(1, 5);
        SMASearchNode<DummySearchNode> depth1Cost3 = createNode(1, 3);
        SMASearchNode<DummySearchNode> depth1Cost1 = createNode(1, 1);

        for (SMASearchNode<DummySearchNode> node : Arrays.asList(depth1Cost5, depth1Cost3, depth1Cost1))
            frontier.addNode(node);

        assertEquals(frontier.getDeepestLeastCostNode(), depth1Cost1);
        assertTrue(frontier.removeNode(depth1Cost1));
        assertFalse(frontier.removeNode(depth1Cost1));
        assertEquals(frontier.getDeepestLeastCostNode(), depth1Cost3);

        SMASearchNode<DummySearchNode> depth3Cost0 = createNode(3, 0);
        frontier.addNode(depth3Cost0);
        frontier.removeNode(depth3Cost0);
        assertEquals(frontier.getDeepestLeastCostNode(), depth1Cost3);
    }

    private SMASearchNode<DummySearchNode> createNode(int depth, double cost) {
        return new SMASearchNode<>(new DummySearchNode(), null, depth, DoubleCost.valueOf(cost));
    }

    private SMASearchNode<DummySearchNode> createNodeWithParent(int depth, double cost, SMASearchNode<DummySearchNode> parent) {
        SMASearchNode<DummySearchNode> result = new SMASearchNode<>(new DummySearchNode(), parent, depth, DoubleCost.valueOf(cost));
        Collection<SMASearchNode<DummySearchNode>> children = parent.getChildrenInMemory();
        if (children.isEmpty()) {
            parent.initialiseChildren(new NonEmptyIterable<InformedSearchNode<DummySearchNode>>());
            children = parent.getChildrenInMemory();
        }
        children.add(result);
        return result;
    }

    private static class DummySearchNode implements SearchNode {
        @Override
        public boolean isGoal() { return false; }

        @Override
        public DoubleCost getCost() { return DoubleCost.valueOf(0); }
    }

    private static class NonEmptyIterable<T> implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                @Override
                public boolean hasNext() { return true; }

                @Override
                public T next() { throw new UnsupportedOperationException(); }

                @Override
                public void remove() { throw new UnsupportedOperationException(); }
            };
        }
    }
}
