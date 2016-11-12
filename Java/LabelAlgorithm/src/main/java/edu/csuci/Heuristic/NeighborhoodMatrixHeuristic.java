package edu.csuci.Heuristic;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 11/09/16.
 */
public class NeighborhoodMatrixHeuristic implements MatrixHeuristic {
    private NeighborhoodComparator neighborhoodComparator;
    private int depth;

    private NeighborhoodMatrixHeuristic(NeighborhoodComparator neighborhoodComparator, int depth) {
        this.neighborhoodComparator = neighborhoodComparator;
        this.depth = depth;
    }

    public static NeighborhoodMatrixHeuristic ascendingNeighborhood(int depth) {
        return new NeighborhoodMatrixHeuristic(new NeighborhoodAscendingComparator(), depth);
    }

    public static NeighborhoodMatrixHeuristic descendingNeighborhood(int depth) {
        return new NeighborhoodMatrixHeuristic(new NeighborhoodDescendingComparator(), depth);
    }

    @Override
    public int[] runHeuristic(int[][] graph) {
        List<Integer> vertexOrder = new ArrayList<>(graph.length);
        final int[] neighborhoodSizes = new int[graph.length];

        for (int i = 0; i < graph.length; i++) {
            vertexOrder.add(i);
            neighborhoodSizes[i] = countNeighborhood(graph, i, depth);
        }

        neighborhoodComparator.setNeighborhoodSizes(neighborhoodSizes);

        System.out.println("Original Order: " + Arrays.toString(vertexOrder.toArray()));
        System.out.println("Neighborhoods: " + Arrays.toString(neighborhoodSizes));
        Collections.sort(vertexOrder, neighborhoodComparator);
        System.out.println("New Order: " + Arrays.toString(vertexOrder.toArray()));

        return vertexOrder.stream().mapToInt(x -> x).toArray();
    }

    private int countNeighborhood(int[][] graph, int vertex, int depth) {
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


    private static abstract class NeighborhoodComparator implements Comparator<Integer> {
        protected int[] neighborhoodSizes;

        public void setNeighborhoodSizes(int[] neighborhoodSizes) {
            this.neighborhoodSizes = neighborhoodSizes;
        }

        @Override
        public abstract int compare(Integer o1, Integer o2);
    }

    private static class NeighborhoodAscendingComparator extends NeighborhoodComparator {
        @Override
        public int compare(Integer o1, Integer o2) {
            int res = Integer.compare(neighborhoodSizes[o1], neighborhoodSizes[o2]);
            if (res == 0) {
                return Integer.compare(o1, o2);
            } else {
                return res;
            }
        }
    }

    private static class NeighborhoodDescendingComparator extends NeighborhoodComparator {
        @Override
        public int compare(Integer o1, Integer o2) {
            int res = Integer.compare(neighborhoodSizes[o2], neighborhoodSizes[o1]);
            if (res == 0) {
                return Integer.compare(o2,o1);
            } else {
                return res;
            }
        }
    }
}
