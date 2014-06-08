package com.github.dieterdepaepe.jsearch.problem.npuzzle;

/**
 * Represents the different moves that can be made in the sliding puzzle.
 * @author Dieter De Paepe
 */
public enum Move {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1);

    private int deltaColumn;
    private int deltaRow;

    Move(int deltaColumn, int deltaRow) {
        this.deltaColumn = deltaColumn;
        this.deltaRow = deltaRow;
    }

    public int getDeltaColumn() {
        return deltaColumn;
    }

    public int getDeltaRow() {
        return deltaRow;
    }

    /**
     * Gets the opposite move for the given move.
     * @param move a move
     * @return the move that would cancel out the given move
     */
    public static Move inverse(Move move) {
        switch (move) {
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            case UP: return DOWN;
            case DOWN: return UP;
            default: throw new IllegalArgumentException();
        }
    }
}
