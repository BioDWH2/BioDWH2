package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.io.obo.OboTerm;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class OntologyGraphExporter<D extends DataSource> extends GraphExporter<D> {
    public OntologyGraphExporter(final D dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id");
        try {
            final OboReader reader = new OboReader(dataSource.resolveSourceFilePath(workspace, getOntologyFileName()),
                                                   StandardCharsets.UTF_8);
            for (final OboEntry entry : reader)
                if (entry instanceof OboTerm)
                    exportTerm(graph, (OboTerm) entry);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + getOntologyFileName() + "'", e);
        }
        return true;
    }

    private void exportTerm(final Graph graph, final OboTerm term) {
        final NodeBuilder builder = graph.buildNode().withLabel("Term");
        builder.withProperty("id", term.getFirst("id"));
        builder.withProperty("name", term.getFirst("name"));
        builder.withPropertyIfNotNull("xrefs", term.get("xref"));
        if (Boolean.TRUE.equals(term.isObsolete()))
            builder.withProperty("obsolete", true);
        builder.build();
    }

    protected abstract String getOntologyFileName();
}
