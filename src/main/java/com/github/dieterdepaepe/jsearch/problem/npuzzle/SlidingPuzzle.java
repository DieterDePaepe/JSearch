package com.github.dieterdepaepe.jsearch.problem.npuzzle;

/**
 * Class bundling all actions related to working with a sliding puzzle (N-Puzzle). The state of the puzzle is contained
 * in the {@link com.github.dieterdepaepe.jsearch.problem.npuzzle.PuzzleFields} class, which is used as argument to
 * the methods of this class.
 * @author Dieter De Paepe
 */
public class SlidingPuzzle {
    private int dimension;
    private int emptyFieldValue;

    /**
     * Creates a new puzzle instance for puzzles of the specified dimension.
     * @param dimension the dimension - should be 2, 3 or 4
     */
    public SlidingPuzzle(int dimension) {
        if (dimension < 2 || dimension > 4)
            throw new IllegalArgumentException("Dimension not supported: " + dimension);

        this.dimension = dimension;
        this.emptyFieldValue = dimension * dimension - 1;
    }

    /**
     * Creates a new field state from the given values.
     * @param values all values in the range {@code [0 .. dim x dim[}, ordered as they should appear in the puzzle
     * @return a field state containing the specified values
     * @throws java.lang.IllegalArgumentException if not all values are acceptable or if insufficient values are provided
     */
    public PuzzleFields createFields(int... values) {
        int size = dimension * dimension;
        if (values.length != size)
            throw new IllegalArgumentException("Specified values do not match puzzle size.");

        boolean[] presentValues = new boolean[size];
        PuzzleFields fields = new PuzzleFields();
        for (int i = 0; i < size; i++) {
            int value = values[i];
            if (value < 0 || value >= size)
                throw new IllegalArgumentException("Invalid value specified: " + value);

            presentValues[value] = true;
            fields.setValue(i, value);
        }

        for (int i = 0; i < size; i++)
            if (!presentValues[i])
                throw new IllegalArgumentException("Missing value \"" + i + "\".");

        return fields;
    }

    /**
     * Creates a new field state for the puzzle.
     * @return a field state where all fields have values {@code 0, 1, ..., dim x dim - 1}
     */
    public PuzzleFields createFields() {
        int size = dimension * dimension;
        int[] values = new int[size];
        for (int i = 0; i < size; i++)
            values[i] = i;
        return createFields(values);
    }

    public int getValue(PuzzleFields fields, int row, int column) {
        if (row < 0 || row >= dimension)
            throw new IllegalArgumentException("Invalid row: " + row);
        if (column < 0 || column >= dimension)
            throw new IllegalArgumentException("Invalid column: " + column);

        return fields.getValue(row * dimension + column);
    }

    public PuzzleIndex findValue(PuzzleFields fields, int value) {
        int size = dimension * dimension;
        for (int index = 0; index < size; index++)
            if (fields.getValue(index) == value)
                return to2DIndex(index);
        return null;
    }

    /**
     * Performs a move of the empty field in the sliding puzzle.
     * @param fields the starting state of the puzzle
     * @param move the move to execute on the empty field
     * @return the resulting state of the puzzle
     */
    public PuzzleFields move(PuzzleFields fields, Move move) {
        PuzzleIndex emptySpace = findValue(fields, emptyFieldValue);
        int newRow = emptySpace.row + move.getDeltaRow();
        int newColumn = emptySpace.column + move.getDeltaColumn();
        if (newRow >= 0 && newRow < dimension && newColumn >= 0 && newColumn < dimension) {
            PuzzleFields copy = new PuzzleFields(fields);
            switchValues(copy, emptySpace.row, emptySpace.column, newRow, newColumn);
            return copy;
        } else {
            return fields;
        }
    }

    public String toString(PuzzleFields fields) {
        StringBuilder builder = new StringBuilder();

        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                if (column > 0)
                    builder.append(" ");
                builder.append(String.format("%2d", getValue(fields, row, column)));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Counts the number of inversions in the specified field.
     * <p/>
     * An inversion is an occurrence of 2 values {@code A} and {@code B} such that {@code B} occurs before
     * {@code A} in the field (when seeing the field as a 1-dimensional array) and {@code A < B}. The empty field
     * does not count as a value for this calculation.
     * @param fields the field
     * @return the number of inversions
     */
    private int getInversions(PuzzleFields fields) {
        int numberOfFields = dimension * dimension;
        int numberOfInversions = 0;

        for (int fieldIndex = 0; fieldIndex < numberOfFields; fieldIndex++) {
            int fieldValue = fields.getValue(fieldIndex);
            if (fieldValue == emptyFieldValue)
                continue;
            for (int i = fieldIndex + 1; i < numberOfFields; i++) {
                int otherFieldValue = fields.getValue(i);
                if (otherFieldValue == emptyFieldValue)
                    continue;
                if (fieldValue > otherFieldValue)
                    numberOfInversions++;
            }
        }
        return numberOfInversions;
    }

    /**
     * Checks whether the 2 field configurations can reach each other by making only valid moves. This allows for
     * checking whether or not a specific puzzle problem can be solved.
     * @param fields1 the first fields
     * @param fields2 the other fields
     * @return true if a set of move actions could convert the first fields into the other
     */
    public boolean canReach(PuzzleFields fields1, PuzzleFields fields2) {
        //A more detailed explanation can be found at
        // http://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
        int field1Inversions = getInversions(fields1);
        int field2Inversions = getInversions(fields2);

        //For each move, the number of inversions of the fields changes by (dim - 1), 0 or -(dim - 1).

        // For odd width puzzles, the change in inversion is always even. Two field configurations are reachable
        // if their inversion parity is the same.
        if (dimension % 2 == 1)
            return (field1Inversions % 2) == (field2Inversions % 2);

        // For even width puzzles, the change in inversion is odd if the empty field changes row, and 0 otherwise.
        int field1EmptyRow = findValue(fields1, emptyFieldValue).row;
        int field2EmptyRow = findValue(fields2, emptyFieldValue).row;
        return ((field1Inversions + field1EmptyRow) % 2) == ((field2Inversions + field2EmptyRow) % 2);
    }

    private void switchValues(PuzzleFields fields, int row1, int column1, int row2, int column2) {
        int index1 = to1DIndex(row1, column1);
        int index2 = to1DIndex(row2, column2);
        int value1 = fields.getValue(index1);
        int value2 = fields.getValue(index2);
        fields.setValue(index2, value1);
        fields.setValue(index1, value2);
    }

    private PuzzleIndex to2DIndex(int index) {
        return new PuzzleIndex(index / dimension, index % dimension);
    }

    private int to1DIndex(int row, int column) {
        return row * dimension + column;
    }

    public int getDimension() {
        return dimension;
    }
}
