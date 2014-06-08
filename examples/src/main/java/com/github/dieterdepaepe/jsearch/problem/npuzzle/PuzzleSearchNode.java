package com.github.dieterdepaepe.jsearch.problem.npuzzle;

import com.github.dieterdepaepe.jsearch.datastructure.lightweight.SingleLinkedListing;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;

/**
 * Implementation of {@code SearchNode} for solving the N-Puzzle problem.
 * @author Dieter De Paepe
 */
public class PuzzleSearchNode implements SearchNode {
    private PuzzleFields puzzleFields;
    private SingleLinkedListing<Move> moves;
    private int movesPerformed;
    private boolean isGoal;

    /**
     * Creates a new node.
     * @param puzzleFields the field values of the puzzle in this search node
     * @param moves the moves performed from the start node to reach this search node
     * @param movesPerformed the number of moves performed so far
     * @param isGoal whether or not this node is an acceptable solution
     */
    public PuzzleSearchNode(PuzzleFields puzzleFields, SingleLinkedListing<Move> moves, int movesPerformed, boolean isGoal) {
        this.puzzleFields = puzzleFields;
        this.moves = moves;
        this.movesPerformed = movesPerformed;
        this.isGoal = isGoal;
    }

    public PuzzleFields getPuzzleFields() {
        return puzzleFields;
    }

    public SingleLinkedListing<Move> getMoves() {
        return moves;
    }

    public int getMovesPerformed() {
        return movesPerformed;
    }

    @Override
    public boolean isGoal() {
        return isGoal;
    }

    @Override
    public double getCost() {
        return movesPerformed;
    }

    @Override
    public Object getSearchSpaceState() {
        return puzzleFields;
    }

    @Override
    public String toString() {
        return "PuzzleSearchNode{" +
                "moves=" + moves +
                '}';
    }
}
