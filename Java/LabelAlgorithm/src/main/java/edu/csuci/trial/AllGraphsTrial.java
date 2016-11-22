package edu.csuci.trial;

import edu.csuci.Heuristic.DummyMatrixHeuristic;
import edu.csuci.Heuristic.MatrixHeuristic;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.label.MatrixLabeler;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by jhelling on 11/18/16.
 */
public class AllGraphsTrial implements Trial {
    private final String name;
    private final int vertices;
    private final boolean debug;
    private final PrintStream output;
    private final MatrixGraphGenerator graphGenerator;
    private final MatrixHeuristic heuristic;
    private int[][] labelResult;

    public AllGraphsTrial(String name, int vertices, boolean debug, PrintStream output, MatrixGraphGenerator graphGenerator) {
        this.name = name;
        this.vertices = vertices;
        this.debug = debug;
        this.output = output;
        this.graphGenerator = graphGenerator;
        this.heuristic = new DummyMatrixHeuristic();
        this.labelResult = null;
    }

    @Override
    public void runTrial() throws IOException {
        System.out.printf("Labeling all graphs on %d vertices", vertices);
        output.printf("Labeling all graphs on %d vertices\n", vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");

        long total = 0;

        long numGraphs = (long) Math.pow(2, vertices*(vertices - 1)/2);
        for (long i = 0; i < numGraphs; i++) {
            testSetup(i);
            total += testRun();
            testCheck();
        }
        System.out.printf("Total Time: %d ms\n", total);
        System.out.printf("Average Time per Graph: %d ms\n",  total/numGraphs);
    }

    private void testSetup(long bits) {
        long start, end;
        start = System.currentTimeMillis();
        graphGenerator.generateGraphFromUpperRightTriangleBits(bits);
        end = System.currentTimeMillis();
        System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", graphGenerator.getVertices(),
                graphGenerator.getDensity(), graphGenerator.countEdges());
        System.out.printf("Setup: %d ms\n", end - start);
        if (debug) {
            MatrixGraphGenerator.printGraph(graphGenerator.getGraph());
        }
    }

    private long testRun() {
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
        return end - start;
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
