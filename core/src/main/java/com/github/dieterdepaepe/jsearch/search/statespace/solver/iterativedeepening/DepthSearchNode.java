package com.github.dieterdepaepe.jsearch.search.statespace.solver.iterativedeepening;

import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;

/**
 * A {@code SearchNode} which has a depth value associated with it, which is used by
 * {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.iterativedeepening.IterativeDeepeningSolver}
 * to limit the search space in a search.
 *
 * <p>Despite its name, the depth does not need to be equal to the depth of the search node in the search tree.
 * It can be used as any general limitation for the search space.</p>
 * @author Dieter De Paepe
 */
public interface DepthSearchNode extends SearchNode {
    /**
     * Gets the depth of this node.
     * @return the depth
     */
    public int getDepth();
}
