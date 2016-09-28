package edu.csuci.thesislabel;

import java.util.*;

/**
 * Created by jhelling on 9/28/16.
 */
public class Labeler {
    public static Map<Integer, List<Integer>> labelGraph(Map<Integer, Set<Integer>> graph) {
        // Preallocate the lists for holding the labeling alphabet
        Map<Integer, List<Integer>> labels = new HashMap<>();
        graph.forEach((k, v) -> labels.put(k, new ArrayList<>(graph.size() * graph.size() / 4)));

        // Initialize graph connections for shared symbols
        int[][] connections = new int[graph.size() * graph.size()][graph.size() * graph.size()];
        for (int i = 0; i < connections.length; i++) {
            connections[i][i] = 1;
        }

        Integer lambda = 0;
        Set<Integer> currentClique = new HashSet<>();

        for (int v = 0; v < graph.size(); v++) {
            Set<Integer> v_value = graph.get(v);
            if (v_value.isEmpty()) {
                labels.get(v).add(lambda);
                lambda += 1;
            } else {
                Set<Integer> v_neighbors = graph.get(v_value);
                for (int w = 0; w < v_neighbors.size(); w++) {
                    Set<Integer> w_neighbors = graph.get(w);
                    if (connections[v][w] == 0) {
                        graph.get(v).add(lambda);
                        graph.get(w).add(lambda);
                        currentClique.add(w);
                        for (int q = 0; q < w_neighbors.size(); q++) {
                            if (q != w
                                    && graph.get(q).containsAll(currentClique)) {
                                currentClique.add(q);
                                labels.get(q).add(lambda);
                            }
                        }
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
