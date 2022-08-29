package de.unibi.agbi.biodwh2.sider.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;
import de.unibi.agbi.biodwh2.sider.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MedDRA concept types are hierarchical: PT - Represents a single medical concept LLT - Lowest level of the
 * terminology, related to a single PT as a synonym, lexical variant, or quasisynonym (Note: All PTs have an identical
 * LLT)
 * <p>
 * Compound ids are in the old STITCH format with CID0 for stereo and CID1 for flat (merged) compounds. The newer format
 * changed to CIDs (single) and CIDm (merged). More info: https://www.biostars.org/p/155342/
 */
public class SiderGraphExporter extends GraphExporter<SiderDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiderGraphExporter.class);
    private static final String DRUG_LABEL = "Drug";
    private static final String MEDDRA_TERM_LABEL = "MeddraTerm";
    private static final String UMLS_ID_KEY = "umls_id";
    private static final String MEDDRA_ID_KEY = "meddra_id";
    private static final String FLAT_ID_KEY = "flat_id";
    private static final String STEREO_ID_KEY = "stereo_id";
    private static final String ATC_CODES_KEY = "atc_codes";
    private static final String HAS_SIDE_EFFECT_LABEL = "HAS_SIDE_EFFECT";
    private static final String HAS_SIDE_EFFECT_FREQUENCY_LABEL = "HAS_SIDE_EFFECT_FREQUENCY";

    public SiderGraphExporter(final SiderDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, FLAT_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, STEREO_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(MEDDRA_TERM_LABEL, UMLS_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(MEDDRA_TERM_LABEL, MEDDRA_ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, CombinedDrug> flatIdDrugMap = new HashMap<>();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drugs...");
        populateCombinedDrugsWithATC(workspace, flatIdDrugMap);
        populateCombinedDrugsWithName(workspace, flatIdDrugMap);
        final Map<String, Long> flatIdNodeIdMap = exportDrugs(workspace, graph, flatIdDrugMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting indications...");
        addAllIndications(workspace, graph, flatIdNodeIdMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting side effects...");
        addAllSideEffects(workspace, graph, flatIdNodeIdMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting side effect frequencies...");
        addAllSideEffectFrequencies(workspace, graph, flatIdNodeIdMap);
        return true;
    }

    private void populateCombinedDrugsWithATC(final Workspace workspace,
                                              final Map<String, CombinedDrug> flatIdDrugMap) {
        for (final DrugAtc atcCode : parseTsvFile(workspace, SiderUpdater.DRUG_ATC_FILE_NAME, DrugAtc.class)) {
            if (!flatIdDrugMap.containsKey(atcCode.id))
                flatIdDrugMap.put(atcCode.id, new CombinedDrug(atcCode.id));
            flatIdDrugMap.get(atcCode.id).atcCodes.add(atcCode.atc);
        }
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final String fileName,
                                         final Class<T> typeClass) throws ExporterException {
        try {
            final MappingIterator<T> iterator = FileUtils.openTsv(workspace, dataSource, fileName, typeClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void populateCombinedDrugsWithName(final Workspace workspace,
                                               final Map<String, CombinedDrug> flatIdDrugMap) {
        for (final DrugName drug : parseTsvFile(workspace, SiderUpdater.DRUG_NAMES_FILE_NAME, DrugName.class)) {
            if (!flatIdDrugMap.containsKey(drug.id))
                flatIdDrugMap.put(drug.id, new CombinedDrug(drug.id));
            flatIdDrugMap.get(drug.id).name = drug.name;
        }
    }

    private Map<String, Long> exportDrugs(final Workspace workspace, final Graph graph,
                                          final Map<String, CombinedDrug> flatIdDrugMap) {
        for (final Indication entry : parseGzipTsvFile(workspace, SiderUpdater.INDICATIONS_FILE_NAME,
                                                       Indication.class)) {
            updateDrugEntry(flatIdDrugMap, entry.flatCompoundId, entry.stereoCompoundId);
        }
        for (final SideEffect entry : parseGzipTsvFile(workspace, SiderUpdater.SIDE_EFFECTS_FILE_NAME,
                                                       SideEffect.class)) {
            updateDrugEntry(flatIdDrugMap, entry.flatCompoundId, entry.stereoCompoundId);
        }
        for (final Frequency entry : parseGzipTsvFile(workspace, SiderUpdater.FREQUENCIES_FILE_NAME, Frequency.class)) {
            updateDrugEntry(flatIdDrugMap, entry.flatCompoundId, entry.stereoCompoundId);
        }
        final Map<String, Long> flatIdNodeIdMap = new HashMap<>();
        for (final CombinedDrug drug : flatIdDrugMap.values()) {
            final Node node;
            if (drug.name != null && drug.atcCodes.size() > 0)
                node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, drug.stereoId, FLAT_ID_KEY, drug.flatId, "name",
                                     drug.name, ATC_CODES_KEY, drug.atcCodes.toArray(new String[0]));
            else if (drug.name != null)
                node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, drug.stereoId, FLAT_ID_KEY, drug.flatId, "name",
                                     drug.name);
            else if (drug.atcCodes.size() > 0)
                node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, drug.stereoId, FLAT_ID_KEY, drug.flatId, ATC_CODES_KEY,
                                     drug.atcCodes.toArray(new String[0]));
            else
                node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, drug.stereoId, FLAT_ID_KEY, drug.flatId);
            flatIdNodeIdMap.put(drug.flatId, node.getId());
        }
        return flatIdNodeIdMap;
    }

    private <T> Iterable<T> parseGzipTsvFile(final Workspace workspace, final String fileName,
                                             final Class<T> typeClass) throws ExporterException {
        try {
            final MappingIterator<T> iterator = FileUtils.openGzipTsv(workspace, dataSource, fileName, typeClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void updateDrugEntry(final Map<String, CombinedDrug> flatIdDrugMap, final String flatId,
                                 final String stereoId) {
        CombinedDrug drug = flatIdDrugMap.get(flatId);
        if (drug == null) {
            drug = new CombinedDrug(flatId);
            flatIdDrugMap.put(flatId, drug);
        }
        drug.stereoId = drug.stereoId != null ? drug.stereoId : stereoId;
    }

    private void addAllIndications(final Workspace workspace, final Graph graph,
                                   final Map<String, Long> flatIdNodeIdMap) throws ExporterException {
        for (final Indication indication : parseGzipTsvFile(workspace, SiderUpdater.INDICATIONS_FILE_NAME,
                                                            Indication.class)) {
            if (skipConcept(indication.meddraConceptType))
                continue;
            final Node termNode = getOrAddTermNode(graph, indication.umlsConceptId, indication.conceptName,
                                                   indication.meddraUmlsConceptId, indication.meddraConceptName);
            graph.addEdge(flatIdNodeIdMap.get(indication.flatCompoundId), termNode, "INDICATES", "label",
                          indication.label, "detection_method", indication.detectionMethod);
        }
    }

    private boolean skipConcept(final String conceptType) {
        return StringUtils.isNotEmpty(conceptType) && !"PT".equals(conceptType);
    }

    private Node getOrAddTermNode(final Graph graph, final String umlsConceptId, final String umlsConceptName,
                                  final String meddraUmlsConceptId, final String meddraConceptName) {
        Node termNode = null;
        if (StringUtils.isNotEmpty(meddraUmlsConceptId)) {
            termNode = graph.findNode(MEDDRA_TERM_LABEL, MEDDRA_ID_KEY, meddraUmlsConceptId);
            if (termNode == null) {
                termNode = graph.addNode(MEDDRA_TERM_LABEL, MEDDRA_ID_KEY, meddraUmlsConceptId, UMLS_ID_KEY,
                                         umlsConceptId, "meddra_name", meddraConceptName, "umls_name", umlsConceptName);
            }
        } else {
            for (final Node candidate : graph.findNodes(MEDDRA_TERM_LABEL, UMLS_ID_KEY, umlsConceptId))
                if (candidate.getProperty(MEDDRA_ID_KEY) == null) {
                    termNode = candidate;
                    break;
                }
            if (termNode == null)
                termNode = graph.addNode(MEDDRA_TERM_LABEL, UMLS_ID_KEY, umlsConceptId, "umls_name", umlsConceptName);
        }
        return termNode;
    }

    private void addAllSideEffects(final Workspace workspace, final Graph graph,
                                   final Map<String, Long> flatIdNodeIdMap) throws ExporterException {
        graph.beginEdgeIndicesDelay(HAS_SIDE_EFFECT_LABEL);
        for (final SideEffect sideEffect : parseGzipTsvFile(workspace, SiderUpdater.SIDE_EFFECTS_FILE_NAME,
                                                            SideEffect.class)) {
            if (skipConcept(sideEffect.meddraConceptType))
                continue;
            final Node termNode = getOrAddTermNode(graph, sideEffect.umlsConceptId, null,
                                                   sideEffect.meddraUmlsConceptId, sideEffect.sideEffectName);
            graph.addEdge(flatIdNodeIdMap.get(sideEffect.flatCompoundId), termNode, HAS_SIDE_EFFECT_LABEL, "label",
                          sideEffect.label);
        }
        graph.endEdgeIndicesDelay(HAS_SIDE_EFFECT_LABEL);
    }

    private void addAllSideEffectFrequencies(final Workspace workspace, final Graph graph,
                                             final Map<String, Long> flatIdNodeIdMap) throws ExporterException {
        graph.beginEdgeIndicesDelay(HAS_SIDE_EFFECT_FREQUENCY_LABEL);
        for (final Frequency frequency : parseGzipTsvFile(workspace, SiderUpdater.FREQUENCIES_FILE_NAME,
                                                          Frequency.class)) {
            if (skipConcept(frequency.meddraConceptType))
                continue;
            final Node termNode = getOrAddTermNode(graph, frequency.umlsConceptId, null, frequency.meddraUmlsConceptId,
                                                   frequency.sideEffectName);
            final EdgeBuilder builder = graph.buildEdge().fromNode(flatIdNodeIdMap.get(frequency.flatCompoundId))
                                             .toNode(termNode);
            builder.withLabel(HAS_SIDE_EFFECT_FREQUENCY_LABEL);
            builder.withProperty("frequency", frequency.frequency);
            builder.withProperty("frequency_lower_bound", frequency.frequencyLowerBound);
            builder.withProperty("frequency_upper_bound", frequency.frequencyUpperBound);
            if (frequency.placebo != null)
                builder.withProperty("placebo", frequency.placebo.equalsIgnoreCase("placebo"));
            builder.build();
        }
        graph.endEdgeIndicesDelay(HAS_SIDE_EFFECT_FREQUENCY_LABEL);
    }
}
