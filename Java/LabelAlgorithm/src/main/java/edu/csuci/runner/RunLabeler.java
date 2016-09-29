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
        long start, end, totalTime;

        int iterations = Integer.parseInt(args[0]);
        int lowerBound = Integer.parseInt(args[1]);
        int upperBound = Integer.parseInt(args[2]);
        double density = Double.parseDouble(args[3]);

        System.out.println("=== Starting Warm-up Phase ===");
        totalTime = 0;
        for (int i = 0; i < iterations/10; i++) {
            int vertices = rng.nextInt(upperBound - lowerBound + 1) + lowerBound;
            System.out.println("Vertices=" + vertices + " : Density=" + density);

            start = System.currentTimeMillis();
            List<Set<Integer>> testData = GraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);

            start = System.currentTimeMillis();
            List<Set<Integer>> result = Labeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            System.out.printf("Run %d: %d ms\n", i+1, end - start);
        }
        System.out.printf("Total Warm-up Time: %d ms\n", totalTime);
        System.out.printf("Average Warm-up Time per Graph: %d ms\n", totalTime/iterations);

        System.out.println("=== Starting Benchmark Phase ===");
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            int vertices = rng.nextInt(upperBound - lowerBound + 1) + lowerBound;
            System.out.println("Vertices=" + vertices + " : Density=" + density);

            start = System.currentTimeMillis();
            List<Set<Integer>> testData = GraphGenerator.randomGraph(vertices, density, rng);
            end = System.currentTimeMillis();
            System.out.printf("Setup %d: %d ms\n", i+1, end - start);

            start = System.currentTimeMillis();
            List<Set<Integer>> result = Labeler.labelGraph(testData);
            end = System.currentTimeMillis();
            totalTime += end - start;
            System.out.printf("Run %d: %d ms\n", i+1, end - start);
        }
        System.out.printf("Total Benchmark Time: %d ms\n", totalTime);
        System.out.printf("Average Benchmark Time per Graph: %d ms\n", totalTime/iterations);
    }
}
