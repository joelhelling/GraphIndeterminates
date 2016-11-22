package edu.csuci.runner;

import edu.csuci.trial.Trial;

import java.io.IOException;

/**
 * LabelAlgorithm
 * California State University Channel Islands
 * Constructing an Indeterminate String from its Associated Graph
 * Created by Joel on 9/29/16.
 */
public class RunLabeler {
    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new CommandLineParser(args);

        Trial trial = parser.parseArgs();

        if (trial != null) {
            trial.runTrial();
        }
    }
}
