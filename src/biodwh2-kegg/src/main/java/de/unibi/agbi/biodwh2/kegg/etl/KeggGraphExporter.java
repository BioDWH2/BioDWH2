package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;

import java.util.*;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    private final Map<String, String> idPrefixLabelMap = new HashMap<>();
    private final Map<String, Long> referenceLookup = new HashMap<>();

    public KeggGraphExporter(final KeggDataSource dataSource) {
        super(dataSource);
        idPrefixLabelMap.put("CPD", "Compound");
        idPrefixLabelMap.put("DR", "Drug");
        idPrefixLabelMap.put("ED", "EnvFactor");
        idPrefixLabelMap.put("GN", "Genome");
        idPrefixLabelMap.put("HSA", "Gene");
        idPrefixLabelMap.put("KO", "Orthology");
        idPrefixLabelMap.put("VG", "Virus");
        idPrefixLabelMap.put("TAX", "Taxonomy");
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys("id", "pmid", "doi");
        exportDrugs(graph);
        return true;
    }

    private void exportDrugs(final Graph graph) {
        for (final Drug drug : dataSource.drugs)
            exportDrug(graph, drug);
    }

    private void exportDrug(final Graph graph, final Drug drug) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, drug);
        builder.withPropertyIfNotNull("formula", drug.formula);
        builder.withPropertyIfNotNull("exact_mass", drug.exactMass);
        builder.withPropertyIfNotNull("molecular_weight", drug.molecularWeight);
        builder.withPropertyIfNotNull("atoms", drug.atoms);
        builder.withPropertyIfNotNull("bonds", drug.bonds);
        final Node node = builder.build();
        addAllReferencesForEntry(graph, drug, node);
    }

    private NodeBuilder getNodeBuilderForKeggEntry(final Graph graph, final KeggEntry entry) {
        final NodeBuilder builder = graph.buildNode().withLabels(entry.tags.toArray(new String[0]));
        builder.withProperty("id", entry.id);
        if (entry.names.size() > 0)
            builder.withProperty("names", entry.names.toArray(new String[0]));
        if (entry.externalIds.size() > 0)
            builder.withProperty("external_identifier", entry.externalIds.toArray(new String[0]));
        if (entry.remarks.size() > 0)
            builder.withProperty("remarks", entry.remarks.toArray(new String[0]));
        if (entry.comments.size() > 0)
            builder.withProperty("comments", entry.comments.toArray(new String[0]));
        return builder;
    }

    private void addAllReferencesForEntry(final Graph graph, final KeggEntry entry, final Node node) {
        for (final Reference reference : entry.references) {
            final Long referenceNodeId;
            final boolean doiAvailable = reference.doi != null && reference.doi.length() > 0;
            final boolean pmidAvailable = reference.pmid != null && reference.pmid.length() > 0;
            if (doiAvailable && referenceLookup.containsKey(reference.doi)) {
                referenceNodeId = referenceLookup.get(reference.doi);
            } else if (pmidAvailable && referenceLookup.containsKey(reference.pmid)) {
                referenceNodeId = referenceLookup.get(reference.pmid);
            } else {
                referenceNodeId = graph.addNodeFromModel(reference).getId();
                if (pmidAvailable)
                    referenceLookup.put(reference.pmid, referenceNodeId);
                if (doiAvailable)
                    referenceLookup.put(reference.doi, referenceNodeId);
            }
            if (reference.remarks != null)
                graph.addEdge(node, referenceNodeId, "HAS_REFERENCE", "remarks", reference.remarks);
            else
                graph.addEdge(node, referenceNodeId, "HAS_REFERENCE");
        }
    }
}
