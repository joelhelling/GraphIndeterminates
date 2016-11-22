package edu.csuci.trial;

import edu.csuci.Heuristic.MatrixHeuristic;
import edu.csuci.Heuristic.ShuffleMatrixHeuristic;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.label.MatrixLabeler;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by jhelling on 11/18/16.
 */
public class ShuffleTrial extends AbstractTrial {
    private MatrixGraphGenerator graphGenerator;
    private MatrixHeuristic heuristic = new ShuffleMatrixHeuristic();
    private int[][] labelResult;

    public ShuffleTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output, MatrixGraphGenerator graphGenerator) {
        super(name, warmUp, iterations, debug, output);
        this.graphGenerator = graphGenerator;
        this.labelResult = null;
    }

    @Override
    public void runTrial() throws IOException {
        testSetup();
        runWarmup();
        runBenchmark();
    }

    private void testSetup() {
        System.out.printf("Trial %s: %d vertices and %d iterations\n", name, graphGenerator.getVertices(), iterations);
        output.printf("Trial %s: %d vertices and %d iterations\n", name, graphGenerator.getVertices(), iterations);
        output.println("Edges -- Labels -- Time (milliseconds)");

        long start, end;

        start = System.currentTimeMillis();
        int[][] testData = graphGenerator.generateGraph();
        end = System.currentTimeMillis();
        System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", graphGenerator.getVertices(),
                graphGenerator.getDensity(), graphGenerator.countEdges());
        System.out.printf("Setup: %d ms\n", end - start);
        if (debug) {
            MatrixGraphGenerator.printGraph(testData);
        }
    }

    private void runWarmup() {
        run("Warm-up", warmUp);
    }

    private void runBenchmark() {
        run("Benchmark", iterations);
    }

    private void run(String trialType, int runs) {
        System.out.printf("Trial %s: %s\n", name, trialType);
        output.printf("Trial %s: %s\n", name, trialType);

        int smallestNumberLabels = Integer.MAX_VALUE;

        for (int i = 0; i < runs; i++) {
            int numLabels = testRun();

            if (numLabels < smallestNumberLabels) {
                smallestNumberLabels = numLabels;
            }

            testCheck();
        }

        System.out.printf("Fewest number of labels: %d labels\n", smallestNumberLabels);
        output.printf("Fewest number of labels: %d labels\n", smallestNumberLabels);
    }

    private int testRun() {
        long start, end;

        start = System.currentTimeMillis();
        labelResult = MatrixLabeler.labelGraph(graphGenerator.getGraph(), heuristic);
        end = System.currentTimeMillis();
        int labels = MatrixLabeler.countLabels(labelResult);
        System.out.printf("Run: %d Labels; %d ms\n", labels, end - start);
        output.printf("%d\t%d\t%d\n", graphGenerator.countEdges(), labels, end - start);
        if (debug) {
            MatrixLabeler.printLabels(labelResult);
        }

        return labels;
    }

    private void testCheck() {
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
