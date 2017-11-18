package com.github.dieterdepaepe.jsearch.datastructure.priority;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;

import java.util.*;

/**
 * A <a href="http://en.wikipedia.org/wiki/Fibonacci_heap">Fibonacci heap</a> is an efficient priority queue which
 * provides better amortized running times than {@link java.util.PriorityQueue}.
 * Each stored entry has an accompanying key which represents its priority. A lower key value represents a higher
 * priority. Entries will be returned in order of their priority. When 2 entries have the same priority, the order
 * is undefined.
 *
 * <p>This implementation allows for a total of {@code Integer.MAX_VALUE} entries to be stored in a single heap.
 * There are no limitations regarding the values or keys that can be stored within the heap.</p>
 *
 * <p>The Fibonacci heap provides the following worst case running times:</p>
 * <ul>
 *     <li>{@code O(1)}: insertion, merging 2 heaps, minimum entry retrieval</li>
 *     <li>{@code O(log n)}, but amortized {@code O(1)}: reducing the key of a stored entry</li>
 *     <li>{@code O(n)}, but amortized {@code O(log n)}: removing the minimum entry, deleting any specified entry</li>
 * </ul>
 *
 * <p>Due to the weak binding of a {@link FibonacciHeapEntry} and its corresponding heap (required for efficiency
 * reasons), all relevant methods are contained in this heap class and use entries as parameters.
 * This makes it possible for a user to provide foreign entries as method parameters, corrupting the data structure.
 * We define an <strong>ownership</strong> relation from heap to entry to document these issues. An entry is owned
 * by a heap when that entry is present in that heap. The behaviour of any heap methods taking an entry as
 * parameter is only defined when the parameter entry is owned by that heap. In some cases it can be determined that
 * a provided entry is no longer part of a heap, and that method will throw an {@code IllegalArgumentException} to
 * indicate the mistake. Note that this exception is thrown on a best-effort basis, and cannot be depended on for
 * the correctness of a program.</p>
 *
 * <p>This implementation is not thread-safe.</p>
 *
 * @param <K> the type of the keys stored in this heap
 * @param <V> the type of the values stored in this heap
 * @author Dieter De Paepe
 */
public class FibonacciHeap<K, V> implements Iterable<FibonacciHeapEntry<K, V>> {
    private static final double PHI = 1.6180339887498948482;
    private static final int MAX_ENTRY_DEGREE_PLUS_ONE = (int) Math.floor(Math.log(Integer.MAX_VALUE) / Math.log(PHI)) + 1;

    private Comparator<K> keyComparator;
    private FibonacciHeapEntry<K, V> minTreeRoot;
    private int size;

    /**
     * Creates a new heap that uses the specified comparator for its keys.
     * @param keyComparator the comparator used for key ordering
     * @param <K> the type of keys stored in the heap
     * @param <V> the type of values stored in the heap
     * @return an empty heap
     */
    public static <K, V> FibonacciHeap<K, V> create(Comparator<K> keyComparator) {
        return new FibonacciHeap<>(keyComparator);
    }

    /**
     * Creates a new heap which uses the natural ordering of its keys.
     * @param <K> the type of keys stored in the heap
     * @param <V> the type of values stored in the heap
     * @return an empty heap
     */
    public static <K extends Comparable, V> FibonacciHeap<K, V> create() {
        return new FibonacciHeap<>(Ordering.<K>natural());
    }

    private FibonacciHeap(Comparator<K> keyComparator) {
        this.keyComparator = Preconditions.checkNotNull(keyComparator);
        minTreeRoot = null;
        size = 0;
    }

    /**
     * Inserts an element into this heap.
     * <p>
     * This operation has a running time of {@code O(1)}
     * @param key the priority key for this element (a lower value means higher priority)
     * @param element the element to insert
     * @return the entry, owned by this heap, used to store the provided element
     */
    public FibonacciHeapEntry<K, V> insert(K key, V element) throws IllegalArgumentException {
        FibonacciHeapEntry<K, V> newTreeRoot = new FibonacciHeapEntry<>(key, element);

        // The new element is inserted as a new single-element tree
        if (minTreeRoot == null) {
            minTreeRoot = newTreeRoot;
            newTreeRoot.nextSibling = newTreeRoot;
            newTreeRoot.prevSibling = newTreeRoot;
        } else {
            insertBehind(newTreeRoot, minTreeRoot);
            if (keyComparator.compare(newTreeRoot.key, minTreeRoot.key) <= -1)
                minTreeRoot = newTreeRoot;
        }

        size++;
        return newTreeRoot;
    }

