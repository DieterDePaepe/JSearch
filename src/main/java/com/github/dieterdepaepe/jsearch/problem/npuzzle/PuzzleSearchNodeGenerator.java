package com.github.dieterdepaepe.jsearch.problem.npuzzle;

import com.github.dieterdepaepe.jsearch.datastructure.lightweight.SingleLinkedListing;
import com.github.dieterdepaepe.jsearch.search.statespace.Heuristic;
import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNodeGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@code SearchNodeGenerator} for solving the N-Puzzle problem.
 * @author Dieter De Paepe
 */
public class PuzzleSearchNodeGenerator implements SearchNodeGenerator<PuzzleSearchNode, PuzzleEnvironment> {
    @Override
    public List<InformedSearchNode<PuzzleSearchNode>> generateSuccessorNodes(PuzzleSearchNode node, PuzzleEnvironment environment, Heuristic<? super PuzzleSearchNode, ? super PuzzleEnvironment> heuristic) {
        List<InformedSearchNode<PuzzleSearchNode>> result = new ArrayList<>(4);
        PuzzleFields startingFieldsState = node.getPuzzleFields();

        for (Move move : Move.values()) {
            PuzzleFields movedFieldsState = environment.getPuzzle().move(startingFieldsState, move);

            //Skip if the move had no effect
            if (movedFieldsState == startingFieldsState)
                continue;

            //Construct the new search node by adding one extra move
            PuzzleSearchNode newNode = new PuzzleSearchNode(
                    movedFieldsState,
                    new SingleLinkedListing<>(node.getMoves(), move),
                    node.getMovesPerformed() + 1,
                    environment.getTargetState().equals(movedFieldsState));

            //Add heuristic information to the search node
            result.add(new InformedSearchNode<>(newNode, heuristic.estimateRemainingCost(newNode, environment)));
        }

        return result;
    }

    public PuzzleSearchNode createStartState(PuzzleFields fieldState, PuzzleEnvironment environment) {
        return new PuzzleSearchNode(fieldState, null, 0, environment.getTargetState().equals(fieldState));
    }
}
