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

    public static int[][] fastRandomGraph(int n, double density, Random rng) {
        int[][] graph = new int[n][n];

        for (int i = 1; i < graph.length; i++) {
           for (int j = i+1; j < graph[i].length; j++) {
               if (i != j) {
                   if (rng.nextDouble() <= density) {
                       graph[i][i] = 1;
                       graph[i][j] = 1;
                       graph[j][i] = 1;
                   }
               }
           }
        }
        return graph;
    }

    public static int countEdges(List<Set<Integer>> graph) {
        return graph.stream().map(Set::size).reduce(0, (x,y) -> x + y)/2;
    }

    public static int countEdges(int[][] graph) {
        int count = 0;
        for (int i = 0; i < graph.length; i++) {
            for (int j = i+1; j < graph[i].length; j++) {
                if (graph[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
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

    public static void printGraph(int[][] graph) {
        StringBuilder res = new StringBuilder();
        res.append("Graph:\n");

        for (int i = 0; i < graph.length; i++) {
            res.append(i).append(": [");
            boolean hasNeighbors = false;
            for (int j = 0; j < graph.length; j++) {
                if (i != j && graph[i][j] == 1) {
                    res.append(j).append(", ");
                    hasNeighbors = true;
                }
            }
            if (hasNeighbors) {
                res.delete(res.length() - 2, res.length());
            }
            res.append("] hasNeighbors:").append(hasNeighbors).append("\n");
        }
        System.out.println(res.toString());
    }

    public static void printAdjacencyMatrix(int[][] graph) {
        StringBuilder res = new StringBuilder();
        res.append("Matrix:\n");

        for (int i = 0; i < graph.length; i++) {
            res.append(i).append(": [");
            for (int j = 0; j < graph.length; j++) {
                res.append(graph[i][j]).append(", ");
            }
            res.delete(res.length() - 2, res.length());
            res.append("]").append("\n");
        }
        System.out.println(res.toString());
    }
}
