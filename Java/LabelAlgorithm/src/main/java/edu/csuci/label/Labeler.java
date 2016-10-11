package edu.csuci.label;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                        labels.get(v).add(lambda);
                        labels.get(w).add(lambda);
                        // Check through neighbors of v again to add to clique
                        for (Integer q : vNeighbors) {
                            if (!q.equals(w) && graph.get(q).containsAll(currentClique)) {
                                labels.get(q).add(lambda);
                                currentClique.add(q);
                            }
                        }
                        while (!currentClique.isEmpty()) {
                            Integer c = currentClique.iterator().next();
                            currentClique.remove(c);
                            connections[c][v] = 1;
                            connections[v][c] = 1;
                            for (Integer d : currentClique) {
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
    public static List<Set<Integer>> checkLabeling(List<Set<Integer>> labels) {
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
        return result;
    }

    public static int[][] labelGraph(int[][] graph) {
        int[][] labels = new int[graph.length][graph.length*graph.length/4 + 1];
        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length; j++) {
                labels[i][j] = 0;
            }
        }
        int[][] connections = new int[graph.length][graph.length];
        for (int i = 0; i < connections.length; i++) {
            for (int j = 0; j < connections[i].length; j++) {
                connections[i][j] = 0;
            }
        }

        int[] indexes = new int[graph.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = 0;
        }
        int[] currentClique = new int[graph.length];
        for (int i = 0; i < currentClique.length; i++) {
            currentClique[i] = 0;
        }
        int ccIndex = 0;

        int lambda = 1;

        int v;
        int w;
        int q;

        boolean isSubset;
        int qi;

        int cci, ccj;

        for (v = 0; v < graph.length; v++) {
            if (graph[v][v] == 0) {
                labels[v][indexes[v]++] = lambda;
                lambda++;
            } else {
                for (w = v+1; w < graph.length; w++) {
                    if (graph[v][w] == 0) {
                        continue;
                    }
                    if (connections[v][w] == 0) {
                        currentClique[ccIndex++] = w;
                        labels[v][indexes[v]++] = lambda;
                        labels[w][indexes[w]++] = lambda;

                        for (q = w+1; q < graph.length; q++) {
                            if (graph[v][q] == 0) {
                                continue;
                            }
                            isSubset = true;
                            for (qi = 0; qi < ccIndex; qi++) {
                                if (graph[q][currentClique[qi]] == 0) {
                                    isSubset = false;
                                    break;
                                }
                            }
                            if (isSubset) {
                                currentClique[ccIndex++] = q;
                                labels[q][indexes[q]++] = lambda;
                            }
                        }

                        for (cci = 0; cci < ccIndex; cci++) {
                            connections[v][currentClique[cci]] = 1;
                            connections[currentClique[cci]][v] = 1;
                            for (ccj = cci+1; ccj < ccIndex; ccj++) {
                                connections[currentClique[cci]][currentClique[ccj]] = 1;
                                connections[currentClique[ccj]][currentClique[cci]] = 1;
                            }
                            currentClique[cci] = 0;
                        }
                        ccIndex = 0;
                        lambda++;
                    }
                }
            }
        }
        return labels;
    }

    public static int[][] checkLabeling(int[][] labels) {
        int[][] graph = new int[labels.length][labels.length];

        for (int i = 0; i < graph.length; i++) {
            for (int j = i+1; j < graph[i].length; j++) {
                if (hasIntersection(labels[i], labels[j])) {
                    graph[i][i] = 1;
                    graph[i][j] = 1;
                    graph[j][i] = 1;
                } else {
                    graph[i][j] = 0;
                    graph[j][i] = 0;
                }
            }
        }
        return graph;
    }

    private static boolean hasIntersection(int[] fLabels, int[] sLabels) {
        int fIndex = 0;
        int sIndex = 0;

        while(fIndex < fLabels.length && fLabels[fIndex] != 0) {
            while(sIndex < sLabels.length && sLabels[sIndex] != 0 && sLabels[sIndex] <= fLabels[fIndex]) {
                if (sLabels[sIndex] == fLabels[fIndex]) {
                    return true;
                }
                sIndex++;
            }
            fIndex++;
        }
        return false;
    }

    public static int countLabels(List<Set<Integer>> labels) {
        return labels.stream().flatMap(Set::stream).collect(Collectors.toSet()).size();
    }

    public static int countLabels(int[][] labels) {
        Set<Integer> count = new HashSet<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length && labels[i][j] != 0; j++) {
                count.add(labels[i][j]);
            }
        }
        return count.size();
    }

    public static void printLabels(List<Set<Integer>> labels) {
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

    public static void printLabels(int[][] labels) {
        StringBuilder res = new StringBuilder();
        res.append("Labels:\n");

        for (int i = 0; i < labels.length; i++) {
            res.append(i).append(": [");
            for (int j = 0; j < labels.length && labels[i][j] != 0; j++) {
                res.append(labels[i][j]).append(", ");
            }
            res.delete(res.length() - 2, res.length());
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
