package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.Solution;
import com.github.dieterdepaepe.jsearch.search.statespace.Solver;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicManager;
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
    public abstract Solver<SearchNode, Object> getBasicTestSolver();

    @Test
    public void testShouldReturnNothingIfNoStartStates() {
        Solver<SearchNode, Object> solver = getBasicTestSolver();

        DummyGenerator generator = new DummyGenerator(ArrayListMultimap.<DummySearchNode, DummySearchNode>create());
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>();
        solver.solve(Collections.<InformedSearchNode<DummySearchNode>>emptyList(), null, heuristic, generator, manager);

        assertNull(manager.getSolution());
    }

    @Test
    public void testShouldFindNoSolutionIfNonePresent() {
        Solver<SearchNode, Object> solver = getBasicTestSolver();

        ListMultimap<DummySearchNode, DummySearchNode> stateChildren = ArrayListMultimap.create();
        DummyGenerator generator = new DummyGenerator(stateChildren);
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>();

        DummySearchNode startState1 = new DummySearchNode("a", 2.0, 0, false);
        DummySearchNode startState2 = new DummySearchNode("b", 1.0, 0, false);
        DummySearchNode startState3 = new DummySearchNode("c", 3.0, 0, false);
        DummySearchNode startState4 = new DummySearchNode("d", 4.0, 0, false);

        List<InformedSearchNode<DummySearchNode>> startNodes = Arrays.asList(
                new InformedSearchNode<>(startState1, startState1.getHeuristicValue()),
                new InformedSearchNode<>(startState2, startState2.getHeuristicValue()),
                new InformedSearchNode<>(startState3, startState3.getHeuristicValue()),
                new InformedSearchNode<>(startState4, startState4.getHeuristicValue())
        );

        //Case containing only start nodes
        solver.solve(startNodes, null, heuristic, generator, manager);
        Solution<? extends DummySearchNode> solution = manager.getSolution();
        assertNull(solution);

        //Case containing start and child nodes
        DummySearchNode childState1 = new DummySearchNode("e", 2, 0, false);
        DummySearchNode childState2 = new DummySearchNode("f", 5, 0, false);
        stateChildren.put(startState2, childState1);
        stateChildren.put(startState2, childState2);

        solver.solve(startNodes, null, heuristic, generator, manager);
        solution = manager.getSolution();
        assertNull(solution);
    }

    @Test
    public void testShouldFindSolutionAmongStartStates() {
        Solver<SearchNode, Object> solver = getBasicTestSolver();

        DummyGenerator generator = new DummyGenerator(ArrayListMultimap.<DummySearchNode, DummySearchNode>create());
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>();

        DummySearchNode startState1 = new DummySearchNode("a", 2.0, 0, false);
        DummySearchNode startState2 = new DummySearchNode("b", 1.0, 0, false);
        DummySearchNode startState3 = new DummySearchNode("c", 3.0, 0, true);
        DummySearchNode startState4 = new DummySearchNode("d", 4.0, 0, false);

        solver.solve(Arrays.asList(
                new InformedSearchNode<>(startState1, startState1.getHeuristicValue()),
                new InformedSearchNode<>(startState2, startState2.getHeuristicValue()),
                new InformedSearchNode<>(startState3, startState3.getHeuristicValue()),
                new InformedSearchNode<>(startState4, startState4.getHeuristicValue())
        ), null, heuristic, generator, manager);

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

        List<InformedSearchNode<DummySearchNode>> startNodes = Arrays.asList(
                new InformedSearchNode<>(startState1, startState1.getHeuristicValue()),
                new InformedSearchNode<>(startState2, startState2.getHeuristicValue()),
                new InformedSearchNode<>(startState3, startState3.getHeuristicValue()),
                new InformedSearchNode<>(startState4, startState4.getHeuristicValue())
        );

        ListMultimap<DummySearchNode, DummySearchNode> stateChildren = ArrayListMultimap.create();
        stateChildren.put(startState2, childState1);
        stateChildren.put(startState2, childState2);

        Solver<SearchNode, Object> solver = getBasicTestSolver();
        DummyGenerator generator = new DummyGenerator(stateChildren);
        DummyHeuristic heuristic = new DummyHeuristic();
        BasicManager<DummySearchNode> manager = new BasicManager<>();

        solver.solve(startNodes, null, heuristic, generator, manager);
        Solution<? extends DummySearchNode> solution = manager.getSolution();
        assertNotNull(solution);
        assertEquals(solution.getNode(), childState2);
        assertTrue(solution.isOptimal());
    }
}
