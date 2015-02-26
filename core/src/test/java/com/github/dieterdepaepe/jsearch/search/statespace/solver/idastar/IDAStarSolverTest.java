package com.github.dieterdepaepe.jsearch.search.statespace.solver.idastar;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.Solver;
import com.github.dieterdepaepe.jsearch.search.statespace.Solvers;
import com.github.dieterdepaepe.jsearch.search.statespace.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.statespace.dev.LoggingGenerator;
import com.github.dieterdepaepe.jsearch.search.statespace.solver.BasicSolverTest;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.idastar.IDAStarSolver}.
 * @author Dieter De Paepe
 */
public class IDAStarSolverTest extends BasicSolverTest {
    @Override
    public Solver<SearchNode, Object> getBasicTestSolver() {
        return new IDAStarSolver();
    }

    @Test
    public void testNodeExpansionOrder() {
        // Search space for this test, node depth is their cost, goals nodes are written in capitals.
        //0: +-a-+  +-b-+
        //1: |   c  d   e
        //   |     / \  |\
        //2: f    g  |  h i
        //   |       |
        //3: j       K
        //   |
        //4: L

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", 0, 0, false);
        DummySearchNode c = new DummySearchNode("c", 1, 0, false);
        DummySearchNode d = new DummySearchNode("d", 0, 1, false);
        DummySearchNode e = new DummySearchNode("e", 0, 1, false);
        DummySearchNode f = new DummySearchNode("f", 2, 0, false);
        DummySearchNode g = new DummySearchNode("g", 0, 2, false);
        DummySearchNode h = new DummySearchNode("h", 1, 1, false);
        DummySearchNode i = new DummySearchNode("i", 1, 1, false);
        DummySearchNode j = new DummySearchNode("j", 1, 2, false);
        DummySearchNode k = new DummySearchNode("k", 3, 0, true);
        DummySearchNode l = new DummySearchNode("l", 4, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(a, f);
        successors.put(a, c);
        successors.put(b, d);
        successors.put(b, e);
        successors.put(f, j);
        successors.put(j, l);
        successors.put(d, g);
        successors.put(d, k);
        successors.put(e, h);
        successors.put(e, i);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        IDAStarSolver solver = new IDAStarSolver();

        Solvers.solve(solver, manager, generator, heuristic, null, a, b);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(
                a, b, //Depth 0
                a, c, b, d, e, //Depth 1
                a, f, c, b, d, g, e, h, i, //Depth 2
                a, f, j, c, b, d, g, k, e, h, i //Depth 3
                //Search is finished before depth 4 is reached
        ));
        assertEquals(manager.getSolution().getNode(), k);
        assertTrue(manager.getSolution().isOptimal());
    }

    @Test
    public void testNodeExpansionOrderForBorderlineCosts() {
        // Search space for this test, nodes are shown per depth with cost between brackets, no goal nodes
        // a(0)  b(-Inf)  c(+Inf)
        //      /  \
        // d(-10)  e(10)
        //    |     |
        // f(-5)   g(5)

        //Note: g has a lower cost than its parent, which may lead to problems in other solvers, but works for this one.

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", Double.NEGATIVE_INFINITY, 0, false);
        DummySearchNode c = new DummySearchNode("c", Double.POSITIVE_INFINITY, 0, false);
        DummySearchNode d = new DummySearchNode("d", -10, 0, false);
        DummySearchNode e = new DummySearchNode("e", 10, 0, false);
        DummySearchNode f = new DummySearchNode("f", -5, 0, false);
        DummySearchNode g = new DummySearchNode("g", 5, 0, false);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(b, d);
        successors.put(b, e);
        successors.put(d, f);
        successors.put(e, g);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.POSITIVE_INFINITY));
        IDAStarSolver solver = new IDAStarSolver();

        Solvers.solve(solver, manager, generator, heuristic, null, a, b, c);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(
                b,
                b, d,
                b, d, f,
                a, b, d, f,
                a, b, d, f, e, g,
                a, b, d, f, e, g, c
        ));
        assertNull(manager.getSolution());
    }

    @Test
    public void findsOptimalSolution() {
        // Search space for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //   <start>
        //  /   |   \
        // a    b   |
        //      |   |
        //      C   |
        //          D

        DummySearchNode a = new DummySearchNode("a", 0, 0, false);
        DummySearchNode b = new DummySearchNode("b", 0, 0, false);
        DummySearchNode c = new DummySearchNode("c", 1, 0, true);
        DummySearchNode d = new DummySearchNode("d", 2, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(b, c);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        IDAStarSolver solver = new IDAStarSolver();

        Solvers.solve(solver, manager, generator, heuristic, null, a, b, d);

        assertEquals(manager.getSolution().getNode(), c);
        assertTrue(manager.getSolution().isOptimal());
    }
}
