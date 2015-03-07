package com.github.dieterdepaepe.jsearch.datastructure.priority;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@code Iterator} which iterates all entries in a {@code FibonacciHeap}.
 *
 * @author Dieter De Paepe
 */
class FibonacciHeapIterator<K, V> implements Iterator<FibonacciHeapEntry<K, V>> {
    private final Deque<Iterator<FibonacciHeapEntry<K, V>>> stack;

    FibonacciHeapIterator(FibonacciHeapEntry<K, V> root) {
        this.stack = new ArrayDeque<>();
        stack.addLast(new SelfAndSiblings<>(checkNotNull(root)));
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public FibonacciHeapEntry<K, V> next() {
        Iterator<FibonacciHeapEntry<K, V>> itr = stack.getLast(); // throws NSEE if empty
        FibonacciHeapEntry<K, V> result = itr.next();
        if (!itr.hasNext())
            stack.removeLast();
        if (result.child != null)
            stack.addLast(new SelfAndSiblings<>(result.child));
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removal is not supported.");
    }

    private static class SelfAndSiblings<K, V> implements Iterator<FibonacciHeapEntry<K, V>> {
        private FibonacciHeapEntry<K, V> startEntry;
        private FibonacciHeapEntry<K, V> nextEntry;
        private boolean hasStarted;

        private SelfAndSiblings(FibonacciHeapEntry<K, V> startEntry) {
            this.startEntry = startEntry;
            this.nextEntry = startEntry;
            this.hasStarted = false;
        }

        @Override
        public boolean hasNext() {
            return !hasStarted || nextEntry != startEntry;
        }

        @Override
        public FibonacciHeapEntry<K, V> next() {
            FibonacciHeapEntry<K, V> result = nextEntry;
            hasStarted = true;
            nextEntry = nextEntry.nextSibling;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal is not supported.");
        }
    }
}
