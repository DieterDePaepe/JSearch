package com.github.dieterdepaepe.jsearch.datastructure.lightweight;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * An immutable list-like structure that allows reuse of (prefixes of) other listings to conserve memory.
 *
 * <p>This class is implemented as a minimalistic single-linked list. It is
 * handy for conserving memory when storing information that could be represented as a tree like structure.
 * Typical use cases are graph traversal or action tracking in search problems.</p>
 *
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
 * @param <T> the type of the elements stored
 * @author Dieter De Paepe
 */
public class SingleLinkedListing<T> {
    public static final SingleLinkedListing EMPTY_LISTING = new EmptyListing();

    private final SingleLinkedListing<T> prefixListing;
    private final T element;

    /**
     * Creates a new listing formed by appending a single element to the given listing.
     * @param prefixListing the listing to be expanded
     * @param element the element to add
     */
    protected SingleLinkedListing(SingleLinkedListing<T> prefixListing, T element) {
        this.prefixListing = prefixListing;
        this.element = element;
    }

    /**
     * Creates a new listing formed by appending the specified elements to this listing.
     * @param elements the elements to append
     * @return a new listing
     */
    public SingleLinkedListing<T> extend(Iterable<T> elements) {
        SingleLinkedListing<T> result = this;
        for (T element : elements)
            result = new SingleLinkedListing<>(result, element);
        return result;
    }

    /**
     * Creates a new listing by appending the specified elements to this listing.
     * @param element the element to append
     * @param others  any other elements to append
     * @return a new listing
     */
    @SuppressWarnings({"varargs", "unchecked"})
    public SingleLinkedListing<T> extend(T element, T... others) {
        SingleLinkedListing<T> result = new SingleLinkedListing<>(this, element);
        for (T other : others)
            result = new SingleLinkedListing<>(result, other);
        return result;
    }

    /**
     * Creates a new list containing the items stored in the specified listing.
     * @return a mutable list that is not backed by the listing
     * @see #toInverseList()
     */
    public List<T> toList() {
        List<T> list = toInverseList();
        Collections.reverse(list);
        return list;
    }

    /**
     * Creates a new list containing the items stored in the specified listing. The first element in the list
     * will be the last element in the listing.
     * @return a mutable list that is not backed by the listing
     */
    public List<T> toInverseList() {
        List<T> result = new ArrayList<>();
        for (T element : fromEndToStart())
            result.add(element);
        return result;
    }

    /**
     * Returns an {@code Iterable} that will start at the end of this listing and move towards to the front.
     * @return an {@code Iterable} whose {@code Iterator}s will not support removal
     */
    public Iterable<T> fromEndToStart() {
        return new LinkIterable();
    }

    /**
     * Returns whether or not this listing is empty.
     * @return true if this listing is empty
     */
    public boolean isEmpty() {
        return this == EMPTY_LISTING;
    }

    /**
     * Gets the prefix of this listing, containing one less element.
     * @return null if no more elements are present in this listing
     * @throws java.util.NoSuchElementException if called on an empty listing
     */
    public SingleLinkedListing<T> getPrefixListing() throws NoSuchElementException {
        return prefixListing;
    }

    /**
     * Gets the last element of the listing.
     * @return an element
     * @throws java.util.NoSuchElementException if called on empty listing
     */
    public T getElement() throws NoSuchElementException {
        return element;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        int elementsToPrint = 10;
        List<T> elements = Lists.newArrayList(Iterables.limit(fromEndToStart(), elementsToPrint + 1));
        Collections.reverse(elements);

        if (elements.size() > elementsToPrint)
            builder.append("..., ");

        elements = elements.subList(Math.max(elements.size() - elementsToPrint, 0), elements.size());
        Joiner.on(", ").appendTo(builder, elements);
        builder.append("]");

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SingleLinkedListing))
            return false;

        SingleLinkedListing<T> listing1 = this;
        SingleLinkedListing<?> listing2 = (SingleLinkedListing) o;

        while (listing1 != listing2) {
            // Explicit check for empty listing, because we shouldn't trust the equals method of the contained elements.
            if (listing1 == EMPTY_LISTING || listing2 == EMPTY_LISTING)
                return false;
            T e1 = listing1.element;
            Object e2 = listing2.element;
            if (!(e1 == null ? e2 == null : e1.equals(e2)))
                return false;
            listing1 = listing1.getPrefixListing();
            listing2 = listing2.getPrefixListing();
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (T t : fromEndToStart())
            hashCode = 31 * hashCode + (t == null ? 0 : t.hashCode());
        return hashCode;
    }

    /**
     * Returns the empty listing.
     */
    @SuppressWarnings("unchecked")
    public static <T> SingleLinkedListing<T> of() {
        return (SingleLinkedListing<T>) EMPTY_LISTING;
    }

    /**
     * Creates a new listing of the specified elements.
     * @param element the first element in the new listing
     * @param others any additional elements to be added to the listing
     * @param <T> the type of the listing
     * @return a listing of the specified elements
     */
    @SafeVarargs
    public static <T> SingleLinkedListing<T> of(T element, T... others) {
        SingleLinkedListing<T> listing = of();
        return listing.extend(element, others);
    }

    /**
     * Creates a new listing of the specified elements.
     * @param elements the elements
     * @param <T> the type of the listing
     * @return a listing of the specified elements
     */
    public static <T> SingleLinkedListing<T> of(Iterable<T> elements) {
        SingleLinkedListing<T> listing = of();
        return listing.extend(elements);
    }

    private class LinkIterable implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return new LinkIterator<>(SingleLinkedListing.this);
        }
    }

    private static class LinkIterator<T> implements Iterator<T> {
        private SingleLinkedListing<T> currentListing;

        private LinkIterator(SingleLinkedListing<T> startLink) {
            this.currentListing = startLink;
        }

        @Override
        public boolean hasNext() {
            return currentListing != EMPTY_LISTING;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            T value = currentListing.getElement();
            currentListing = currentListing.getPrefixListing();
            return value;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal is not supported.");
        }
    }

    private static final class EmptyListing extends SingleLinkedListing {
        @SuppressWarnings("unchecked")
        public EmptyListing() {
            super(null, null);
        }

        @Override
        public SingleLinkedListing getPrefixListing() {
            throw new NoSuchElementException();
        }

        @Override
        public Object getElement() {
            throw new NoSuchElementException();
        }
    }
}
