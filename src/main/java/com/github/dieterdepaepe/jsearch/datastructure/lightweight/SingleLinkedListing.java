package com.github.dieterdepaepe.jsearch.datastructure.lightweight;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A list-like structure that allows reuse of prefixes of other listings to conserve memory.
 * <p/>
 * This class is implemented as a single-linked list, where only information about the elements is stored. It is
 * handy for conserving memory when storing lists with a high amount of shared data. Typical use cases are graph
 * traversal or action tracking in search problems.
 * <p/>
 * <h2>Memory usage</h2>
 * Assume we have a tree structure in which we want to track the path of each leaf to the root.
 * The tree has the following properties:
 * <ul>
 *     <li>{@code d}: the branching degree of each node</li>
 *     <li>{@code h}: the height of the tree</li>
 * </ul>
 *
 * <h3>SingleLinkedListing</h3>
 * We store 2 pointers for each element in the tree:<br>
 * <tt>MemCost = 2 x #elements = 2 x (d^h - 1)</tt>
 *
 * <h3>ArrayList</h3>
 * We store a single list containing the path for each leaf:<br>
 * <tt>MemCost = #leafs x treeDepth = d^(h - 1) x h</tt>
 *
 * <h3>Comparison</h3>
 * The 2 costs can be compared with the following formula:<br>
 * <tt>Cost(SingleLinkedListing) = Cost(ArrayList) x F</tt><br>
 * where<br>
 * <tt>F = 2d/h - 2/(h x d^h)</tt><br>
 * In other words, if {@code 2d/h} is smaller than {@code 1}, less memory will be used.
 *
 * @author Dieter De Paepe
 */
public class SingleLinkedListing<T> {
    private SingleLinkedListing<T> prefixListing;
    private T element;

    /**
     * Creates a new instance that represents a new list formed by appending a single element to an already existing
     * list.
     * @param prefixListing the list to be expanded
     * @param element the element to add
     */
    public SingleLinkedListing(SingleLinkedListing<T> prefixListing, T element) {
        this.prefixListing = prefixListing;
        this.element = element;
    }

    /**
     * Creates a new instance that represents a list with a single element.
     * @param element the element
     */
    public SingleLinkedListing(T element) {
        this(null, element);
    }

    /**
     * Gets the prefix of this listing, containing one less element.
     * @return null if no more elements are present in this listing
     */
    public SingleLinkedListing<T> getPrefixListing() {
        return prefixListing;
    }

    /**
     * Gets the last element of the listing.
     * @return an element
     */
    public T getElement() {
        return element;
    }

    /**
     * Returns a mutable list containing the items stored in this listing.
     * @param invert true if the list should be inverted (so the last added element is at the front of the returned list)
     * @return a mutable list that is not backed by this listing
     */
    public List<T> toList(boolean invert) {
        List<T> result = new ArrayList<T>();
        for (T element : this.fromEndToStart())
            result.add(element);

        if (!invert)
            Collections.reverse(result);

        return result;
    }

    /**
     * Returns an {@code Iterable} that will start at the end of this listing and work its way to the front.
     * @return an {@code Iterable} whose {@code Iterator}s will not support removal
     */
    public Iterable<T> fromEndToStart() {
        return new LinkIterable();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        int elementsToPrint = 10;
        List<T> elements = Lists.newArrayList(Iterables.limit(this.fromEndToStart(), elementsToPrint + 1));
        Collections.reverse(elements);

        if (elements.size() > elementsToPrint)
            builder.append("..., ");

        elements = elements.subList(Math.max(elements.size() - elementsToPrint, 0), elements.size());
        for (int i = 0; i < elements.size(); i++) {
            builder.append(elements.get(i));
            if (i != elements.size() - 1)
                builder.append(", ");
        }
        builder.append("]");

        return builder.toString();
    }

    private class LinkIterable implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return new LinkIterator<T>(SingleLinkedListing.this);
        }
    }

    private static class LinkIterator<T> implements Iterator<T> {
        private SingleLinkedListing<T> nextValue;

        private LinkIterator(SingleLinkedListing<T> startLink) {
            this.nextValue = startLink;
        }

        @Override
        public boolean hasNext() {
            return nextValue != null;
        }

        @Override
        public T next() {
            T value = nextValue.getElement();
            nextValue = nextValue.getPrefixListing();
            return value;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal is not supported.");
        }
    }
}
