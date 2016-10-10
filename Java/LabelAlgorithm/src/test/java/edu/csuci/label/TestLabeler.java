package edu.csuci.label;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jhelling on 9/28/16.
 */
public class TestLabeler {
    @Test
    public void givenAnEmptyLabelingShouldReturnEmpty() {
        List<Set<Integer>> empty = Collections.emptyList();
        Assert.assertEquals(Labeler.labelGraph(empty), Collections.emptyList());
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

        Assert.assertEquals(Labeler.labelGraph(graphNoEdges), result);
    }

    @Test
    public void givenAGraphThatContainsThreeCliqueThenUseThreeLabels() {
        int[][] graph = {{1,1,0,1,1},{1,1,0,0,1},{0,0,1,0,1},{1,0,0,1,1},{1,1,1,1,1}};

        int[][] labels = Labeler.fastLabelGraph(graph);

        List<List<Integer>> l = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            l.add(Arrays.stream(labels[i]).boxed().collect(Collectors.toList()));
        }

        int[][] resultLabels = {{1,2,0,0,0},{1,0,0,0,0},{3,0,0,0,0},{2,0,0,0,0},{1,2,3,0,0}};
        List<List<Integer>> rl = new ArrayList<>(resultLabels.length);
        for (int i = 0; i < resultLabels.length; i++) {
            rl.add(Arrays.stream(resultLabels[i]).boxed().collect(Collectors.toList()));
        }

        Assert.assertEquals(rl, l);
    }

    @Test
    public void GraphThatProducesALotOfLabelsOnOneVertexWillNotErrorWithArrayOutOfBounds() {
        int[][] graph = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 1, 1, 1, 1, 1},
                {0, 0, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 0, 1, 1, 1},
                {0, 1, 1, 0, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 0, 1},
                {0, 1, 1, 1, 1, 0, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1},
        };
        int[][] labels = Labeler.fastLabelGraph(graph);

        List<List<Integer>> l = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            l.add(Arrays.stream(labels[i]).boxed().collect(Collectors.toList()));
        }

        int[][] resultLabels = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 6, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 5, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 6, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 3, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {4, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0},

        };
        List<List<Integer>> rl = new ArrayList<>(resultLabels.length);
        for (int i = 0; i < resultLabels.length; i++) {
            rl.add(Arrays.stream(resultLabels[i]).boxed().collect(Collectors.toList()));
        }

        Assert.assertEquals(rl, l);

    }
}
