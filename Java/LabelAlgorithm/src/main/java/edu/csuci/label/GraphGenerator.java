package edu.csuci.label;

import java.util.*;

/**
 * Created by Joel on 9/28/2016.
 */
public class GraphGenerator {
    public static List<Set<Integer>> transformToList(int[][] adjMatrix) {
        List<Set<Integer>> result = new ArrayList<>(adjMatrix.length);
        for (int i = 0; i < adjMatrix.length; i++) {
            result.add(i, new HashSet<>(adjMatrix.length));
        }

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix[i].length; j++) {
                if (i != j && adjMatrix[i][j] == 1) {
                    result.get(i).add(j);
                    result.get(j).add(i);
                }
            }
        }
        return result;
    }

    public static List<Set<Integer>> randomGraph(int n, double density, Random rng) {
        int[][] result = new int[n][n];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                if (i == j) {
                    result[i][j] = 1;
                } else {
                    if (rng.nextDouble() <= density) {
                        result[i][j] = 1;
                    }
                }
            }
        }
        return GraphGenerator.transformToList(result);
    }
}
