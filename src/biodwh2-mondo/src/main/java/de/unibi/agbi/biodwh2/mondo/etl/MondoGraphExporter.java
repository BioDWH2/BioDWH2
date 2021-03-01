package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.mondo.MondoDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MondoGraphExporter extends GraphExporter<MondoDataSource> {
    public MondoGraphExporter(final MondoDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id");
        try {
            OboReader reader = new OboReader(dataSource.resolveSourceFilePath(workspace, "mondo.obo"),
                                             StandardCharsets.UTF_8.name());
            for (OboEntry entry : reader)
                if (entry.getType().equals("Term"))
                    exportEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export mondo.obo", e);
        }
        return true;
    }

    private void exportEntry(final Graph graph, final OboEntry entry) {
        if (entry.containsKey("is_obsolete") && "true".equalsIgnoreCase(entry.getFirst("is_obsolete")))
            return;
        Node node = createNode(graph, "Term");
        node.setProperty("id", entry.getFirst("id"));
        node.setProperty("name", entry.getFirst("name"));
        // TODO: more properties and relationships
        graph.update(node);
    }
}
