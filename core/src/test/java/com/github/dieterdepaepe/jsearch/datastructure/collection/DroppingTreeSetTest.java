package com.github.dieterdepaepe.jsearch.datastructure.collection;

import com.beust.jcommander.internal.Lists;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * Test class for {@link DroppingTreeSet}.
 */
public class DroppingTreeSetTest {
    @Test
    public void testSortedSetConstructor() {
        SortedSet<Integer> sortedSet = new TreeSet<>();
        sortedSet.addAll(Arrays.asList(4, 2, 3, 1));

        for (DroppingTreeSet.Conserve conserve : DroppingTreeSet.Conserve.values()) {
            DroppingTreeSet<Integer> set = new DroppingTreeSet<>(sortedSet, 4, conserve);
            assertEquals(set.size(), 4);
            assertEquals(set, sortedSet);

            boolean threwException = false;
            try {
                new DroppingTreeSet<>(sortedSet, 3, conserve);
            } catch (IllegalArgumentException e) {
                threwException = true;
            }

            assertTrue(threwException);
        }
    }

    @Test
    public void testCollectionConstructor() {
        List<Integer> list = Arrays.asList(4, 2, 3, 1, 2, 1, 1, 4); //4 unique elements

        for (DroppingTreeSet.Conserve conserve : DroppingTreeSet.Conserve.values()) {
            DroppingTreeSet<Integer> set = new DroppingTreeSet<>(list, 4, conserve);
            assertEquals(set.size(), 4);
            assertEquals(set, new TreeSet<>(list));

            boolean threwException = false;
            try {
                new DroppingTreeSet<>(list, 3, conserve);
            } catch (IllegalArgumentException e) {
                threwException = true;
            }

            assertTrue(threwException);
        }
    }

    @Test
    public void testConserve() {
        DroppingTreeSet<Integer> set = new DroppingTreeSet<>(3, DroppingTreeSet.Conserve.LOWEST);
        List<Integer> items = Arrays.asList(8, 1, 2, 4, 6, 7, 9, 0, 3, 5);

        set.offerAll(items);
        assertEquals(set.size(), 3);
        assertEquals(Lists.newArrayList(set), Arrays.asList(0, 1, 2));

        set = new DroppingTreeSet<>(3, DroppingTreeSet.Conserve.HIGHEST);
        set.offerAll(items);
        assertEquals(set.size(), 3);
        assertEquals(Lists.newArrayList(set), Arrays.asList(7, 8, 9));
    }


    @Test
    public void testOffer() {
        DroppingTreeSet<Integer> set = new DroppingTreeSet<>(3, DroppingTreeSet.Conserve.HIGHEST);
        assertTrue(set.offer(5));
        assertFalse(set.offer(5));
        assertEquals(set.size(), 1);

        assertTrue(set.offerAll(Arrays.asList(5, 3, 2, 1)));
        assertTrue(set.offerAll(Arrays.asList(5, 4, 2, 1)));
        assertFalse(set.offerAll(Arrays.asList(5, 3)));
        assertEquals(set.size(), 3);
    }

    @Test
    public void testAdd() {
        DroppingTreeSet<Integer> set = new DroppingTreeSet<>(3, DroppingTreeSet.Conserve.HIGHEST);
        assertTrue(set.offer(5));
        assertFalse(set.add(5));
        assertEquals(set.size(), 1);

        assertTrue(set.addAll(Arrays.asList(5, 1, 2, 3, 5)));
        assertFalse(set.addAll(Arrays.asList(2, 3, 5)));
        boolean threwException = false;
        try {
            set.addAll(Arrays.asList(5, 4, 2, 1));
        } catch (IllegalArgumentException e) {
            threwException = true;
        }
        assertTrue(threwException);

        threwException = false;
        try {
            set.add(0);
        } catch (IllegalArgumentException e) {
            threwException = true;
        }
        assertTrue(threwException);

        assertEquals(set.size(), 3);
        assertEquals(Lists.newArrayList(set), Arrays.asList(3, 4, 5));
    }
}