    /**
     * Decreases the priority key of the given entry. If the new key value is greater than or equal to the
     * current key value, nothing will happen. <strong>When passing an {@code entry} that is owned by another heap,
     * the result is undefined.</strong>
     * <p>
     * This operation has a worst case timing of {@code O(log n)}, but an amortized time of {@code O(1)}
     * @param entry the entry, owned by this heap, for which to reduce the priority key
     * @param newKey the new key value
     * @throws IllegalArgumentException if it can be determined that the specified entry
     * is no longer part of any heap
     */
    public void decreaseKey(FibonacciHeapEntry<K, V> entry, K newKey) throws IllegalArgumentException {
        if (entry.hasBeenRemoved())
            throw new IllegalArgumentException("Attempting to decrease the key of an entry that is no longer present in a heap.");

        if (keyComparator.compare(newKey, entry.key) >= 0)
            return;

        entry.key = newKey;

        // If the new key value violates the heap property, promote the entry to the root list.
        if (entry.parent != null && keyComparator.compare(entry.key, entry.parent.key) < 0) {
            FibonacciHeapEntry<K, V> entryToPromote = entry;
            // If a marked parent loses another child, this parent is promoted himself.
            // We use a while construct here to avoid recursion in the promote method
            while (entryToPromote != null)
                entryToPromote = promote(entryToPromote);
        }

        // Check if the entry is now the minimum entry in the heap
        if (keyComparator.compare(entry.key, minTreeRoot.key) < 0)
            minTreeRoot = entry;
    }

    /**
     * Adjusts the references of both entries so that {@code child} becomes a child entry of {@code parent}. Also
     * updates the degree of {@code parent}.
     * @param child the child
     * @param parent the new parent for {@code child}
     */
    private void makeChildOf(FibonacciHeapEntry<K, V> child, FibonacciHeapEntry<K, V> parent) {
        if (parent.child != null)
            insertBehind(child, parent.child);
        else {
            parent.child = child;
            child.nextSibling = child;
            child.prevSibling = child;
        }
        child.parent = parent;
        parent.degree++;
    }

    /**
     * Changes the sibling pointers of {@code entryToInsert} and {@code entry} so that {@code entryToInsert}
     * appears after {@code entry} in the sibling linked list.
     * Any sibling pointers held by {@code entryToInsert} will be lost.
     * @param entryToInsert the entry to insert behind {@code entry}
     * @param entry an entry
     */
    private void insertBehind(FibonacciHeapEntry<K, V> entryToInsert, FibonacciHeapEntry<K, V> entry) {
        entryToInsert.nextSibling = entry.nextSibling;
        entryToInsert.prevSibling = entry;
        entry.nextSibling.prevSibling = entryToInsert;
        entry.nextSibling = entryToInsert;
    }

    /**
     * Changes the sibling points of {@code entry}, {@code entry}'s next sibling and {@code entry}'s child list
     * so that the child list appears next in the sibling list of {@code entry}. This method only changes the sibling
     * pointers, not any of the following: parent degree, parent references (of the children), child reference
     * (of the parent), the mark. If {@code entry} has no children, no action is taken.
     * @param entry the entry for which to splice the children into the sibling list
     */
    private void moveChildrenToSiblings(FibonacciHeapEntry<K, V> entry) {
        if (entry.child != null) {
            entry.child.prevSibling.nextSibling = entry.nextSibling;
            entry.nextSibling.prevSibling = entry.child.prevSibling;
            entry.nextSibling = entry.child;
            entry.child.prevSibling = entry;
        }
    }

    /**
     * Moves the given entry from its current location to the root list. This method will adjust all related references
     * and other relevant fields for the {@code entry}, its siblings and the root list. If the parent of
     * {@code entry} was marked before this method call, this parent should also be promoted and it will be returned
     * by this method.
     * <p>
     * This method assumes the given {@code entry} is not currently in the root list (it has a parent).
     * @param entry the entry to move to the root list
     * @return the parent of entry if it should also be promoted, null otherwise
     */
    private FibonacciHeapEntry<K, V> promote(FibonacciHeapEntry<K, V> entry) {
        FibonacciHeapEntry<K, V> parentEntry = entry.parent;
        parentEntry.degree--;

        // Adjust navigation references for parent and his children
        if (entry.nextSibling == entry) {
            parentEntry.child = null;
        } else {
            parentEntry.child = entry.nextSibling;
            extract(entry);
        }

        // Entry becomes a tree root - these are never marked
        entry.marked = false;

        // Adjust navigation references for entry and the root list
        entry.parent = null;
        insertBehind(entry, minTreeRoot);

        if (parentEntry.marked) {
            return parentEntry;
        } else {
            // Only mark parent if he is not a tree root
            parentEntry.marked = parentEntry.parent != null;
            return null;
        }
    }

    /**
     * Adjust the navigation references of {@code entry}'s siblings, so that they no longer reference {@code entry},
     * but refer to each other instead. The references of {@code entry} will remain unchanged.
     * @param entry the entry
     */
    private void extract(FibonacciHeapEntry<K, V> entry) {
        entry.nextSibling.prevSibling = entry.prevSibling;
        entry.prevSibling.nextSibling = entry.nextSibling;
    }

