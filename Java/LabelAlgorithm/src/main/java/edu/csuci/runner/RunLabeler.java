package edu.csuci.runner;

import edu.csuci.label.GraphGenerator;
import edu.csuci.label.Labeler;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by jhelling on 9/29/16.
 */
public class RunLabeler {
    public static void main(String[] args) {
        Random rng = new Random(System.currentTimeMillis());
        OptionParser parser = new OptionParser();
        OptionSpec<Void> nvertices = parser.accepts("nvertices", "Runs the labeling algorithm on random graphs with specified vertices");
        OptionSpec<Void> fastVertices = parser.accepts("fnvertices", "Runs the optimized labeling algorithm on random graphs with specified vertices");
        OptionSpec<Void> ascending = parser.accepts("ascending", "Runs the optimized labeling algorithm on random graphs ");
        OptionSpec<Void> debug = parser.accepts("debug", "If specified, runs checking algorithm and prints out results");

        OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("help","h"), "Help").forHelp();
        OptionSpec<Integer> warmUp = parser.acceptsAll(Arrays.asList("w","warmup"), "The number of warm-up runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).required();
        OptionSpec<Integer> iterations = parser.acceptsAll(Arrays.asList("i","iterations"), "The number of benchmark runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).required();
        OptionSpec<Integer> vertices = parser.acceptsAll(Arrays.asList("v","vertices"), "The number of vertices in the generated graphs")
                .withRequiredArg().ofType(Integer.class).required();
        OptionSpec<Double> density = parser.acceptsAll(Arrays.asList("d","density"), "Between 0.0 and 1.0. Probability of having an edge between two vertices")
                .withRequiredArg().ofType(Double.class).defaultsTo(0.5);
        OptionSpec<String> file = parser.acceptsAll(Arrays.asList("o", "output"), "The file to output data to").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        PrintStream output;
        if (options.has(file)) {
            try {
                output = new PrintStream(file.value(options));
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
        } else if (options.has(ascending)){
            try {
                RunLabeler.runAscendingVertices(warmUp.value(options), iterations.value(options), vertices.value(options), rng, options.has(debug), output);
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
            List<Set<Integer>> testData = GraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, GraphGenerator.countEdges(testData));
            GraphGenerator.printGraph(testData);

            start = System.currentTimeMillis();
            List<Set<Integer>> result = Labeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, Labeler.countLabels(result), end - start);
            Labeler.printLabels(result);

            start = System.currentTimeMillis();
            List<Set<Integer>> check = Labeler.checkLabeling(result);
            GraphGenerator.printGraph(check);
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
            List<Set<Integer>> testData = GraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            int edges = GraphGenerator.countEdges(testData);
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, edges);
            if (debug) {
                GraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            List<Set<Integer>> result = Labeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            int labels = Labeler.countLabels(result);
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, labels, end - start);
            output.printf("%d\t%d\t%d\n", edges, labels, end - start);
            if (debug) {
                Labeler.printLabels(result);
            }

            if (debug) {
                start = System.currentTimeMillis();
                List<Set<Integer>> check = Labeler.checkLabeling(result);
                GraphGenerator.printGraph(check);
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
        output.printf("Trial: %s with %d vertices\n", trialName, vertices);
        output.println("Edges -- Labels -- Time (milliseconds)");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            int[][] testData = GraphGenerator.fastRandomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            int edges = GraphGenerator.countEdges(testData);
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, edges);
            if (debug) {
                GraphGenerator.printAdjacencyMatrix(testData);
                GraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            int[][] result = Labeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            int labels = Labeler.countLabels(result);
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, labels, end - start);
            output.printf("%d\t%d\t%d\n", edges, labels, end - start);
            if (debug) {
                Labeler.printLabels(result);
            }

            if (debug) {
                start = System.currentTimeMillis();
                int[][] check = Labeler.checkLabeling(result);
                end = System.currentTimeMillis();
                GraphGenerator.printGraph(check);

                System.out.printf("Check %d: %b %d ms\n", i + 1, GraphGenerator.graphEquals(testData, check) ? "PASSED" : "FAILED", end - start);
            }
        }
        System.out.printf("Total %s Time: %d ms\n", trialName, totalTime);
        System.out.printf("Average %s Time per Graph: %d ms\n", trialName, totalTime/iterations);
        System.out.println("=== Ending " + trialName + " Phase ===");
    }

    private static void runAscendingVertices(int warmups, int iterations, int vertices, Random rng, boolean debug, PrintStream output) throws IOException {
        System.out.printf("Starting Trial: %d to %d vertices\n", 1, vertices);
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
                int[][] testData = GraphGenerator.fastRandomGraph(i, density, rng);
                end = System.currentTimeMillis();
                int edges = GraphGenerator.countEdges(testData);
                System.out.printf("Setup %d: %d ms\n", j+1, end - start);
                System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", i, density, edges);
                if (debug) {
                    GraphGenerator.printGraph(testData);
                    GraphGenerator.printAdjacencyMatrix(testData);
                }

                start = System.currentTimeMillis();
                int[][] result = Labeler.labelGraph(testData);
                end = System.currentTimeMillis();
                total += end - start;
                int labels = Labeler.countLabels(result);
                System.out.printf("Run %d: %d Labels; %d ms\n", j+1, labels, end - start);
                output.printf("%d\t%d\t%d\n", edges, labels, end - start);
                if (debug) {
                    Labeler.printLabels(result);
                }

                if (debug) {
                    start = System.currentTimeMillis();
                    int[][] check = Labeler.checkLabeling(result);
                    end = System.currentTimeMillis();
                    GraphGenerator.printGraph(check);

                    System.out.printf("Check %d: %b %d ms\n", j+1, GraphGenerator.graphEquals(testData, check) ? "PASSED" : "FAILED", end - start);
                }
            }
            System.out.printf("Total Warm-up Time: %d ms\n", total);
            System.out.printf("Average Warm-up Time per Graph: %d ms\n", total/warmups);
            System.out.println("=== Ending Warm-up Phase ===");

            total = 0;

            for (int j = 0; j < iterations; j++) {
                double density = rng.nextDouble();
                start = System.currentTimeMillis();
                int[][] testData = GraphGenerator.fastRandomGraph(i, density, rng);
                end = System.currentTimeMillis();
                int edges = GraphGenerator.countEdges(testData);
                System.out.printf("Setup %d: %d ms\n", j+1, end - start);
                System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", i, density, edges);
                if (debug) {
                    GraphGenerator.printGraph(testData);
                    GraphGenerator.printAdjacencyMatrix(testData);
                }

                start = System.currentTimeMillis();
                int[][] result = Labeler.labelGraph(testData);
                end = System.currentTimeMillis();
                total += end - start;
                int labels = Labeler.countLabels(result);
                System.out.printf("Run %d: %d Labels; %d ms\n", j+1, labels, end - start);
                output.printf("%d\t%d\t%d\n", edges, labels, end - start);
                if (debug) {
                    Labeler.printLabels(result);
                }

                if (debug) {
                    start = System.currentTimeMillis();
                    int[][] check = Labeler.checkLabeling(result);
                    end = System.currentTimeMillis();
                    GraphGenerator.printGraph(check);

                    System.out.printf("Check %d: %b %d ms\n", j+1, GraphGenerator.graphEquals(testData, check) ? "PASSED" : "FAILED", end - start);
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
}
