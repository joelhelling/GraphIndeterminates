package edu.csuci.label;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/20/2016.
 */
public class ListLabeler {
    public static List<IntOpenHashSet> labelGraph(List<IntOpenHashSet> graph) {
        // Preallocate the lists for holding the labeling alphabet
        int vertices = graph.size();
        List<IntOpenHashSet> labels = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            labels.add(i, new IntOpenHashSet(vertices));
        }

        // Initialize graph connections for shared symbols
        int[][] connections = new int[vertices][vertices];

        // Start with empty current clique
        Integer lambda = 1;
        IntOpenHashSet currentClique = new IntOpenHashSet(vertices);
        IntIterator cliqueIterator;

        IntIterator wIterator;
        IntIterator qIterator;
        for (int v = 0; v < vertices; v++) {
            IntOpenHashSet vNeighbors = graph.get(v);
            if (vNeighbors.isEmpty()) {
                labels.get(v).add(lambda);
                lambda += 1;
            } else {
                // Check through neighbors of v
                wIterator = vNeighbors.iterator();
                while (wIterator.hasNext()) {
                    int w = wIterator.nextInt();
                    if (connections[v][w] == 0) {
                        currentClique.add(w);
                        labels.get(v).add(lambda);
                        labels.get(w).add(lambda);
                        // Check through neighbors of v again to add to clique
                        qIterator = vNeighbors.iterator();
                        while (qIterator.hasNext()) {
                            int q = qIterator.nextInt();
                            if (q != w && graph.get(q).containsAll(currentClique)) {
                                labels.get(q).add(lambda);
                                currentClique.add(q);
                            }
                        }
                        while (!currentClique.isEmpty()) {
                            int c = currentClique.iterator().nextInt();
                            currentClique.rem(c);
                            connections[c][v] = 1;
                            connections[v][c] = 1;
                            cliqueIterator = currentClique.iterator();
                            while (cliqueIterator.hasNext()) {
                                int d = cliqueIterator.nextInt();
                                connections[c][d] = 1;
                                connections[d][c] = 1;
                                connections[d][v] = 1;
                                connections[v][d] = 1;
                            }
                        }
                        lambda += 1;
                    }
                }
            }
        }
        return labels;
    }

    // Recreate the graph from the labels and compare the original graph for correctness
    public static List<IntOpenHashSet> checkLabeling(List<IntOpenHashSet> labels) {
        List<IntOpenHashSet> result = new ArrayList<>(labels.size());
        for (int i = 0; i < labels.size(); i++) {
            result.add(i, new IntOpenHashSet(result.size()));
        }

        for (int i = 0; i < labels.size(); i++) {
            for (int j = i + 1; j < labels.size(); j++) {
                IntOpenHashSet intersection = new IntOpenHashSet(labels.get(i));
                intersection.retainAll(labels.get(j));
                if (!intersection.isEmpty()) {
                    result.get(i).add(j);
                    result.get(j).add(i);
                }
            }
        }
        return result;
    }

    public static int countLabels(List<IntOpenHashSet> labels) {
        return labels.stream().flatMap(Set::stream).collect(Collectors.toSet()).size();
    }

    public static void printLabels(List<IntOpenHashSet> labels) {
        StringBuilder res = new StringBuilder();
        res.append("Labels:\n");
        int line = 0;
        for (Set<Integer> vertexLabels : labels) {
            res.append(line++).append(": [");
            for (Integer i : vertexLabels) {
                res.append(i.toString()).append(", ");
            }
            if (vertexLabels.size() > 0) {
                res.delete(res.length() - 2, res.length());
            }
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
