package edu.csuci.graph;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/21/2016.
 */
public class MapGraphGenerator {
    private static Map<Integer, List<Integer>> transformToList(int[][] adjMatrix) {
        Map<Integer, List<Integer>> result = new TreeMap<>();
        for (int i = 0; i < adjMatrix.length; i++) {
            result.put(i, new ArrayList<>(adjMatrix.length));
        }

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = i + 1; j < adjMatrix[i].length; j++) {
                if (i != j && adjMatrix[i][j] == 1) {
                    result.get(i).add(j);
                    result.get(j).add(i);
                }
            }
        }
        return result;
    }

    public static Map<Integer, List<Integer>> randomGraph(int n, double density, Random rng) {
        int[][] result = new int[n][n];

        for (int i = 0; i < result.length; i++) {
            for (int j = i + 1; j < result[i].length; j++) {
                if (rng.nextDouble() <= density) {
                    result[i][i] = 1;
                    result[j][j] = 1;
                    result[i][j] = 1;
                    result[j][i] = 1;
                }
            }
        }
        return transformToList(result);
    }

    public static int countEdges(Map<Integer, List<Integer>> graph) {
        return graph.values().stream().mapToInt(List::size).sum()/2;
    }

    public static boolean graphEquals(Map<Integer, List<Integer>> left, Map<Integer, List<Integer>> right) {
        return left.equals(right);
    }

    public static void printGraph(Map<Integer, List<Integer>> graph) {
        StringBuilder res = new StringBuilder();
        res.append("Graph:\n");
        int line = 0;
        for (Map.Entry<Integer, List<Integer>> neighbors : graph.entrySet()) {
            res.append(line++).append(": [");
            for (Integer i : neighbors.getValue()) {
                res.append(i.toString()).append(", ");
            }
            if (neighbors.getValue().size() > 0) {
                res.delete(res.length() - 2, res.length());
            }
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
