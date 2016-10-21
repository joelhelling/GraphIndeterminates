package edu.csuci.label;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class TestListLabeler {
    @Test
    public void givenAnEmptyLabelingShouldReturnEmpty() {
        List<Set<Integer>> empty = Collections.emptyList();
        Assert.assertEquals(ListLabeler.labelGraph(empty), Collections.emptyList());
    }

    @Test
    public void givenAGraphWithNoEdgesShouldReturnOneLabelPerVertex() {
        List<Set<Integer>> graphNoEdges = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            graphNoEdges.add(new HashSet<>());
        }

        List<Set<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(new HashSet<>());
            result.get(i).add(i);
        }

        Assert.assertEquals(ListLabeler.labelGraph(graphNoEdges), result);
    }
}
