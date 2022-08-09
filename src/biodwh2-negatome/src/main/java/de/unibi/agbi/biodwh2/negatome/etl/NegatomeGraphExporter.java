package de.unibi.agbi.biodwh2.negatome.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.negatome.NegatomeDataSource;
import de.unibi.agbi.biodwh2.negatome.model.ProteinPair;

public class NegatomeGraphExporter extends GraphExporter<NegatomeDataSource> {
    public NegatomeGraphExporter(final NegatomeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Protein", "id", false, IndexDescription.Type.UNIQUE));
        for (final ProteinPair pair : dataSource.pairs.values()) {
            final EdgeBuilder builder = graph.buildEdge().withLabel("NOT_INTERACTS_WITH");
            builder.fromNode(getOrCreateProtein(graph, pair.uniProtId1));
            builder.toNode(getOrCreateProtein(graph, pair.uniProtId2));
            builder.withPropertyIfNotNull("manual_pmid", pair.manualPmid);
            builder.withPropertyIfNotNull("manual_pmcid", pair.manualPmcid);
            builder.withPropertyIfNotNull("manual_evidence", pair.manualEvidence);
            builder.withPropertyIfNotNull("pdb_codes", pair.pdbCodes);
            builder.withPropertyIfNotNull("pdb_evidence", pair.pdbEvidence);
            builder.withPropertyIfNotNull("is_manual", pair.isManual);
            builder.withPropertyIfNotNull("is_manual_stringent", pair.isManualStringent);
            builder.withPropertyIfNotNull("is_pdb", pair.isPDB);
            builder.withPropertyIfNotNull("is_pdb_stringent", pair.isPDBStringent);
            builder.build();
        }
        return true;
    }

    private Node getOrCreateProtein(final Graph graph, final String id) {
        Node node = graph.findNode("Protein", "id", id);
        if (node == null)
            node = graph.addNode("Protein", "id", id);
        return node;
    }
}
