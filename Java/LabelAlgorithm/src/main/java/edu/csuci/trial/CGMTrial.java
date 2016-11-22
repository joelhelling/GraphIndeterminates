package edu.csuci.trial;

import edu.csuci.graph.MapGraphGenerator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.export.CoverUtils;
import it.unipi.di.export.ECCc;
import it.unipi.di.interfaces.ListGraph;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jhelling on 11/18/16.
 */
public class CGMTrial extends TwoStageTrial {
    private MapGraphGenerator graphGenerator;
    private ECCc cliqueSolver;

    public CGMTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output,
                    MapGraphGenerator graphGenerator) {
        super(name, warmUp, iterations, debug, output);
        this.graphGenerator = graphGenerator;
    }

    @Override
    protected void testSetup() {
        long start, end;
        start = System.currentTimeMillis();
        Map<Integer, List<Integer>> testData = graphGenerator.generateGraph();
        ListGraph lg = new ListGraph(testData);
        cliqueSolver = new ECCc(lg);
        end = System.currentTimeMillis();
        System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", graphGenerator.getVertices(),
                graphGenerator.getDensity(), graphGenerator.countEdges());
        System.out.printf("Setup: %d ms\n", end - start);
        if (debug) {
            MapGraphGenerator.printGraph(testData);
        }
    }

    @Override
    protected long testRun() {
        long start, end;

        start = System.currentTimeMillis();
        cliqueSolver.start(debug);
        int removed = CoverUtils.minimalize(cliqueSolver.solution());
        end = System.currentTimeMillis();
        List<IntOpenHashSet> cliqueResult = cliqueSolver.solution();
        System.out.printf("Run: %d Cliques; %d ms with %d redundant cliques\n", cliqueResult.size(), end - start,
                removed);
        output.printf("%d\t%d\t%d\n", graphGenerator.countEdges(), cliqueResult.size(), end - start);
        if (debug) {
            cliqueResult.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));
        }
        return end - start;
    }

    @Override
    protected void testCheck() {
        if (debug) {
            long start, end;
            start = System.currentTimeMillis();
            boolean check = cliqueSolver.checkSolution();
            end = System.currentTimeMillis();

            System.out.printf("Check: %b %d ms\n", check, end - start);
        }
    }
}
