package com.github.dieterdepaepe.jsearch.problem.npuzzle;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * Test class for {@code SlidingPuzzle} and related classes.
 * @author Dieter De Paepe
 */
public class SlidingPuzzleTest {
    @Test
    public void testCreateFields() {
        SlidingPuzzle puzzle = new SlidingPuzzle(3);

        PuzzleFields standardFields = puzzle.createFields();
        PuzzleFields manualFields = puzzle.createFields(0, 1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(standardFields, manualFields);
    }

    @Test
    public void testGetValue() {
        SlidingPuzzle puzzle = new SlidingPuzzle(3);
        PuzzleFields fields = puzzle.createFields();

        assertEquals(puzzle.getValue(fields, 0, 0), 0);
        assertEquals(puzzle.getValue(fields, 1, 1), 4);
        assertEquals(puzzle.getValue(fields, 2, 2), 8);
    }

    @Test
    public void testFindValue() {
        SlidingPuzzle puzzle = new SlidingPuzzle(3);
        PuzzleFields fields = puzzle.createFields();

        assertEquals(puzzle.findValue(fields, 4), new PuzzleIndex(1, 1));
    }

    @Test
    public void testMove() {
        SlidingPuzzle puzzle = new SlidingPuzzle(3);
        PuzzleFields fields = puzzle.createFields(
                0, 1, 2,
                3, 8, 4,
                5, 6, 7);

        PuzzleFields resultUp = puzzle.createFields(
                0, 8, 2,
                3, 1, 4,
                5, 6, 7);

        PuzzleFields resultDown = puzzle.createFields(
                0, 1, 2,
                3, 6, 4,
                5, 8, 7);

        PuzzleFields resultLeft = puzzle.createFields(
                0, 1, 2,
                8, 3, 4,
                5, 6, 7);

        PuzzleFields resultRight = puzzle.createFields(
                0, 1, 2,
                3, 4, 8,
                5, 6, 7);

        assertEquals(puzzle.move(fields, Move.UP), resultUp);
        assertEquals(puzzle.move(fields, Move.DOWN), resultDown);
        assertEquals(puzzle.move(fields, Move.LEFT), resultLeft);
        assertEquals(puzzle.move(fields, Move.RIGHT), resultRight);
    }

    @Test
    public void testCanReach() {
        //Test for even width puzzle
        SlidingPuzzle puzzle = new SlidingPuzzle(4);
        PuzzleFields solution = puzzle.createFields();
        PuzzleFields solvable = puzzle.createFields(
                5,  0,  9,  1,
                6, 10,  3, 13,
                4, 15,  8, 14,
                7, 11, 12,  2
        );
        PuzzleFields unsolvable = puzzle.createFields(
                0,  1,  2,  3,
                4,  5,  6,  7,
                8,  9,  10, 11,
                12, 14, 13, 15
        );
        assertTrue(puzzle.canReach(solution, solvable));
        assertFalse(puzzle.canReach(solution, unsolvable));

        puzzle = new SlidingPuzzle(3);
        solution = puzzle.createFields();
        solvable = puzzle.createFields(
                1, 3, 2,
                0, 5, 7,
                6, 8, 4
        );
        unsolvable = puzzle.createFields(
                1, 0, 2,
                3, 4, 5,
                6, 7, 8
        );
        assertTrue(puzzle.canReach(solution, solvable));
        assertFalse(puzzle.canReach(solution, unsolvable));
    }
}
