package de.unibi.agbi.biodwh2.kegg.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeggGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String VARIANT_LABEL = "Variant";
    static final String REFERENCE_LABEL = "Reference";
    static final String DISEASE_LABEL = "Disease";
    static final String NETWORK_LABEL = "Network";
    static final String DRUG_GROUP_LABEL = "DrugGroup";
    static final String GENE_LABEL = "Gene";
    static final String TARGETS_LABEL = "TARGETS";

    private final Map<String, String> idPrefixLabelMap = new HashMap<>();

    public KeggGraphExporter(final KeggDataSource dataSource) {
        super(dataSource);
        idPrefixLabelMap.put("CPD", "Compound");
        idPrefixLabelMap.put("DR", DRUG_LABEL);
        idPrefixLabelMap.put("ED", "EnvFactor");
        idPrefixLabelMap.put("GN", "Genome");
        idPrefixLabelMap.put("HSA", GENE_LABEL);
        idPrefixLabelMap.put("KO", "Orthology");
        idPrefixLabelMap.put("VG", "Virus");
        idPrefixLabelMap.put("TAX", "Taxonomy");
        idPrefixLabelMap.put("GL", "Glycan");
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(VARIANT_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(NETWORK_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_GROUP_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "doi", IndexDescription.Type.UNIQUE));
        exportHumanGenesList(workspace, graph);
        exportDrugs(graph);
        exportVariants(graph);
        exportDiseases(graph);
        exportNetworks(graph);
        exportDrugGroups(graph);
        return true;
    }

    private void exportHumanGenesList(final Workspace workspace, final Graph graph) {
        try {
            final MappingIterator<String[]> iterator = FileUtils.openTsv(workspace, dataSource,
                                                                         KeggUpdater.HUMAN_GENES_LIST_FILE_NAME,
                                                                         String[].class);
            while (iterator.hasNext()) {
                final String[] row = iterator.next();
                if (row != null && row.length == 2)
                    exportHumanGene(graph, row);
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportHumanGene(final Graph graph, final String[] row) {
        final String id = row[0].trim();
        if (row[1].contains(";")) {
            final String[] symbolsAndName = StringUtils.split(row[1], ";", 2);
            final String[] symbols = Arrays.stream(StringUtils.split(symbolsAndName[0], ",")).map(String::trim).toArray(
                    String[]::new);
            graph.addNode(GENE_LABEL, "id", id, "name", symbolsAndName[1].trim(), "symbols", symbols);
        } else
            graph.addNode(GENE_LABEL, "id", id, "name", row[1].trim());
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
        builder.withPropertyIfNotNull("bracket", drug.bracket);
        builder.withPropertyIfNotNull("name_abbreviation", drug.nameAbbreviation);
        final Node node = builder.build();
        addAllReferencesForEntry(graph, drug, node);
        for (final Sequence sequence : drug.sequences)
            graph.addEdge(node, graph.addNodeFromModel(sequence), "HAS_SEQUENCE");
        exportDrugGeneRelations(graph, drug, node);
        // TODO: efficacy
        // TODO: efficacyDiseases
        // TODO: classes
        // TODO: networkTargets
        // TODO: sources
        // TODO: mixtures
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

    private void exportDrugGeneRelations(final Graph graph, final Drug drug, final Node node) {
        for (final NameIdsPair target : drug.targets) {
            final Node geneNode = findEntry(graph, target.ids);
            if (geneNode != null)
                graph.addEdge(node, geneNode, TARGETS_LABEL);
            else
                LOGGER.warn("Failed to add targets relation for drug " + drug.id + " and target " + target);
        }
        for (final Metabolism metabolism : drug.metabolisms) {
            final Node geneNode = findEntry(graph, metabolism.target.ids);
            if (geneNode != null)
                graph.addEdge(node, geneNode, TARGETS_LABEL, "type", "substrate", "target_type",
                              metabolism.type.toLowerCase(Locale.ROOT));
            else
                LOGGER.warn(
                        "Failed to add metabolism relation for drug " + drug.id + " and target " + metabolism.target);
        }
        for (final Interaction interaction : drug.interactions) {
            final Node geneNode = findEntry(graph, interaction.target.ids);
            if (geneNode == null) {
                LOGGER.warn(
                        "Failed to add interaction relation for drug " + drug.id + " and target " + interaction.target);
                continue;
            }
            switch (interaction.type.toLowerCase(Locale.ROOT)) {
                case "cyp inhibition":
                    graph.addEdge(node, geneNode, TARGETS_LABEL, "type", "inhibitor", "target_type", "cyp");
                    break;
                case "cyp induction":
                    graph.addEdge(node, geneNode, TARGETS_LABEL, "type", "inducer", "target_type", "cyp");
                    break;
                case "transporter inhibition":
                    graph.addEdge(node, geneNode, TARGETS_LABEL, "type", "inhibitor", "target_type", "transporter");
                    break;
                case "transporter induction":
                    graph.addEdge(node, geneNode, TARGETS_LABEL, "type", "inducer", "target_type", "transporter");
                    break;
                case "enzyme inhibition":
                    graph.addEdge(node, geneNode, TARGETS_LABEL, "type", "inhibitor", "target_type", "enzyme");
                    break;
                default:
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Unhandled interaction type " + interaction.type);
                    graph.addEdge(node, geneNode, TARGETS_LABEL, "type", interaction.type);
                    break;
            }
        }
    }

    private Node findEntry(final Graph graph, final String id) {
        if (id.startsWith("HSA"))
            return graph.findNode(GENE_LABEL, "id", id.toLowerCase(Locale.ROOT));
        if (id.startsWith("DG"))
            return graph.findNode(DRUG_GROUP_LABEL, "id", id);
        if (id.startsWith("D"))
            return graph.findNode(DRUG_LABEL, "id", id);
        return null;
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
        final Map<Long, Set<Long>> addedHierarchyRelationsCache = new HashMap<>();
        for (final DrugGroup drugGroup : dataSource.drugGroups)
            exportDrugGroupHierarchy(graph, addedHierarchyRelationsCache, drugGroup);
    }

    private void exportDrugGroup(final Graph graph, final DrugGroup drugGroup) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, drugGroup, DRUG_GROUP_LABEL);
        if (drugGroup.nameStems.size() > 0)
            builder.withProperty("name_stems", drugGroup.nameStems.toArray(new String[0]));
        builder.withPropertyIfNotNull("name_abbreviation", drugGroup.nameAbbreviation);
        final Node node = builder.build();
        addAllReferencesForEntry(graph, drugGroup, node);
    }

    private void exportDrugGroupHierarchy(final Graph graph, final Map<Long, Set<Long>> addedHierarchyRelationsCache,
                                          final DrugGroup drugGroup) {
        // TODO: classes
        for (final ParentChildRelation relation : drugGroup.members) {
            final Node parent = relation.parent == null ? graph.findNode(DRUG_GROUP_LABEL, "id", drugGroup.id) :
                                findEntry(graph, relation.parent.ids);
            final Node child = findEntry(graph, relation.child.ids);
            if (parent != null && child != null) {
                if (!addedHierarchyRelationsCache.containsKey(parent.getId()))
                    addedHierarchyRelationsCache.put(parent.getId(), new HashSet<>());
                final Set<Long> childIds = addedHierarchyRelationsCache.get(parent.getId());
                if (!childIds.contains(child.getId())) {
                    graph.addEdge(parent, child, "HAS_MEMBER");
                    childIds.add(child.getId());
                }
            } else {
                final String parentInfo = relation.parent == null ? drugGroup.id : relation.parent.toString();
                LOGGER.warn("Failed to add drug hierarchy relation with parent " + parentInfo + " and child " +
                            relation.child);
            }
        }
    }

    private Node findEntry(final Graph graph, final Iterable<String> ids) {
        for (final String id : ids) {
            final Node node = findEntry(graph, id);
            if (node != null)
                return node;
        }
        return null;
    }
}
