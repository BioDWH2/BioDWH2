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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(KeggGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String VARIANT_LABEL = "Variant";
    static final String REFERENCE_LABEL = "Reference";
    static final String DISEASE_LABEL = "Disease";
    static final String NETWORK_LABEL = "Network";
    static final String DRUG_GROUP_LABEL = "DrugGroup";
    static final String GENE_LABEL = "Gene";
    static final String COMPOUND_LABEL = "Compound";
    static final String ORGANISM_LABEL = "Organism";
    static final String TARGETS_LABEL = "TARGETS";

    public KeggGraphExporter(final KeggDataSource dataSource) {
        super(dataSource);
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
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "doi", IndexDescription.Type.UNIQUE));
        exportHumanGenesList(workspace, graph);
        exportCompoundsList(workspace, graph);
        exportOrganismsList(workspace, graph);
        exportDrugs(graph);
        exportVariants(graph);
        exportDiseases(graph);
        exportNetworks(graph);
        exportDrugGroups(graph);
        return true;
    }

    private void exportHumanGenesList(final Workspace workspace, final Graph graph) {
        for (final String[] row : openTSV(workspace, KeggUpdater.HUMAN_GENES_LIST_FILE_NAME))
            if (row != null && row.length == 2)
                exportHumanGene(graph, row);
    }

    private Iterable<String[]> openTSV(final Workspace workspace, final String fileName) {
        try {
            final MappingIterator<String[]> iterator = FileUtils.openTsv(workspace, dataSource, fileName,
                                                                         String[].class);
            return () -> iterator;
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

    private void exportCompoundsList(Workspace workspace, Graph graph) {
        for (final String[] row : openTSV(workspace, KeggUpdater.COMPOUNDS_LIST_FILE_NAME))
            if (row != null && row.length == 2)
                graph.addNode(COMPOUND_LABEL, "id", StringUtils.split(row[0], ":", 2)[1], "names",
                              StringUtils.splitByWholeSeparator(row[1], "; "));
    }

    private void exportOrganismsList(Workspace workspace, Graph graph) {
        for (final String[] row : openTSV(workspace, KeggUpdater.ORGANISMS_LIST_FILE_NAME))
            if (row != null && row.length == 4)
                graph.addNode(ORGANISM_LABEL, "id", row[0], "symbol", row[1], "name", row[2], "taxonomy", row[3]);
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
        if (id.startsWith("C"))
            return graph.findNode(COMPOUND_LABEL, "id", id);
        return null;
    }

    private void exportVariants(final Graph graph) {
        for (final Variant variant : dataSource.variants)
            exportVariant(graph, variant);
    }

    private void exportVariant(final Graph graph, final Variant variant) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, variant, VARIANT_LABEL);
        builder.withPropertyIfNotNull("organism", variant.organism);
        // TODO: genes
        // TODO: networks
        // TODO: variations
        final Node node = builder.build();
        addAllReferencesForEntry(graph, variant, node);
    }

    private void exportDiseases(final Graph graph) {
        for (final Disease disease : dataSource.diseases)
            exportDisease(graph, disease);
        final Map<Long, Set<Long>> addedHierarchyRelationsCache = new HashMap<>();
        for (final Disease disease : dataSource.diseases)
            exportDiseaseHierarchy(graph, addedHierarchyRelationsCache, disease);
    }

    private void exportDisease(final Graph graph, final Disease disease) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, disease, DISEASE_LABEL);
        builder.withPropertyIfNotNull("description", disease.description);
        if (disease.categories.size() > 0)
            builder.withProperty("categories", disease.categories.toArray(new String[0]));
        if (disease.envFactors.size() > 0)
            builder.withProperty("env_factors",
                                 disease.envFactors.stream().map(Object::toString).toArray(String[]::new));
        if (disease.carcinogens.size() > 0)
            builder.withProperty("carcinogens",
                                 disease.carcinogens.stream().map(Object::toString).toArray(String[]::new));
        if (disease.pathogens.size() > 0)
            builder.withProperty("pathogens", disease.pathogens.stream().map(Object::toString).toArray(String[]::new));
        if (disease.pathogenModules.size() > 0)
            builder.withProperty("pathogen_modules",
                                 disease.pathogenModules.stream().map(Object::toString).toArray(String[]::new));
        // TODO: networks
        // TODO: drugs
        // TODO: genes
        final Node node = builder.build();
        addAllReferencesForEntry(graph, disease, node);
    }

    private void exportDiseaseHierarchy(final Graph graph, final Map<Long, Set<Long>> addedHierarchyRelationsCache,
                                        final Disease disease) {
        final Node node = graph.findNode(DISEASE_LABEL, "id", disease.id);
        if (!addedHierarchyRelationsCache.containsKey(node.getId()))
            addedHierarchyRelationsCache.put(node.getId(), new HashSet<>());
        for (final NameIdsPair group : disease.subGroups) {
            if (group.ids.size() == 0) {
                LOGGER.warn(
                        "Failed to add disease hierarchy relation with parent " + disease.id + " and child " + group);
                continue;
            }
            final String childId = StringUtils.split(group.ids.get(0), ":", 2)[1];
            final Node child = graph.findNode(DISEASE_LABEL, "id", childId);
            if (child == null) {
                LOGGER.warn(
                        "Failed to add disease hierarchy relation with parent " + disease.id + " and child " + group);
                continue;
            }
            final Set<Long> childIds = addedHierarchyRelationsCache.get(node.getId());
            if (!childIds.contains(child.getId())) {
                graph.addEdge(node, child, "HAS_MEMBER");
                childIds.add(child.getId());
            }
        }
        for (final NameIdsPair group : disease.superGroups) {
            if (group.ids.size() == 0) {
                LOGGER.warn(
                        "Failed to add disease hierarchy relation with parent " + group + " and child " + disease.id);
                continue;
            }
            final String parentId = StringUtils.split(group.ids.get(0), ":", 2)[1];
            final Node parent = graph.findNode(DISEASE_LABEL, "id", parentId);
            if (parent == null) {
                LOGGER.warn(
                        "Failed to add disease hierarchy relation with parent " + group + " and child " + disease.id);
                continue;
            }
            if (!addedHierarchyRelationsCache.containsKey(parent.getId()))
                addedHierarchyRelationsCache.put(parent.getId(), new HashSet<>());
            final Set<Long> childIds = addedHierarchyRelationsCache.get(parent.getId());
            if (!childIds.contains(node.getId())) {
                graph.addEdge(parent, node, "HAS_MEMBER");
                childIds.add(node.getId());
            }
        }
    }

    private void exportNetworks(final Graph graph) {
        for (final Network network : dataSource.networks)
            exportNetwork(graph, network);
    }

    private void exportNetwork(final Graph graph, final Network network) {
        final NodeBuilder builder = getNodeBuilderForKeggEntry(graph, network, NETWORK_LABEL);
        builder.withPropertyIfNotNull("type", network.type);
        // TODO: definition
        // TODO: expandedDefinition
        // TODO: genes
        // TODO: variants
        // TODO: diseases
        // TODO: members
        // TODO: perturbants
        // TODO: classes
        // TODO: metabolites
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
