package edu.csuci.graph;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/20/2016.
 */
public class ListGraphGenerator {
    private int vertices;
    private double density;
    private final Random rng;
    private final List<Set<Integer>> graph;

    public ListGraphGenerator(int vertices, double density) {
        this.vertices = vertices;
        this.density = density;
        this.rng = new Random(System.currentTimeMillis());
        this.graph = new ArrayList<>();
    }

    public ListGraphGenerator(int vertices, double density, long seed) {
        this.vertices = vertices;
        this.density = density;
        this.rng = new Random(seed);
        this.graph = new ArrayList<>();
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

    public void setDensity(double density) {
        this.density = density;
    }

    public void randomizeDensity() {
        this.density = rng.nextDouble();
    }

    public List<Set<Integer>> getGraph() {
        return graph;
    }

    public List<Set<Integer>> generateGraph() {
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

    private List<Set<Integer>> transformToList(int[][] adjMatrix) {
        List<Set<Integer>> result = new ArrayList<>(adjMatrix.length);
        for (int i = 0; i < adjMatrix.length; i++) {
            result.add(i, new HashSet<>(adjMatrix.length));
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
        return graph.stream().map(Set::size).reduce(0, (x,y) -> x + y)/2;
    }

    public boolean graphEquals(List<Set<Integer>> other) {
        return graph.equals(other);
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
