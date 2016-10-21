package edu.csuci.fileparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 10/17/16.
 */
public class AsciiArcsIO {
    public static int[][] readGraph(String fileName) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(fileName));

        int graphSize = scan.nextInt();

        int[][] graph = new int[graphSize][graphSize];

        while (scan.hasNextLine()) {
            // switch from 1-based to 0-based
            int row = scan.nextInt() - 1;
            int col = scan.nextInt() - 1;

            graph[row][col] = 1;
            graph[col][row] = 1;
            graph[col][col] = 1;
            graph[row][row] = 1;
        }

        return graph;
    }
}
