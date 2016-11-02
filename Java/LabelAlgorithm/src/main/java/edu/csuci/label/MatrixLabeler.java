package edu.csuci.label;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class MatrixLabeler {
    public static int[][] labelGraph(int[][] graph) {
        int[][] labels = new int[graph.length][graph.length];
        int[][] connections = new int[graph.length][graph.length];

        int[] indexes = new int[graph.length];

        int[] currentClique = new int[graph.length];

        int[] vertexOrder = runHeuristic(graph, 0);

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
                labels[vvert][indexes[vvert]++] = lambda;
                lambda++;
            } else {
                for (w = v + 1; w < vertexOrder.length; w++) {
                    wvert = vertexOrder[w];
                    if (graph[vvert][wvert] == 0) {
                        continue;
                    }
                    if (connections[vvert][wvert] == 0) {
                        currentClique[ccIndex++] = wvert;
                        if (indexes[vvert] >= labels[vvert].length) {
                            labels[vvert] = Arrays.copyOf(labels[vvert], labels[vvert].length * 2);
                        }
                        labels[vvert][indexes[vvert]++] = lambda;
                        if (indexes[wvert] >= labels[wvert].length) {
                            labels[wvert] = Arrays.copyOf(labels[wvert], labels[wvert].length * 2);
                        }
                        labels[wvert][indexes[wvert]++] = lambda;

                        for (q = w + 1; q < vertexOrder.length; q++) {
                            qvert = vertexOrder[q];
                            if (graph[vvert][qvert] == 0) {
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
                                currentClique[ccIndex++] = qvert;
                                if (indexes[qvert] >= labels[qvert].length) {
                                    labels[qvert] = Arrays.copyOf(labels[qvert], labels[qvert].length * 2);
                                }
                                labels[qvert][indexes[qvert]++] = lambda;
                            }
                        }

                        for (cci = 0; cci < ccIndex; cci++) {
                            connections[vvert][currentClique[cci]] = 1;
                            connections[currentClique[cci]][vvert] = 1;
                            for (ccj = cci + 1; ccj < ccIndex; ccj++) {
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

    private static int[] runHeuristic(int[][] graph, int depth) {
        List<Integer> vertexOrder = new ArrayList<>(graph.length);
        final int[] neighborhoodSizes = new int[graph.length];

        for (int i = 0; i < graph.length; i++) {
            vertexOrder.add(i);
            neighborhoodSizes[i] = countNeighborhood(graph, i, depth);
        }

        Collections.sort(vertexOrder, (o1, o2) -> {
            int res = Integer.compare(neighborhoodSizes[o1], neighborhoodSizes[o2]);
            if (res == 0) {
                return Integer.compare(o1, o2);
            } else {
                return res;
            }
        });

        System.out.println("Order: " + Arrays.toString(vertexOrder.toArray()));
        System.out.println("Neighborhoods: " + Arrays.toString(neighborhoodSizes));

        return vertexOrder.stream().mapToInt(x -> x).toArray();
    }

    private static int countNeighborhood(int[][] graph, int vertex, int depth) {
        Set<Integer> frontier = new HashSet<>();
        Set<Integer> visitedVertices = new HashSet<>();

        frontier.add(vertex);

        int neighborhoodCount = frontier.size();

        for (int i = 0; i < depth; i++) {
            if (frontier.isEmpty()) {
                return neighborhoodCount;
            } else {
                Set<Integer> newFrontier = new HashSet<>();
                visitedVertices.addAll(frontier);
                for (int v : frontier) {
                    for (int j = 0; j < graph[v].length; j++) {
                        if (v != j && graph[v][j] != 0 && !visitedVertices.contains(j)) {
                            newFrontier.add(j);
                        }
                    }
                }
                neighborhoodCount += newFrontier.size();
                frontier = newFrontier;
            }
        }

        return neighborhoodCount;
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
            int j = 0;
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
