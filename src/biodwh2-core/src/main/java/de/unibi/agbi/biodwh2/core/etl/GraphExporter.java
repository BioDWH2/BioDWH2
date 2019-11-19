package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class GraphExporter {
    public final boolean export(Workspace workspace, DataSource dataSource) throws ExporterException {
        Graph graph = exportGraph(dataSource);
        if (graph != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(
                        dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GraphML));
                return new GraphMLGraphWriter().write(outputStream, graph);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    protected abstract Graph exportGraph(DataSource dataSource);
}