    /**
     * Removes {@code entry}'s references to its siblings and child .
     */
    private void releaseSiblingsAndChild(FibonacciHeapEntry<K, V> entry) {
        entry.nextSibling = null;
        entry.prevSibling = null;
        entry.child = null;
    }

    /**
     * Removes and returns the entry with the lowest key value from the heap. The heap loses ownership
     * over the returned entry.
     * <p>
     * This operation has a worst case timing of {@code O(n)}, but an amortized time of {@code O(log n)}
     * @return the entry with the lowest key stored in this heap or null if this heap is empty
     */
    public FibonacciHeapEntry<K, V> deleteMinimum() {
        if (minTreeRoot == null)
            return null;

        FibonacciHeapEntry<K, V> result = minTreeRoot;

        // To achieve the amortized time of O(log n), we need to perform a cleanup step. In this step, we will
        // merge any tree roots that have the same degree. We use an array to efficiently match roots with the same degree.
        // It can be proven that any entry has at most a degree of floor(log_phi(n)), this means that there may be at
        // most floor(log_phi(n))+1 different degrees in the root list.
        // Instead of calculating this value based on the current size of the heap, we use a predefined value that is
        // sufficient for any case.
        @SuppressWarnings("unchecked")
        FibonacciHeapEntry<K, V>[] treeRootsPerDegree =
                (FibonacciHeapEntry<K, V>[]) new FibonacciHeapEntry[MAX_ENTRY_DEGREE_PLUS_ONE];

        // Merge any children of the minTreeRoot in the root list, after the minTreeRoot
        moveChildrenToSiblings(minTreeRoot);

        // Prepare to iterate over all tree roots
        FibonacciHeapEntry<K, V> borderEntry = minTreeRoot;
        FibonacciHeapEntry<K, V> currentEntry = borderEntry.nextSibling;
        FibonacciHeapEntry<K, V> newMinTreeRoot = currentEntry;

        // Iterate over all tree roots, except for the minTreeRoot
        while (currentEntry != borderEntry) {
            // Update the parent and mark for any former children of minTreeRoot
            currentEntry.parent = null;
            currentEntry.marked = false;

            FibonacciHeapEntry<K, V> nextEntry = currentEntry.nextSibling;

            // Find and merge any trees that have the same root degree
            FibonacciHeapEntry<K, V> sameDegreeEntry = treeRootsPerDegree[currentEntry.degree];
            while (sameDegreeEntry != null) {
                treeRootsPerDegree[currentEntry.degree] = null;
                // Merge sameDegreeEntry and currentEntry
                if (keyComparator.compare(sameDegreeEntry.key, currentEntry.key) < 0) {
                    extract(currentEntry);
                    makeChildOf(currentEntry, sameDegreeEntry);
                    currentEntry = sameDegreeEntry;
                } else {
                    extract(sameDegreeEntry);
                    makeChildOf(sameDegreeEntry, currentEntry);
                }
                // currentEntry == the root of the 2 merged trees

                // Check again
                sameDegreeEntry = treeRootsPerDegree[currentEntry.degree];
            }
            treeRootsPerDegree[currentEntry.degree] = currentEntry;

            // Keep an eye out for the entry with the lowest key
            if (keyComparator.compare(currentEntry.key, newMinTreeRoot.key) <= 0)
                newMinTreeRoot = currentEntry;

            currentEntry = nextEntry;
        }


        if (newMinTreeRoot == minTreeRoot) {
            minTreeRoot = null;
        } else {
            extract(minTreeRoot);
            minTreeRoot = newMinTreeRoot;
        }

        size--;

        // Release all references, allowing GC.
        releaseSiblingsAndChild(result);

        return result;
    }

