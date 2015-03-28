package com.github.dieterdepaepe.jsearch.search.constructive.solver;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.SearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.Solution;
import com.github.dieterdepaepe.jsearch.search.constructive.Solver;
import com.github.dieterdepaepe.jsearch.search.constructive.Solvers;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.constructive.dev.LoggingGenerator;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * Testing class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.RBFSSolver}.
 * @author Dieter De Paepe
 */
public class RBFSSolverTest extends BasicSolverTest {
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
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.POSITIVE_INFINITY));
        RBFSSolver solver = new RBFSSolver();

        Solvers.solve(solver, manager, generator, heuristic, null, a);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(a, b, c, d, c, e, d));
        assertEquals(manager.getSolution().getNode(), f);
        assertTrue(manager.getSolution().isOptimal());
    }

    @Test
    public void testRespectsCutoffCost() {
        DummySearchNode startState = new DummySearchNode("a", 1.0, 0, false);
        DummySearchNode child1 = new DummySearchNode("b", 2.0, 0, false);
        DummySearchNode child2 = new DummySearchNode("c", 4.0, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> stateChildren = ArrayListMultimap.create();
        stateChildren.put(startState, child1);
        stateChildren.put(child1,child2);

        RBFSSolver solver = new RBFSSolver();
        DummyGenerator<DummySearchNode> generator = new DummyGenerator<>(stateChildren);
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(3.));

        Solvers.solve(solver, manager, generator, heuristic, null, startState);

        Solution<? extends DummySearchNode> solution = manager.getSolution();
        assertNull(solution);
    }

    @Override
    public Solver<SearchNode, Object> getBasicTestSolver() {
        return new RBFSSolver();
    }
}
