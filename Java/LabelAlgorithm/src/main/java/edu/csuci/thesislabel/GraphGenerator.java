package edu.csuci.thesislabel;

import java.util.*;

/**
 * Created by Joel on 9/28/2016.
 */
public class GraphGenerator {
    public static Map<Integer, Set<Integer>> transformToMap(int[][] adjMatrix) {
        Map<Integer, Set<Integer>> result = new HashMap<>(adjMatrix.length);

        for (int i = 0; i < adjMatrix.length; i++) {
            result.put(i, new HashSet<>());
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

    public static Map<Integer, Set<Integer>> randomGraph(int n, double density, Random rng) {
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
        return GraphGenerator.transformToMap(result);
    }
}
