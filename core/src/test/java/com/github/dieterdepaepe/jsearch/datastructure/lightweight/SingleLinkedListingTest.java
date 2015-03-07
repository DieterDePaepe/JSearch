package com.github.dieterdepaepe.jsearch.datastructure.lightweight;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.testng.Assert.*;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.datastructure.lightweight.SingleLinkedListing}
 * @author Dieter De Paepe
 */
public class SingleLinkedListingTest {
    @Test
    public void testFactoryMethods() {
        SingleLinkedListing<String> listing = SingleLinkedListing.of();
        assertEquals(listing.toList(), Collections.<String>emptyList());
        assertEquals(listing.toInverseList(), Collections.<String>emptyList());
        assertTrue(Iterables.elementsEqual(listing.fromEndToStart(), Collections.<String>emptyList()));

        listing = SingleLinkedListing.of("A");
        assertEquals(listing.toList(), Collections.singletonList("A"));
        assertEquals(listing.toInverseList(), Collections.singletonList("A"));
        assertTrue(Iterables.elementsEqual(listing.fromEndToStart(), Collections.singletonList("A")));

        listing = SingleLinkedListing.of("A", "B", "C");
        assertEquals(listing.toList(), Lists.newArrayList("A", "B", "C"));
        assertEquals(listing.toInverseList(), Lists.newArrayList("C", "B", "A"));
        assertTrue(Iterables.elementsEqual(listing.fromEndToStart(), Lists.newArrayList("C", "B", "A")));

        listing = SingleLinkedListing.of(Lists.newArrayList("A", "B", "C"));
        assertEquals(listing.toList(), Lists.newArrayList("A", "B", "C"));
        assertEquals(listing.toInverseList(), Lists.newArrayList("C", "B", "A"));
        assertTrue(Iterables.elementsEqual(listing.fromEndToStart(), Lists.newArrayList("C", "B", "A")));
    }

    @Test
    public void testExtend() {
        SingleLinkedListing<String> listing = SingleLinkedListing.of();
        listing = listing.extend("A");
        assertEquals(listing.toList(), Collections.singletonList("A"));

        listing = listing.extend("B", "C");
        assertEquals(listing.toList(), Lists.newArrayList("A", "B", "C"));

        listing = listing.extend(Lists.newArrayList("D", "E", "F"));
        assertEquals(listing.toList(), Lists.newArrayList("A", "B", "C", "D", "E", "F"));
    }

    @Test
    public void testManualIteration() throws Exception {
        SingleLinkedListing<String> listing = SingleLinkedListing.of("A", "B");
        assertFalse(listing.isEmpty());
        assertEquals("B", listing.getElement());

        listing = listing.getPrefixListing();
        assertFalse(listing.isEmpty());
        assertEquals("A", listing.getElement());

        listing = listing.getPrefixListing();
        assertTrue(listing.isEmpty());

        try {
            listing.getElement();
            fail();
        } catch (NoSuchElementException e) {
            // pass
        }

        try {
            listing.getPrefixListing();
            fail();
        } catch (NoSuchElementException e) {
            // pass
        }
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        SingleLinkedListing<EqualWrapper> listing = SingleLinkedListing.of();
        assertEquals(listing, listing);
        assertEquals(1, listing.hashCode());

        listing = SingleLinkedListing.of(wrap(1), wrap(2), wrap(3));
        SingleLinkedListing<EqualWrapper> listing2 = SingleLinkedListing.of(wrap(1), wrap(2));
        assertNotEquals(listing, listing2);
        assertNotEquals(listing.hashCode(), listing2.hashCode());

        listing2 = listing2.extend(wrap(3));
        assertEquals(listing, listing2);
        assertEquals(listing.hashCode(), listing2.hashCode());

        SingleLinkedListing<?> listing3 = SingleLinkedListing.of(new Object() {
            //Badly implemented equals
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        });

        // Assert our hack is working
        assertTrue(listing3.toList().equals(Lists.newArrayList("A")));

        // We define listing equality as equality of the elements, so this should be equal
        assertTrue(listing3.equals(SingleLinkedListing.of("A"))); //Works due to broken equals implementation

        // The badly implemented equals should not confuse the element comparison
        assertNotEquals(SingleLinkedListing.of(), listing3);
        assertNotEquals(listing3, SingleLinkedListing.of());
    }

    @Test
    public void testToString() {
        assertEquals(SingleLinkedListing.of().toString(), "[]");
        assertEquals(SingleLinkedListing.of("A", "B").toString(), "[A, B]");
        assertEquals(
                SingleLinkedListing.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K").toString(),
                "[..., B, C, D, E, F, G, H, I, J, K]");
    }

    private static EqualWrapper wrap(Object o) {
        return new EqualWrapper(o);
    }

    private static class EqualWrapper {
        private Object identity;

        /**
         * Creates a new instance that will use the specified object as its {@code equal}ity identifier
         * @param identity may be null
         */
        public EqualWrapper(Object identity) {
            this.identity = identity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EqualWrapper that = (EqualWrapper) o;

            return !(identity != null ? !identity.equals(that.identity) : that.identity != null);
        }

        @Override
        public int hashCode() {
            return identity != null ? identity.hashCode() : 0;
        }
    }
}
