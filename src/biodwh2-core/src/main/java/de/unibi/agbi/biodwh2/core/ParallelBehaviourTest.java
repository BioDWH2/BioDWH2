package de.unibi.agbi.biodwh2.core;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.stream.Stream;

import picocli.CommandLine;

import org.apache.commons.io.FileUtils;

/**
 * Test class for measuring parallel vs sequential execution of data source processing.
 */
public class ParallelBehaviourTest {

    public static void main(String[] args) {

        System.out.println("--- TESTING PARALLEL BEHAVIOUR ---\n");

        // parse cmd line args (not yet implemented in this case ...)
        final CmdArgsParallelTest commandLine = parseCommandLine(args);

        // mock workspace and data sources
        Workspace workspace = new Workspace("");
        final String[] ids = { "Mock1", "Mock2", "Mock3" };
        final DataSource[] datasourcesS, datasourcesP;

        // prepare output formatting
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        int numIterations = 1;
        long start, stop, elapsed, averageExecutionTime;

        if(commandLine.numIterations > 0) {
            numIterations = commandLine.numIterations;
        }

        if(commandLine.parallel) {

            // => PARALLEL
            datasourcesP = new DataSourceLoader().getDataSources(ids);
            start = System.currentTimeMillis();

            for(int i = 0; i < numIterations; i++) {

                Stream.of(datasourcesP).parallel().forEach(
                        ds -> {
                            try {
                                ds.prepare(workspace);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ds.parse(workspace);
                            ds.export(workspace);
                        });
            }

            stop = System.currentTimeMillis();
            elapsed = stop - start;
            averageExecutionTime = elapsed/numIterations;
            System.out.println("AVG PARALLEL EXECUTION TIME: " + averageExecutionTime + " MS (" + df.format((float) averageExecutionTime) + " SECONDS), " + numIterations + " ITERATIONS");

        } else {

            // => SEQUENTIAL
            datasourcesS = new DataSourceLoader().getDataSources(ids);
            start = System.currentTimeMillis();

            for(int i = 0; i < numIterations; i++) {

                for(DataSource ds : datasourcesS) {
                    try {
                        ds.prepare(workspace);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ds.parse(workspace);
                    ds.export(workspace);
                }
            }

            stop = System.currentTimeMillis();
            elapsed = stop - start;
            averageExecutionTime = elapsed/numIterations;
            System.out.println("AVG SEQUENTIAL EXECUTION TIME: " + averageExecutionTime + " MS (" + df.format((float) averageExecutionTime) + " SECONDS), " + numIterations + " ITERATIONS");
        }

        // cleanup ...
        try {
            FileUtils.forceDelete(new File("./sources/"));
        } catch (IOException e) {
            e.printStackTrace();
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
