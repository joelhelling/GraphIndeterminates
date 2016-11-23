package edu.csuci.heuristic;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class DescendinglabelCountQComparator extends QComparator {
    @Override
    public int compare(Integer o1, Integer o2) {
        if (labelCounts[o2] == labelCounts[o1]) {
            return o2.compareTo(o1);
        } else {
            return Integer.compare(labelCounts[o2], labelCounts[o1]);
        }
    }
}
