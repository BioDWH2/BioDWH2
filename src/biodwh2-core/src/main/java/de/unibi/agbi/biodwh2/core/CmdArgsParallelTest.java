package de.unibi.agbi.biodwh2.core;

import picocli.CommandLine;

/**
 * Definition of command line arguments for the parallelism test class.
 */
public class CmdArgsParallelTest {

    @CommandLine.Option(names = {"-p", "--p"}, usageHelp = true, description = "Run parallelizable pipeline steps in parallel", order = 0)
    public boolean parallel;

    @CommandLine.Option(names = {"-i", "--iterations"}, description = "Number of iterations", order = 1)
    public int numIterations = 10;

    @CommandLine.Option(names = {"-t", "--threads"}, description = "Number of iterations", order = 2)
    public int numThreads = Runtime.getRuntime().availableProcessors();

}
