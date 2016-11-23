package edu.csuci.trial;

import edu.csuci.graph.ListGraphGenerator;
import edu.csuci.label.ListLabeler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.io.PrintStream;
import java.util.List;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by jhelling on 11/22/16.
 */
public class ListTrial extends TwoStageTrial {
    private final ListGraphGenerator graphGenerator;
    private List<IntOpenHashSet> labelResult;

    public ListTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output,
                       ListGraphGenerator graphGenerator) {
        super(name, warmUp, iterations, debug, output);
        this.graphGenerator = graphGenerator;
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
            ListGraphGenerator.printGraph(graphGenerator.getGraph());
        }
    }

    @Override
    protected long testRun() {
        long start, end;
        start = System.currentTimeMillis();
        labelResult = ListLabeler.labelGraph(graphGenerator.getGraph());
        end = System.currentTimeMillis();
        int labels = ListLabeler.countLabels(labelResult);
        System.out.printf("Run: %d Labels; %d ms\n", labels, end - start);
        output.printf("%d\t%d\t%d\n", graphGenerator.countEdges(), labels, end - start);
        if (debug) {
            ListLabeler.printLabels(labelResult);
        }
        return end - start;
    }

    @Override
    protected void testCheck() {
        if (debug) {
            long start, end;
            start = System.currentTimeMillis();
            List<IntOpenHashSet> check = ListLabeler.checkLabeling(labelResult);
            end = System.currentTimeMillis();
            ListGraphGenerator.printGraph(check);

            System.out.printf("Check: %b %d ms\n", graphGenerator.graphEquals(check) ? "PASSED" : "FAILED", end - start);
        }
    }
}
