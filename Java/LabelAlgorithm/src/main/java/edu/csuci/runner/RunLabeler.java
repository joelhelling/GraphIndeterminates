package edu.csuci.runner;

import edu.csuci.label.GraphGenerator;
import edu.csuci.label.Labeler;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by jhelling on 9/29/16.
 */
public class RunLabeler {
    public static void main(String[] args) {
        Random rng = new Random(System.currentTimeMillis());

        int iterations = Integer.parseInt(args[0]);
        int lowerBound = Integer.parseInt(args[1]);
        int upperBound = Integer.parseInt(args[2]);
        double density = Double.parseDouble(args[3]);

        RunLabeler.runTrials("Warm-up", iterations/10, lowerBound, upperBound, density, rng);
        RunLabeler.runTrials("Benchmark", iterations, lowerBound, upperBound, density, rng);
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
}
