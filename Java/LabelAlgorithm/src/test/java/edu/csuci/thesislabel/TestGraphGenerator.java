package edu.csuci.thesislabel;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Joel on 9/28/2016.
 */
public class TestGraphGenerator {
    @Test
    public void zeroDensityGraphShouldReturnEmptyMap() {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        result.put(0, new HashSet<>());
        result.put(1, new HashSet<>());
        result.put(2, new HashSet<>());
        result.put(3, new HashSet<>());

        Random rng = new Random(10);

        Assert.assertEquals(GraphGenerator.randomGraph(4, 0.00, rng), result);
    }

    @Test
    public void oneDensityGraphShouldReturnCompleteGraph() {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        result.put(0, Stream.of(1, 2, 3).collect(Collectors.toCollection(HashSet::new)));
        result.put(1, Stream.of(0, 2, 3).collect(Collectors.toCollection(HashSet::new)));
        result.put(2, Stream.of(0, 1, 3).collect(Collectors.toCollection(HashSet::new)));
        result.put(3, Stream.of(0, 1, 2).collect(Collectors.toCollection(HashSet::new)));

        Random rng = new Random(10);

        Assert.assertEquals(GraphGenerator.randomGraph(4, 1.00, rng), result);
    }
}
