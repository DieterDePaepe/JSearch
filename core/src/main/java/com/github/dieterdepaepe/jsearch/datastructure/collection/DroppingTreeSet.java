package com.github.dieterdepaepe.jsearch.datastructure.collection;

import com.google.common.collect.ForwardingNavigableSet;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link NavigableSet} implementation based on a {@link TreeSet} with
 * capacity constraints. The elements are ordered using their natural ordering,
 * or by a {@link Comparator} provided at set creation time, depending
 * on which constructor is used. The set has a maximum size and will
 * preserve the highest or lowest elements that have been added to it,
 * depending on the {@link Conserve} criteria specified during creation.
 * This means that adding an element might include an implicit removal of
 * another element.
 *
 * <p>This implementation provides guaranteed log(n) time cost for the basic
 * operations ({@code add}, {@code remove} and {@code contains}).</p>
 *
 * <p>Note that the ordering maintained by a set (whether or not an explicit
 * comparator is provided) must be <i>consistent with equals</i> if it is to
 * correctly implement the {@code Set} interface.  (See {@code Comparable}
 * or {@code Comparator} for a precise definition of <i>consistent with
 * equals</i>.)  This is so because the {@code Set} interface is defined in
 * terms of the {@code equals} operation, but a {@code TreeSet} instance
 * performs all element comparisons using its {@code compareTo} (or
 * {@code compare}) method, so two elements that are deemed equal by this method
 * are, from the standpoint of the set, equal.  The behavior of a set
 * <i>is</i> well-defined even if its ordering is inconsistent with equals; it
 * just fails to obey the general contract of the {@code Set} interface.</p>
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access the set concurrently, and at least one
 * of the threads modifies the set, it <i>must</i> be synchronized
 * externally.</p>
 *
 * <p>The iterators returned by this class's {@code iterator} method are
 * <i>fail-fast</i>: if the set is modified at any time after the iterator is
 * created, in any way except through the iterator's own {@code remove}
 * method, the iterator will throw a {@link ConcurrentModificationException}.
 * Thus, in the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.</p>
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i></p>
 *
 * @param <E> the type of elements maintained by this set
 * @author Dieter De Paepe
 */
public class DroppingTreeSet<E> extends ForwardingNavigableSet<E> {
    private TreeSet<E> innerSet;
    private int maximumSize;
    private Conserve conserve;

    /**
     * Constructs a new, empty, capacity-constrained tree set, sorted according to the specified
     * comparator.  All elements inserted into the set must be <i>mutually
     * comparable</i> by the specified comparator: {@code comparator.compare(e1, e2)}
     * must not throw a {@code ClassCastException} for any elements
     * {@code e1} and {@code e2} in the set.  If the user attempts to add
     * an element to the set that violates this constraint, the
     * {@code add} call will throw a {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this set.
     *                   If {@code null}, the {@linkplain Comparable natural
     *                   ordering} of the elements will be used.
     * @param maximumSize the maximum capacity of this set
     * @param conserve the specification of which elements should be dropped when
     *                        the set would grow beyond its {@code maximumSize}
     * @throws IllegalArgumentException if {@code maximumSize < 0}
     * @throws NullPointerException if {@code discardStrategy} is {@code null}
     */
    public DroppingTreeSet(Comparator<? super E> comparator, int maximumSize, Conserve conserve) {
        checkArgument(maximumSize >= 0, "Maximum size should be >= 0.");
        checkNotNull(conserve);

        this.innerSet = new TreeSet<>(comparator);
        this.maximumSize = maximumSize;
        this.conserve = conserve;
    }

    /**
     * Constructs a new, empty, capacity-constrained tree set, sorted according to the
     * natural ordering of its elements.  All elements inserted into
     * the set must implement the {@link Comparable} interface.
     * Furthermore, all such elements must be <i>mutually
     * comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.  If the user attempts to add an element
     * to the set that violates this constraint (for example, the user
     * attempts to add a string element to a set whose elements are
     * integers), the {@code add} call will throw a
     * {@code ClassCastException}.
     *
     * @throws IllegalArgumentException if {@code maximumSize < 0}
     * @throws NullPointerException if {@code discardStrategy} is {@code null}
     */
    public DroppingTreeSet(int maximumSize, Conserve conserve) {
        this((Comparator<E>) null, maximumSize, conserve);
    }

    /**
     * Constructs a new, capacity-constrained tree set containing the same elements and
     * using the same ordering as the specified sorted set.
     *
     * @param sortedSet sorted set whose elements will comprise the new set
     * @throws NullPointerException if the specified sorted set or {@code discardStrategy} is {@code null}
     * @throws IllegalArgumentException if {@code maximumSize < 0} or
     *                                  if the specified sorted set is larger than {@code maximumSize}
     */
    public DroppingTreeSet(SortedSet<E> sortedSet, int maximumSize, Conserve conserve) throws IllegalArgumentException {
        this(sortedSet.comparator(), maximumSize, conserve);
        checkArgument(maximumSize >= sortedSet.size(), "Specified set is larger than allowed capacity: (%d > %d).", sortedSet.size(), maximumSize);
        this.addAll(sortedSet);
    }

