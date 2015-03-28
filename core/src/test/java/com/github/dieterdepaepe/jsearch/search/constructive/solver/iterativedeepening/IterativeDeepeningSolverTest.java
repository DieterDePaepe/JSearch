package com.github.dieterdepaepe.jsearch.search.constructive.solver.iterativedeepening;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.search.constructive.Solvers;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.constructive.dev.LoggingGenerator;
import com.github.dieterdepaepe.jsearch.search.constructive.solver.DepthFirstSolver;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.iterativedeepening.IterativeDeepeningSolver}.
 * @author Dieter De Paepe
 */
public class IterativeDeepeningSolverTest {

    @Test
    public void findsSolutionAtLowerDepthFirst() {
        // Search space for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //  +-a-+--+--+--+
        //  b   |  |  |  |
        //  |   |  |  |  |
        //  C   |  |  |  |
        //      d  |  |  |
        //         |  E  |
        //         F     |
        //               G

        DummyDepthSearchNode a = new DummyDepthSearchNode("a", 0, 0, false, 0);
        DummyDepthSearchNode b = new DummyDepthSearchNode("b", 1, 0, false, 1);
        DummyDepthSearchNode c = new DummyDepthSearchNode("c", 2, 0, true,  2);
        DummyDepthSearchNode d = new DummyDepthSearchNode("d", 3, 0, false, 1);
        DummyDepthSearchNode e = new DummyDepthSearchNode("e", 4, 0, true,  1);
        DummyDepthSearchNode f = new DummyDepthSearchNode("f", 5, 0, true,  1);
        DummyDepthSearchNode g = new DummyDepthSearchNode("g", 6, 0, true,  1);

        ListMultimap<DummyDepthSearchNode, DummyDepthSearchNode> successors = ArrayListMultimap.create();
        successors.put(a, b);
        successors.put(b, c);
        successors.put(a, d);
        successors.put(a, f);
        successors.put(a, e);
        successors.put(a, g);

        LoggingGenerator<DummyDepthSearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummyDepthSearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.POSITIVE_INFINITY));
        IterativeDeepeningSolver<DummyDepthSearchNode, Object> solver = new IterativeDeepeningSolver<>(new DepthFirstSolver());

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(a, b, d, f, e)); //g is pruned by bound
        assertEquals(manager.getSolution().getNode(), e);
        assertTrue(manager.getSolution().isOptimal());
    }

    @Test
    public void testNodeExpansionOrder() {
        // Search space for this test, nodes are ordered by depth, no goal nodes
        //  +---a---+
        //  |   |   |
        //  b   c   d
        //  | \    / \
        //  e  f  g  h
        //   / |  |  |
        //  i  j  k  l

        DummyDepthSearchNode a = new DummyDepthSearchNode("a", 0, 0, false, 0);
        DummyDepthSearchNode b = new DummyDepthSearchNode("b", 0, 0, false, 1);
        DummyDepthSearchNode c = new DummyDepthSearchNode("c", 0, 0, false, 1);
        DummyDepthSearchNode d = new DummyDepthSearchNode("d", 0, 0, false, 1);
        DummyDepthSearchNode e = new DummyDepthSearchNode("e", 0, 0, false, 2);
        DummyDepthSearchNode f = new DummyDepthSearchNode("f", 0, 0, false, 2);
        DummyDepthSearchNode g = new DummyDepthSearchNode("g", 0, 0, false, 2);
        DummyDepthSearchNode h = new DummyDepthSearchNode("h", 0, 0, false, 2);
        DummyDepthSearchNode i = new DummyDepthSearchNode("i", 0, 0, false, 3);
        DummyDepthSearchNode j = new DummyDepthSearchNode("j", 0, 0, false, 3);
        DummyDepthSearchNode k = new DummyDepthSearchNode("k", 0, 0, false, 3);
        DummyDepthSearchNode l = new DummyDepthSearchNode("l", 0, 0, false, 3);

        ListMultimap<DummyDepthSearchNode, DummyDepthSearchNode> successors = ArrayListMultimap.create();
        successors.put(a, b);
        successors.put(a, c);
        successors.put(a, d);
        successors.put(b, e);
        successors.put(b, f);
        successors.put(d, g);
        successors.put(d, h);
        successors.put(f, i);
        successors.put(f, j);
        successors.put(g, k);
        successors.put(h, l);

        LoggingGenerator<DummyDepthSearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummyDepthSearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.POSITIVE_INFINITY));
        IterativeDeepeningSolver<DummyDepthSearchNode, Object> solver = new IterativeDeepeningSolver<>(new DepthFirstSolver());

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(
                a, b, c, d, //first iteration
                a, b, e, f, c, d, g, h, //second iteration
                a, b, e, f, i, j, c, d, g, k, h, l)); // third iteration
        assertNull(manager.getSolution());
    }
}
