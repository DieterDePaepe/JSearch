package com.github.dieterdepaepe.jsearch.datastructure.priority;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Test class for FibonacciHeap.
 * @author Dieter De Paepe
 */
public class FibonacciHeapTest {
    private Random random;
    private FibonacciHeap<Object> heap;

    @BeforeMethod
    public void setupTest() {
        heap = new FibonacciHeap<>();
        random = new Random(0);
    }

    @Test
    public void testBasicUse() {
        assertTrue(heap.isEmpty());
        assertEquals(heap.size(), 0);

        FibonacciHeapEntry<Object> firstEntry = heap.insert(null, 1);
        assertFalse(heap.isEmpty());
        assertEquals(heap.size(), 1);
        assertEquals(heap.findMinimum(), firstEntry);

        FibonacciHeapEntry<Object> secondEntry = heap.insert(null, 2);
        assertFalse(heap.isEmpty());
        assertEquals(heap.size(), 2);
        assertEquals(heap.findMinimum(), firstEntry);

        FibonacciHeapEntry<Object> thirdEntry = heap.insert(null, 0);
        assertFalse(heap.isEmpty());
        assertEquals(heap.findMinimum(), thirdEntry);
        assertEquals(heap.size(), 3);

        FibonacciHeapEntry<Object> result1 = heap.deleteMinimum();
        assertFalse(heap.isEmpty());
        assertEquals(heap.size(), 2);
        assertEquals(result1, thirdEntry);
        assertEquals(heap.findMinimum(), firstEntry);

        heap.decreaseKey(secondEntry, 0);
        assertFalse(heap.isEmpty());
        assertEquals(heap.size(), 2);
        assertEquals(heap.findMinimum(), secondEntry);

        heap.delete(firstEntry);
        assertFalse(heap.isEmpty());
        assertEquals(heap.size(), 1);
        assertEquals(heap.findMinimum(), secondEntry);

        FibonacciHeapEntry<Object> result2 = heap.deleteMinimum();
        assertTrue(heap.isEmpty());
        assertEquals(heap.size(), 0);
        assertEquals(result2, secondEntry);
        assertEquals(heap.findMinimum(), null);
        assertEquals(heap.deleteMinimum(), null);
    }

    @Test
    public void testPriorityOrderWithoutDuplicates() {
        int numberOfEntries = 100000;
        List<Integer> values = generateNumbers(numberOfEntries);
        Collections.shuffle(values, random);

        for (Integer value : values)
            heap.insert(value, value);

        for (int expectedValue = 0; expectedValue < numberOfEntries; expectedValue++) {
            assertEquals(heap.findMinimum().getValue(), expectedValue, "Heap returned incorrect element");
            assertEquals(heap.deleteMinimum().getValue(), expectedValue, "Heap returned incorrect element");
        }
        
        assertTrue(heap.isEmpty());
        assertEquals(heap.size(), 0);
    }

    @Test
    public void testPriorityOrderWithDuplicates() {
        List<Integer> values = generateNumbers(100000);
        values.addAll(generateNumbers(100, 1000, 100));
        values.addAll(generateNumbers(10,  1000, 1000));
        Collections.shuffle(values, random);

        for (Integer value : values)
            heap.insert(value, value);

        Collections.sort(values);
        for (Integer value : values) {
            assertEquals(heap.findMinimum().getValue(), value, "Heap returned incorrect element");
            assertEquals(heap.deleteMinimum().getValue(), value, "Heap returned incorrect element");
        }

        assertTrue(heap.isEmpty());
        assertEquals(heap.size(), 0);
    }

