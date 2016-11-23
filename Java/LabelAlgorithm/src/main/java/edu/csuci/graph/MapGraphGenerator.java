package edu.csuci.graph;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/21/2016.
 */
public class MapGraphGenerator {
    private int vertices;
    private double density;
    private final Random rng;
    private final Map<Integer, List<Integer>> graph;

    public MapGraphGenerator(int vertices, double density) {
        this.vertices = vertices;
        this.density = density;
        this.rng = new Random(System.currentTimeMillis());
        this.graph = new TreeMap<>();
    }

    public MapGraphGenerator(int vertices, double density, long seed) {
        this.vertices = vertices;
        this.density = density;
        this.rng = new Random(seed);
        this.graph = new TreeMap<>();
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

    public void setDensity(double density) {
        this.density = density;
    }

    public Map<Integer, List<Integer>> getGraph() {
        return graph;
    }

    public Map<Integer, List<Integer>> generateGraph() {
        int[][] result = new int[vertices][vertices];

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

    private Map<Integer, List<Integer>> transformToList(int[][] adjMatrix) {
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

    public int countEdges() {
        return graph.values().stream().mapToInt(List::size).sum()/2;
    }

    public boolean graphEquals(Map<Integer, List<Integer>> other) {
        return graph.equals(other);
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