    /**
     * Deletes the given entry from this heap. <strong>When passing an {@code entry} that is owned by another heap,
     * the result is undefined.</strong>
     * <p>
     * This operation has a run time equal to that of the {@link #deleteMinimum()} method if the provided entry is
     * the lowest element in this heap. If this is not the case, it has a run time of {@code O(log n)}.
     * @param entry an entry owned by this queue
     * @throws IllegalArgumentException if it can be determined that the specified entry is no longer part of any heap
     */
    public void delete(FibonacciHeapEntry<K, V> entry) {
        if (entry.hasBeenRemoved())
            throw new IllegalArgumentException("Attempting to delete an entry that is no longer present in a heap.");

        // This method is typically explained as decreasing the key of the entry to negative infinity and than removing
        // the lowest entry. There are 2 issues with this approach:
        //  - if the heap already contains items with negative infinity as weight, an incorrect entry may be deleted
        //  - there exists a faster way

        // Three different 3 cases can occur:
        //  1 - the entry is currently the minimum tree root (it would be the next to be returned by this heap)
        //  2 - the entry is in the root list, but currently not the minimum root
        //  3 - the entry is not part of the root list

        // Case 1: use the deleteMinimum method - same time duration as this method
        // Case 2: remove the entry from the root list, and splice its children in the root list: O(1)
        //         update the pointers of the children: O(log n)
        // Case 3: we use the same approach as the decreaseKey operation to move the entry (and potentially some
        //            some of its ancestors) to the root list: O(log n), but amortized O(1)
        //         revert to case 2

        // Case 1
        if (minTreeRoot == entry) {
            deleteMinimum();
            return;
        }

        // Case 2 and 3
        if (entry.parent != null) { // Case 3 only
            // Note that we cannot use decreaseKey itself, since the entry might already have its key set to
            // negative infinity, in which case no action would be taken.
            FibonacciHeapEntry<K, V> entryToPromote = entry;
            while (entryToPromote != null)
                entryToPromote = promote(entryToPromote);
        }

        // Update child pointers to parent
        FibonacciHeapEntry<K, V> travellingEntry = entry.child;
        for (int i = entry.degree; i > 0; i--) {
            travellingEntry.parent = null;
            travellingEntry.marked = false;
            travellingEntry = travellingEntry.nextSibling;
        }

        // Move children to root list and remove entry
        moveChildrenToSiblings(entry);
        extract(entry);

        // Release remaining references, for GC
        releaseSiblingsAndChild(entry);

        size--;
    }

    /**
     * Absorbs all elements of the specified heap into this heap. <strong>This causes this heap to take ownership over all
     * entries owned by {@code otherHeap}.</strong> After this call, {@code otherHeap} will have been cleared.
     * <p>
     * This operation runs in {@code O(1)}.
     * @param otherHeap the heap to merge, which will be cleared as a result of this call
     */
    public void merge(FibonacciHeap<K, V> otherHeap) {
        if (minTreeRoot != null && otherHeap.minTreeRoot != null) {
            minTreeRoot.nextSibling.prevSibling = otherHeap.minTreeRoot.prevSibling;
            otherHeap.minTreeRoot.prevSibling.nextSibling = minTreeRoot.nextSibling;
            minTreeRoot.nextSibling = otherHeap.minTreeRoot;
            otherHeap.minTreeRoot.prevSibling = minTreeRoot;
        }
        if (minTreeRoot == null ||
                (otherHeap.minTreeRoot != null && keyComparator.compare(minTreeRoot.key, otherHeap.minTreeRoot.key) > 0))
            minTreeRoot = otherHeap.minTreeRoot;

        size += otherHeap.size;

        otherHeap.clear();
    }

    /**
     * Returns an iterator that iterates over all entries in the heap in no particular order. The iterator does not
     * support removal. The behavior of the iterator is undefined once the heap is modified.
     * @return an Iterator
     */
    public Iterator<FibonacciHeapEntry<K, V>> iterator() {
        if (isEmpty())
            return Collections.emptyIterator();
        else
            return new FibonacciHeapIterator<>(minTreeRoot);
    }

    /**
     * Returns a collection view of the heap. The collection is backed by the heap, so any changes to the heap will
     * be reflected in the collection. If the heap is modified while an iteration over the collection is in progress,
     * the results of the iteration are undefined.
     * @return an unmodifiable collection
     */
    public Collection<V> asCollection() {
        final Function<FibonacciHeapEntry<K, V>, V> valueFunction = new Function<FibonacciHeapEntry<K, V>, V>() {
            @Override
            public V apply(FibonacciHeapEntry<K, V> input) {
                return input.getValue();
            }
        };
        return new AbstractCollection<V>() {
            @Override
            public Iterator<V> iterator() {
                return Iterators.transform(FibonacciHeap.this.iterator(), valueFunction);
            }

            @Override
            public int size() {
                return FibonacciHeap.this.size();
            }
        };
    }

    /**
     * Removes all stored entries from this heap. The ownership of all entries is abandoned.
     */
    public void clear() {
        minTreeRoot = null;
        size = 0;
    }

    /**
     * Returns, but does not remove, the entry with the lowest key stored in this heap.
     * <p>
     * This operation runs in {@code O(1)}.
     * @return an entry owned by this heap
     */
    public FibonacciHeapEntry<K, V> findMinimum() {
        return minTreeRoot;
    }

    /**
     * Returns the number of elements stored in this heap.
     * @return the size of the heap
     */
    public int size() {
        return size;
    }

    /**
     * Returns whether this heap contains any elements.
     * @return true if this heap is empty
     */
    public boolean isEmpty() {
        return minTreeRoot == null;
    }

    @Override
    public String toString() {
        return "FibonacciHeap{" +
                "size=" + size +
                '}';
    }
}
