package edu.csuci.label;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jhelling on 9/28/16.
 */
public class Labeler {
    public static List<Set<Integer>> labelGraph(List<Set<Integer>> graph) {
        // Preallocate the lists for holding the labeling alphabet
        int vertices = graph.size();
        List<Set<Integer>> labels = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            labels.add(i, new HashSet<>(vertices));
        }

        // Initialize graph connections for shared symbols
        int[][] connections = new int[vertices][vertices];
        for (int i = 0; i < connections.length; i++) {
            connections[i][i] = 1;
        }

        // Start with empty current clique
        Integer lambda = 0;
        Set<Integer> currentClique = new HashSet<>(vertices);

        for (int v = 0; v < vertices; v++) {
            Set<Integer> vNeighbors = graph.get(v);
            if (vNeighbors.isEmpty()) {
                labels.get(v).add(lambda);
                lambda += 1;
            } else {
                // Check through neighbors of v
                for (Integer w : vNeighbors) {
                    if (connections[v][w] == 0) {
                        currentClique.add(w);
                        // Check through neighbors of v again to add to clique
                        for (Integer q : vNeighbors) {
                            if (!q.equals(w) && graph.get(q).containsAll(currentClique)) {
                                currentClique.add(q);
                            }
                        }
                        labels.get(v).add(lambda);
                        while (!currentClique.isEmpty()) {
                            Integer c = currentClique.iterator().next();
                            labels.get(c).add(lambda);
                            currentClique.remove(c);
                            for (Integer d : currentClique) {
                                connections[c][d] = 1;
                                connections[d][c] = 1;
                                connections[c][v] = 1;
                                connections[v][c] = 1;
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
    public static boolean checkLabeling(List<Set<Integer>> graph, List<Set<Integer>> labels) {
        List<Set<Integer>> result = new ArrayList<>(labels.size());
        for (int i = 0; i < labels.size(); i++) {
            result.add(i, new HashSet<>(result.size()));
        }

        for (int i = 0; i < labels.size(); i++) {
            for (int j = i + 1; j < labels.size(); j++) {
                Set<Integer> intersection = new HashSet<>(labels.get(i));
                intersection.retainAll(labels.get(j));
                if (!intersection.isEmpty()) {
                    result.get(i).add(j);
                    result.get(j).add(i);
                }
            }
        }
        return graph.equals(result);
    }

    public static int countLabels(List<Set<Integer>> labels) {
        return labels.stream().map(Set::size).reduce(0, (x,y) -> x + y);
    }

    public static void printLabels(List<Set<Integer>> labels) {
        StringBuilder res = new StringBuilder();
        res.append("Labels:\n");
        int line = 0;
        for (Set<Integer> vertexLabels : labels) {
            res.append((line++) + ": [");
            for (Integer i : vertexLabels) {
                res.append(i.toString() + ", ");
            }
            res.delete(res.length()-2, res.length());
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
