package com.github.dieterdepaepe.jsearch.search.constructive.cost;

import com.github.dieterdepaepe.jsearch.search.constructive.Cost;
import com.google.common.math.IntMath;

/**
 * A cost consisting of a single integer value.
 * @author Dieter De Paepe
 */
public class IntegerCost implements Cost {
    private final int value;

    public static IntegerCost valueOf(int value) {
        return new IntegerCost(value);
    }

    protected IntegerCost(int value) {
        this.value = value;
    }

    @Override
    public IntegerCost add(Cost cost) {
        IntegerCost other = (IntegerCost) cost;
        return new IntegerCost(IntMath.checkedAdd(this.value, other.value));
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Cost o) {
        IntegerCost other = (IntegerCost) o;
        return Integer.compare(value, other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerCost that = (IntegerCost) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "IntegerCost: " + value;
    }
}
