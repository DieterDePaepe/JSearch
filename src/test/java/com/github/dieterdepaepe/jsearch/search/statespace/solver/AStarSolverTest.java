package com.github.dieterdepaepe.jsearch.search.statespace.solver;

import static org.testng.Assert.*;

import com.github.dieterdepaepe.jsearch.datastructure.lightweight.SingleLinkedListing;
import com.github.dieterdepaepe.jsearch.problem.npuzzle.*;
import com.github.dieterdepaepe.jsearch.search.statespace.Solution;
import com.github.dieterdepaepe.jsearch.search.statespace.Solver;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicManager;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Testing class for {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.AStarSolver}.
 */
public class AStarSolverTest {
    @Test
    public void testSolvesNPuzzle() {
        List<Move> moves1 = Arrays.asList();
        List<Move> moves2 = Arrays.asList(Move.UP, Move.LEFT, Move.LEFT, Move.DOWN);
        List<Move> moves3 = Arrays.asList(Move.LEFT, Move.LEFT, Move.UP, Move.RIGHT, Move.DOWN, Move.LEFT);

        for (List<Move> moves : Arrays.asList(moves1, moves2, moves3)) {
            SlidingPuzzle puzzle = new SlidingPuzzle(3);
            PuzzleFields targetFieldState = puzzle.createFields();
            PuzzleEnvironment environment = new PuzzleEnvironment(puzzle, targetFieldState);
            ManhattanDistance heuristic = new ManhattanDistance();
            PuzzleSearchNodeGenerator nodeGenerator = new PuzzleSearchNodeGenerator();

            PuzzleFields startFieldState = targetFieldState;
            for (Move move : moves)
                startFieldState = puzzle.move(startFieldState, move);

            Solver solver = new AStarSolver();
            BasicManager<PuzzleSearchNode> manager = new BasicManager<>();

            assertTrue(puzzle.canReach(startFieldState, targetFieldState));
            solver.solve(
                    nodeGenerator.createStartState(startFieldState, environment, heuristic),
                    environment,
                    heuristic,
                    nodeGenerator,
                    manager
            );

            Solution<? extends PuzzleSearchNode> solution = manager.getSolution();

            assertTrue(solution.isOptimal());
            assertEquals(solution.getNode().getCost(), 0. + moves.size());
            assertEquals(solution.getNode().getPuzzleFields(), targetFieldState);
            assertEquals(SingleLinkedListing.toList(solution.getNode().getMoves(), false), getInverseMoves(moves));
        }
    }

    private static List<Move> getInverseMoves(List<Move> moves) {
        List<Move> result = new ArrayList<>(moves.size());
        for (int i = moves.size() - 1; i >= 0; i--)
            result.add(Move.inverse(moves.get(i)));
        return result;
    }
}
