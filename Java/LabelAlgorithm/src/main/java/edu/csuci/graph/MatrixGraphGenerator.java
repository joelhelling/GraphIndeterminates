package edu.csuci.graph;

import java.util.Random;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/2016.
 */
public class MatrixGraphGenerator {
    private int vertices;
    private double density;
    private Random rng;
    private int[][] graph;

    public MatrixGraphGenerator(int vertices, double density) {
        this.vertices = vertices;
        this.density = density;
        this.rng = new Random(System.currentTimeMillis());
        this.graph = new int[0][0];
    }

    public MatrixGraphGenerator(int vertices, double density, long seed) {
        this.vertices = vertices;
        this.density = density;
        this.rng = new Random(seed);
        this.graph = new int[0][0];
    }

    public int getVertices() {
        return vertices;
    }

    public void setVertices(int vertices) {
        this.vertices = vertices;
    }

    public double getDensity() {
        return density;
    }

    public void randomizeDensity() {
        this.density = rng.nextDouble();
    }

    public int[][] getGraph() {
        return graph;
    }

    public int[][] generateGraph() {
        graph = new int[vertices][vertices];

        for (int i = 0; i < graph.length; i++) {
            for (int j = i+1; j < graph[i].length; j++) {
                if (rng.nextDouble() <= density) {
                    graph[i][i] = 1;
                    graph[j][j] = 1;
                    graph[i][j] = 1;
                    graph[j][i] = 1;
                }
            }
        }
        return graph;
    }

    public int[][] generateGraphFromUpperRightTriangleBits(long bits) {
        graph = new int[vertices][vertices];
        long mask = 0x1;

        for (int i = 0; i < graph.length; i++) {
            for (int j = i + 1; j  < graph[i].length; j++) {
                if ((bits & mask) == 0x1) {
                    graph[i][j] = 1;
                    graph[j][i] = 1;
                    graph[i][i] = 1;
                    graph[j][j] = 1;
                }
                bits = bits >>> 1;
            }
        }
        return graph;
    }

     public int countEdges() {
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

    public boolean graphEquals(int[][] other) {
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j] != other[i][j]) {
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
