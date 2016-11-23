package edu.csuci.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/28/2016.
 */
public class TestListGraphGenerator {
    @Test
    public void zeroDensityGraphShouldReturnEmptyMap() {
        List<Set<Integer>> result = new ArrayList<>(4);
        result.add(0, new HashSet<>());
        result.add(1, new HashSet<>());
        result.add(2, new HashSet<>());
        result.add(3, new HashSet<>());

        Random rng = new Random(10);

        ListGraphGenerator lgg = new ListGraphGenerator(4, rng.nextDouble(), 10);
        Assert.assertEquals(lgg.generateGraph(), result);
    }

    @Test
    public void oneDensityGraphShouldReturnCompleteGraph() {
        List<Set<Integer>> result = new ArrayList<>(4);
        result.add(0, Stream.of(1, 2, 3).collect(Collectors.toCollection(HashSet::new)));
        result.add(1, Stream.of(0, 2, 3).collect(Collectors.toCollection(HashSet::new)));
        result.add(2, Stream.of(0, 1, 3).collect(Collectors.toCollection(HashSet::new)));
        result.add(3, Stream.of(0, 1, 2).collect(Collectors.toCollection(HashSet::new)));

        Random rng = new Random(10);

        ListGraphGenerator lgg = new ListGraphGenerator(4, rng.nextDouble(), 10);
        Assert.assertEquals(lgg.generateGraph(), result);
    }
}
