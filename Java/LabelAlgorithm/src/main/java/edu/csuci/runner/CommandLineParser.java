package edu.csuci.runner;

import edu.csuci.Heuristic.DummyMatrixHeuristic;
import edu.csuci.Heuristic.MatrixHeuristic;
import edu.csuci.Heuristic.NeighborhoodMatrixHeuristic;
import edu.csuci.Heuristic.ShuffleMatrixHeuristic;
import edu.csuci.graph.ListGraphGenerator;
import edu.csuci.graph.MapGraphGenerator;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.trial.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Created by jhelling on 11/18/16.
 */
public class CommandLineParser {
    private OptionSpec<Void> alln;
    private OptionSpec<Void> nvertices;
    private OptionSpec<Void> fastVertices;
    private OptionSpec<Void> shuffleOrder;
    private OptionSpec<Void> ascending;
    private OptionSpec<Void> cgm;
    private OptionSpec<Void> kellermankou;

    private OptionSpec<Integer> ascHeuristic;
    private OptionSpec<Integer> descHeuristic;
    private OptionSpec<Void> shuffleHeuristic;

    private OptionSpec<Void> debug;

    private OptionSpec<Void> help;
    private OptionSpec<Integer> warmUp;
    private OptionSpec<Integer> iterations;
    private OptionSpec<Integer> vertices;
    private OptionSpec<Double> density;
    private OptionSpec<String> outFile;

    private OptionParser parser;
    private OptionSet options;

    public CommandLineParser(String[] args) {
        parser = new OptionParser();

        /* Trials Available */
        alln = parser.accepts("alln", "Runs the matrix labeling algorithm on all possible graphs on given number of vertices");
        nvertices = parser.accepts("nvertices", "Runs the list labeling algorithm on random graphs with specified vertices");
        fastVertices = parser.accepts("fnvertices", "Runs the matrix labeling algorithm on random graphs with specified vertices");
        shuffleOrder = parser.accepts("shuffle", "Runs the matrix labeling algorithm on a random graph with different absolute ordering of the vertices and find the least number of symbols");
        ascending = parser.accepts("ascending", "Runs the matrix labeling algorithm on random graphs ");
        cgm = parser.accepts("cgm", "Run the edge clique covering algorithm found in Clique Covering of Large Real-World Networks");
        kellermankou = parser.accepts("kk", "Run the edge clique covering algorithm described in Covering Edges by Cliques with Regard to Keyword Conflicts and Intersection Graphs");

        ascHeuristic = parser.accepts("heurasc", "Runs the labeling algorithm with the vertices ordered by ascending neighborhood size with a specified depth")
                .availableIf(fastVertices).withRequiredArg().ofType(Integer.class).defaultsTo(0);
        descHeuristic = parser.accepts("heurdesc", "Runs the labeling algorithm with the vertices ordered by descending neighborhood size with a specified depth")
                .availableIf(fastVertices).withRequiredArg().ofType(Integer.class).defaultsTo(0);
        shuffleHeuristic = parser.accepts("heurshuffle", "Runs the labeling algorithm with the vertices ordered randomly")
                .availableIf(fastVertices).availableIf(shuffleOrder);

        debug = parser.accepts("debug", "If specified, runs checking algorithm and prints out results");

        help = parser.acceptsAll(Arrays.asList("help","h"), "Help").forHelp();
        warmUp = parser.accepts("warmup", "The number of warm-up runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).defaultsTo(20);
        iterations = parser.accepts("iterations", "The number of benchmark runs of the algorithm")
                .withRequiredArg().ofType(Integer.class).defaultsTo(100);
        vertices = parser.accepts("vertices", "The number of vertices in the generated graphs")
                .withRequiredArg().ofType(Integer.class).defaultsTo(100);
        density = parser.accepts("density", "Between 0.0 and 1.0. Probability of having an edge between two vertices")
                .withRequiredArg().ofType(Double.class).defaultsTo(0.5);
        outFile = parser.accepts("output", "The file to output data to")
                .withRequiredArg().ofType(String.class);

        options = parser.parse(args);
    }

    public Trial parseArgs() throws IOException {
        PrintStream output = parseOutput();
        MatrixHeuristic mh = parseHeuristic();

        if (options.has(help)) {
            parser.printHelpOn(System.out);
        } else if (options.has(alln)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            return new AllGraphsTrial("All Graphs", vertices.value(options), options.has(debug), output, mgg);
        } else if (options.has(nvertices)) {
            ListGraphGenerator lgg = new ListGraphGenerator(vertices.value(options), density.value(options));
            return new ListTrial("HRSS", warmUp.value(options), iterations.value(options), options.has(debug), output, lgg);
        } else if (options.has(fastVertices)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            return new MatrixTrial("HRSS", warmUp.value(options), iterations.value(options), options.has(debug), output, mgg, mh);
        } else if (options.has(shuffleOrder)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            return new ShuffleTrial("Shuffle", warmUp.value(options), iterations.value(options), options.has(debug), output, mgg);
        } else if (options.has(ascending)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            return new AscendingTrial("Ascending", warmUp.value(options), iterations.value(options), options.has(debug), output, mgg, mh, vertices.value(options));
        } else if (options.has(cgm)) {
            MapGraphGenerator mgg = new MapGraphGenerator(vertices.value(options), density.value(options));
            return new CGMTrial("CGM", warmUp.value(options), iterations.value(options), options.has(debug), output, mgg);
        } else if (options.has(kellermankou)) {
            MatrixGraphGenerator mgg = new MatrixGraphGenerator(vertices.value(options), density.value(options));
            return new KKTrial("KK", warmUp.value(options), iterations.value(options), options.has(debug), output, mgg);
        } else {
            parser.printHelpOn(System.out);
        }
        return null;
    }

    private PrintStream parseOutput() {
        if (options.has(outFile)) {
            try {
                return new PrintStream(outFile.value(options));
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe);
                System.out.println("Using System.out instead...");
                return System.out;
            }
        }
        return System.out;
    }

    private MatrixHeuristic parseHeuristic() {
        if (options.has(descHeuristic)) {
            return NeighborhoodMatrixHeuristic.descendingNeighborhood(descHeuristic.value(options));
        } else if (options.has(ascHeuristic)){
            return NeighborhoodMatrixHeuristic.ascendingNeighborhood(ascHeuristic.value(options));
        } else if (options.has(shuffleHeuristic)) {
            return new ShuffleMatrixHeuristic();
        } else {
            return new DummyMatrixHeuristic();
        }
    }
}
