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
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class ChEBIGraphExporter extends GraphExporter<ChEBIDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(ChEBIGraphExporter.class);
    public static final String COMPOUND_LABEL = "Compound";
    public static final String STRUCTURE_LABEL = "Structure";
    public static final String ORIGIN_LABEL = "Origin";

    public ChEBIGraphExporter(final ChEBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(STRUCTURE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORIGIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<Integer, String> compoundInchiMap = collectCompoundInchi(workspace);
        final Map<Integer, List<DBAccession>> compoundXrefsMap = collectCompoundXrefs(workspace);
        final Map<Integer, List<Name>> compoundNamesMap = collectCompoundNames(workspace);
        final Map<Integer, List<String>> compoundReferencesMap = collectCompoundReferences(workspace);
        exportCompounds(workspace, graph, compoundInchiMap, compoundXrefsMap, compoundNamesMap, compoundReferencesMap);
        compoundInchiMap.clear();
        compoundXrefsMap.clear();
        compoundReferencesMap.clear();
        exportStructures(workspace, graph);
        exportRelations(workspace, graph);
        exportChemicalData(workspace, graph);
        exportCompoundOrigins(workspace, graph);
        return true;
    }

    private Map<Integer, String> collectCompoundInchi(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound InChI...");
        final Map<Integer, String> result = new HashMap<>();
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.INCHI_FILE_NAME, ChEBIIdInchi.class,
                                        (entry) -> result.put(entry.chebiId, entry.inchi));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.INCHI_FILE_NAME + "'", e);
        }
        return result;
    }

    private Map<Integer, List<DBAccession>> collectCompoundXrefs(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound xrefs...");
        final Map<Integer, List<DBAccession>> result = new HashMap<>();
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.DATABASE_ACCESSION_FILE_NAME,
                                        DBAccession.class,
                                        (entry) -> result.computeIfAbsent(entry.compoundId, (id) -> new ArrayList<>())
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

    private Map<Integer, List<String>> collectCompoundReferences(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting compound references...");
        final Map<Integer, List<String>> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeaderWithoutQuoting(workspace, dataSource, ChEBIUpdater.REFERENCE_FILE_NAME,
                                                          Reference.class,
                                                          (entry) -> result.computeIfAbsent(entry.compoundId,
                                                                                            (id) -> new ArrayList<>())
                                                                           .add(entry.referenceId + '|' +
                                                                                entry.referenceDbName + '|' +
                                                                                entry.locationInRef + '|' +
                                                                                entry.referenceName));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.REFERENCE_FILE_NAME + "'", e);
        }
        return result;
    }

    private void exportCompounds(final Workspace workspace, final Graph graph,
                                 final Map<Integer, String> compoundInchiMap,
                                 final Map<Integer, List<DBAccession>> compoundXrefsMap,
                                 final Map<Integer, List<Name>> compoundNamesMap,
                                 final Map<Integer, List<String>> compoundReferencesMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting compounds...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.COMPOUNDS_FILE_NAME, Compound.class,
                                            (entry) -> exportCompound(graph, compoundInchiMap, compoundXrefsMap,
                                                                      compoundNamesMap, compoundReferencesMap, entry));
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.COMPOUNDS_FILE_NAME, Compound.class,
                                            (entry) -> exportCompoundChildOfRelation(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.COMPOUNDS_FILE_NAME + "'", e);
        }
    }

    private static void exportCompound(final Graph graph, final Map<Integer, String> compoundInchiMap,
                                       final Map<Integer, List<DBAccession>> compoundXrefsMap,
                                       final Map<Integer, List<Name>> compoundNamesMap,
                                       final Map<Integer, List<String>> compoundReferencesMap, final Compound entry) {
        final Map<String, Object> properties = new HashMap<>();
        final String inchi = compoundInchiMap.get(entry.id);
        if (inchi != null)
            properties.put("inchi", inchi);
        final List<DBAccession> xrefs = compoundXrefsMap.get(entry.id);
        if (xrefs != null) {
            final String[] xrefsArray = xrefs.stream().map((x) -> x.source + '|' + x.type + '|' + x.accessionNumber)
                                             .toArray(String[]::new);
            properties.put("xrefs", xrefsArray);
            addSpecificXrefTypeToProperties(xrefs, properties, "CAS Registry Number", "cas_registry_number");
            addSpecificXrefTypeToProperties(xrefs, properties, "DrugBank accession", "drugbank_id");
            addSpecificXrefTypeToProperties(xrefs, properties, "KEGG DRUG accession", "kegg_drug");
            addSpecificXrefTypeToProperties(xrefs, properties, "KEGG COMPOUND accession", "kegg_compound");
            addSpecificXrefTypeToProperties(xrefs, properties, "Drug Central accession", "drugcentral_id");
        }
        final List<Name> names = compoundNamesMap.get(entry.id);
        if (names != null) {
            final String[] namesArray = names.stream().map(
                    (x) -> x.source + '|' + x.type + '|' + x.language + '|' + x.adapted + '|' + x.name).toArray(
                    String[]::new);
            properties.put("names", namesArray);
        }
        final List<String> references = compoundReferencesMap.get(entry.id);
        if (references != null)
            properties.put("references", references.toArray(new String[0]));
        graph.addNodeFromModel(entry, properties);
    }

    private static void addSpecificXrefTypeToProperties(final List<DBAccession> xrefs,
                                                        final Map<String, Object> properties, final String type,
                                                        final String propertyKey) {
        final String[] casNumbers = xrefs.stream().filter((x) -> type.equals(x.type)).map((x) -> x.accessionNumber)
                                         .distinct().toArray(String[]::new);
        if (casNumbers.length > 1)
            properties.put(propertyKey + 's', casNumbers);
        else if (casNumbers.length == 1)
            properties.put(propertyKey, casNumbers[0]);
    }

    private void exportCompoundChildOfRelation(final Graph graph, final Compound entry) {
        if (entry.parentId != null && !"null".equals(entry.parentId)) {
            final Node node = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.id);
            final Node parentNode = graph.findNode(COMPOUND_LABEL, ID_KEY, Integer.parseInt(entry.parentId));
            graph.addEdge(node, parentNode, "CHILD_OF");
        }
    }

    private void exportStructures(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting structures...");
        try {
            FileUtils.openGzipCsvWithHeader(workspace, dataSource, ChEBIUpdater.STRUCTURES_FILE_NAME, Structure.class,
                                            (entry) -> exportStructure(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.STRUCTURES_FILE_NAME + "'", e);
        }
    }

    private void exportStructure(final Graph graph, final Structure entry) {
        Node structureNode = graph.findNode(STRUCTURE_LABEL, ID_KEY, entry.id);
        if (structureNode == null)
            structureNode = graph.addNodeFromModel(entry);
        final Node compoundNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.compoundId);
        if (!graph.containsEdge("HAS_STRUCTURE", compoundNode, structureNode))
            graph.addEdge(compoundNode, structureNode, "HAS_STRUCTURE");
    }

    private void exportRelations(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting compound relations...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.RELATION_FILE_NAME, Relation.class,
                                        (entry) -> exportRelation(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.RELATION_FILE_NAME + "'", e);
        }
    }

    private void exportRelation(final Graph graph, final Relation entry) {
        final Node firstNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.initId);
        final Node secondNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.finalId);
        graph.addEdge(firstNode, secondNode, entry.type.toUpperCase(), "status", entry.status);
    }

    private void exportChemicalData(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting chemical data...");
        final Map<Integer, Map<String, List<Map.Entry<String, String>>>> compoundChemicalDataMap = new HashMap<>();
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.CHEMICAL_DATA_FILE_NAME, ChemicalData.class,
                                        (entry) -> {
                                            final var sourceChemicalDataMap = compoundChemicalDataMap.computeIfAbsent(
                                                    entry.compoundId, (id) -> new HashMap<>());
                                            final var sources = sourceChemicalDataMap.computeIfAbsent(entry.source,
                                                                                                      (type) -> new ArrayList<>());
                                            sources.add(new AbstractMap.SimpleEntry<>(entry.type, entry.chemicalData));
                                        });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.CHEMICAL_DATA_FILE_NAME + "'", e);
        }
        for (final var compoundEntry : compoundChemicalDataMap.entrySet()) {
            final Node compoundNode = graph.findNode(COMPOUND_LABEL, ID_KEY, compoundEntry.getKey());
            for (final var sourceChemicalDataMap : compoundEntry.getValue().entrySet()) {
                final NodeBuilder builder = graph.buildNode().withLabel("ChemicalData");
                builder.withPropertyIfNotNull("source", sourceChemicalDataMap.getKey());
                for (final var chemicalData : sourceChemicalDataMap.getValue()) {
                    final String key = StringUtils.replace(chemicalData.getKey().toLowerCase(), " ", "_");
                    builder.withPropertyIfNotNull(key, chemicalData.getValue());
                }
                final Node chemicalDataNode = builder.build();
                graph.addEdge(compoundNode, chemicalDataNode, "HAS_CHEMICAL_DATA");
            }
        }
    }

    private void exportCompoundOrigins(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting compound origins...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.COMPOUND_ORIGINS_FILE_NAME,
                                        CompoundOrigin.class, (entry) -> {
                        final Node compoundNode = graph.findNode(COMPOUND_LABEL, ID_KEY, entry.compoundId);
                        final Node node = graph.addNodeFromModel(entry);
                        graph.addEdge(compoundNode, node, "HAS_ORIGIN");
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + ChEBIUpdater.COMPOUND_ORIGINS_FILE_NAME + "'", e);
        }
    }
}
