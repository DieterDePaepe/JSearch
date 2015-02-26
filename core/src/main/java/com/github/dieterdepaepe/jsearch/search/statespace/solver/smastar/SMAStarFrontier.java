package com.github.dieterdepaepe.jsearch.search.statespace.solver.smastar;

import com.github.dieterdepaepe.jsearch.search.statespace.Cost;
import com.github.dieterdepaepe.jsearch.search.statespace.SearchNode;
import com.google.common.collect.ComparisonChain;

import java.util.*;

/**
 * Utility class for the {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.smastar.SMAStarSolver}. It
 * keeps track of the {@link com.github.dieterdepaepe.jsearch.search.statespace.solver.smastar.SMASearchNode}s on the
 * frontier and allows efficient querying of them.
 * @param <T> the type of {@code SearchNode} being stored
 * @author Dieter De Paepe
 */
class SMAStarFrontier<T extends SearchNode> {
    private TreeMap<CostAndDepth, List<SMASearchNode<T>>> frontier;

    SMAStarFrontier() {
        frontier = new TreeMap<>();
    }

    /**
     * Adds a node to storage.
     * @param node the node to add
     */
    public void addNode(SMASearchNode<T> node) {
        CostAndDepth key = new CostAndDepth(node.getTotalEstimatedCost(), node.getDepth());
        List<SMASearchNode<T>> nodes = frontier.get(key);
        if (nodes == null) {
            nodes = new LinkedList<>();
            frontier.put(key, nodes);
        }

        nodes.add(node);
    }

    /**
     * Gets the node with the greatest depth out of all nodes with the lowest cost.
     * @return a node currently being stored
     */
    public SMASearchNode<T> getDeepestLeastCostNode() {
        List<SMASearchNode<T>> nodes = frontier.firstEntry().getValue();
        return nodes.get(0);
    }

    /**
     * Removes the leaf node with the smallest depth out of all nodes with the highest cost.
     * @return the node that was just removed
     */
    public SMASearchNode<T> removeShallowestHighestCostLeafNode() {
        for (Map.Entry<CostAndDepth, List<SMASearchNode<T>>> entry : frontier.descendingMap().entrySet()) {
            List<SMASearchNode<T>> candidateNodes = entry.getValue();
            Iterator<SMASearchNode<T>> iterator = candidateNodes.iterator();
            while (iterator.hasNext()) {
                SMASearchNode<T> candidateNode = iterator.next();
                if (candidateNode.getChildrenInMemory().isEmpty()) {
                    iterator.remove();
                    if (candidateNodes.isEmpty())
                        frontier.remove(entry.getKey());
                    return candidateNode;
                }
            }
        }

        throw new IllegalStateException("No leaf node present in frontier.");
    }

    /**
     * Removes a node from storage.
     * @param nodeToRemove the node to remove
     * @return true if the node was being stored
     */
    public boolean removeNode(SMASearchNode<T> nodeToRemove) {
        CostAndDepth costAndDepth = new CostAndDepth(nodeToRemove.getTotalEstimatedCost(), nodeToRemove.getDepth());
        List<SMASearchNode<T>> nodes = frontier.get(costAndDepth);

        if (nodes == null)
            return false;

        boolean result = nodes.remove(nodeToRemove);
        if (nodes.isEmpty())
            frontier.remove(costAndDepth);

        return result;
    }

    /**
     * Helper class that serves as index. It consists of a cost and depth value and sorts according to
     * increasing cost as primary key and decreasing depth as secondary key.
     */
    private static class CostAndDepth implements Comparable<CostAndDepth> {
        private final Cost cost;
        private final int depth;

        private CostAndDepth(Cost cost, int depth) {
            this.cost = cost;
            this.depth = depth;
        }

        @Override
        public int compareTo(CostAndDepth o) {
            return ComparisonChain.start()
                    .compare(this.cost, o.cost, CostUtil.COST_COMPARATOR)
                    .compare(o.depth, this.depth)
                    .result();
        }

        @Override
        public String toString() {
            return "CostAndDepth{" + cost + ", " + depth + '}';
        }
    }
}
