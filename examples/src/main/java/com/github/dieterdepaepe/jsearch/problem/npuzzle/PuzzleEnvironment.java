package com.github.dieterdepaepe.jsearch.problem.npuzzle;

/**
 * Problem environment for solving the N-Puzzle problem.
 * @author Dieter De Paepe
 */
public class PuzzleEnvironment {
    private SlidingPuzzle puzzle;
    private PuzzleFields targetState;

    public PuzzleEnvironment(SlidingPuzzle puzzle, PuzzleFields targetState) {
        this.puzzle = puzzle;
        this.targetState = targetState;
    }

    public SlidingPuzzle getPuzzle() {
        return puzzle;
    }

    public PuzzleFields getTargetState() {
        return targetState;
    }
}
