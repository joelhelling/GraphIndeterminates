package edu.csuci.graphresults;

import de.unijena.minet.KellermanKouAlgorithm;
import edu.csuci.graph.MapGraphGenerator;
import edu.csuci.graph.MatrixGraphGenerator;
import edu.csuci.heuristic.DescendinglabelCountQComparator;
import edu.csuci.heuristic.MatrixHeuristic;
import edu.csuci.heuristic.NeighborhoodMatrixHeuristic;
import edu.csuci.heuristic.QComparator;
import edu.csuci.label.MatrixLabeler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.export.CoverUtils;
import it.unipi.di.export.ECCc;
import it.unipi.di.interfaces.ListGraph;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jhelling on 12/12/16.
 */
public class CompleteGraphs {
    @Test
    public void hrssOnTriangleFreeGraph() {
        MatrixGraphGenerator mgg = new MatrixGraphGenerator(1000, 1.0);
        int[][] graph = mgg.generateGraph();
        MatrixGraphGenerator.printGraph(graph);

        MatrixHeuristic mh = NeighborhoodMatrixHeuristic.descendingNeighborhood(1);
        QComparator qComparator = new DescendinglabelCountQComparator();
        int[][] labels = MatrixLabeler.labelGraph(graph, mh, qComparator);

        MatrixLabeler.printLabels(labels);
        System.out.println(MatrixLabeler.countLabels(labels));
    }

    @Test
    public void kkOnTriangleFreeGraph() {
        MatrixGraphGenerator mgg = new MatrixGraphGenerator(1000, 1.0);
        int[][] graph = mgg.generateGraph();
        MatrixGraphGenerator.printGraph(graph);

        List<IntOpenHashSet> cliques = KellermanKouAlgorithm.coverGraph(graph);
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());
    }
    @Test
    public void cgmOnCompleteGraph() {
        MapGraphGenerator mg = new MapGraphGenerator(1000, 1.0);
        Map<Integer, List<Integer>> graph = mg.generateGraph();
        MapGraphGenerator.printGraph(graph);

        ListGraph lg = new ListGraph(graph);
        ECCc cliqueSolver = new ECCc(lg);
        cliqueSolver.start(false);
        List<IntOpenHashSet> cliques = cliqueSolver.solution();
        int removed = CoverUtils.minimalize(cliques);
        System.out.println("Removed " + removed);
        cliques.forEach(x -> System.out.println(Arrays.toString(x.toIntArray())));

        System.out.println(cliques.size());
    }
}
