package com.github.dieterdepaepe.jsearch.problem.npuzzle;

/**
 * A 2 dimensional index of a specific field in a {@link SlidingPuzzle}.
 * @author Dieter De Paepe
 */
public class PuzzleIndex {
    public final int row;
    public final int column;

    public PuzzleIndex(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PuzzleIndex that = (PuzzleIndex) o;

        if (column != that.column) return false;
        if (row != that.row) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }
}
