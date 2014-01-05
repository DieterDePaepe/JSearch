package com.github.dieterdepaepe.jsearch.problem.npuzzle;

/**
 * An array-like structure that stores the field values used in a {@link SlidingPuzzle}.
 * <p>
 * This implementation uses a {@code long} as internal storage. It can store a maximum of 16 numbers, with each number
 * in the range <tt>[0..15]</tt>.
 * @author Dieter De Paepe
 */
public class PuzzleFields {
    private long fieldArray;

    public PuzzleFields(PuzzleFields original) {
        this.fieldArray = original.fieldArray;
    }

    public PuzzleFields() {
    }

    public int getValue(int index) {
        if (index < 0 || index > 15)
            throw new IllegalArgumentException("Invalid index: " + index);

        long mask = ((long) 0xF) << (index * 4);
        return (int) ((mask & fieldArray) >>> index * 4);
    }

    public void setValue(int index, int value) {
        if (index < 0 || index > 15)
            throw new IllegalArgumentException("Invalid index: " + index);
        if (value < 0 || value > 15)
            throw new IllegalArgumentException("Invalid value: " + value);

        // Clear current value
        long mask = ~(((long) 0xF) << (index * 4));
        fieldArray &= mask;

        // Store new value
        mask = ((long) value) << (index * 4);
        fieldArray |= mask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PuzzleFields that = (PuzzleFields) o;
        return fieldArray == that.fieldArray;
    }

    @Override
    public int hashCode() {
        return (int) (fieldArray ^ (fieldArray >>> 32));
    }
}
