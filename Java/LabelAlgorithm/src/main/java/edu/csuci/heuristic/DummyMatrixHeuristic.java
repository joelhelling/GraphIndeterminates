package edu.csuci.heuristic;

import java.util.stream.IntStream;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 11/12/2016.
 */
public class DummyMatrixHeuristic implements MatrixHeuristic{
    @Override
    public int[] runHeuristic(int[][] graph) {
        return IntStream.range(0, graph.length).toArray();
    }
}
