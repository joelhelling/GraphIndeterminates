package edu.csuci.trial;

import java.io.PrintStream;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by jhelling on 11/18/16.
 */
public abstract class TwoStageTrial extends AbstractTrial {
    public TwoStageTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output) {
        super(name, warmUp, iterations, debug, output);
    }

    @Override
    public void runTrial() {
        runWarmup();
        runBenchmark();
    }

    private void runWarmup() {
        run("Warm-up", warmUp);
    }

    private void runBenchmark() {
        run("Benchmark", iterations);
    }

    private void run(String trialType, int runs) {
        System.out.printf("Trial %s: %s\n", name, trialType);
        output.printf("Trial %s: %s\n", name, trialType);

        for (int i = 0; i < runs; i++) {
            testSetup();
            stats.updateStats(testRun());
            testCheck();
        }
        stats.printStats(System.out);
        stats.printStats(output);
    }

    protected abstract void testSetup();

    protected abstract TrialResult testRun();

    protected abstract void testCheck();
}
