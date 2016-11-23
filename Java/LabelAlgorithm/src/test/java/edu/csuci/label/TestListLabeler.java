package edu.csuci.label;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/16.
 */
public class TestListLabeler {
    @Test
    public void givenAnEmptyLabelingShouldReturnEmpty() {
        List<IntOpenHashSet> empty = Collections.emptyList();
        Assert.assertEquals(ListLabeler.labelGraph(empty), Collections.emptyList());
    }

    @Test
    public void givenAGraphWithNoEdgesShouldReturnOneLabelPerVertex() {
        List<IntOpenHashSet> graphNoEdges = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            graphNoEdges.add(new IntOpenHashSet());
        }

        List<IntOpenHashSet> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(new IntOpenHashSet());
            result.get(i).add(i);
        }

        Assert.assertEquals(ListLabeler.labelGraph(graphNoEdges), result);
    }
}
