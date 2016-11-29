package edu.csuci.trial;

import java.io.PrintStream;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 11/28/2016.
 */
public class TrialStats {
    private long edges;
    private long labels;
    private long time;

    private long iterations = 0;

    public TrialStats() {
        edges = 0;
        labels = 0;
        time = 0;
    }

    public void updateStats(TrialResult result) {
        edges += result.getEdges();
        labels += result.getLabels();
        time += result.getTime();

        iterations++;
    }

    public void printStats(PrintStream output) {
        output.printf("Total Time: %d ms\n", time);
        output.printf("Average Edges per Graph: %d Edges\n", edges/iterations);
        output.printf("Average Labels per Graph: %d Labels\n", labels/iterations);
        output.printf("Average Time per Graph: %d ms\n", time/iterations);
   }
}
