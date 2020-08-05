package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

import java.io.IOException;

public class GeneOntologyGraphExporter extends GraphExporter<GeneOntologyDataSource> {
    public GeneOntologyGraphExporter(final GeneOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id");
        try {
            OboReader reader = new OboReader(dataSource.resolveSourceFilePath(workspace, "go.obo"), "UTF-8");
            for (OboEntry entry : reader)
                if (entry.getName().equals("Term"))
                    exportEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export go.obo", e);
        }
        return true;
    }

    private void exportEntry(final Graph graph, final OboEntry entry) {
        if (entry.containsKey("is_obsolete") && "true".equalsIgnoreCase(entry.getFirst("is_obsolete")))
            return;
        Node node = createNode(graph, "Term");
        node.setProperty("id", entry.getFirst("id"));
        node.setProperty("name", entry.getFirst("name"));
        node.setProperty("namespace", entry.getFirst("namespace"));
        // is_a, disjoint_from, consider
        graph.update(node);
    }
}
