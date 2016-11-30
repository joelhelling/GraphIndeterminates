package edu.csuci.heuristic;

import java.util.Comparator;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public abstract class QComparator implements Comparator<Integer> {
    protected int[] labelCounts;
    protected int[] neighborhoodSizes;

    public void setNeighborhoodSizes(int[] neighborhoodSizes) {
        this.neighborhoodSizes = neighborhoodSizes;
    }

    public void setLabelCounts(int[] labelCounts) {
        this.labelCounts = labelCounts;
    }

    @Override
    public abstract int compare(Integer o1, Integer o2);
}
