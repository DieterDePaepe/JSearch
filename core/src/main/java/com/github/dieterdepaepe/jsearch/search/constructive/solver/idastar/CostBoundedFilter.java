package com.github.dieterdepaepe.jsearch.search.constructive.solver.idastar;

import com.github.dieterdepaepe.jsearch.search.constructive.Cost;
import com.github.dieterdepaepe.jsearch.search.constructive.InformedSearchNode;
import com.google.common.base.Predicate;

/**
* A {@code Predicate} which returns a {@code true} value for {@code InformedSearchNode}s whose total estimated value
 * is not greater than a specified bound. It keeps track of the lowest cost which failed this test.
* @author Dieter De Paepe
*/
class CostBoundedFilter implements Predicate<InformedSearchNode<?>> {
    private Cost maxAllowedCost;
    private Cost minExceedingCost;
    private boolean filteredAnItem;

    /**
     * Creates a new predicate which checks whether or not the cost of tested nodes is below the given bound.
     * @param maxAllowedCost the maximum allowed cost bound, {@code null} to allow no value
     */
    CostBoundedFilter(Cost maxAllowedCost) {
        this.maxAllowedCost = maxAllowedCost;
        this.minExceedingCost = null;
        this.filteredAnItem = false;
    }

    @Override
    public boolean apply(InformedSearchNode<?> input) {
        Cost estimatedTotalCost = input.getEstimatedTotalCost();
        if (maxAllowedCost != null && estimatedTotalCost.compareTo(maxAllowedCost) <= 0)
            return true;

        if (!filteredAnItem || minExceedingCost.compareTo(estimatedTotalCost) > 0) {
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
    public Cost getMinExceedingCost() {
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
