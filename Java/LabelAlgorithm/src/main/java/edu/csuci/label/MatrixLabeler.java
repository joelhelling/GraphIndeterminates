package edu.csuci.label;

import edu.csuci.heuristic.MatrixHeuristic;
import edu.csuci.heuristic.QComparator;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class MatrixLabeler {
    public static int[][] labelGraph(int[][] graph, MatrixHeuristic vHeuristic, QComparator qComparator) {
        int[][] labels = new int[graph.length][graph.length];
        int[][] connections = new int[graph.length][graph.length];

        int[] indexes = new int[graph.length];

        int[] labelCount = new int[graph.length];

        int[] currentClique = new int[graph.length];

        int[] vertexOrder = vHeuristic.runHeuristic(graph);

        List<Integer> qOrder = new ArrayList<>(graph.length);

        int ccIndex = 0;

        int lambda = 1;

        int v, w, q;

        int vvert, wvert, qvert;

        boolean isSubset;
        int qi;

        int cci, ccj;

        for (v = 0; v < vertexOrder.length; v++) {
            vvert = vertexOrder[v];
            if (graph[vvert][vvert] == 0) {
                labels[vvert][indexes[vvert]] = lambda;
                indexes[vvert]++;
                labelCount[vvert]++;
                lambda++;
            } else {
                for (w = v + 1; w < vertexOrder.length; w++) {
                    wvert = vertexOrder[w];
                    if (graph[vvert][wvert] == 0) {
                        continue;
                    }
                    if (connections[vvert][wvert] == 0) {
                        currentClique[ccIndex] = wvert;
                        ccIndex++;

                        if (indexes[vvert] >= labels[vvert].length) {
                            labels[vvert] = Arrays.copyOf(labels[vvert], labels[vvert].length * 2);
                        }
                        labels[vvert][indexes[vvert]] = lambda;
                        indexes[vvert]++;

                        if (indexes[wvert] >= labels[wvert].length) {
                            labels[wvert] = Arrays.copyOf(labels[wvert], labels[wvert].length * 2);
                        }
                        labels[wvert][indexes[wvert]] = lambda;
                        indexes[wvert]++;

                        for (q = 0; q < graph[vvert].length; q++) {
                            if (q == vvert || q == wvert || graph[vvert][q] == 0) {
                                continue;
                            }
                            qOrder.add(q);
                        }
                        qComparator.setLabelCounts(labelCount);
                        qOrder.sort(qComparator);

                        for (q = 0; q < qOrder.size(); q++) {
                            qvert = qOrder.get(q);
                            if (qvert == vvert || qvert == wvert || graph[vvert][qvert] == 0) {
                                continue;
                            }
                            isSubset = true;
                            for (qi = 0; qi < ccIndex; qi++) {
                                if (graph[qvert][currentClique[qi]] == 0) {
                                    isSubset = false;
                                    break;
                                }
                            }
                            if (isSubset) {
                                currentClique[ccIndex] = qvert;
                                ccIndex++;

                                if (indexes[qvert] >= labels[qvert].length) {
                                    labels[qvert] = Arrays.copyOf(labels[qvert], labels[qvert].length * 2);
                                }
                                labels[qvert][indexes[qvert]] = lambda;
                                indexes[qvert]++;
                            }
                        }

                        labelCount[vvert]++;
                        for (cci = 0; cci < ccIndex; cci++) {
                            labelCount[currentClique[cci]]++;
                            connections[vvert][currentClique[cci]] = 1;
                            connections[currentClique[cci]][vvert] = 1;
                            for (ccj = cci + 1; ccj < ccIndex; ccj++) {
                                connections[currentClique[cci]][currentClique[ccj]] = 1;
                                connections[currentClique[ccj]][currentClique[cci]] = 1;
                            }
                            currentClique[cci] = 0;
                        }
                        qOrder.clear();
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
            for (int j = i + 1; j < graph[i].length; j++) {
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

        while (fIndex < fLabels.length && fLabels[fIndex] != 0) {
            while (sIndex < sLabels.length && sLabels[sIndex] != 0 && sLabels[sIndex] <= fLabels[fIndex]) {
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
            int j;
            res.append(i).append(": [");
            for (j = 0; j < labels[i].length && labels[i][j] != 0; j++) {
                res.append(labels[i][j]).append(", ");
            }
            if (j > 0) {
                res.delete(res.length() - 2, res.length());
            }
            res.append("]\n");
        }
        System.out.println(res.toString());
    }
}
