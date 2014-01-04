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
        listing = new SingleLinkedListing<String>(listing, "B");
        listing = new SingleLinkedListing<String>(listing, "C");
        listing = new SingleLinkedListing<String>(listing, "D");

        SingleLinkedListing<String> listingX = new SingleLinkedListing<String>(listing, "X");

        SingleLinkedListing<String> listingYZ = new SingleLinkedListing<String>(listing, "Y");
        listingYZ = new SingleLinkedListing<String>(listingYZ, "Z");

        List<String> expectedX = Arrays.asList("A", "B", "C", "D", "X");
        List<String> expectedYZ = Arrays.asList("A", "B", "C", "D", "Y", "Z");

        assertEquals(listingX.getPrefixListing(), listing);

        assertEquals(listingX.toList(false), expectedX);
        assertEquals(listingYZ.toList(false), expectedYZ);

        Collections.reverse(expectedX);
        Collections.reverse(expectedYZ);

        assertEquals(Lists.newArrayList(listingX.fromEndToStart()), expectedX);
        assertEquals(Lists.newArrayList(listingYZ.fromEndToStart()), expectedYZ);
        assertEquals(Lists.newArrayList(listingX.toList(true)), expectedX);
        assertEquals(Lists.newArrayList(listingYZ.toList(true)), expectedYZ);
    }
}
