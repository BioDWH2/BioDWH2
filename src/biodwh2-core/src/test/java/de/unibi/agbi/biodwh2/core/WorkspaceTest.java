package de.unibi.agbi.biodwh2.core;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Disabled("For development and demonstration purposes only")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkspaceTest {

    private Workspace workspace;
    private final String[] ids = {"Mock1", "Mock2", "Mock3"};

    private DecimalFormat df;

    @BeforeAll
    void setup() {
        workspace = new Workspace("");
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
    }

    @ParameterizedTest
    @MethodSource("parallelProvider")
    void processDataSourcesInParallel(boolean isParallel, int numIterations) {

        long start, stop, elapsed, averageExecutionTime;

        if (numIterations > 0) {
            numIterations = numIterations;
        }

        if (isParallel) {

            // => PARALLEL
            final DataSource[] datasources = new DataSourceLoader().getDataSources(ids);
            start = System.currentTimeMillis();

            for (int i = 0; i < numIterations; i++) {

                Stream.of(datasources).parallel().forEach(ds -> {
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
            averageExecutionTime = elapsed / numIterations;
            System.out.println("AVG PARALLEL EXECUTION TIME: " + averageExecutionTime + " MS (" +
                               df.format((float) averageExecutionTime) + " SECONDS), " + numIterations + " ITERATIONS");

        } else {

            // => SEQUENTIAL
            final DataSource[] datasources = new DataSourceLoader().getDataSources(ids);
            start = System.currentTimeMillis();

            for (int i = 0; i < numIterations; i++) {

                for (DataSource ds : datasources) {
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
            averageExecutionTime = elapsed / numIterations;
            System.out.println("AVG SEQUENTIAL EXECUTION TIME: " + averageExecutionTime + " MS (" +
                               df.format((float) averageExecutionTime) + " SECONDS), " + numIterations + " ITERATIONS");
        }

        // cleanup ...
        try {
            FileUtils.forceDelete(new File("./sources/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @ParameterizedTest
    @MethodSource("parallelPoolProvider")
    void processDataSourcesInParallelWithPool(int numThreads) {

        final DataSource[] datasources = new DataSourceLoader().getDataSources(ids);
        ForkJoinPool customPool = null;
        long start, stop, elapsed;

        try {

            System.out.println("Creating new custom thread pool with " + numThreads + " threads ... ");
            // init new pool and add task to parse and export each datasource
            customPool = new ForkJoinPool(numThreads);
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
            System.out.println("=> TIME TAKEN " + (elapsed) + " MS (" + df.format(elapsed / 1000) + " SECONDS)");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (customPool != null) {
                System.out.println("Shutting down custom pool ...");
                customPool.shutdown();
            }
        }

    }

    static Stream<Arguments> parallelProvider() {
        return Stream.of(arguments(true, 10), arguments(false, 10));
    }

    static Stream<Arguments> parallelPoolProvider() {
        return Stream.of(arguments(2), arguments(4), arguments(6), arguments(Runtime.getRuntime().availableProcessors()));
    }

}

























