package edu.csuci.runner;

import edu.csuci.label.GraphGenerator;
import edu.csuci.label.Labeler;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;
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
        OptionSpec<Void> debug = parser.accepts("debug", "If specified, runs checking algorithm and prints out results");

        OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("help","h"), "Help").forHelp();
        OptionSpec<Integer> warmUp = parser.acceptsAll(Arrays.asList("w","warmup"), "The number of warm-up runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).required();
        OptionSpec<Integer> iterations = parser.acceptsAll(Arrays.asList("i","iterations"), "The number of benchmark runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).required();
        OptionSpec<Integer> vertices = parser.acceptsAll(Arrays.asList("v","vertices"), "The number of vertices in the generated graphs")
                .withRequiredArg().ofType(Integer.class).required();
        OptionSpec<Double> density = parser.acceptsAll(Arrays.asList("d","density"), "Between 0.0 and 1.0. Probability of having an edge between two vertices")
                .withRequiredArg().ofType(Double.class).required();

        OptionSet options = parser.parse(args);

        if (options.has(help)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        } else if (options.has(nvertices)){
            RunLabeler.runTrialsNVertices("Warm-up", warmUp.value(options), vertices.value(options), density.value(options), rng, options.has(debug));
            RunLabeler.runTrialsNVertices("Benchmark", iterations.value(options), vertices.value(options), density.value(options), rng, options.has(debug));
        } else {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }
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

    private static void runTrialsNVertices(String trialName, int iterations, int vertices, double density, Random rng, boolean debug) {
        long totalTime, start, end;
        System.out.println("=== Starting " + trialName + " Phase ===");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            List<Set<Integer>> testData = GraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);
            System.out.printf("Vertices: %d; Density: %f; Edges: %d\n", vertices, density, GraphGenerator.countEdges(testData));
            if (debug) {
                GraphGenerator.printGraph(testData);
            }

            start = System.currentTimeMillis();
            List<Set<Integer>> result = Labeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            System.out.printf("Run %d: %d Labels; %d ms\n", i+1, Labeler.countLabels(result), end - start);
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
}
