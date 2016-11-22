package edu.csuci.trial;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by jhelling on 11/18/16.
 */
public abstract class AbstractTrial implements Trial {
    protected final String name;
    protected final int iterations;
    protected final int warmUp;
    protected final boolean debug;
    protected final PrintStream output;

    public AbstractTrial(String name, int warmUp, int iterations, boolean debug, PrintStream output) {
        this.name = name;
        this.warmUp = warmUp;
        this.iterations = iterations;
        this.debug = debug;
        this.output = output;
    }

    @Override
    public abstract void runTrial() throws IOException;
}
