package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import com.github.dieterdepaepe.jsearch.problem.dummy.DummyGenerator;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummyHeuristic;
import com.github.dieterdepaepe.jsearch.problem.dummy.DummySearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.Manager;
import com.github.dieterdepaepe.jsearch.search.statespace.Solution;
import com.github.dieterdepaepe.jsearch.search.statespace.dev.LoggingGenerator;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Testing class for {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.DepthFirstSolver}.
 * @author Dieter De Paepe
 */
public class DepthFirstSolverTest {
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

        LoggingGenerator<DummySearchNode, Object> generator = new LoggingGenerator<>(new DummyGenerator(successors));
        DummyHeuristic heuristic = new DummyHeuristic();
        MyManager manager = new MyManager();
        DepthFirstSolver solver = new DepthFirstSolver();

        solver.solve(new InformedSearchNode<>(a, 0), null, heuristic, generator, manager);

        assertEquals(generator.getExpandedNodes(), Arrays.asList(a, c, e, g, d, b));

        List<Solution<? extends DummySearchNode>> foundSolutions = manager.solutions;
        List<DummySearchNode> expectedSolutionNodes =  Arrays.asList(i, f, h, f);

        for (int index = 0; index < foundSolutions.size(); index++) {
            Solution<? extends DummySearchNode> foundSolution = foundSolutions.get(index);
            assertEquals(foundSolution.getNode(), expectedSolutionNodes.get(index), "Unexpected search node for index " + index);
            assertEquals(foundSolution.isOptimal(), index == expectedSolutionNodes.size() - 1, "Incorrect optimality flag for index " + index);
        }
    }

    private static class MyManager implements Manager<DummySearchNode> {
        List<Solution<? extends DummySearchNode>> solutions = new ArrayList<>();

        @Override
        public boolean continueSearch() {
            return true;
        }

        @Override
        public void registerSolution(Solution<? extends DummySearchNode> solution) {
            solutions.add(solution);
        }

        @Override
        public double getCostBound() {
            return Double.POSITIVE_INFINITY;
        }
    }
}
