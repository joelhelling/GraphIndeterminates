package edu.csuci.trial;

import edu.csuci.Heuristic.MatrixHeuristic;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.label.MatrixLabeler;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;

/**
 * Created by jhelling on 11/18/16.
 */
public class AscendingTrial extends AbstractTrial {
    private MatrixGraphGenerator graphGenerator;
    private MatrixHeuristic heuristic;
    private final int vertices;
    private int[][] labelResult;

    public AscendingTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output,
                          MatrixGraphGenerator graphGenerator, MatrixHeuristic heuristic, int vertices) {
        super(name, warmUp, iterations, debug, output);
        this.graphGenerator = graphGenerator;
        this.heuristic = heuristic;
        this.vertices = vertices;
    }

    @Override
    public void runTrial() throws IOException {
        System.out.printf("Trial %s: %d to %d vertices\n", name, 1, vertices);
        output.printf("Trial %s: %d to %d vertices\n", name, 1, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");

        BigInteger total = BigInteger.ZERO;

        for (int i = 1; i <= vertices; i++) {
            graphGenerator.setVertices(i);
            runWarmup();
            total = total.add(BigInteger.valueOf(runBenchmark()));
        }

        System.out.printf("Grand Total Benchmark Time: %s ms\n", total.toString());
        System.out.printf("Average Benchmark Time per Graph: %s ms\n", total.divide(BigInteger.valueOf(iterations*vertices)).toString());
    }

    private long runWarmup() {
        return run("Warm-up", warmUp);
    }

    private long runBenchmark() {
        return run("Benchmark", iterations);
    }

    private long run(String trialType, int runs) {
        System.out.printf("Trial %s: %s\n", name, trialType);
        output.printf("Trial %s: %s\n", name, trialType);
        long total;

        total = 0;

        for (int i = 0; i < runs; i++) {
            testSetup();
            total += testRun();
            testCheck();
        }
        System.out.printf("Total %s Time: %d ms\n", trialType, total);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialType, total/runs);

        return total;
    }

    private void testSetup() {
        long start, end;

        graphGenerator.randomizeDensity();
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
