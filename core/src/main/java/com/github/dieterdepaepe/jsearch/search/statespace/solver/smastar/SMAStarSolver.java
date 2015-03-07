package com.github.dieterdepaepe.jsearch.search.statespace.solver.smastar;

import com.github.dieterdepaepe.jsearch.search.statespace.*;
import com.github.dieterdepaepe.jsearch.search.statespace.util.BasicSolution;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of <a href="http://en.wikipedia.org/wiki/SMA*">Simple Memory-bounded A*</a>. This solver minimises
 * the number of expanded nodes like {@code A*} while using limited memory. It will find
 * the optimal {@link com.github.dieterdepaepe.jsearch.search.statespace.Solution} that is reachable using the
 * restricted memory. This solution will be marked as optimal if it can be guaranteed no cheaper solutions existed
 * outside the memory bounded search space.
 *
 * <p><b>Important: </b> This solver assumes a correct implementation of the
 * {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#equals(Object)} to recognise identical states
 * when regenerating discarded children for a certain node. If the {@code Iterable}s returned by the
 * {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNodeGenerator} doesn't create new objects for each
 * iteration (such as is the case for collections), the default {@link Object#equals(Object)}
 * implementation will suffice.</p>
 *
 * <p>This solver does not use the {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode#getSearchSpaceState()}
 * information.</p>
 *
 * <p>During a search, the solver keeps a maximum number of {@link com.github.dieterdepaepe.jsearch.search.statespace.SearchNode}s
 * in memory. Each expanded node in memory will also track the {@code Iterable} produced by the corresponding
 * {@code SearchNodeGenerator}. Because of this, memory can be saved by having the {@code SearchNodeGenerator}
 * return generating {@code Iterable}s rather than collections.</p>
 *
 * <p>This implementation is thread-safe.</p>
 * @author Dieter De Paepe
 */
public class SMAStarSolver implements Solver<SearchNode, Object> {
    private int maxSearchNodesUsed;

    /**
     * Creates a new solver instance that will keep at most {@code maxNodesInMemory} nodes in memory.
     * @param maxNodes the maximum number of nodes
     * @throws java.lang.IllegalArgumentException if {@code maxNodes} is <= 0
     */
    public SMAStarSolver(int maxNodes) {
        checkArgument(maxNodes > 0, "Maximum number of nodes in memory should be > 0.");

        this.maxSearchNodesUsed = maxNodes;
    }

    @Override
    public <S extends SearchNode, E> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                E environment,
                                                Heuristic<? super S, ? super E> heuristic,
                                                SearchNodeGenerator<S, E> searchNodeGenerator,
                                                Manager<? super S> manager) {
        SMAStarFrontier<S> frontier = new SMAStarFrontier<>();
        BoundaryNodeCostTracker boundaryNodeCostTracker = new BoundaryNodeCostTracker();

        SMASearchNode<S> smaRootNode = new SMASearchNode<>(null, null, 0, CostUtil.MIN_COST);
        smaRootNode.initialiseChildren(startNodes);
        if (!smaRootNode.getCurrentChildIterator().hasNext())
            return;
        frontier.addNode(smaRootNode);

        // The total number of SMASearchNodes containing a search node in memory:
        // this includes all nodes on the frontier, their ancestors (which may or may not be on the frontier) but
        // excludes the root node.
        int nodesInMemory = 0;

        while (manager.continueSearch()) {
            SMASearchNode<S> cheapestNode = frontier.getDeepestLeastCostNode();

            if (CostUtil.COST_COMPARATOR.compare(cheapestNode.getTotalEstimatedCost(), manager.getCostBound()) > 0)
                return;

            if (cheapestNode.getTotalEstimatedCost() == CostUtil.MAX_COST)
                return;

            if (cheapestNode.getSearchNode() != null && cheapestNode.getSearchNode().isGoal()) {
                boolean isOptimal = CostUtil.COST_COMPARATOR.compare(cheapestNode.getTotalEstimatedCost(), boundaryNodeCostTracker.getMinimumBoundaryCost()) <= 0;
                manager.registerSolution(new BasicSolution<>(cheapestNode.getSearchNode(), isOptimal));
                return;
            }

            boolean hasChildren = true;
            if (!cheapestNode.hasInitialisedChildren()) {
                cheapestNode.initialiseChildren(searchNodeGenerator.generateSuccessorNodes(cheapestNode.getSearchNode(), environment, heuristic));
                hasChildren = cheapestNode.getCurrentChildIterator().hasNext();
            }

            if (hasChildren) {
                SMASearchNode<S> newChild = generateChild(cheapestNode, frontier, boundaryNodeCostTracker);
                if (!cheapestNode.shouldBeOnFrontier())
                    frontier.removeNode(cheapestNode);
                if (newChild != null) {
                    if (nodesInMemory >= maxSearchNodesUsed) {
                        purgeWorstNode(frontier);
                    } else {
                        nodesInMemory++;
                    }
                    frontier.addNode(newChild);
                }
            } else {
                frontier.removeNode(cheapestNode);
                cheapestNode.setTotalEstimatedCost(CostUtil.MAX_COST);
                frontier.addNode(cheapestNode);
            }
        }
    }

    /**
     * Removes the shallowest, highest cost node from the frontier. The parent of the removed node will be updated
     * and will be added to the frontier if needed.
     * @param frontier the frontier
     */
    private <T extends SearchNode> void purgeWorstNode(SMAStarFrontier<T> frontier) {
        SMASearchNode<T> worstNode = frontier.removeShallowestHighestCostLeafNode();
        SMASearchNode<T> parent = worstNode.getParent();

        // If the parent wasn't already on the frontier, it should be after removal of its child
        if (!parent.shouldBeOnFrontier())
            frontier.addNode(parent);

        parent.removeChildFromMemory(worstNode);
    }


    /**
     * Generates/regenerates a child node that was not yet/no longer present in the passed node. The generated node
     * is linked to its parent and has a proper estimated cost assigned.
     * <p/>
     * During this call, the estimated costs for the parent node, and any predecessors may be updated.
     * @param node the node for which to generate a child
     * @param frontier the frontier, used for updating any costs - the child node will not be added to the frontier
     * @param boundaryNodeCostTracker the tracker to update if the child is at the maximum allowed depth
     * @return the newly generated child node or null if all children are already present in the parent
     */
    private <T extends SearchNode> SMASearchNode<T> generateChild(SMASearchNode<T> node, SMAStarFrontier<T> frontier, BoundaryNodeCostTracker boundaryNodeCostTracker) {
        Iterator<InformedSearchNode<T>> childIterator = node.getCurrentChildIterator();

        SMASearchNode<T> result = null;
        while (childIterator.hasNext() && result == null) {
            InformedSearchNode<T> nextChild = childIterator.next();
            SMASearchNode<T> tempSearchNode = makeSMASearchNode(nextChild, node, boundaryNodeCostTracker);
            if (!node.getChildrenInMemory().contains(tempSearchNode)) {
                result = tempSearchNode;
                node.getChildrenInMemory().add(result);
            }
        }

        if (!childIterator.hasNext())
            propagateCostEstimates(node, frontier);

        if (result != null)
            return result;

        if (!node.getChildHasBeenPrunedInIteration())
            return null; //No new child available
        else {
            node.resetChildIterator();
            return generateChild(node, frontier, boundaryNodeCostTracker);
        }
    }

    /**
     * Updates the estimated cost for {@code node} and its ancestors.
     * @param node the node
     * @param frontier the frontier to update when the cost of {@code node} is changed
     */
    private <T extends SearchNode> void propagateCostEstimates(SMASearchNode<T> node, SMAStarFrontier<T> frontier) {
        if (node.getCurrentChildIterator().hasNext())
            return;

        Cost newCostEstimate = node.getCheapestPurgedCostInIteration();
        for (SMASearchNode<T> child : node.getChildrenInMemory()) {
            newCostEstimate = CostUtil.COST_COMPARATOR.min(newCostEstimate, child.getTotalEstimatedCost());
        }

        if (newCostEstimate == node.getTotalEstimatedCost())
            return;

        //Do not use node.shouldBeOnFrontier() here - the state of node is being changed by the caller of this method.
        boolean wasInFrontier = frontier.removeNode(node);
        node.setTotalEstimatedCost(newCostEstimate);
        if (wasInFrontier)
            frontier.addNode(node);

        if (node.getParent() != null)
            propagateCostEstimates(node.getParent(), frontier);
    }

    /**
     * Creates a new {@code SMASearchNode} of the given node.
     * @param node the node to be converted
     * @param parent the parent of the new node
     * @param boundaryNodeCostTracker the tracker to update if the child is at the maximum allowed depth
     * @return a new node, that is not yet added to its parent
     */
    private <T extends SearchNode> SMASearchNode<T> makeSMASearchNode(InformedSearchNode<T> node, SMASearchNode<T> parent, BoundaryNodeCostTracker boundaryNodeCostTracker) {
        int childDepth = parent.getDepth() + 1;
        Cost childCost;
        if (childDepth >= maxSearchNodesUsed && !node.getSearchNode().isGoal()) {
            childCost = CostUtil.MAX_COST;
            boundaryNodeCostTracker.update(node.getEstimatedTotalCost());
        } else {
            childCost = CostUtil.COST_COMPARATOR.max(node.getEstimatedTotalCost(), parent.getTotalEstimatedCost());
        }
        return new SMASearchNode<>(node.getSearchNode(), parent, childDepth, childCost);
    }

    /**
     * Helper class that tracks the actual estimated costs of any {@code SMASearchNode} that was not further expanded
     * due to it's depth being too large to fit all predecessors in memory.
     */
    private static class BoundaryNodeCostTracker {
        private Cost minimumBoundaryCost = CostUtil.MAX_COST;

        void update(Cost costOfBoundaryNode) {
            minimumBoundaryCost = CostUtil.COST_COMPARATOR.min(minimumBoundaryCost, costOfBoundaryNode);
        }

        Cost getMinimumBoundaryCost() {
            return minimumBoundaryCost;
        }
    }
}
