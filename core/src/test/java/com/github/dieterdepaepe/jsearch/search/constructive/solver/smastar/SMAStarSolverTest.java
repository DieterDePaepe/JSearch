package com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.Solver;
import com.github.dieterdepaepe.jsearch.search.constructive.Solvers;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.constructive.dev.LoggingGenerator;
import com.github.dieterdepaepe.jsearch.search.constructive.solver.BasicSolverTest;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.smastar.SMAStarSolver}.
 * @author Dieter De Paepe
 */
public class SMAStarSolverTest extends BasicSolverTest {

    @Test
    public void testNodeExpansionOrder() {
        // Search space for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //  ----a----
        //  |   |   b
        //  c   |   |
        // / \  d   |
        // e |  |   |
        //   |  F   |
        //   g      |
        //   |      H
        //   I

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", 1, 0, false);
        DummySearchNode c = new DummySearchNode("c", 0.5, 1.5, false);
        DummySearchNode d = new DummySearchNode("d", 2, 1, false);
        DummySearchNode e = new DummySearchNode("e", 0, 4, false);
        DummySearchNode f = new DummySearchNode("f", 5, 0, true);
        DummySearchNode g = new DummySearchNode("g", 4, 2, false);
        DummySearchNode h = new DummySearchNode("h", 7, 0, true);
        DummySearchNode i = new DummySearchNode("i", 8, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(a, c);
        successors.put(a, d);
        successors.put(a, b);
        successors.put(c, e);
        successors.put(c, g);
        successors.put(g, i);
        successors.put(d, f);
        successors.put(b, h);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        SMAStarSolver solver = new SMAStarSolver(4);

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertEquals(manager.getSolution().getNode(), f);
        assertTrue(manager.getSolution().isOptimal());
        assertEquals(generator.getExpandedNodes(), Arrays.asList(a, b, c, d, b, e ,d));
    }

    @Test
    public void testNodeExpansionOrder2() {
        // Search space for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //  ----a----
        //  |   |   b
        //  c   |   |
        // / \  d   |
        // e |  |   |
        // | F  |   |
        // |    G   |
        // H        |
        //          I

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", 1, 0, false);
        DummySearchNode c = new DummySearchNode("c", 0.5, 1.5, false);
        DummySearchNode d = new DummySearchNode("d", 2, 1, false);
        DummySearchNode e = new DummySearchNode("e", 0, 4, false);
        DummySearchNode f = new DummySearchNode("f", 5, 0, true);
        DummySearchNode g = new DummySearchNode("g", 6, 0, true);
        DummySearchNode h = new DummySearchNode("h", 7, 0, true);
        DummySearchNode i = new DummySearchNode("i", 8, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(a, c);
        successors.put(a, d);
        successors.put(a, b);
        successors.put(b, i);
        successors.put(c, e);
        successors.put(c, f);
        successors.put(d, g);
        successors.put(e, h);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        SMAStarSolver solver = new SMAStarSolver(4);

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertEquals(manager.getSolution().getNode(), f);
        assertTrue(manager.getSolution().isOptimal());
        assertEquals(generator.getExpandedNodes(), Arrays.asList(a, b, c, d, b, e));
    }

    @Test
    public void testFindsSuboptimalSolutionDueToMemoryRestrictions() {
        // Search space for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //  ----a-------
        //  |      b   |
        //  c     / \  |
        // / \   d   | |
        // | |   |   | |
        // | |   E   | |
        // | F       | |
        // |         G |
        // H           |
        //             I

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", 1, 0, false);
        DummySearchNode c = new DummySearchNode("c", 2, 0, false);
        DummySearchNode d = new DummySearchNode("d", 3, 0, false);
        DummySearchNode e = new DummySearchNode("e", 4, 0, true);
        DummySearchNode f = new DummySearchNode("f", 5, 0, true);
        DummySearchNode g = new DummySearchNode("g", 6, 0, true);
        DummySearchNode h = new DummySearchNode("h", 7, 0, true);
        DummySearchNode i = new DummySearchNode("i", 8, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(a, b);
        successors.put(a, c);
        successors.put(a, i);
        successors.put(b, d);
        successors.put(b, g);
        successors.put(c, h);
        successors.put(c, f);
        successors.put(d, e);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        SMAStarSolver solver = new SMAStarSolver(3); //Insufficient memory to find the true optimal solution (E)

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertEquals(manager.getSolution().getNode(), f);
        assertFalse(manager.getSolution().isOptimal());
        assertEquals(generator.getExpandedNodes(), Arrays.asList(a, b, c));
    }

    @Test
    public void testFindsNoSolutionDueToMemoryRestrictions() {
        // Search space for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //    a
        //  / | \
        // b  |  |
        // |  c  |
        // |     d
        // E

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", 1, 0, false);
        DummySearchNode c = new DummySearchNode("c", 2, 0, false);
        DummySearchNode d = new DummySearchNode("d", 3, 0, false);
        DummySearchNode e = new DummySearchNode("e", 4, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(a, b);
        successors.put(a, c);
        successors.put(a, d);
        successors.put(b, e);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        SMAStarSolver solver = new SMAStarSolver(2); //Insufficient memory to find any solution

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertNull(manager.getSolution());
        assertEquals(generator.getExpandedNodes(), Arrays.asList(a));
    }

    @Override
    public Solver<SearchNode, Object> getBasicTestSolver() {
        return new SMAStarSolver(100);
    }
}
