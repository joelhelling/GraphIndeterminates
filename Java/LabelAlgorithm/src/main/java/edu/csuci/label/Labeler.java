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
}
