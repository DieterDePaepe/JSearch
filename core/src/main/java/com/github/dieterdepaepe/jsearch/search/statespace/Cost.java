package com.github.dieterdepaepe.jsearch.search.statespace;

/**
 * Represents a cost associated with a {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode}.
 *
 * <p>While most problems will use simple numeric costs, it can be useful to have several levels of granularity.
 * Depending on the problem, these levels could be compared on the same level (eg: 2 apples are equally good as a
 * banana), or could indicate a strict order (eg: one coconut is always better than any amount of apples). Don't try
 * to use a single level to compare strict orders, this is known as <i>score folding</i>, and will not work for all
 * cases.</p>
 */
public interface Cost extends Comparable<Cost> {
    /**
     * Returns a new cost that is the sum of this and the specified cost.
     * @param cost the cost to add
     * @return a new cost
     */
    public Cost add(Cost cost);
}
