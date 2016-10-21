package edu.csuci.graph;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/20/2016.
 */
public class ListGraphGenerator {
    private static List<Set<Integer>> transformToList(int[][] adjMatrix) {
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
        return transformToList(result);
    }

    public static int countEdges(List<Set<Integer>> graph) {
        return graph.stream().map(Set::size).reduce(0, (x,y) -> x + y)/2;
    }

    public static boolean graphEquals(List<Set<Integer>> left, List<Set<Integer>> right) {
        return left.equals(right);
    }

    public static void printGraph(List<Set<Integer>> graph) {
        StringBuilder res = new StringBuilder();
        res.append("Graph:\n");
        int line = 0;
        for (Set<Integer> neighbors : graph) {
            res.append(line++).append(": [");
            for (Integer i : neighbors) {
                res.append(i.toString()).append(", ");
            }
            if (neighbors.size() > 0) {
                res.delete(res.length() - 2, res.length());
            }
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
