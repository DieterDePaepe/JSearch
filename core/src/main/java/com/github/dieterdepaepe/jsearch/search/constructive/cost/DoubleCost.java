package com.github.dieterdepaepe.jsearch.search.constructive.cost;

import com.github.dieterdepaepe.jsearch.search.constructive.Cost;

/**
 * A cost consisting of a single double value.
 * @author Dieter De Paepe
 */
public class DoubleCost implements Cost {
    private final double value;

    public static DoubleCost valueOf(double value) {
        return new DoubleCost(value);
    }

    protected DoubleCost(double value) {
        this.value = value;
    }

    @Override
    public DoubleCost add(Cost cost) {
        DoubleCost other = (DoubleCost) cost;
        return new DoubleCost(this.value + other.value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Cost o) {
        DoubleCost other = (DoubleCost) o;
        return Double.compare(value, other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleCost that = (DoubleCost) o;

        if (Double.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return "DoubleCost: " + value;
    }
}
