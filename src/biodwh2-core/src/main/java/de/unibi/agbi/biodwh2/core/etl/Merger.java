package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class Merger {
    public final boolean merge(Workspace workspace, List<DataSource> dataSources,
                               PrintWriter writer) throws MergerException {
        try {
            String line;
            for (DataSource dataSource : dataSources) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                        dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.RDFTurtle)),
                                                                                 StandardCharsets.UTF_8));
                while ((line = reader.readLine()) != null) {
                    writer.println(line);

                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
