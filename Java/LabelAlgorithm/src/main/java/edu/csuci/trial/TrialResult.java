package edu.csuci.trial;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 11/28/2016.
 */
public class TrialResult {
    private long edges;
    private long labels;
    private long time;

    public TrialResult(long edges, long labels, long time) {
        this.edges = edges;
        this.labels = labels;
        this.time = time;
    }

    public long getEdges() {
        return edges;
    }

    public long getLabels() {
        return labels;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return String.format("Edges: %d; Labels: %d; Time: %d ms", edges, labels, time);
    }
}
