package com.github.dieterdepaepe.jsearch.datastructure.lightweight;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test class for {@link com.github.dieterdepaepe.jsearch.datastructure.lightweight.SingleLinkedListing}
 * @author Dieter De Paepe
 */
public class SingleLinkedListingTest {
    @Test
    public void testUsage() {
        SingleLinkedListing<String> listing = new SingleLinkedListing<String>("A");
        listing = new SingleLinkedListing<>(listing, "B");
        listing = new SingleLinkedListing<>(listing, "C");
        listing = new SingleLinkedListing<>(listing, "D");

        SingleLinkedListing<String> listingX = new SingleLinkedListing<String>(listing, "X");

        SingleLinkedListing<String> listingYZ = new SingleLinkedListing<String>(listing, "Y");
        listingYZ = new SingleLinkedListing<>(listingYZ, "Z");

        List<String> expectedX = Arrays.asList("A", "B", "C", "D", "X");
        List<String> expectedYZ = Arrays.asList("A", "B", "C", "D", "Y", "Z");

        assertEquals(listingX.getPrefixListing(), listing);

        assertEquals(SingleLinkedListing.toList(listingX, false), expectedX);
        assertEquals(SingleLinkedListing.toList(listingYZ, false), expectedYZ);

        Collections.reverse(expectedX);
        Collections.reverse(expectedYZ);

        assertEquals(Lists.newArrayList(SingleLinkedListing.fromEndToStart(listingX)), expectedX);
        assertEquals(Lists.newArrayList(SingleLinkedListing.fromEndToStart(listingYZ)), expectedYZ);
        assertEquals(Lists.newArrayList(SingleLinkedListing.fromEndToStart(listingX)), expectedX);
        assertEquals(Lists.newArrayList(SingleLinkedListing.fromEndToStart(listingYZ)), expectedYZ);
    }

    @Test
    public void testEmptyList() {
        SingleLinkedListing<String> listing = null;

        assertEquals(Lists.newArrayList(SingleLinkedListing.fromEndToStart(listing)), Collections.emptyList());
        assertEquals(SingleLinkedListing.toList(listing, false), Collections.emptyList());
    }
}
