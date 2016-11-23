package edu.csuci.heuristic;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class AscendingLabelCountQComparator extends QComparator {

    @Override
    public int compare(Integer o1, Integer o2) {
        if (labelCounts[o1] == labelCounts[o2]) {
            return o1.compareTo(o2);
        } else {
            return Integer.compare(labelCounts[o1], labelCounts[o2]);
        }
    }
}
