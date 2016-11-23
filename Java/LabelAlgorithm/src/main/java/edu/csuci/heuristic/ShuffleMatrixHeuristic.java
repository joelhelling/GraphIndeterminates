package edu.csuci.heuristic;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 11/11/2016.
 */
public class ShuffleMatrixHeuristic implements MatrixHeuristic {
    @Override
    public int[] runHeuristic(int[][] graph) {
        Random rng = new Random(System.currentTimeMillis());
        int[] shuffle = IntStream.range(0, graph.length).toArray();

        for (int i = shuffle.length; i > 1; i--) {
            swap(shuffle, i - 1, rng.nextInt(i));
        }
        return shuffle;
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
