package com.github.dieterdepaepe.jsearch.datastructure.priority;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@code Iterator} which iterates all entries in a {@code FibonacciHeap}.
 * @author Dieter De Paepe
 */
class FibonacciHeapIterator<T> implements Iterator<FibonacciHeapEntry<T>> {
    private final Deque<Iterator<FibonacciHeapEntry<T>>> stack;

    FibonacciHeapIterator(FibonacciHeapEntry<T> root) {
        this.stack = new ArrayDeque<>();
        stack.addLast(new SelfAndSiblings<>(checkNotNull(root)));
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public FibonacciHeapEntry<T> next() {
        Iterator<FibonacciHeapEntry<T>> itr = stack.getLast(); // throws NSEE if empty
        FibonacciHeapEntry<T> result = itr.next();
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

    private static class SelfAndSiblings<T> implements Iterator<FibonacciHeapEntry<T>> {
        private FibonacciHeapEntry<T> startEntry;
        private FibonacciHeapEntry<T> nextEntry;
        private boolean hasStarted;

        private SelfAndSiblings(FibonacciHeapEntry<T> startEntry) {
            this.startEntry = startEntry;
            this.nextEntry = startEntry;
            this.hasStarted = false;
        }

        @Override
        public boolean hasNext() {
            return !hasStarted || nextEntry != startEntry;
        }

        @Override
        public FibonacciHeapEntry<T> next() {
            FibonacciHeapEntry<T> result = nextEntry;
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
