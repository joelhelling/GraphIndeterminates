package edu.csuci.runner;

import de.unijena.minet.KellermanKouAlgorithm;
import edu.csuci.Heuristic.DummyMatrixHeuristic;
import edu.csuci.Heuristic.MatrixHeuristic;
import edu.csuci.Heuristic.NeighborhoodMatrixHeuristic;
import edu.csuci.Heuristic.ShuffleMatrixHeuristic;
import edu.csuci.graph.ListGraphGenerator;
import edu.csuci.graph.MapGraphGenerator;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.label.ListLabeler;
import edu.csuci.label.MatrixLabeler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.export.CoverUtils;
import it.unipi.di.export.ECCc;
import it.unipi.di.interfaces.ListGraph;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/29/16.
 */
public class RunLabeler {
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();

        /* Trials Available */
        OptionSpec<Void> alln = parser.accepts("alln", "Runs the matrix labeling algorithm on all possible graphs on given number of vertices");
        OptionSpec<Void> nvertices = parser.accepts("nvertices", "Runs the list labeling algorithm on random graphs with specified vertices");
        OptionSpec<Void> fastVertices = parser.accepts("fnvertices", "Runs the matrix labeling algorithm on random graphs with specified vertices");
        OptionSpec<Void> shuffleOrder = parser.accepts("shuffle", "Runs the matrix labeling algorithm on a random graph with different absolute ordering of the vertices and find the least number of symbols");
        OptionSpec<Void> ascending = parser.accepts("ascending", "Runs the matrix labeling algorithm on random graphs ");
        OptionSpec<Void> cgm = parser.accepts("cgm", "Run the edge clique covering algorithm found in Clique Covering of Large Real-World Networks");
        OptionSpec<Void> kellermankou = parser.accepts("kk", "Run the edge clique covering algorithm described in Covering Edges by Cliques with Regard to Keyword Conflicts and Intersection Graphs");

        OptionSpec<Integer> ascHeuristic = parser.accepts("heurasc", "Runs the labeling algorithm with the vertices ordered by ascending neighborhood size with a specified depth")
                .availableIf(fastVertices).withRequiredArg().ofType(Integer.class).defaultsTo(0);
        OptionSpec<Integer> descHeuristic = parser.accepts("heurdesc", "Runs the labeling algorithm with the vertices ordered by descending neighborhood size with a specified depth")
                .availableIf(fastVertices).withRequiredArg().ofType(Integer.class).defaultsTo(0);
        OptionSpec<Void> shuffleHeuristic = parser.accepts("heurshuffle", "Runs the labeling algorithm with the vertices ordered randomly")
                .availableIf(fastVertices).availableIf(shuffleOrder);

        OptionSpec<Void> debug = parser.accepts("debug", "If specified, runs checking algorithm and prints out results");

        OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("help","h"), "Help").forHelp();
        OptionSpec<Integer> warmUp = parser.accepts("warmup", "The number of warm-up runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).defaultsTo(20);
        OptionSpec<Integer> iterations = parser.accepts("iterations", "The number of benchmark runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).defaultsTo(100);
        OptionSpec<Integer> vertices = parser.accepts("vertices", "The number of vertices in the generated graphs")
                .withRequiredArg().ofType(Integer.class).defaultsTo(100);
        OptionSpec<Double> density = parser.accepts("density", "Between 0.0 and 1.0. Probability of having an edge between two vertices")
                .withRequiredArg().ofType(Double.class).defaultsTo(0.5);
        OptionSpec<String> outFile = parser.accepts("output", "The file to output data to")
                .withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        PrintStream output;
        if (options.has(outFile)) {
            try {
                output = new PrintStream(outFile.value(options));
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe);
                System.out.println("Using System.out instead...");
                output = System.out;
            }
        } else {
            output = System.out;
        }

        MatrixHeuristic mh;
        if (options.has(descHeuristic)) {
            mh = NeighborhoodMatrixHeuristic.descendingNeighborhood(descHeuristic.value(options));
        } else if (options.has(ascHeuristic)){
            mh = NeighborhoodMatrixHeuristic.ascendingNeighborhood(ascHeuristic.value(options));
        } else if (options.has(shuffleHeuristic)) {
            mh = new ShuffleMatrixHeuristic();
        } else {
            mh = new DummyMatrixHeuristic();
        }

        if (options.has(help)) {
            parser.printHelpOn(System.out);
        } else if (options.has(alln)) {
            runAllGraphsNVertices(vertices.value(options), mh, options.has(debug), output);
        } else if (options.has(nvertices)) {
            ListGraphGenerator lgg = new ListGraphGenerator(vertices.value(options), density.value(options));
            RunLabeler.runTrialsNVertices("Warm-up", warmUp.value(options), lgg, options.has(debug), output);
            RunLabeler.runTrialsNVertices("Benchmark", iterations.value(options), lgg, options.has(debug), output);
        } else if (options.has(fastVertices)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            RunLabeler.runFastTrialsNVertices("Warm-up", warmUp.value(options), mgg, mh, options.has(debug), output);
            RunLabeler.runFastTrialsNVertices("Benchmark", iterations.value(options), mgg, mh, options.has(debug), output);
        } else if (options.has(shuffleOrder)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            RunLabeler.sampleRandomOrder(mgg, mh, options.has(debug), output);
        } else if (options.has(ascending)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            RunLabeler.runAscendingVertices(warmUp.value(options), iterations.value(options), vertices.value(options), mgg, mh, options.has(debug), output);
        } else if (options.has(cgm)) {
            MapGraphGenerator mgg = new MapGraphGenerator(vertices.value(options), density.value(options));
            RunLabeler.runCGMVertices("Warm-up", warmUp.value(options), mgg, options.has(debug), output);
            RunLabeler.runCGMVertices("Benchmark", iterations.value(options), mgg, options.has(debug), output);
        } else if (options.has(kellermankou)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            RunLabeler.runKKVertices("Warm-up", warmUp.value(options), mgg, options.has(debug), output);
            RunLabeler.runKKVertices("Benchmark", iterations.value(options), mgg, options.has(debug), output);
        } else {
            parser.printHelpOn(System.out);
        }
        output.close();
    }

    private static void runAllGraphsNVertices(int vertices, MatrixHeuristic mh, boolean debug, PrintStream output) throws FileNotFoundException {
        long start, end, total;
        MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices, 0);

        System.out.printf("Labeling all graphs on %d vertices", vertices);
        output.printf("Labeling all graphs on %d vertices\n", vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");

        total = 0;

        long numGraphs = (long) Math.pow(2, vertices*(vertices - 1)/2);
        for (long i = 0; i < numGraphs; i++) {
            start = System.currentTimeMillis();
            int[][] testData = mgg.generateGraphFromUpperRightTriangleBits(i);
            end = System.currentTimeMillis();
            int edges = mgg.countEdges();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Edges: %d\n", vertices, edges);
            if (debug) {
                MatrixGraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            int[][] result = MatrixLabeler.labelGraph(testData, mh);
            end = System.currentTimeMillis();
            total += end - start;
            int labels = MatrixLabeler.countLabels(result);
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, labels, end - start);
            output.printf("%d\t%d\t%d\n", edges, labels, end - start);
            if (debug) {
                MatrixLabeler.printLabels(result);
            }

            if (debug) {
                start = System.currentTimeMillis();
                int[][] check = MatrixLabeler.checkLabeling(result);
                end = System.currentTimeMillis();
                MatrixGraphGenerator.printGraph(check);

                System.out.printf("Check %d: %b %d ms\n", i + 1, mgg.graphEquals(check) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total Time: %d ms\n", total);
        System.out.printf("Average Time per Graph: %d ms\n",  total/numGraphs);
    }

    private static void runTrialsNVertices(String trialName, int iterations, ListGraphGenerator lgg, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial: %s with %d vertices\n", trialName, lgg.getVertices());
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            List<Set<Integer>> testData = lgg.generateGraph();
            end = System.currentTimeMillis();
            int edges = lgg.countEdges();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", lgg.getVertices(), lgg.getDensity(), edges);
            if (debug) {
                ListGraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            List<Set<Integer>> result = ListLabeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            int labels = ListLabeler.countLabels(result);
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, labels, end - start);
            output.printf("%d\t%d\t%d\n", edges, labels, end - start);
            if (debug) {
                ListLabeler.printLabels(result);
            }

            if (debug) {
                start = System.currentTimeMillis();
                List<Set<Integer>> check = ListLabeler.checkLabeling(result);
                ListGraphGenerator.printGraph(check);
                end = System.currentTimeMillis();
                System.out.printf("Check %d: %b %d ms\n", i + 1, lgg.graphEquals(check) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void runFastTrialsNVertices(String trialName, int iterations, MatrixGraphGenerator mgg, MatrixHeuristic mh, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial HRSS: %s with %d vertices\n", trialName, mgg.getVertices());
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            int[][] testData = mgg.generateGraph();
            end = System.currentTimeMillis();
            int edges = mgg.countEdges();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", mgg.getVertices(), mgg.getDensity(), edges);
            if (debug) {
                MatrixGraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            int[][] result = MatrixLabeler.labelGraph(testData, mh);
            end = System.currentTimeMillis();
            totalTime += end - start;
            int labels = MatrixLabeler.countLabels(result);
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, labels, end - start);
            output.printf("%d\t%d\t%d\n", edges, labels, end - start);
            if (debug) {
                MatrixLabeler.printLabels(result);
            }

            if (debug) {
                start = System.currentTimeMillis();
                int[][] check = MatrixLabeler.checkLabeling(result);
                end = System.currentTimeMillis();
                MatrixGraphGenerator.printGraph(check);

                System.out.printf("Check %d: %b %d ms\n", i + 1, mgg.graphEquals(check) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void sampleRandomOrder(MatrixGraphGenerator mgg, MatrixHeuristic mh, boolean debug, PrintStream output) {
        long total, start, end;
        int iterations = (int) Math.sqrt(mgg.getVertices());

        output.printf("Trial HRSS shuffle with %d vertices and %d iterations\n", mgg.getVertices(), iterations);
        output.println("Edges -- Labels -- Time (milliseconds)");

        start = System.currentTimeMillis();
        int[][] testData = mgg.generateGraph();
        end = System.currentTimeMillis();
        int edges = mgg.countEdges();
        System.out.printf("Setup: %d ms\n", end - start);
        System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", mgg.getVertices(), mgg.getDensity(), edges);
        if (debug) {
            MatrixGraphGenerator.printGraph(testData);
        }

        int smallestNumLabels = Integer.MAX_VALUE;
        total = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            int[][] result = MatrixLabeler.labelGraph(testData, mh);
            end = System.currentTimeMillis();
            total += end - start;
            int labels = MatrixLabeler.countLabels(result);
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, labels, end - start);
            output.printf("%d\t%d\t%d\n", edges, labels, end - start);

            if (labels < smallestNumLabels) {
                smallestNumLabels = labels;
            }
            if (debug) {
                MatrixLabeler.printLabels(result);
            }

            if (debug) {
                start = System.currentTimeMillis();
                int[][] check = MatrixLabeler.checkLabeling(result);
                end = System.currentTimeMillis();
                MatrixGraphGenerator.printGraph(check);

                System.out.printf("Check %d: %b %d ms\n", i + 1, mgg.graphEquals(check) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total Time: %d ms\n", total);
        System.out.printf("Average Time per Graph: %d ms\n", total/iterations);
        System.out.printf("Fewest number of labels: %d labels\n", smallestNumLabels);
        output.printf("Fewest number of labels: %d labels\n", smallestNumLabels);
    }

    private static void runAscendingVertices(int warmups, int iterations, int vertices, MatrixGraphGenerator mgg, MatrixHeuristic mh, boolean debug, PrintStream output) throws IOException {
        System.out.printf("Starting Trial HRSS: %d to %d vertices\n", 1, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        long start, end, total, grandTotal;

        grandTotal = 0;

        for (int i = 1; i <= vertices; i++) {
            System.out.printf("=== Starting Warm-up Phase for %d vertices ===\n", i);
            output.printf("Trial: Warm-up with %d vertices\n", i);

            total = 0;

            mgg.setVertices(i);

            for (int j = 0; j < warmups; j++) {
                mgg.randomizeDensity();
                start = System.currentTimeMillis();
                int[][] testData = mgg.generateGraph();
                end = System.currentTimeMillis();
                int edges = mgg.countEdges();
                System.out.printf("Setup %d: %d ms\n", j+1, end - start);
                System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", i, mgg.getDensity(), edges);
                if (debug) {
                    MatrixGraphGenerator.printGraph(testData);
                }

                start = System.currentTimeMillis();
                int[][] result = MatrixLabeler.labelGraph(testData, mh);
                end = System.currentTimeMillis();
                total += end - start;
                int labels = MatrixLabeler.countLabels(result);
                System.out.printf("Run %d: %d Labels; %d ms\n", j+1, labels, end - start);
                output.printf("%d\t%d\t%d\n", edges, labels, end - start);
                if (debug) {
                    MatrixLabeler.printLabels(result);
                }

                if (debug) {
                    start = System.currentTimeMillis();
                    int[][] check = MatrixLabeler.checkLabeling(result);
                    end = System.currentTimeMillis();
                    MatrixGraphGenerator.printGraph(check);

                    System.out.printf("Check %d: %b %d ms\n", j+1, mgg.graphEquals(check) ? "PASSED" : "FAILED", end - start);
                }
            }
            System.out.printf("Total Warm-up Time: %d ms\n", total);
            System.out.printf("Average Warm-up Time per Graph: %d ms\n", total/warmups);
            System.out.println("=== Ending Warm-up Phase ===");

            total = 0;

            output.printf("Trial: Benchmark with %d vertices\n", i);
            for (int j = 0; j < iterations; j++) {
                mgg.randomizeDensity();
                start = System.currentTimeMillis();
                int[][] testData = mgg.generateGraph();
                end = System.currentTimeMillis();
                int edges = mgg.countEdges();
                System.out.printf("Setup %d: %d ms\n", j+1, end - start);
                System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", i, mgg.getDensity(), edges);
                if (debug) {
                    MatrixGraphGenerator.printGraph(testData);
                }

                start = System.currentTimeMillis();
                int[][] result = MatrixLabeler.labelGraph(testData, mh);
                end = System.currentTimeMillis();
                total += end - start;
                int labels = MatrixLabeler.countLabels(result);
                System.out.printf("Run %d: %d Labels; %d ms\n", j+1, labels, end - start);
                output.printf("%d\t%d\t%d\n", edges, labels, end - start);
                if (debug) {
                    MatrixLabeler.printLabels(result);
                }

                if (debug) {
                    start = System.currentTimeMillis();
                    int[][] check = MatrixLabeler.checkLabeling(result);
                    end = System.currentTimeMillis();
                    MatrixGraphGenerator.printGraph(check);

                    System.out.printf("Check %d: %b %d ms\n", j+1, mgg.graphEquals(check) ? "PASSED" : "FAILED", end - start);
                }
            }
            System.out.printf("Total Benchmark Time: %d ms\n", total);
            System.out.printf("Average Benchmark Time per Graph: %d ms\n", total/iterations);
            System.out.println("=== Ending Benchmark Phase ===");
            grandTotal += total;
        }

        System.out.printf("Grand Total Benchmark Time: %d ms\n", grandTotal);
        System.out.printf("Average Benchmark Time per Graph: %d ms\n", grandTotal/(iterations*vertices));
        System.out.println("=== Ending Benchmark ===");
    }

    private static void runCGMVertices(String trialName, int iterations, MapGraphGenerator mgg, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial CGM: %s with %d vertices\n", trialName, mgg.getVertices());
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            Map<Integer, List<Integer>> testData = mgg.generateGraph();
            ListGraph lg = new ListGraph(testData);
            ECCc coverer = new ECCc(lg);
            end = System.currentTimeMillis();
            int edges = mgg.countEdges();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", mgg.getVertices(), mgg.getDensity(), edges);
            if (debug) {
                MapGraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            coverer.start(debug);
            end = System.currentTimeMillis();
            totalTime += end - start;
            List<IntOpenHashSet> result = coverer.solution();
            int cliques = result.size();
            System.out.printf("Initial %d: %d Cliques; %d ms\n", i+1, cliques, end - start);
            output.printf("Initial: %d\t%d\t%d\n", edges, cliques, end - start);
            if (debug) {
                result.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));
            }

            start = System.currentTimeMillis();
            int removed = CoverUtils.minimalize(result);
            end = System.currentTimeMillis();

            System.out.printf("Final %d: %d Cliques; %d ms\n", i+1, cliques - removed, end - start);
            output.printf("Final: %d\t%d\t%d\n", edges, cliques - removed, end - start);

            if (debug) {
                start = System.currentTimeMillis();
                boolean check = coverer.checkSolution();
                end = System.currentTimeMillis();

                System.out.printf("Check %d: %b %d ms\n", i + 1, check, end - start);
            }
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void runKKVertices(String trialName, int iterations, MatrixGraphGenerator mgg, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial KK: %s with %d vertices\n", trialName, mgg.getVertices());
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            int[][] testData = mgg.generateGraph();
            end = System.currentTimeMillis();
            int edges = mgg.countEdges();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", mgg.getVertices(), mgg.getDensity(), edges);
            if (debug) {
                MatrixGraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            List<IntOpenHashSet> result = KellermanKouAlgorithm.coverGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            int cliques = result.size();
            System.out.printf("Initial %d: %d Cliques; %d ms\n", i+1, cliques, end - start);
            output.printf("Initial: %d\t%d\t%d\n", edges, cliques, end - start);
            if (debug) {
                result.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));
            }

            start = System.currentTimeMillis();
            int removed = CoverUtils.minimalize(result);
            end = System.currentTimeMillis();

            System.out.printf("Final %d: %d Cliques; %d ms\n", i+1, cliques - removed, end - start);
            output.printf("Final: %d\t%d\t%d\n", edges, cliques - removed, end - start);
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }
}
