package com.github.dieterdepaepe.jsearch.search.constructive.solver;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.Solver;
import com.github.dieterdepaepe.jsearch.search.constructive.Solvers;
import com.github.dieterdepaepe.jsearch.search.constructive.StateSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.constructive.dev.LoggingGenerator;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Testing class for {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.AStarStateSolver}.
 */
public class AStarStateSolverTest extends BasicSolverTest {

    @Test
    public void testNodeExpansionOrder() {
        // Search state space for this test. It can be seen as a directed graph. Every edge has cost 2, except for
        // A->B, which has cost 5 and A->E, which has cost 4.
        //
        //   --<--a---->----b---->---c---->---d
        //   |    |         |
        //   |    |         |
        //   e-->-          |
        //   |              |
        //   -------->------
        //

        // Search tree for this test, nodes are ordered from cheap to expensive, goals nodes are written in capitals.
        //       ----a1------
        //  a2  |           |
        //  |   |  |--b2    |
        //  |   |  |   |     b1
        //  |   |  |   |    |
        //  |   |  |   c2   |
        //  |   |  |   |    c1
        //  |---e--    |    |
        //            D2    |
        //                  D1

        DummySearchNode a1 = new DummySearchNode("a1", 0, 0, false, "a");
        DummySearchNode a2 = new DummySearchNode("a2", 4, 0, false, "a");
        DummySearchNode b1 = new DummySearchNode("b1", 5, 0, false, "b");
        DummySearchNode b2 = new DummySearchNode("b2", 4, 0, false, "b");
        DummySearchNode c1 = new DummySearchNode("c1", 7, 0, false, "c");
        DummySearchNode c2 = new DummySearchNode("c2", 6, 0, false, "c");
        DummySearchNode d1 = new DummySearchNode("d1", 9, 0, true, "d");
        DummySearchNode d2 = new DummySearchNode("d2", 8, 0, true, "d");
        DummySearchNode e = new DummySearchNode("e", 2, 5.5, false, "e");

        ListMultimap<DummySearchNode, DummySearchNode> successors = ArrayListMultimap.create();
        successors.put(a1, e);
        successors.put(a1, b1);
        successors.put(e, a2);
        successors.put(e, b2);
        successors.put(b2, c2);
        successors.put(c2, d2);
        successors.put(b1, c1);
        successors.put(c1, d1);

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator<>(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        AStarStateSolver solver = new AStarStateSolver();

        Solvers.solve(solver, manager, generator, heuristic, null, a1);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(a1, b1, c1, e, b2, c2, d2));
        assertEquals(manager.getSolution().getNode(), d2);
        assertTrue(manager.getSolution().isOptimal());
    }

    @Override
    public Solver<StateSearchNode, Object> getBasicTestSolver() {
        return new AStarStateSolver();
    }
}