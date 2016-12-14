package edu.csuci.graphresults;

import de.unijena.minet.KellermanKouAlgorithm;
import edu.csuci.graph.MapGraphGenerator;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.heuristic.AscendingLabelCountQComparator;
import edu.csuci.heuristic.MatrixHeuristic;
import edu.csuci.heuristic.NeighborhoodMatrixHeuristic;
import edu.csuci.heuristic.QComparator;
import edu.csuci.label.MatrixLabeler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.export.CoverUtils;
import it.unipi.di.export.ECCc;
import it.unipi.di.interfaces.ListGraph;
import org.junit.Test;

import java.util.*;

/**
 * Created by jhelling on 12/12/16.
 */
public class TriangleOnlyGraphs {
    @Test
    public void hrssOnTriangleOnlyGraph() {
        int[][] graph = generateTriangleOnlyMatrixGraph(1000);
        MatrixGraphGenerator.printGraph(graph);

        MatrixHeuristic mh = NeighborhoodMatrixHeuristic.ascendingNeighborhood(1);
        QComparator qComparator = new AscendingLabelCountQComparator();
        int[][] labels = MatrixLabeler.labelGraph(graph, mh, qComparator);

        int numberOfLabels = MatrixLabeler.countLabels(labels);
        System.out.println(numberOfLabels);

        List<IntOpenHashSet> cliques = MatrixLabeler.convertToListOfSets(numberOfLabels, labels);
        //cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        System.out.println("Labels " + (numberOfLabels - removed));

        //cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

    }

    @Test
    public void kkOnTriangleOnlyGraph() {
        int[][] graph = generateTriangleOnlyMatrixGraph(6);
        MatrixGraphGenerator.printGraph(graph);

        List<IntOpenHashSet> cliques = KellermanKouAlgorithm.coverGraph(graph);
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());
    }
    @Test

    public void cgmOnTriangleOnlyGraph() {
        Map<Integer, List<Integer>> graph = generateTriangleOnlyMapGraph(1000);
        MapGraphGenerator.printGraph(graph);

        ListGraph lg = new ListGraph(graph);
        ECCc cliqueSolver = new ECCc(lg);
        cliqueSolver.start(false);
        List<IntOpenHashSet> cliques = cliqueSolver.solution();
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());

    }

    private int[][] generateTriangleOnlyMatrixGraph(int vertices) {
        int[][] result = new int[vertices][vertices];

        for (int i = 0; i < vertices/3; i++) {
            for (int j = vertices/3; j < 2*vertices/3; j++) {
                result[i][j] = 1;
                result[j][i] = 1;
                result[i][i] = 1;
                result[j][j] = 1;
            }
            for (int j = 2*vertices/3; j < vertices; j++) {
                result[i][j] = 1;
                result[j][i] = 1;
                result[i][i] = 1;
                result[j][j] = 1;
            }
        }

        for (int i = vertices/3; i < 2*vertices/3; i++) {
            for (int j = 2*vertices/3; j < vertices; j++) {
                result[i][j] = 1;
                result[j][i] = 1;
                result[i][i] = 1;
                result[j][j] = 1;
            }
        }

        return result;
    }

    private Map<Integer, List<Integer>> generateTriangleOnlyMapGraph(int vertices) {
        Map<Integer, List<Integer>> result = new HashMap<>(vertices);
        for (int i = 0; i < vertices; i++) {
            result.put(i, new ArrayList<>(vertices/2 + 1));
        }

        for (int i = 0; i < vertices/3; i++) {
            for (int j = vertices/3; j < 2*vertices/3; j++) {
                result.get(i).add(j);
                result.get(j).add(i);
            }
            for (int j = 2*vertices/3; j < vertices; j++) {
                result.get(i).add(j);
                result.get(j).add(i);
            }
        }

        for (int i = vertices/3; i < 2*vertices/3; i++) {
            for (int j = 2*vertices/3; j < vertices; j++) {
                result.get(i).add(j);
                result.get(j).add(i);
            }
        }

        return result;
    }

    private void printGraph(int[][] graph) {
        System.out.println(graph.length + ":");
        for (int i = 0; i < graph.length; i++) {
            System.out.println(Arrays.toString(graph[i]));
        }
        System.out.println();
    }

    private void printGraph(Map<Integer, List<Integer>> graph) {
        graph.forEach((k, v) -> System.out.println(String.format("%d: %s", k, v.toString())));
    }
}
