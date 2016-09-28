package edu.csuci.thesislabel;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by jhelling on 9/28/16.
 */
public class TestLabeler {
    @Test
    public void givenAnEmptyLabelingShouldReturnEmpty() {
        Map<Integer, Set<Integer>> empty = Collections.emptyMap();
        Assert.assertEquals(Labeler.labelGraph(empty), Collections.emptyMap());
    }

    @Test
    public void givenAGraphWithNoEdgesShouldReturnOneLabelPerVertex() {
        Map<Integer, Set<Integer>> graphNoEdges = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            graphNoEdges.put(i, new TreeSet<>());
        }

        Map<Integer, List<Integer>> result = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            result.put(i, new ArrayList<>(1));
            result.get(i).add(i);
        }

        Assert.assertEquals(Labeler.labelGraph(graphNoEdges), result);
    }

    @Test
    public void GivenAGraphWithOneEdgeShouldReturnOneLessThanNumberOfVertices() {

    }
}
