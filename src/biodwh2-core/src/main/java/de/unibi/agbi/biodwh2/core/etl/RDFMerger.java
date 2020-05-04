package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RDFMerger extends Merger {
    public final boolean merge(final Workspace workspace, final List<DataSource> dataSources,
                               final String outputFilePath) throws MergerException {
        PrintWriter writer;
        try {
            writer = new PrintWriter(outputFilePath);
        } catch (FileNotFoundException e) {
            throw new MergerException("Failed to create merge file '" + outputFilePath + "'", e);
        }
        String line;
        for (DataSource dataSource : dataSources) {
            String filePath = dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.RDFTurtle);
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
                while ((line = reader.readLine()) != null)
                    writer.println(line);
            } catch (IOException e) {
                throw new MergerException("Failed to merge RDF graphs", e);
            }
            dataSource.getMetadata().mergeSuccessful = true;
        }
        return true;
    }
}
