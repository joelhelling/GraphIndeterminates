package de.unijena.minet;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.ArrayList;
import java.util.List;

public class KellermanKouAlgorithm {

    public static List<IntOpenHashSet> coverGraph(int[][] graph) {
        List<IntOpenHashSet> solution = new ArrayList<>(graph.length);

        List<IntOpenHashSet> graphEdges = new ArrayList<>(graph.length);
        for (int i = 0; i < graph.length; i++) {
            graphEdges.add(new IntOpenHashSet());
            for (int j = 0; j < i; j++) {
                if (graph[i][j] != 0) {
                    graphEdges.get(i).add(j);
                }
            }
        }

        int k = 0;

        for (int i = 0; i < graph.length; i++) {
            IntOpenHashSet W = graphEdges.get(i);
            if (W.isEmpty()) {
                solution.add(k, new IntOpenHashSet());
                solution.get(k).add(i);
                k = k + 1;
            } else {
                IntOpenHashSet U = new IntOpenHashSet();
                for (int l = 0; l < k; l++) {
                    if (W.containsAll(solution.get(l))) {
                        U.addAll(solution.get(l));
                        solution.get(l).add(i);
                        if (U.equals(W)) {
                            break;
                        }
                    }
                }
                W.removeAll(U);
                while (!W.isEmpty()) {
                    solution.add(k, new IntOpenHashSet());
                    int largestSize = -1;
                    int largestCliqueIndex = -1;

                    for (int l = 0; l < k; l++) {
                        IntOpenHashSet intersect = new IntOpenHashSet(solution.get(l));
                        intersect.retainAll(W);
                        if (intersect.size() > largestSize) {
                            largestSize = intersect.size();
                            largestCliqueIndex = l;
                        }
                    }

                    solution.get(k).addAll(solution.get(largestCliqueIndex));
                    solution.get(k).retainAll(W);

                    W.removeAll(solution.get(k));

                    solution.get(k).add(i);

                    k = k + 1;
                }
            }
        }

        return solution;
    }
}