    @Test
    public void testDecreaseKey() {
        int numberOfValues = 100000;
        List<Integer> values = generateNumbers(numberOfValues);
        Collections.shuffle(values, random);

        @SuppressWarnings("unchecked")
        FibonacciHeapEntry<Object>[] entries = new FibonacciHeapEntry[numberOfValues];
        for (Integer value : values)
            entries[value] = heap.insert(value, value);

        Collections.sort(values);

        // Query the heap to make it structure itself
        for (int i = 0; i < 999; i++) {
            assertEquals(heap.findMinimum().getKey(), (double) i, "Heap entry contains incorrect key");
            assertEquals(heap.deleteMinimum().getValue(), i, "Heap returned incorrect element");
        }

        // Decrease the root
        assertEquals(heap.findMinimum(), entries[999]);
        heap.decreaseKey(heap.findMinimum(), 0);
        assertEquals(heap.findMinimum().getKey(), 0.);
        assertEquals(heap.deleteMinimum().getValue(), 999);

        // Heap contains values [1000 .. numberOfValues[
        for (int i = 0; i < 1000; i++) {
            int entryToDecrease = 1005 + 50 * i;
            int newKeyValue = entryToDecrease - random.nextInt(entryToDecrease);
            heap.decreaseKey(entries[entryToDecrease], newKeyValue);
            values.set(entryToDecrease, newKeyValue);
        }

        values = values.subList(1000, values.size());
        Collections.sort(values);

        for (Integer expectedValue : values) {
            assertEquals(heap.deleteMinimum().getKey(), Double.valueOf(expectedValue));
        }

        assertTrue(heap.isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDecreaseKeyOnRemovedEntry() {
        FibonacciHeapEntry<Object> entry = heap.insert(new Object(), 0);
        heap.deleteMinimum();
        heap.decreaseKey(entry, -1);
    }

    @Test
    public void testMerge() {
        List<Integer> values1 = generateNumbers(5000, 0, 1);
        List<Integer> values2 = generateNumbers(5000, -2500, 1);
        List<Integer> values3 = generateNumbers(5000, -5000, 2);

        for (Integer value : values1)
            heap.insert(null, value);

        FibonacciHeap<Object> heap2 = new FibonacciHeap<>();
        for (Integer value : values2)
            heap2.insert(null, value);

        FibonacciHeap<Object> heap3 = new FibonacciHeap<>();
        for (Integer value : values3)
            heap3.insert(null, value);

        // Add and remove an extra element to heap2 and heap3 so they form proper trees
        heap2.insert(null, Double.NEGATIVE_INFINITY);
        heap2.deleteMinimum();
        heap3.insert(null, Double.NEGATIVE_INFINITY);
        heap3.deleteMinimum();

        heap.merge(heap2);
        assertEquals(heap.size(), 10000);
        assertEquals(heap2.size(), 0);
        assertFalse(heap.isEmpty());
        assertTrue(heap2.isEmpty());

        List<Integer> expectedValues = new ArrayList<>(10000);
        expectedValues.addAll(values1);
        expectedValues.addAll(values2);
        Collections.sort(expectedValues);

        for (int i = 0; i < 5000; i++) {
            assertEquals(heap.deleteMinimum().getKey(), Double.valueOf(expectedValues.get(i)));
        }

        heap2.merge(heap3);
        assertEquals(heap2.size(), 5000);
        assertEquals(heap3.size(), 0);
        assertTrue(heap3.isEmpty());
        assertFalse(heap2.isEmpty());

        heap.merge(heap2);
        expectedValues = expectedValues.subList(5000, expectedValues.size());
        expectedValues.addAll(values3);
        Collections.sort(expectedValues);

        for (Integer expectedValue : expectedValues) {
            assertEquals(heap.deleteMinimum().getKey(), Double.valueOf(expectedValue));
        }
    }

    @Test
    public void testDelete() {
        List<FibonacciHeapEntry<Object>> entries = new ArrayList<>();
        for (Integer i : generateNumbers(100)) {
            entries.add(heap.insert(i, i));
        }

        // Delete root before trees are built
        assertEquals(heap.findMinimum(), entries.get(0));
        heap.delete(entries.get(0));
        assertEquals(heap.findMinimum(), entries.get(1));
        assertEquals(heap.size(), 99);

        // Delete root after trees are built
        heap.deleteMinimum();
        assertEquals(heap.findMinimum(), entries.get(2));
        heap.delete(heap.findMinimum());
        assertEquals(heap.findMinimum(), entries.get(3));
        assertEquals(heap.size(), 97);

        // Delete some other elements
        for (int i = 50; i < 60; i++)
            heap.delete(entries.get(i));

        for (int i = 3; i < 100; i++) {
            if (i == 50)
                i = 60;
            assertEquals(heap.deleteMinimum(), entries.get(i));
        }

        assertTrue(heap.isEmpty());
        assertEquals(heap.size(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteOnAlreadyRemovedElement() {
        FibonacciHeapEntry<Object> entry = heap.insert(new Object(), 0);
        heap.deleteMinimum();
        heap.delete(entry);
    }

    @Test
    public void testDeleteDuplicates() {
        for (int i = 0; i < 4; i++) {
            heap = new FibonacciHeap<>();
            List<FibonacciHeapEntry<Object>> entries = new ArrayList<>();
            for (int entry = 0; entry < 4; entry++) {
                entries.add(heap.insert(null, Double.NEGATIVE_INFINITY));
            }

            FibonacciHeapEntry<Object> firstDeleted = heap.deleteMinimum();
            FibonacciHeapEntry<Object> deletionVictim = entries.get(i);
            if (deletionVictim != firstDeleted)
                heap.delete(deletionVictim);
            else
                continue;

            assertEquals(heap.size(), 2);
            assertNotEquals(heap.deleteMinimum(), deletionVictim);
            assertNotEquals(heap.deleteMinimum(), deletionVictim);
            assertTrue(heap.isEmpty());
            assertEquals(heap.size(), 0);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExceptionOnNanInsert() {
        heap.insert(new Object(), Double.NaN);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExceptionOnNanDecreaseKey() {
        FibonacciHeapEntry<Object> entry = heap.insert(new Object(), 0);
        heap.decreaseKey(entry, Double.NaN);
    }

    private List<Integer> generateNumbers(int amount, int start, int step) {
        List<Integer> values = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            values.add(start);
            start += step;
        }
        return values;
    }

    private List<Integer> generateNumbers(int amount) {
        return generateNumbers(amount, 0, 1);
    }
}
