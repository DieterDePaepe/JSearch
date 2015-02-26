package com.github.dieterdepaepe.jsearch.search.statespace.solver.smastar;

import com.github.dieterdepaepe.jsearch.search.statespace.Cost;
import com.google.common.collect.Ordering;

/**
 * Utility class which provides a custom {@link Cost} ordering for which 2 specific values are always the lowest
 * and highest cost possible.
 */
class CostUtil {
    private CostUtil() {}

    /**
     * The lowest possible value for the order defined by {@link CostUtil#COST_COMPARATOR}.
     */
    public static final Cost MIN_COST = new CostBoundary("Min cost bound");
    /**
     * The highest possible value for the order defined by {@link CostUtil#COST_COMPARATOR}.
     */
    public static final Cost MAX_COST = new CostBoundary("Max cost bound");

    /**
     * An ordering for which {@link CostUtil#MIN_COST} and {@link CostUtil#MAX_COST} are always the minimum and maximum
     * values.
     */
    public static final Ordering<Cost> COST_COMPARATOR = new Ordering<Cost>() {
        @Override
        public int compare(Cost o1, Cost o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == MIN_COST) {
                return -1;
            } else if (o2 == MIN_COST) {
                return 1;
            } else if (o1 == MAX_COST) {
                return 1;
            } else if (o2 == MAX_COST) {
                return -1;
            } else {
                return Ordering.natural().compare(o1, o2);
            }
        }
    };

    private static class CostBoundary implements Cost {
        private String string;

        public CostBoundary(String string) {
            this.string = string;
        }

        @Override
        public Cost add(Cost cost) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(Cost o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
