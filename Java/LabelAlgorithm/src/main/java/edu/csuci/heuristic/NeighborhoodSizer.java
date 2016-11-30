package edu.csuci.heuristic;

/**
 * Created by jhelling on 11/29/16.
 */
public class NeighborhoodSizer {
    private static int[] neighborhoodSizes;

    public static int[] calcNeighborhoodSizes(int[][] graph, int depth) {
        if (neighborhoodSizes != null) {
            return neighborhoodSizes;
        }
        neighborhoodSizes = new int[graph.length];

        for (int i = 0; i < graph.length; i++) {
            neighborhoodSizes[i] = countNeighborhood(graph, i, depth);
        }
        return neighborhoodSizes;
    }

    private static int countNeighborhood(int[][] graph, int vertex, int depth) {
        int[] frontier = new int[graph.length];

        frontier[vertex] = 1;

        int neighborhoodCount = 1;
        boolean isEmpty = false;

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            if (isEmpty) {
                break;
            } else {
                isEmpty = true;
                for (int j = 0; j < frontier.length; j++) {
                    if (frontier[j] == currentDepth) {
                        for (int k = 0; k < graph[j].length; k++) {
                            if (k != j && graph[j][k] != 0 && frontier[k] == 0) {
                                isEmpty = false;
                                neighborhoodCount += 1;
                                frontier[k] = currentDepth + 1;
                            }
                        }
                    }
                }
            }
        }
        return neighborhoodCount;
    }
}