    /**
     * Constructs a new, limited-capacity tree set containing the elements in the specified
     * collection, sorted according to the <i>natural ordering</i> of its
     * elements.  All elements inserted into the set must implement the
     * {@link Comparable} interface.  Furthermore, all such elements must be
     * <i>mutually comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.
     *
     * @param collection collection whose elements will comprise the new set
     * @throws ClassCastException   if the elements in {@code c} are
     *                              not {@link Comparable}, or are not mutually comparable
     * @throws NullPointerException if the specified collection or {@code discardStrategy} is null
     * @throws IllegalArgumentException if {@code maximumSize < 0} or if the specified
     *                                  collection has more than {@code maximumSize} equal objects
     */
    public DroppingTreeSet(Collection<? extends E> collection, int maximumSize, Conserve conserve) {
        this(maximumSize, conserve);

        for (E element : collection) {
            boolean maxSizeWasReached = size() == maximumSize;
            if (innerAdd(element) != AdditionResult.SET_NOT_MODIFIED && maxSizeWasReached)
                throw new IllegalArgumentException("Specified collection cannot fit in this set.");
        }
    }

    /**
     * Inserts the specified element into the set if it is not already present.
     * Since this set tracks only a limited set of items, another item might be
     * dropped from the set as a result, or the specified element itself may not
     * end up in the set.
     * This method is generally preferable to the {@link #add(Object)} method,
     * because is returns {@code false} rather than throwing an exception when
     * the item is refused by the set due to capacity constraints.
     *
     * @param element the element to add
     * @return <tt>true</tt> if the element was added to this set, else <tt>false</tt>
     * @throws ClassCastException   if the specified object cannot be compared
     *                              with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     * @see #add(Object)
     */
    public boolean offer(E element) {
        AdditionResult result = innerAdd(element);
        return result == AdditionResult.ITEM_ADDED;
    }

    /**
     * Offers all of the elements in the specified collection to this set.
     *
     * @param collection collection containing elements to be offered to this set
     * @return {@code true} if this set changed as a result of the call
     * @throws ClassCastException   if the elements provided cannot be compared
     *                              with the elements currently in the set
     * @throws NullPointerException if the specified collection is null or
     *                              if any element is null and this set uses natural ordering, or
     *                              its comparator does not permit null elements
     */
    public boolean offerAll(Collection<? extends E> collection) {
        boolean isModified = false;
        for (E element : collection)
            isModified |= offer(element);
        return isModified;
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if
     * the set contains no element {@code e2} such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}. Since this set tracks only a
     * limited set of elements, another stored element might be dropped or the
     * specified element itself may be refused by the set. In the latter case
     * an {@link IllegalArgumentException} is thrown, as specified
     * in {@link Collection#add(Object)}.
     *
     * @param element element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     * element
     * @throws ClassCastException   if the specified object cannot be compared
     *                              with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     * @throws IllegalArgumentException if the element is refused by the set due to its maximum capacity
     * @see #offer(Object)
     */
    @Override
    public boolean add(E element) {
        AdditionResult result = innerAdd(element);
        if (result == AdditionResult.ITEM_ADDED)
            return true;
        else if (result == AdditionResult.SET_NOT_MODIFIED)
            return false;
        else
            throw new IllegalArgumentException("Element refused due to size constraints.");
    }

    /**
     * Adds all of the elements in the specified collection to this set by calling {@link #add(Object)}
     * for each element. Because a specific element might be refused by the set when the set
     * has reached its maximum capacity, resulting in an exception, this method may or may not
     * throw an exception depending on the iteration order of the specified collection.
     * Because of this, it is typically preferred to use {@link #offerAll(java.util.Collection)} instead.
     *
     * @param collection collection containing elements to be added to this set
     * @return {@code true} if this set changed as a result of the call
     * @throws ClassCastException   if the elements provided cannot be compared
     *                              with the elements currently in the set
     * @throws NullPointerException if the specified collection is null or
     *                              if any element is null and this set uses natural ordering, or
     *                              its comparator does not permit null elements
     * @throws IllegalArgumentException if any element was immediately refused by the set due to size constraints
     * @see #offerAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return standardAddAll(collection);
    }

    private AdditionResult innerAdd(E element) {
        boolean added = innerSet.add(element);
        if (!added)
            return AdditionResult.SET_NOT_MODIFIED;

        if (innerSet.size() <= maximumSize)
            return AdditionResult.ITEM_ADDED;

        E elementRemoved;
        if (conserve == Conserve.HIGHEST)
            elementRemoved = innerSet.pollFirst();
        else
            elementRemoved = innerSet.pollLast();

        if (elementRemoved == element)
            return AdditionResult.ITEM_REFUSED;
        else
            return AdditionResult.ITEM_ADDED;
    }

    @Override
    protected NavigableSet<E> delegate() {
        return innerSet;
    }

    /**
     * Definition of which items to conserve when items have to be purged due to capacity constraints.
     */
    public enum Conserve {
        HIGHEST,
        LOWEST
    }

    private enum AdditionResult {
        SET_NOT_MODIFIED,
        ITEM_ADDED,
        ITEM_REFUSED
    }
}
