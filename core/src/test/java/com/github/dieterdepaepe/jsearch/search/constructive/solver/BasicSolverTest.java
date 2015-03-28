package com.github.dieterdepaepe.jsearch.search.constructive.solver;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.Solution;
import com.github.dieterdepaepe.jsearch.search.constructive.Solver;
import com.github.dieterdepaepe.jsearch.search.constructive.Solvers;
import com.github.dieterdepaepe.jsearch.search.constructive.StateSearchNode;
import com.github.dieterdepaepe.jsearch.search.constructive.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.constructive.util.BasicManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Test class for basic behaviour of any solver class.
 * @author Dieter De Paepe
 */
public abstract class BasicSolverTest {
    public abstract Solver<? super StateSearchNode, Object> getBasicTestSolver();

    @Test
    public void testShouldReturnNothingIfNoStartStates() {
        Solver<? super StateSearchNode, Object> solver = getBasicTestSolver();

        DummyGenerator<DummySearchNode> generator = new DummyGenerator<>(ArrayListMultimap.<DummySearchNode, DummySearchNode>create());
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));
        Solvers.solve(solver, manager, generator, heuristic, null, Collections.<DummySearchNode>emptyList());

        assertNull(manager.getSolution());
    }

    @Test
    public void testShouldFindNoSolutionIfNonePresent() {
        Solver<? super StateSearchNode, Object> solver = getBasicTestSolver();

        ListMultimap<DummySearchNode, DummySearchNode> stateChildren = ArrayListMultimap.create();
        DummyGenerator<DummySearchNode> generator = new DummyGenerator<>(stateChildren);
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));

        DummySearchNode startState1 = new DummySearchNode("a", 2.0, 0, false);
        DummySearchNode startState2 = new DummySearchNode("b", 1.0, 0, false);
        DummySearchNode startState3 = new DummySearchNode("c", 3.0, 0, false);
        DummySearchNode startState4 = new DummySearchNode("d", 4.0, 0, false);
        List<DummySearchNode> startStates = Arrays.asList(startState1, startState2, startState3, startState4);

        //Case containing only start nodes
        Solvers.solve(solver, manager, generator, heuristic, null, startStates);
        Solution<? extends DummySearchNode> solution = manager.getSolution();
        assertNull(solution);

        //Case containing start and child nodes
        DummySearchNode childState1 = new DummySearchNode("e", 2, 0, false);
        DummySearchNode childState2 = new DummySearchNode("f", 5, 0, false);
        stateChildren.put(startState2, childState1);
        stateChildren.put(startState2, childState2);

        Solvers.solve(solver, manager, generator, heuristic, null, startStates);
        solution = manager.getSolution();
        assertNull(solution);
    }

    @Test
    public void testShouldFindSolutionAmongStartStates() {
        Solver<? super StateSearchNode, Object> solver = getBasicTestSolver();

        DummyGenerator<DummySearchNode> generator = new DummyGenerator<>(ArrayListMultimap.<DummySearchNode, DummySearchNode>create());
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));

        DummySearchNode startState1 = new DummySearchNode("a", 2.0, 0, false);
        DummySearchNode startState2 = new DummySearchNode("b", 1.0, 0, false);
        DummySearchNode startState3 = new DummySearchNode("c", 3.0, 0, true);
        DummySearchNode startState4 = new DummySearchNode("d", 4.0, 0, false);

        Solvers.solve(solver, manager, generator, heuristic, null, startState1, startState2, startState3, startState4);

        Solution<? extends DummySearchNode> solution = manager.getSolution();
        assertNotNull(solution);
        assertEquals(solution.getNode(), startState3);
        assertTrue(solution.isOptimal());
    }

    @Test
    public void testShouldFindSolutionAmongChildStates() {
        DummySearchNode startState1 = new DummySearchNode("a", 2.0, 0, false);
        DummySearchNode startState2 = new DummySearchNode("b", 1.0, 0, false);
        DummySearchNode startState3 = new DummySearchNode("c", 3.0, 0, false);
        DummySearchNode startState4 = new DummySearchNode("d", 6.0, 0, false);
        DummySearchNode childState1 = new DummySearchNode("e", 3, 0, false);
        DummySearchNode childState2 = new DummySearchNode("f", 4, 0, true);

        ListMultimap<DummySearchNode, DummySearchNode> stateChildren = ArrayListMultimap.create();
        stateChildren.put(startState2, childState1);
        stateChildren.put(startState2, childState2);

        Solver<? super StateSearchNode, Object> solver = getBasicTestSolver();
        DummyGenerator<DummySearchNode> generator = new DummyGenerator<>(stateChildren);
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.MAX_VALUE));

        Solvers.solve(solver, manager, generator, heuristic, null, startState1, startState2, startState3, startState4);
        Solution<? extends DummySearchNode> solution = manager.getSolution();
        assertNotNull(solution);
        assertEquals(solution.getNode(), childState2);
        assertTrue(solution.isOptimal());
    }
}
