package de.unibi.agbi.biodwh2.core;

import picocli.CommandLine;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/**
 * Test class for setting up a custom thread pool via cmd line arguments.
 */
public class ParallelPoolBehaviourTest {

    public static void main(String[] args) {

        System.out.println("--- TESTING CUSTOM THREAD POOL BEHAVIOUR ---\n");

        // parse cmd line args (not yet implemented in this case ...)
        final CmdArgsParallelTest commandLine = parseCommandLine(args);

        // mock workspace and data sources
        Workspace workspace = new Workspace("");
        final String[] ids = {"Mock1", "Mock2", "Mock3"};
        final DataSource[] datasources = new DataSourceLoader().getDataSources(ids);
        ForkJoinPool customPool = null;
        long start, stop, elapsed;

        // prepare output formatting
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        try {

            System.out.println("Creating new custom thread pool with " + commandLine.numThreads + " threads ... ");

            // init new pool and add task to parse and export each datasource
            customPool = new ForkJoinPool(commandLine.numThreads);
            start = System.currentTimeMillis();
            customPool.submit(() -> Stream.of(datasources).parallel().forEach(ds -> {

                try {
                    ds.prepare(workspace);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ds.parse(workspace);
                ds.export(workspace);
            })).get();

            stop = System.currentTimeMillis();
            elapsed = stop - start;
            System.out.println("=> TIME TAKEN " + (elapsed) + " MS (" + df.format (elapsed/1000) + " SECONDS)");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (customPool != null) {
                System.out.println("Shutting down custom pool ...");
                customPool.shutdown();
            }
        }

    }

    private static CmdArgsParallelTest parseCommandLine(final String... args) {
        final CmdArgsParallelTest result = new CmdArgsParallelTest();
        final CommandLine cmd = new CommandLine(result);
        cmd.setPosixClusteredShortOptionsAllowed(true);
        cmd.parseArgs(args);
        return result;
    }

}
