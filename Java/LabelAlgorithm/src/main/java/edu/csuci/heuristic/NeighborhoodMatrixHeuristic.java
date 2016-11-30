package edu.csuci.heuristic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 11/09/16.
 */
public class NeighborhoodMatrixHeuristic implements MatrixHeuristic {
    private final NeighborhoodComparator neighborhoodComparator;
    private final int depth;

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

        for (int i = 0; i < graph.length; i++) {
            vertexOrder.add(i);
        }

        neighborhoodComparator.setNeighborhoodSizes(NeighborhoodSizer.calcNeighborhoodSizes(graph, depth));

        vertexOrder.sort(neighborhoodComparator);
        return vertexOrder.stream().mapToInt(x -> x).toArray();
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
