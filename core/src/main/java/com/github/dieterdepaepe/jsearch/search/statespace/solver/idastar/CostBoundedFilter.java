package com.github.dieterdepaepe.jsearch.search.statespace.solver.idastar;

import com.github.dieterdepaepe.jsearch.search.statespace.InformedSearchNode;
import com.google.common.base.Predicate;

/**
* A {@code Predicate} which returns a {@code true} value for {@code InformedSearchNode}s whose total estimated value
 * is not greater than a specified bound. It keeps track of the lowest cost which failed this test.
* @author Dieter De Paepe
*/
class CostBoundedFilter implements Predicate<InformedSearchNode<?>> {
    private double maxAllowedCost;
    private double minExceedingCost;
    private boolean filteredAnItem;

    /**
     * Creates a new predicate which checks whether or not the cost of tested nodes is below the given bound.
     * @param maxAllowedCost the maximum allowed cost bound
     */
    CostBoundedFilter(double maxAllowedCost) {
        this.maxAllowedCost = maxAllowedCost;
        this.minExceedingCost = Double.NaN;
        this.filteredAnItem = false;
    }

    @Override
    public boolean apply(InformedSearchNode<?> input) {
        double estimatedTotalCost = input.getEstimatedTotalCost();
        if (estimatedTotalCost <= maxAllowedCost)
            return true;

        if (!filteredAnItem || minExceedingCost > estimatedTotalCost) {
            filteredAnItem = true;
            minExceedingCost = estimatedTotalCost;
        }
        return false;
    }

    /**
     * Gets the lowest cost of all tested items whose cost exceeded the allowed bound. This method is only
     * valid if {@link #hasFilteredAnItem()} is {@code true}.
     * @return the lowest encountered cost which exceeded the allowed bound
     */
    public double getMinExceedingCost() {
        return minExceedingCost;
    }

    /**
     * Returns whether or not this predicate has been applied to an item whose cost was higher than the allowed bound.
     * @return false if all tested items had a cost lower or equal to the allowed bound
     */
    public boolean hasFilteredAnItem() {
        return filteredAnItem;
    }

    @Override
    public String toString() {
        return "CostBoundedFilter{" +
                "maxAllowedCost=" + maxAllowedCost +
                ", minExceedingCost=" + minExceedingCost +
                ", filteredAnItem=" + filteredAnItem +
                '}';
    }
}
