package de.unibi.agbi.biodwh2.core;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void processDataSourcesInParallel() {;

        final DataSource[] datasourcesS, datasourcesP;

        int numIterations = 5;
        boolean parallel = true;

        long start, stop, elapsed, averageExecutionTime;

        if (numIterations > 0) {
            numIterations = numIterations;
        }

        if (parallel) {

            // => PARALLEL
            datasourcesP = new DataSourceLoader().getDataSources(ids);
            start = System.currentTimeMillis();

            for (int i = 0; i < numIterations; i++) {

                Stream.of(datasourcesP).parallel().forEach(ds -> {
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
            datasourcesS = new DataSourceLoader().getDataSources(ids);
            start = System.currentTimeMillis();

            for (int i = 0; i < numIterations; i++) {

                for (DataSource ds : datasourcesS) {
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

}