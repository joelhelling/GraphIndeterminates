package edu.csuci.trial;

import de.unijena.minet.KellermanKouAlgorithm;
import edu.csuci.graph.MatrixGraphGenerator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.export.CoverUtils;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jhelling on 11/18/16.
 */
public class KKTrial extends TwoStageTrial {
    private MatrixGraphGenerator graphGenerator;

    public KKTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output,
                   MatrixGraphGenerator graphGenerator) {
        super(name, warmUp, iterations, debug, output);
        this.graphGenerator = graphGenerator;
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
    protected long testRun() {
        long start, end;
        start = System.currentTimeMillis();
        List<IntOpenHashSet> cliqueResult = KellermanKouAlgorithm.coverGraph(graphGenerator.getGraph());
        int removed = CoverUtils.minimalize(cliqueResult);
        end = System.currentTimeMillis();
        if (debug) {
            cliqueResult.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));
        }
        System.out.printf("Run: %d Cliques; %d ms with %d redundant cliques\n", cliqueResult.size(), end - start,
                removed);
        output.printf("%d\t%d\t%d\n", graphGenerator.countEdges(), cliqueResult.size(), end - start);
        return end - start;
    }

    @Override
    protected void testCheck() {

    }
}
