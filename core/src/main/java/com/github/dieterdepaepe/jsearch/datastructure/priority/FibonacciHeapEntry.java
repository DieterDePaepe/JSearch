package com.github.dieterdepaepe.jsearch.datastructure.priority;

/**
 * A data container used by a {@link FibonacciHeap} to store and organize data owned by that heap. Each entry can
 * be owned by at most a single {@code FibonacciHeap}, this owning heap should be used to perform any actions involving
 * this entry.
 *
 * @author Dieter De Paepe
 */
public class FibonacciHeapEntry<K, V> {
    V value;
    K key;
    FibonacciHeapEntry<K, V> parent;
    FibonacciHeapEntry<K, V> nextSibling;
    FibonacciHeapEntry<K, V> prevSibling;
    FibonacciHeapEntry<K, V> child;
    /** The number of children of this entry */
    int degree;
    boolean marked;

    /**
     * Creates a new unmarked entry that contains no references.
     * @param key the priority key
     * @param value the value to store in this entry
     */
    FibonacciHeapEntry(K key, V value) {
        this.value = value;
        this.key = key;
    }

    /**
     * Returns whether this entry has been cleared of its sibling references (indicating it has been removed from its heap).
     * @return true if the entry has been removed from its heap
     */
    boolean hasBeenRemoved() {
        return nextSibling == null;
    }

    /**
     * Returns the value stored in this entry.
     * @return the stored value
     */
    public V getValue() {
        return value;
    }

    /**
     * Replaces the value stored in this entry.
     * @param value the new value
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Gets the key stored in this entry.
     * @return the key value
     * @see FibonacciHeap#decreaseKey(FibonacciHeapEntry, Object)
     */
    public K getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "FibonacciHeapEntry{" +
                key + ", " +
                value +
                '}';
    }
}
