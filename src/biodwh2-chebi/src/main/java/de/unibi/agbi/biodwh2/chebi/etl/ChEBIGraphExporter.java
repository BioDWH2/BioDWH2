package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.chebi.ChEBIDataSource;
import de.unibi.agbi.biodwh2.chebi.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChEBIGraphExporter extends GraphExporter<ChEBIDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(ChEBIGraphExporter.class);
    public static final String COMPOUND_LABEL = "Compound";
    public static final String STRUCTURE_LABEL = "Structure";
    public static final String SPECIES_LABEL = "Species";
    public static final String CHEMICAL_DATA_LABEL = "ChemicalData";

    public ChEBIGraphExporter(final ChEBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(STRUCTURE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CHEMICAL_DATA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<Integer, String> sourcePrefixes = collectSourcePrefixes(workspace);
        final Map<Integer, String> statusMap = collectStatus(workspace);
        final Map<Integer, RelationType> relationTypesMap = collectRelationTypes(workspace);
        final Map<Integer, List<DBAccession>> compoundXrefsMap = collectCompoundXrefs(workspace);
        final Map<Integer, List<Name>> compoundNamesMap = collectCompoundNames(workspace);
        final Map<Integer, List<String>> compoundReferencesMap = collectCompoundReferences(workspace, sourcePrefixes);
        final Map<Integer, List<Integer>> compoundSecondaryIdsMap = collectCompoundSecondaryIds(workspace);
        exportCompounds(workspace, graph, sourcePrefixes, statusMap, compoundXrefsMap, compoundNamesMap,
                        compoundReferencesMap, compoundSecondaryIdsMap);
        compoundXrefsMap.clear();
        compoundNamesMap.clear();
        compoundReferencesMap.clear();
        exportStructures(workspace, graph, statusMap);
        exportRelations(workspace, graph, sourcePrefixes, statusMap, relationTypesMap);
        exportChemicalData(workspace, graph, statusMap);
        exportCompoundOrigins(workspace, graph, sourcePrefixes, statusMap);
        // ignored: comments
        // TODO: WURCS
        return true;
    }

    private Map<Integer, String> collectSourcePrefixes(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting sources...");
        final Map<Integer, String> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.SOURCE_FILE_NAME, Source.class,
                                            (entry) -> result.put(entry.id,
                                                                  StringUtils.isNotEmpty(entry.prefix) ? entry.prefix :
                                                                  entry.name));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.SOURCE_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, String> collectStatus(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting status...");
        final Map<Integer, String> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.STATUS_FILE_NAME, Status.class,
                                            (entry) -> result.put(entry.id, entry.name));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.STATUS_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, RelationType> collectRelationTypes(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting relation types...");
        final Map<Integer, RelationType> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.RELATION_TYPE_FILE_NAME,
                                            RelationType.class, (entry) -> result.put(entry.id, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.RELATION_TYPE_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, List<DBAccession>> collectCompoundXrefs(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound xrefs...");
        final Map<Integer, List<DBAccession>> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.DATABASE_ACCESSION_FILE_NAME,
                                            DBAccession.class, (entry) -> result.computeIfAbsent(entry.compoundId,
                                                                                                 (id) -> new ArrayList<>())
                                                                                .add(entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.DATABASE_ACCESSION_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, List<Name>> collectCompoundNames(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound names...");
        final Map<Integer, List<Name>> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.NAMES_FILE_NAME, Name.class,
                                            (entry) -> result.computeIfAbsent(entry.compoundId,
                                                                              (id) -> new ArrayList<>()).add(entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.NAMES_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, List<String>> collectCompoundReferences(final Workspace workspace,
                                                                 final Map<Integer, String> sourcePrefixes) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound references...");
        final Map<Integer, List<String>> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeaderWithoutQuoting(workspace, dataSource, ChEBIUpdater.REFERENCE_FILE_NAME,
                                                          Reference.class,
                                                          (entry) -> result.computeIfAbsent(entry.compoundId,
                                                                                            (id) -> new ArrayList<>())
                                                                           .add(entry.id + '|' +
                                                                                sourcePrefixes.get(entry.sourceId) +
                                                                                '|' + entry.locationInRef + '|' +
                                                                                entry.accessionNumber + '|' +
                                                                                entry.referenceName));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.REFERENCE_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, List<Integer>> collectCompoundSecondaryIds(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound secondary IDs...");
        final Map<Integer, List<Integer>> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.SECONDARY_IDS_FILE_NAME,
                                            SecondaryID.class, (entry) -> result.computeIfAbsent(entry.compoundId,
                                                                                                 (id) -> new ArrayList<>())
                                                                                .add(entry.secondaryId));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.SECONDARY_IDS_FILE_NAME + "'", e);
        }
        return result;
    }

    private void exportCompounds(final Workspace workspace, final Graph graph,
                                 final Map<Integer, String> sourcePrefixes, final Map<Integer, String> statusMap,
                                 final Map<Integer, List<DBAccession>> compoundXrefsMap,
                                 final Map<Integer, List<Name>> compoundNamesMap,
                                 final Map<Integer, List<String>> compoundReferencesMap,
                                 final Map<Integer, List<Integer>> compoundSecondaryIdsMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting compounds...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.COMPOUNDS_FILE_NAME, Compound.class,
                                            (entry) -> exportCompound(graph, sourcePrefixes, statusMap,
                                                                      compoundXrefsMap, compoundNamesMap,
                                                                      compoundReferencesMap, compoundSecondaryIdsMap,
                                                                      entry));
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.COMPOUNDS_FILE_NAME, Compound.class,
                                            (entry) -> exportCompoundChildOfRelation(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.COMPOUNDS_FILE_NAME + "'", e);
        }
    }

    private static void exportCompound(final Graph graph, final Map<Integer, String> sourcePrefixes,
                                       final Map<Integer, String> statusMap,
                                       final Map<Integer, List<DBAccession>> compoundXrefsMap,
                                       final Map<Integer, List<Name>> compoundNamesMap,
                                       final Map<Integer, List<String>> compoundReferencesMap,
                                       final Map<Integer, List<Integer>> compoundSecondaryIdsMap,
                                       final Compound entry) {
        final Map<String, Object> properties = new HashMap<>();
        final List<DBAccession> xrefs = compoundXrefsMap.get(entry.id);
        if (xrefs != null) {
            final String[] xrefsArray = xrefs.stream().map(
                    (x) -> sourcePrefixes.get(x.sourceId) + '|' + x.type + '|' + statusMap.get(x.statusId) + '|' +
                           x.accessionNumber).toArray(String[]::new);
            properties.put("xrefs", xrefsArray);
            addSpecificXrefTypeToProperties(sourcePrefixes, xrefs, properties, "cas", "cas_registry_number");
            addSpecificXrefTypeToProperties(sourcePrefixes, xrefs, properties, "drugbank", "drugbank_id");
            addSpecificXrefTypeToProperties(sourcePrefixes, xrefs, properties, "kegg.drug", "kegg_drug");
            addSpecificXrefTypeToProperties(sourcePrefixes, xrefs, properties, "kegg.compound", "kegg_compound");
            addSpecificXrefTypeToProperties(sourcePrefixes, xrefs, properties, "drugcentral", "drugcentral_id");
        }
        final List<Name> names = compoundNamesMap.get(entry.id);
        if (names != null) {
            final String[] namesArray = names.stream().map(
                    (x) -> x.source + '|' + x.type + '|' + statusMap.get(x.statusId) + '|' + x.languageCode + '|' +
                           x.adapted + '|' + x.name).toArray(String[]::new);
            properties.put("names", namesArray);
        }
        final List<String> references = compoundReferencesMap.get(entry.id);
        if (references != null)
            properties.put("references", references.toArray(new String[0]));
        final List<Integer> secondaryIds = compoundSecondaryIdsMap.get(entry.id);
        if (secondaryIds != null)
            properties.put("secondary_ids", secondaryIds.toArray(new Integer[0]));
        properties.put("status", statusMap.get(entry.statusId));
        graph.addNodeFromModel(entry, properties);
    }

    private static void addSpecificXrefTypeToProperties(final Map<Integer, String> sourcePrefixes,
                                                        final List<DBAccession> xrefs,
                                                        final Map<String, Object> properties, final String source,
                                                        final String propertyKey) {
        final String[] casNumbers = xrefs.stream().filter((x) -> source.equals(sourcePrefixes.get(x.sourceId))).map(
                (x) -> x.accessionNumber).distinct().toArray(String[]::new);
        if (casNumbers.length > 1)
            properties.put(propertyKey + 's', casNumbers);
        else if (casNumbers.length == 1)
            properties.put(propertyKey, casNumbers[0]);
    }

    private void exportCompoundChildOfRelation(final Graph graph, final Compound entry) {
        if (entry.parentId != null) {
            final Node node = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.id);
            final Node parentNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.parentId);
            graph.addEdge(node, parentNode, "CHILD_OF");
        }
    }

    private void exportStructures(final Workspace workspace, final Graph graph, final Map<Integer, String> statusMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting structures...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.STRUCTURES_FILE_NAME, Structure.class,
                                            (entry) -> exportStructure(graph, entry, statusMap));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.STRUCTURES_FILE_NAME + "'", e);
        }
    }

    private void exportStructure(final Graph graph, final Structure entry, final Map<Integer, String> statusMap) {
        Node structureNode = graph.findNode(STRUCTURE_LABEL, ID_KEY, entry.id);
        if (structureNode == null)
            structureNode = graph.addNodeFromModel(entry, "status", statusMap.get(entry.statusId));
        final Node compoundNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.compoundId);
        if (!graph.containsEdge("HAS_STRUCTURE", compoundNode, structureNode))
            graph.addEdge(compoundNode, structureNode, "HAS_STRUCTURE");
    }

    private void exportRelations(final Workspace workspace, final Graph graph,
                                 final Map<Integer, String> sourcePrefixes, final Map<Integer, String> statusMap,
                                 final Map<Integer, RelationType> relationTypesMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting compound relations...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.RELATION_FILE_NAME, Relation.class,
                                            (entry) -> exportRelation(graph, sourcePrefixes, statusMap,
                                                                      relationTypesMap, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.RELATION_FILE_NAME + "'", e);
        }
    }

    private void exportRelation(final Graph graph, final Map<Integer, String> sourcePrefixes,
                                final Map<Integer, String> statusMap, final Map<Integer, RelationType> relationTypesMap,
                                final Relation entry) {
        final Node firstNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.initId);
        final Node secondNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.finalId);
        final RelationType type = relationTypesMap.get(entry.relationTypeId);
        graph.addEdge(firstNode, secondNode, type.code.toUpperCase(), "status", statusMap.get(entry.statusId),
                      "evidence_accession", entry.evidenceAccession, "evidence_source",
                      sourcePrefixes.get(entry.evidenceSourceId));
    }

    private void exportChemicalData(final Workspace workspace, final Graph graph,
                                    final Map<Integer, String> statusMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting chemical data...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.CHEMICAL_DATA_FILE_NAME,
                                            ChemicalData.class, (entry) -> {
                        final Node compoundNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.compoundId);
                        final Node chemicalDataNode = graph.addNodeFromModel(entry, "status",
                                                                             statusMap.get(entry.statusId));
                        graph.addEdge(compoundNode, chemicalDataNode, "HAS_CHEMICAL_DATA");
                        if (entry.structureId != null) {
                            final Node structureNode = graph.findNode(STRUCTURE_LABEL, ID_KEY, entry.structureId);
                            graph.addEdge(structureNode, chemicalDataNode, "HAS_CHEMICAL_DATA");
                        }
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.CHEMICAL_DATA_FILE_NAME + "'", e);
        }
    }

    private void exportCompoundOrigins(final Workspace workspace, final Graph graph,
                                       final Map<Integer, String> sourcePrefixes,
                                       final Map<Integer, String> statusMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting compound origins...");
        final Map<Integer, Map<String, Long>> speciesMap = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.COMPOUND_ORIGINS_FILE_NAME,
                                            CompoundOrigin.class, (entry) -> {
                        var speciesAccessionMap = speciesMap.computeIfAbsent(entry.speciesSourceId,
                                                                             (k) -> new HashMap<>());
                        var speciesNodeId = speciesAccessionMap.get(entry.speciesAccession);
                        if (speciesNodeId == null) {
                            final var speciesSource = sourcePrefixes.get(entry.speciesSourceId);
                            speciesNodeId = graph.addNode(SPECIES_LABEL, ID_KEY,
                                                          speciesSource + ":" + entry.speciesAccession, "description",
                                                          entry.speciesText).getId();
                            speciesAccessionMap.put(entry.speciesAccession, speciesNodeId);
                        }

                        final Node compoundNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.compoundId);
                        if (compoundNode == null) {
                            return;
                        }
                        final var builder = graph.buildEdge("HAS_ORIGIN").fromNode(compoundNode).toNode(speciesNodeId);
                        builder.withModel(entry);
                        builder.withPropertyIfNotNull("source", sourcePrefixes.get(entry.sourceId));
                        builder.withPropertyIfNotNull("component_source", sourcePrefixes.get(entry.componentSourceId));
                        builder.withPropertyIfNotNull("status", statusMap.get(entry.statusId));
                        builder.build();
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.COMPOUND_ORIGINS_FILE_NAME + "'", e);
        }
    }
}
