package com.github.dieterdepaepe.jsearch.problem.npuzzle;


import com.github.dieterdepaepe.jsearch.search.statespace.cost.IntegerCost;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test class for {@code ManhattanDistance} class.
 * @author Dieter De Paepe
 */
public class ManhattanDistanceTest {
    @Test
    public void testEstimateRemainingCost()  {
        SlidingPuzzle puzzle = new SlidingPuzzle(3);
        ManhattanDistance heuristic = new ManhattanDistance();

        PuzzleFields targetFields = puzzle.createFields();
        PuzzleFields testFields = puzzle.createFields(
                2, 1, 3,
                7, 4, 0,
                5, 6, 8
        );
        //Field values:   0 1 2 3 4 5 6 7 8
        //Manhattan dist: 3 0 2 3 0 3 1 2 0 = 14

        PuzzleEnvironment environment = new PuzzleEnvironment(puzzle, targetFields);
        PuzzleSearchNode node = new PuzzleSearchNode(testFields, null, 0, false);

        assertEquals(heuristic.estimateRemainingCost(node, environment), IntegerCost.valueOf(14));
    }

    @Test
    public void testEstimateRemainingCost2() {
        SlidingPuzzle puzzle = new SlidingPuzzle(3);
        ManhattanDistance heuristic = new ManhattanDistance();

        PuzzleFields targetFields = puzzle.createFields();
        PuzzleFields testFields = puzzle.move(targetFields, Move.LEFT);

        PuzzleEnvironment environment = new PuzzleEnvironment(puzzle, targetFields);
        PuzzleSearchNode node = new PuzzleSearchNode(testFields, null, 0, false);

        assertEquals(heuristic.estimateRemainingCost(node, environment), IntegerCost.valueOf(1));
    }
}
