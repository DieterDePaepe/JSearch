package com.github.dieterdepaepe.jsearch.search.constructive.solver.iterativedeepening;

import com.github.dieterdepaepe.jsearch.search.constructive.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A wrapper around another {@code Solver} which adds <i>iterative deepening</i> to the search process.
 *
 * <p>A search will happen in multiple iterations with an increasing depth. In iteration, the search space is limited to
 * nodes whose depth value is smaller than or equal to this depth. A complete search is conducted in each iteration by
 * the wrapped {@code Solver}. This means that a registered "optimal" solution may not be optimal at all, but instead be
 * optimal for the depth-limited search space. Search continues until the {@code Manager} discontinues it or when
 * increasing the search depth would not add any extra search nodes to the search space.</p>
 *
 * <p>Iterative deepening can be used with any {@code Solver}, but is commonly used in combination with depth first search
 * (see {@link com.github.dieterdepaepe.jsearch.search.constructive.solver.DepthFirstSolver}) to form
 * <a href="http://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search">iterative deepening depth
 * first search (IDDFS)</a>. This has several advantages: it enables the use of depth first search in unbounded search
 * spaces, it functions like breadth-first-search and is very memory efficient. There is an overhead due to
 * the revisiting of search nodes, but this is typically a small factor, especially for search spaces with a
 * high branching factor.</p>
 *
 * <p>This class is thread-safe if the wrapped {@code Solver} is.</p>
 * @author Dieter De Paepe
 */
public class IterativeDeepeningSolver<U extends DepthSearchNode, V> implements Solver<U, V> {
    private Solver<? super U, ? super V> solver;
    private int startDepth;
    private int depthDelta;

    /**
     * Creates a new solver which adds iterative deepening to the given solver.
     * @param solver the solver
     * @param startDepth the maximum depth of all nodes to consider in the first search iteration
     * @param depthDelta the depth increase per iteration, should be > 0
     */
    public IterativeDeepeningSolver(Solver<? super U, ? super V> solver, int startDepth, int depthDelta) {
        checkNotNull(solver, "Solver may not be null");
        checkArgument(depthDelta > 0, "Delta should be > 0, but was %s", depthDelta);

        this.solver = solver;
        this.startDepth = startDepth;
        this.depthDelta = depthDelta;
    }

    /**
     * Creates a new solver which adds iterative deepening to the given solver. When a new search is started, the
     * maximum depth considered is 1, and will increase by 1 in every iteration.
     * @param solver the solver
     */
    public IterativeDeepeningSolver(Solver<? super U, ? super V> solver) {
        this(solver, 1, 1);
    }

    @Override
    public <S extends U, E extends V> void solve(Iterable<InformedSearchNode<S>> startNodes,
                                                 E environment,
                                                 Heuristic<? super S, ? super E> heuristic,
                                                 SearchNodeGenerator<S, E> searchNodeGenerator,
                                                 Manager<? super S> manager) {
        int depthLimit = startDepth;
        boolean searchSpaceFullyExamined = false;

        while (manager.continueSearch() && !searchSpaceFullyExamined) {
            DepthLimitedGenerator<S, E> limitedGenerator = new DepthLimitedGenerator<>(searchNodeGenerator, depthLimit);
            solver.solve(startNodes, environment, heuristic, limitedGenerator, manager);
            searchSpaceFullyExamined = !limitedGenerator.filteredAnyResult();
            depthLimit += depthDelta;
        }
    }

    @Override
    public String toString() {
        return "IterativeDeepeningSolver{" +
                "startDepth=" + startDepth +
                ", depthDelta=" + depthDelta +
                ", solver=" + solver +
                '}';
    }
}
