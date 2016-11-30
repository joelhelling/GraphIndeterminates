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
        int op1 = labelCounts[o1] + neighborhoodSizes[o1];
        int op2 = labelCounts[o2] + neighborhoodSizes[o2];
        if (op1 == op2) {
            return o1.compareTo(o2);
        } else {
            return Integer.compare(op1, op2);
        }
    }
}
