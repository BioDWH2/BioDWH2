package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    static final String DRUG_LABEL = "Drug";
    static final String VARIANT_LABEL = "Variant";
    static final String REFERENCE_LABEL = "Reference";
    static final String DISEASE_LABEL = "Disease";
    static final String NETWORK_LABEL = "Network";
    static final String DRUG_GROUP_LABEL = "DrugGroup";

    private final Map<String, String> idPrefixLabelMap = new HashMap<>();

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
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(VARIANT_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(NETWORK_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_GROUP_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "doi", IndexDescription.Type.UNIQUE));
        exportDrugs(graph);
        exportVariants(graph);
        exportDiseases(graph);
        exportNetworks(graph);
        exportDrugGroups(graph);
        return true;
    }

    private void exportDrugs(final Graph graph) {
        for (final Drug drug : dataSource.drugs)
            exportDrug(graph, drug);
    }

    private void exportDrug(final Graph graph, final Drug drug) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, drug, DRUG_LABEL);
        builder.withPropertyIfNotNull("formula", drug.formula);
        builder.withPropertyIfNotNull("exact_mass", drug.exactMass);
        builder.withPropertyIfNotNull("molecular_weight", drug.molecularWeight);
        builder.withPropertyIfNotNull("atoms", drug.atoms);
        builder.withPropertyIfNotNull("bonds", drug.bonds);
        final Node node = builder.build();
        addAllReferencesForEntry(graph, drug, node);
    }

    private NodeBuilder getNodeBuilderForKeggEntry(final Graph graph, final KeggEntry entry, final String label) {
        final NodeBuilder builder = graph.buildNode().withLabel(label);
        builder.withProperty("id", entry.id);
        if (entry.tags.size() > 1)
            builder.withProperty("tags", entry.tags.toArray(new String[0]));
        if (entry.names.size() == 1)
            builder.withProperty("name", entry.names.get(0));
        if (entry.names.size() > 1)
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
            final Node referenceNode = getOrCreateReference(graph, reference);
            if (reference.remarks != null)
                graph.addEdge(node, referenceNode, "HAS_REFERENCE", "remarks", reference.remarks);
            else
                graph.addEdge(node, referenceNode, "HAS_REFERENCE");
        }
    }

    private Node getOrCreateReference(final Graph graph, final Reference reference) {
        Node node = null;
        if (StringUtils.isNotEmpty(reference.doi))
            node = graph.findNode("Reference", "doi", reference.doi);
        if (node == null)
            node = graph.findNode("Reference", "pmid", reference.pmid);
        if (node == null)
            node = graph.addNodeFromModel(reference);
        return node;
    }

    private void exportVariants(final Graph graph) {
        for (final Variant variant : dataSource.variants)
            exportVariant(graph, variant);
    }

    private void exportVariant(final Graph graph, final Variant variant) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, variant, VARIANT_LABEL);
        // TODO
        final Node node = builder.build();
        addAllReferencesForEntry(graph, variant, node);
    }

    private void exportDiseases(final Graph graph) {
        for (final Disease disease : dataSource.diseases)
            exportDisease(graph, disease);
    }

    private void exportDisease(final Graph graph, final Disease disease) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, disease, DISEASE_LABEL);
        builder.withPropertyIfNotNull("description", disease.description);
        if (disease.categories.size() > 0)
            builder.withProperty("categories", disease.categories.toArray(new String[0]));
        // TODO
        final Node node = builder.build();
        addAllReferencesForEntry(graph, disease, node);
    }

    private void exportNetworks(final Graph graph) {
        for (final Network network : dataSource.networks)
            exportNetwork(graph, network);
    }

    private void exportNetwork(final Graph graph, final Network network) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, network, NETWORK_LABEL);
        // TODO
        final Node node = builder.build();
        addAllReferencesForEntry(graph, network, node);
    }

    private void exportDrugGroups(final Graph graph) {
        for (final DrugGroup drugGroup : dataSource.drugGroups)
            exportDrugGroup(graph, drugGroup);
    }

    private void exportDrugGroup(final Graph graph, final DrugGroup drugGroup) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, drugGroup, DRUG_GROUP_LABEL);
        // TODO
        final Node node = builder.build();
        addAllReferencesForEntry(graph, drugGroup, node);
    }
}
