package edu.csuci.graphresults;

import de.unijena.minet.KellermanKouAlgorithm;
import edu.csuci.graph.MapGraphGenerator;
import edu.csuci.heuristic.DescendinglabelCountQComparator;
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
public class TriangleFreeGraphs {
    @Test
    public void hrssOnTriangleFreeGraph() {
        int[][] graph = generateTriangleFreeMatrixGraph(5);
        printGraph(graph);

        MatrixHeuristic mh = NeighborhoodMatrixHeuristic.descendingNeighborhood(1);
        QComparator qComparator = new DescendinglabelCountQComparator();
        int[][] labels = MatrixLabeler.labelGraph(graph, mh, qComparator);

        MatrixLabeler.printLabels(labels);

        int numberOfLabels = MatrixLabeler.countLabels(labels);
        System.out.println(numberOfLabels);

        List<IntOpenHashSet> cliques = MatrixLabeler.convertToListOfSets(numberOfLabels, labels);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

    }

    @Test
    public void kkOnTriangleFreeGraph() {
        int[][] graph = generateTriangleFreeMatrixGraph(1000);
        //printGraph(graph);

        List<IntOpenHashSet> cliques = KellermanKouAlgorithm.coverGraph(graph);
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());
    }

    @Test
    public void cgmOnTriangleFreeGraph() {
        Map<Integer, List<Integer>> graph = generateTriangleFreeMapGraph(5);
        printGraph(graph);

        ListGraph lg = new ListGraph(graph);
        ECCc cliqueSolver = new ECCc(lg);
        cliqueSolver.start(false);
        List<IntOpenHashSet> cliques = cliqueSolver.solution();
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());
    }

    @Test
    public void cgmOnCompleteGraph() {
        MapGraphGenerator mg = new MapGraphGenerator(1000, 1.0);
        Map<Integer, List<Integer>> graph = mg.generateGraph();
        ListGraph lg = new ListGraph(graph);
        ECCc cliqueSolver = new ECCc(lg);
        cliqueSolver.start(false);
        List<IntOpenHashSet> cliques = cliqueSolver.solution();
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());
    }

    private int[][] generateTriangleFreeMatrixGraph(int vertices) {
        int[][] result = new int[vertices][vertices];

        for (int i = 0; i < vertices/2; i++) {
            for (int j = vertices/2; j < vertices; j++) {
                result[i][j] = 1;
                result[j][i] = 1;
                result[i][i] = 1;
                result[j][j] = 1;
            }
        }

        return result;
    }

    private Map<Integer, List<Integer>> generateTriangleFreeMapGraph(int vertices) {
        Map<Integer, List<Integer>> result = new HashMap<>(vertices);
        for (int i = 0; i < vertices; i++) {
            result.put(i, new ArrayList<>(vertices/2 + 1));
        }

        for (int i = 0; i < vertices/2; i++) {
            for (int j = vertices/2; j < vertices; j++) {
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
