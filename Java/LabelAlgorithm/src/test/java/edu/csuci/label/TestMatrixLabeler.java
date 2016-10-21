package edu.csuci.label;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/20/2016.
 */
public class TestMatrixLabeler {
    @Test
    public void givenAGraphThatContainsThreeCliqueThenUseThreeLabels() {
        int[][] graph = {{1,1,0,1,1},{1,1,0,0,1},{0,0,1,0,1},{1,0,0,1,1},{1,1,1,1,1}};

        int[][] labels = MatrixLabeler.labelGraph(graph);

        List<List<Integer>> l = new ArrayList<>(labels.length);
        for (int[] label : labels) {
            l.add(Arrays.stream(label).boxed().collect(Collectors.toList()));
        }

        int[][] resultLabels = {{1,2,0,0,0},{1,0,0,0,0},{3,0,0,0,0},{2,0,0,0,0},{1,2,3,0,0}};
        List<List<Integer>> rl = new ArrayList<>(resultLabels.length);
        for (int[] resultLabel : resultLabels) {
            rl.add(Arrays.stream(resultLabel).boxed().collect(Collectors.toList()));
        }

        Assert.assertEquals(rl, l);
    }

    @Test
    public void GraphThatProducesALotOfLabelsOnOneVertexWillNotErrorWithArrayOutOfBounds() {
        int[][] graph = {
                {1, 1, 1, 0, 1, 1, 0, 1},
                {1, 1, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 0, 0, 1, 1},
                {0, 0, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 1, 1, 0, 1, 1},
                {1, 0, 0, 1, 0, 1, 1, 1},
                {0, 0, 1, 0, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1}
        };
        int[][] labels = MatrixLabeler.labelGraph(graph);

        List<List<Integer>> l = new ArrayList<>(labels.length);
        for (int[] label : labels) {
            l.add(Arrays.stream(label).boxed().collect(Collectors.toList()));
        }

        int[][] resultLabels = {
                {1, 2, 3, 4, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0},
                {2, 5, 0, 0, 0, 0, 0, 0},
                {6, 7, 0, 0, 0, 0, 0, 0},
                {3, 6, 8, 0, 0, 0, 0, 0},
                {4, 7, 9, 0, 0, 0, 0, 0},
                {5, 8, 9, 0, 0, 0, 0, 0},
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0},

        };
        List<List<Integer>> rl = new ArrayList<>(resultLabels.length);
        for (int[] resultLabel : resultLabels) {
            rl.add(Arrays.stream(resultLabel).boxed().collect(Collectors.toList()));
        }

        Assert.assertEquals(rl, l);
    }

    @Test
    public void GraphThatShouldProduceAFourClique() {
        int[][] graph = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 1, 0, 1, 1, 0, 1},
                {0, 0, 1, 0, 0, 0, 0, 1, 1, 1},
                {0, 0, 0, 1, 1, 0, 1, 1, 0, 1},
                {0, 1, 0, 1, 1, 0, 0, 0, 1, 1},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {0, 1, 0, 1, 0, 0, 1, 0, 1, 1},
                {0, 1, 1, 1, 0, 0, 0, 1, 1, 1},
                {0, 0, 1, 0, 1, 0, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        };
        int[][] labels = MatrixLabeler.labelGraph(graph);

        List<List<Integer>> l = new ArrayList<>(labels.length);
        for (int[] label : labels) {
            l.add(Arrays.stream(label).boxed().collect(Collectors.toList()));
        }

        int[][] resultLabels = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 3, 4, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {6, 7, 8, 0, 0, 0, 0, 0, 0, 0},
                {2, 6, 9, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 7, 11, 0, 0, 0, 0, 0, 0, 0},
                {4, 5, 8, 0, 0, 0, 0, 0, 0, 0},
                {5, 9, 11, 0, 0, 0, 0, 0, 0, 0},
                {2, 3, 4, 5, 6, 7, 8, 9, 10, 11},

        };
        List<List<Integer>> rl = new ArrayList<>(resultLabels.length);
        for (int[] resultLabel : resultLabels) {
            rl.add(Arrays.stream(resultLabel).boxed().collect(Collectors.toList()));
        }

        Assert.assertEquals(rl, l);
    }
}
