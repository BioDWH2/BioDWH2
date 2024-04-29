package de.unibi.agbi.biodwh2.markerdb.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.markerdb.MarkerDBDataSource;
import de.unibi.agbi.biodwh2.markerdb.model.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class MarkerDBGraphExporter extends GraphExporter<MarkerDBDataSource> {
    public static final String GENE_LABEL = "Gene";
    public static final String CHEMICAL_LABEL = "Chemical";
    public static final String PROTEIN_LABEL = "Protein";
    public static final String KARYOTYPE_LABEL = "Karyotype";
    public static final String SEQUENCE_VARIANT_LABEL = "SequenceVariant";
    public static final String CONDITION_LABEL = "Condition";

    private final Map<String, Long> conditionNodeIdMap = new HashMap<>();
    private final Set<String> addedRelationshipKeys = new HashSet<>();

    public MarkerDBGraphExporter(final MarkerDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(CHEMICAL_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(KARYOTYPE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(SEQUENCE_VARIANT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportAllCollection(workspace, graph, MarkerDBUpdater.CHEMICALS_FILE_NAME);
        exportAllCollection(workspace, graph, MarkerDBUpdater.PROTEINS_FILE_NAME);
        exportAllCollection(workspace, graph, MarkerDBUpdater.KARYOTYPES_FILE_NAME);
        exportAllCollection(workspace, graph, MarkerDBUpdater.SEQUENCE_VARIANTS_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.DIAGNOSTIC_CHEMICALS_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.DIAGNOSTIC_PROTEIN_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.DIAGNOSTIC_KARYOTYPES_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.PREDICTIVE_GENETICS_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.EXPOSURE_CHEMICALS_FILE_NAME);
        conditionNodeIdMap.clear();
        addedRelationshipKeys.clear();
        return true;
    }

    private void exportAllCollection(final Workspace workspace, final Graph graph, final String fileName) {
        final XmlMapper xmlMapper = new XmlMapper();
        try (final var inputStream = FileUtils.openInput(workspace, dataSource, fileName)) {
            final var collection = xmlMapper.readValue(inputStream, AllCollection.class);
            if (collection.chemicals != null)
                for (final Chemical chemical : collection.chemicals)
                    exportChemical(graph, chemical);
            if (collection.proteins != null)
                for (final Protein protein : collection.proteins)
                    exportProtein(graph, protein);
            if (collection.karyotypes != null)
                for (final Karyotype karyotype : collection.karyotypes)
                    exportKaryotype(graph, karyotype);
            if (collection.sequenceVariants != null)
                for (final SequenceVariant sequenceVariant : collection.sequenceVariants)
                    exportSequenceVariant(graph, sequenceVariant);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void exportChemical(final Graph graph, final Chemical entry) {
        final var node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry.conditions, node);
    }

    private void exportAndConnectConditions(final Graph graph, final List<Condition> conditions, final Node node) {
        if (conditions == null)
            return;
        for (final Condition condition : conditions) {
            final String name = condition.condition != null ? condition.condition : condition.name;
            final Long conditionNodeId = getOrCreateCondition(graph, name);
            final String labelKey = condition.indicationType != null ? condition.indicationType.toUpperCase(
                    Locale.ROOT) : condition.biomarkerCategory.toUpperCase(Locale.ROOT);
            final String key = node.getId() + "|" + conditionNodeId + "|" + labelKey + "|" + condition.citation + "|" +
                               condition.age + "|" + condition.sex + "|" + condition.biofluid + "|" +
                               condition.concentration;
            if (addedRelationshipKeys.contains(key))
                continue;
            addedRelationshipKeys.add(key);
            final var builder = graph.buildEdge(labelKey + "_BIOMARKER_FOR");
            builder.fromNode(node);
            builder.toNode(conditionNodeId);
            transformCitationForEdgeBuilder(builder, condition.citation);
            builder.withPropertyIfNotNull("age", condition.age);
            builder.withPropertyIfNotNull("sex", condition.sex);
            builder.withPropertyIfNotNull("biofluid", condition.biofluid);
            builder.withPropertyIfNotNull("concentration", condition.concentration);
            builder.build();
        }
    }

    private Long getOrCreateCondition(final Graph graph, final String name) {
        Long nodeId = conditionNodeIdMap.get(name);
        if (nodeId == null) {
            nodeId = graph.addNode(CONDITION_LABEL, "name", name).getId();
            conditionNodeIdMap.put(name, nodeId);
        }
        return nodeId;
    }

    private void transformCitationForEdgeBuilder(final EdgeBuilder builder, final String citation) {
        if (citation != null && !"NA".equals(citation) && !"&lt;63".equals(citation)) {
            final String[] parts = StringUtils.split(citation, ";");
            final List<String> citations = new ArrayList<>();
            final List<Integer> pubmedIds = new ArrayList<>();
            for (final String part : parts) {
                if (part.startsWith("Pubmed_ID")) {
                    final var pmid = Integer.parseInt(StringUtils.split(part, ":", 2)[1].trim());
                    pubmedIds.add(pmid);
                } else {
                    citations.add(part);
                }
            }
            if (!citations.isEmpty())
                builder.withProperty("citations", citations.toArray(new String[0]));
            if (!pubmedIds.isEmpty())
                builder.withProperty("pmids", pubmedIds.toArray(new Integer[0]));

        }
    }

    private void exportProtein(final Graph graph, final Protein entry) {
        final var node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry.conditions, node);
    }

    private void exportKaryotype(final Graph graph, final Karyotype entry) {
        final var node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry.conditions, node);
    }

    private void exportSequenceVariant(final Graph graph, final SequenceVariant entry) {
        final var node = graph.addNodeFromModel(entry);
        for (final var measurements : entry.sequenceVariantMeasurements) {
            if (measurements.condition == null)
                continue;
            for (int i = 0; i < measurements.condition.size(); i++) {
                final var condition = measurements.condition.get(i);
                final Long conditionNodeId = getOrCreateCondition(graph, condition);
                final var indicationType = measurements.indicationTypes.get(i);
                final var builder = graph.buildEdge(indicationType.toUpperCase(Locale.ROOT) + "_BIOMARKER_FOR");
                builder.fromNode(node);
                builder.toNode(conditionNodeId);
                if (measurements.reference.get(i) != null)
                    builder.withProperty("pmids", new Integer[]{measurements.reference.get(i)});
                builder.build();
            }
        }
    }

    private void exportSpecificCollection(final Workspace workspace, final Graph graph, final String fileName) {
        final XmlMapper xmlMapper = new XmlMapper();
        try (final var inputStream = FileUtils.openInput(workspace, dataSource, fileName)) {
            final var collection = xmlMapper.readValue(inputStream, SpecificCollection.class);
            if (collection.biomarkers != null) {
                if (collection.biomarkers.chemicals != null)
                    for (final ChemicalSimple chemical : collection.biomarkers.chemicals)
                        exportChemical(graph, chemical);
                if (collection.biomarkers.proteins != null)
                    for (final ProteinSimple protein : collection.biomarkers.proteins)
                        exportProtein(graph, protein);
                if (collection.biomarkers.karyotypes != null)
                    for (final KaryotypeSimple karyotype : collection.biomarkers.karyotypes)
                        exportKaryotype(graph, karyotype);
                if (collection.biomarkers.genes != null)
                    for (final GeneSimple gene : collection.biomarkers.genes)
                        exportGene(graph, gene);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void exportChemical(final Graph graph, final ChemicalSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(CHEMICAL_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry, node);
    }

    private void exportAndConnectConditions(final Graph graph, final ConditionSimple condition, final Node node) {
        final Long conditionNodeId = getOrCreateCondition(graph, condition.conditions);
        final String labelKey = condition.indicationTypes != null ? condition.indicationTypes.toUpperCase(Locale.ROOT) :
                                condition.biomarkerType.toUpperCase(Locale.ROOT);
        final String key =
                node.getId() + "|" + conditionNodeId + "|" + labelKey + "|" + condition.citation + "|" + condition.age +
                "|" + condition.sex + "|" + condition.biofluid + "|" + condition.concentration;
        if (addedRelationshipKeys.contains(key))
            return;
        addedRelationshipKeys.add(key);
        final var builder = graph.buildEdge(labelKey + "_BIOMARKER_FOR");
        builder.fromNode(node);
        builder.toNode(conditionNodeId);
        transformCitationForEdgeBuilder(builder, condition.citation);
        builder.withPropertyIfNotNull("age", condition.age);
        builder.withPropertyIfNotNull("sex", condition.sex);
        builder.withPropertyIfNotNull("biofluid", condition.biofluid);
        builder.withPropertyIfNotNull("concentration", condition.concentration);
        builder.build();
    }

    private void exportProtein(final Graph graph, final ProteinSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(PROTEIN_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry, node);
    }

    private void exportKaryotype(final Graph graph, final KaryotypeSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(KARYOTYPE_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry, node);
    }

    private void exportGene(final Graph graph, final GeneSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(GENE_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        exportAndConnectConditions(graph, entry, node);
    }
}
