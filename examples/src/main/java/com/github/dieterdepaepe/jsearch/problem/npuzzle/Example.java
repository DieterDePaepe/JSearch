package com.github.dieterdepaepe.jsearch.problem.npuzzle;

import com.github.dieterdepaepe.jsearch.search.statespace.Solution;
import com.github.dieterdepaepe.jsearch.search.statespace.Solver;
import com.github.dieterdepaepe.jsearch.search.statespace.Solvers;
import com.github.dieterdepaepe.jsearch.search.statespace.cost.DoubleCost;
import com.github.dieterdepaepe.jsearch.search.statespace.solver.AStarSolver;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicManager;

import java.util.Arrays;
import java.util.List;

/**
 * Example use case for using JSearch on the NPuzzle problem.
 * @author Dieter De Paepe
 */
public class Example {
    public static void main(String[] args) {
        //Moves used to create the starting state from the target state
        List<Move> moves = Arrays.asList(Move.LEFT, Move.LEFT, Move.UP, Move.RIGHT, Move.DOWN, Move.LEFT);
        boolean printIntermediateSteps = true;

        SlidingPuzzle puzzle = new SlidingPuzzle(3);
        PuzzleFields targetFieldState = puzzle.createFields();

        PuzzleFields startFieldState = targetFieldState;
        for (Move move : moves)
            startFieldState = puzzle.move(startFieldState, move);

        //We can use any solver, except for the DepthFirstSolver, because we have an infinite depth search space
        Solver<? super PuzzleSearchNode, ? super PuzzleEnvironment> solver = new AStarSolver();
        BasicManager<PuzzleSearchNode> manager = new BasicManager<>(DoubleCost.valueOf(Double.POSITIVE_INFINITY));
        PuzzleEnvironment environment = new PuzzleEnvironment(puzzle, targetFieldState);
        ManhattanDistance heuristic = new ManhattanDistance();
        PuzzleSearchNodeGenerator nodeGenerator = new PuzzleSearchNodeGenerator();

        System.out.println("Start state:");
        System.out.println(puzzle.toString(startFieldState));

        System.out.println("Target state:");
        System.out.println(puzzle.toString(targetFieldState));

        Solvers.solve(solver, manager, nodeGenerator, heuristic, environment, nodeGenerator.createStartState(startFieldState, environment));
        Solution<? extends PuzzleSearchNode> solution = manager.getSolution();

        if (solution == null) {
            System.out.println("Unable to find a solution.");
        } else {
            List<Move> solutionMoves = solution.getNode().getMoves().toList();
            System.out.printf("Found %s solution consisting of %d steps: %s\n\n", solution.isOptimal() ? "an optimal" : "a non-optimal", solutionMoves.size(), solutionMoves);

            if (!printIntermediateSteps)
                return;

            PuzzleFields state = startFieldState;
            for (int step = 0; step < solutionMoves.size(); step++) {
                Move move = solutionMoves.get(step);
                System.out.printf("State after step %d (move %s):\n", step + 1, move.toString());
                state = puzzle.move(state, move);
                System.out.println(puzzle.toString(state));
            }
        }
    }
}
