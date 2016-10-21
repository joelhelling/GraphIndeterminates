package edu.csuci.label;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class MatrixLabeler {
    public static int[][] labelGraph(int[][] graph) {
        int[][] labels = new int[graph.length][graph.length];
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
        int copyIndex;

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
                        if (indexes[v] >= labels[v].length) {
                            labels[v] = Arrays.copyOf(labels[v], labels[v].length * 2);
                        }
                        labels[v][indexes[v]++] = lambda;
                        if (indexes[w] >= labels[w].length) {
                            labels[w] = Arrays.copyOf(labels[w], labels[w].length * 2);
                        }
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
                                if (indexes[q] >= labels[q].length) {
                                    labels[q] = Arrays.copyOf(labels[q], labels[q].length * 2);
                                }
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

    public static int countLabels(int[][] labels) {
        Set<Integer> count = new HashSet<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length && labels[i][j] != 0; j++) {
                count.add(labels[i][j]);
            }
        }
        return count.size();
    }

    public static void printLabels(int[][] labels) {
        StringBuilder res = new StringBuilder();
        res.append("Labels:\n");

        for (int i = 0; i < labels.length; i++) {
            res.append(i).append(": [");
            for (int j = 0; j < labels[i].length && labels[i][j] != 0; j++) {
                res.append(labels[i][j]).append(", ");
            }
            res.delete(res.length() - 2, res.length());
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
