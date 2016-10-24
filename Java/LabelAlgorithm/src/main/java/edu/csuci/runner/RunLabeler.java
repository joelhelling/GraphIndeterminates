package edu.csuci.runner;

import de.unijena.minet.KellermanKouAlgorithm;
import edu.csuci.fileparser.AsciiArcsIO;
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
import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/29/16.
 */
public class RunLabeler {
    public static void main(String[] args) {
        Random rng = new Random(System.currentTimeMillis());
        OptionParser parser = new OptionParser();
        OptionSpec<Void> nvertices = parser.accepts("nvertices", "Runs the labeling algorithm on random graphs with specified vertices");
        OptionSpec<Void> fastVertices = parser.accepts("fnvertices", "Runs the optimized labeling algorithm on random graphs with specified vertices");
        OptionSpec<Void> ascending = parser.accepts("ascending", "Runs the optimized labeling algorithm on random graphs ");
        OptionSpec<Void> cgm = parser.accepts("cgm", "Run the edge clique covering algorithm found in Clique Covering of Large Real-World Networks");
        OptionSpec<Void> kellermankou = parser.accepts("kk", "Run the edge clique covering algorithm described in Covering Edges by Cliques with Regard to Keyword Conflicts and Intersection Graphs");
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
        OptionSpec<String> inFile = parser.accepts("input", "The file to read graph from with current supported formats")
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

        if (options.has(help)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        } else if (options.has(inFile)) {
            try {
                runSingleGraph(AsciiArcsIO.readGraph(inFile.value(options)), options.has(debug));
            } catch (FileNotFoundException fnfe) {
                System.out.println("Unable to read from file: " + fnfe);
            }
        } else if (options.has(nvertices)) {
            try {
                RunLabeler.runTrialsNVertices("Warm-up", warmUp.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
                RunLabeler.runTrialsNVertices("Benchmark", iterations.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
            } catch (IOException ioe) {
                System.out.println("File opening error: " + ioe);
            }
        } else if (options.has(fastVertices)) {
            try {
                RunLabeler.runFastTrialsNVertices("Warm-up", warmUp.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
                RunLabeler.runFastTrialsNVertices("Benchmark", iterations.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
            } catch (IOException ioe) {
                System.out.println("File opening error: " + ioe);
            }
        } else if (options.has(ascending)) {
            try {
                RunLabeler.runAscendingVertices(warmUp.value(options), iterations.value(options), vertices.value(options), rng, options.has(debug), output);
            } catch (IOException ioe) {
                System.out.println("File opening error: " + ioe);
            }
        } else if (options.has(cgm)) {
            try {
                RunLabeler.runCGMVertices("Warm-up", warmUp.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
                RunLabeler.runCGMVertices("Benchmark", iterations.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
            } catch (IOException ioe) {
                System.out.println("File opening error: " + ioe);
            }
        } else if (options.has(kellermankou)) {
            try {
                RunLabeler.runKKVertices("Warm-up", warmUp.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
                RunLabeler.runKKVertices("Benchmark", iterations.value(options), vertices.value(options), density.value(options), rng, options.has(debug), output);
            } catch (IOException ioe) {
                System.out.println("File opening error: " + ioe);
            }
        } else {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }
        output.close();
    }

    private static void runTrials(String trialName, int iterations, int lowerBound, int upperBound, double density, Random rng) {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            int vertices = rng.nextInt(upperBound - lowerBound + 1) + lowerBound;

            start = System.currentTimeMillis();
            List<Set<Integer>> testData = ListGraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, ListGraphGenerator.countEdges(testData));
            ListGraphGenerator.printGraph(testData);

            start = System.currentTimeMillis();
            List<Set<Integer>> result = ListLabeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, ListLabeler.countLabels(result), end - start);
            ListLabeler.printLabels(result);

            start = System.currentTimeMillis();
            List<Set<Integer>> check = ListLabeler.checkLabeling(result);
            ListGraphGenerator.printGraph(check);
            end = System.currentTimeMillis();
            System.out.printf("Check %d: %b %d ms\n", i+1, (check.equals(testData))?"PASSED":"FAILED", end - start);
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void runTrialsNVertices(String trialName, int iterations, int vertices, double density, Random rng, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial: %s with %d vertices\n", trialName, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            List<Set<Integer>> testData = ListGraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            int edges = ListGraphGenerator.countEdges(testData);
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, edges);
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
                System.out.printf("Check %d: %b %d ms\n", i + 1, (check.equals(testData)) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void runFastTrialsNVertices(String trialName, int iterations, int vertices, double density, Random rng, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial HRSS: %s with %d vertices\n", trialName, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            int[][] testData = MatrixGraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            int edges = MatrixGraphGenerator.countEdges(testData);
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, edges);
            if (debug) {
                MatrixGraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            int[][] result = MatrixLabeler.labelGraph(testData);
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

                System.out.printf("Check %d: %b %d ms\n", i + 1, MatrixGraphGenerator.graphEquals(testData, check) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void runAscendingVertices(int warmups, int iterations, int vertices, Random rng, boolean debug, PrintStream output) throws IOException {
        System.out.printf("Starting Trial HRSS: %d to %d vertices\n", 1, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        long start, end, total, grandTotal;

        grandTotal = 0;

        for (int i = 1; i <= vertices; i++) {
            System.out.printf("=== Starting Warm-up Phase for %d vertices ===\n", i);
            output.printf("Trial: Warm-up with %d vertices\n", i);

            total = 0;

            for (int j = 0; j < warmups; j++) {
                double density = rng.nextDouble();
                start = System.currentTimeMillis();
                int[][] testData = MatrixGraphGenerator.randomGraph(i, density, rng);
                end = System.currentTimeMillis();
                int edges = MatrixGraphGenerator.countEdges(testData);
                System.out.printf("Setup %d: %d ms\n", j+1, end - start);
                System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", i, density, edges);
                if (debug) {
                    MatrixGraphGenerator.printGraph(testData);
                }

                start = System.currentTimeMillis();
                int[][] result = MatrixLabeler.labelGraph(testData);
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

                    System.out.printf("Check %d: %b %d ms\n", j+1, MatrixGraphGenerator.graphEquals(testData, check) ? "PASSED" : "FAILED", end - start);
                }
            }
            System.out.printf("Total Warm-up Time: %d ms\n", total);
            System.out.printf("Average Warm-up Time per Graph: %d ms\n", total/warmups);
            System.out.println("=== Ending Warm-up Phase ===");

            total = 0;

            output.printf("Trial: Benchmark with %d vertices\n", i);
            for (int j = 0; j < iterations; j++) {
                double density = rng.nextDouble();
                start = System.currentTimeMillis();
                int[][] testData = MatrixGraphGenerator.randomGraph(i, density, rng);
                end = System.currentTimeMillis();
                int edges = MatrixGraphGenerator.countEdges(testData);
                System.out.printf("Setup %d: %d ms\n", j+1, end - start);
                System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", i, density, edges);
                if (debug) {
                    MatrixGraphGenerator.printGraph(testData);
                }

                start = System.currentTimeMillis();
                int[][] result = MatrixLabeler.labelGraph(testData);
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

                    System.out.printf("Check %d: %b %d ms\n", j+1, MatrixGraphGenerator.graphEquals(testData, check) ? "PASSED" : "FAILED", end - start);
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

    private static void runCGMVertices(String trialName, int iterations, int vertices, double density, Random rng, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial CGM: %s with %d vertices\n", trialName, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            Map<Integer, List<Integer>> testData = MapGraphGenerator.randomGraph(vertices, density, rng);
            ListGraph lg = new ListGraph(testData);
            ECCc coverer = new ECCc(lg);
            end = System.currentTimeMillis();
            int edges = MapGraphGenerator.countEdges(testData);
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, edges);
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

    private static void runKKVertices(String trialName, int iterations, int vertices, double density, Random rng, boolean debug, PrintStream output)  throws IOException {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        output.printf("Trial KK: %s with %d vertices\n", trialName, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            int[][] testData = MatrixGraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            int edges = MatrixGraphGenerator.countEdges(testData);
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, edges);
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

    private static void runSingleGraph(int[][] graph, boolean debug) {
        long start, end;

        System.out.printf("Labeling graph with %d vertices and %d edges\n", graph.length, MatrixGraphGenerator.countEdges(graph));

        if (debug) {
            MatrixGraphGenerator.printGraph(graph);
        }

        start = System.currentTimeMillis();
        int[][] labels = MatrixLabeler.labelGraph(graph);
        end = System.currentTimeMillis();
        int numLabels = MatrixLabeler.countLabels(labels);
        System.out.printf("Labeling completed with %d labels in %d ms\n", numLabels, end - start);

        if (debug) {
            MatrixLabeler.printLabels(labels);
            start = System.currentTimeMillis();
            int[][] check = MatrixLabeler.checkLabeling(labels);
            end = System.currentTimeMillis();
            MatrixGraphGenerator.printGraph(check);

            System.out.printf("Check %b in %d ms", MatrixGraphGenerator.graphEquals(graph, check), end - start);
        }
    }
}
