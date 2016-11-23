package edu.csuci.graph;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private List<IntOpenHashSet> graph;

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

    public List<IntOpenHashSet> getGraph() {
        return graph;
    }

    public List<IntOpenHashSet> generateGraph() {
        graph = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            graph.add(new IntOpenHashSet());
        }

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (rng.nextDouble() <= density) {
                    graph.get(i).add(j);
                    graph.get(j).add(i);
                }
            }
        }

        return graph;
    }

    public int countEdges() {
        return graph.stream().map(Set::size).reduce(0, (x,y) -> x + y)/2;
    }

    public boolean graphEquals(List<IntOpenHashSet> other) {
        return graph.equals(other);
    }

    public static void printGraph(List<IntOpenHashSet> graph) {
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
