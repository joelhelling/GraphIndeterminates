package edu.csuci.trial;

import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.heuristic.MatrixHeuristic;
import edu.csuci.heuristic.QComparator;
import edu.csuci.label.MatrixLabeler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.export.CoverUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by jhelling on 11/18/16.
 */
public class MatrixTrial extends TwoStageTrial {
    private final MatrixGraphGenerator graphGenerator;
    private final MatrixHeuristic heuristic;
    private final QComparator qComparator;
    private int[][] labelResult;

    public MatrixTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output,
                       MatrixGraphGenerator graphGenerator, MatrixHeuristic heuristic, QComparator qComparator) {
        super(name, warmUp, iterations, debug, output);
        this.graphGenerator = graphGenerator;
        this.heuristic = heuristic;
        this.qComparator = qComparator;
        this.labelResult = null;
    }

    @Override
    protected void testSetup() {
        long start, end;
        start = System.currentTimeMillis();
        graphGenerator.generateGraph();
        end = System.currentTimeMillis();
        System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", graphGenerator.getVertices(),
                graphGenerator.getDensity(), graphGenerator.countEdges());
        System.out.printf("Setup: %d ms\n", end - start);
        if (debug) {
            MatrixGraphGenerator.printGraph(graphGenerator.getGraph());
        }
    }

    @Override
    protected TrialResult testRun() {
        long start, end;
        start = System.currentTimeMillis();
        labelResult = MatrixLabeler.labelGraph(graphGenerator.getGraph(), heuristic, qComparator);
        end = System.currentTimeMillis();
        int labels = MatrixLabeler.countLabels(labelResult);
        List<IntOpenHashSet> cliques = MatrixLabeler.convertToListOfSets(labels, labelResult);
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        labels = labels - removed;
        TrialResult result = new TrialResult(graphGenerator.countEdges(), labels, end - start);
        System.out.printf("Run: %d Labels; %d ms\n", labels, end - start);
        output.printf("%d\t%d\t%d\n", graphGenerator.countEdges(), labels, end - start);
        if (debug) {
            MatrixLabeler.printLabels(labelResult);
        }
        return result;
    }

    @Override
    protected void testCheck() {
        if (debug) {
            long start, end;
            start = System.currentTimeMillis();
            int[][] check = MatrixLabeler.checkLabeling(labelResult);
            end = System.currentTimeMillis();
            MatrixGraphGenerator.printGraph(check);

            System.out.printf("Check: %b %d ms\n", graphGenerator.graphEquals(check) ? "PASSED" : "FAILED", end - start);
        }
    }
}
