package edu.csuci.graph;

import java.util.Random;

/**
 * Created by Joel on 9/28/2016.
 */
public class MatrixGraphGenerator {
    public static int[][] randomGraph(int n, double density, Random rng) {
        int[][] graph = new int[n][n];

        for (int i = 0; i < graph.length; i++) {
           for (int j = i+1; j < graph[i].length; j++) {
               if (i != j) {
                   if (rng.nextDouble() <= density) {
                       graph[i][i] = 1;
                       graph[j][j] = 1;
                       graph[i][j] = 1;
                       graph[j][i] = 1;
                   }
               }
           }
        }
        return graph;
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

    public static boolean graphEquals(int[][] src, int[][] test) {
        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src[i].length; j++) {
                if (src[i][j] != test[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void printGraph(int[][] graph) {
        printAdjacencyMatrix(graph);
        printAdjacencyList(graph);
    }

    private static void printAdjacencyList(int[][] graph) {
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

    private static void printAdjacencyMatrix(int[][] graph) {
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
