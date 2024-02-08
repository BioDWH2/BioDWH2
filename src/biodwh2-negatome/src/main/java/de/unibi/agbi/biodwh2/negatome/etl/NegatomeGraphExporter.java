package de.unibi.agbi.biodwh2.negatome.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.negatome.NegatomeDataSource;
import de.unibi.agbi.biodwh2.negatome.model.PfamPair;
import de.unibi.agbi.biodwh2.negatome.model.ProteinPair;

public class NegatomeGraphExporter extends GraphExporter<NegatomeDataSource> {
    static final String PROTEIN_LABEL = "Protein";
    static final String PFAM_DOMAIN_LABEL = "PfamDomain";

    public NegatomeGraphExporter(final NegatomeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PFAM_DOMAIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        for (final ProteinPair pair : dataSource.proteinPairs.values()) {
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
        for (final PfamPair pair : dataSource.pfamPairs.values()) {
            final EdgeBuilder builder = graph.buildEdge().withLabel("NOT_INTERACTS_WITH");
            builder.fromNode(getOrCreatePfam(graph, pair.pfamId1));
            builder.toNode(getOrCreatePfam(graph, pair.pfamId2));
            builder.withPropertyIfNotNull("is_manual", pair.isManual);
            builder.withPropertyIfNotNull("is_pdb", pair.isPDB);
            builder.build();
        }
        return true;
    }

    private Node getOrCreateProtein(final Graph graph, final String id) {
        final Node node = graph.findNode(PROTEIN_LABEL,ID_KEY, id);
        return node == null ? graph.addNode(PROTEIN_LABEL, ID_KEY, id) : node;
    }

    private Node getOrCreatePfam(final Graph graph, final String id) {
        final Node node = graph.findNode(PFAM_DOMAIN_LABEL, ID_KEY, id);
        return node == null ? graph.addNode(PFAM_DOMAIN_LABEL, ID_KEY, id) : node;
    }
}
