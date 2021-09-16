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
import java.util.*;

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
            LOGGER.info("Collecting drug atc codes...");
        populateCombinedDrugsWithATC(workspace, flatIdDrugMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting drug names...");
        populateCombinedDrugsWithName(workspace, flatIdDrugMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting indications...");
        addAllIndications(workspace, graph, flatIdDrugMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting side effects...");
        addAllSideEffects(workspace, graph, flatIdDrugMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting side effect frequencies...");
        addAllSideEffectFrequencies(workspace, graph, flatIdDrugMap);
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

    private void addAllIndications(final Workspace workspace, final Graph graph,
                                   final Map<String, CombinedDrug> flatIdDrugMap) throws ExporterException {
        for (final Indication indication : parseGzipTsvFile(workspace, SiderUpdater.INDICATIONS_FILE_NAME,
                                                            Indication.class)) {
            if (skipConcept(indication.meddraConceptType))
                continue;
            final Node drugNode = getOrAddDrugNode(graph, flatIdDrugMap, indication.flatCompoundId,
                                                   indication.stereoCompoundId);
            final Node termNode = getOrAddTermNode(graph, indication.umlsConceptId, indication.conceptName,
                                                   indication.meddraUmlsConceptId, indication.meddraConceptName);
            graph.addEdge(drugNode, termNode, "INDICATES", "label", indication.label, "detection_method",
                          indication.detectionMethod);
        }
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

    private boolean skipConcept(final String conceptType) {
        return StringUtils.isNotEmpty(conceptType) && !"PT".equals(conceptType);
    }

    private Node getOrAddDrugNode(final Graph graph, final Map<String, CombinedDrug> flatIdDrugMap, final String flatId,
                                  final String stereoId) {
        Node node = graph.findNode(DRUG_LABEL, STEREO_ID_KEY, stereoId);
        if (node == null) {
            final CombinedDrug drug = flatIdDrugMap.get(flatId);
            if (drug != null) {
                if (drug.name != null && drug.atcCodes.size() > 0)
                    node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, stereoId, FLAT_ID_KEY, flatId, "name", drug.name,
                                         ATC_CODES_KEY, drug.atcCodes.toArray(new String[0]));
                else if (drug.name != null)
                    node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, stereoId, FLAT_ID_KEY, flatId, "name", drug.name);
                else if (drug.atcCodes.size() > 0)
                    node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, stereoId, FLAT_ID_KEY, flatId, ATC_CODES_KEY,
                                         drug.atcCodes.toArray(new String[0]));
            } else
                node = graph.addNode(DRUG_LABEL, STEREO_ID_KEY, stereoId, FLAT_ID_KEY, flatId);
        }
        return node;
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
            final Iterable<Node> umlsTermNodes = graph.findNodes(MEDDRA_TERM_LABEL, UMLS_ID_KEY, umlsConceptId);
            for (final Node candidate : umlsTermNodes)
                if (candidate.getProperty(MEDDRA_ID_KEY) == null)
                    termNode = candidate;
            if (termNode == null)
                termNode = graph.addNode(MEDDRA_TERM_LABEL, UMLS_ID_KEY, umlsConceptId, "umls_name", umlsConceptName);
        }
        return termNode;
    }

    private void addAllSideEffects(final Workspace workspace, final Graph graph,
                                   final Map<String, CombinedDrug> flatIdDrugMap) throws ExporterException {
        for (final SideEffect sideEffect : parseGzipTsvFile(workspace, SiderUpdater.SIDE_EFFECTS_FILE_NAME,
                                                            SideEffect.class)) {
            if (skipConcept(sideEffect.meddraConceptType))
                continue;
            final Node drugNode = getOrAddDrugNode(graph, flatIdDrugMap, sideEffect.flatCompoundId,
                                                   sideEffect.stereoCompoundId);
            final Node termNode = getOrAddTermNode(graph, sideEffect.umlsConceptId, null,
                                                   sideEffect.meddraUmlsConceptId, sideEffect.sideEffectName);
            graph.addEdge(drugNode, termNode, "HAS_SIDE_EFFECT", "label", sideEffect.label);
        }
    }

    private void addAllSideEffectFrequencies(final Workspace workspace, final Graph graph,
                                             final Map<String, CombinedDrug> flatIdDrugMap) throws ExporterException {
        for (final Frequency frequency : parseGzipTsvFile(workspace, SiderUpdater.FREQUENCIES_FILE_NAME,
                                                          Frequency.class)) {
            if (skipConcept(frequency.meddraConceptType))
                continue;
            final Node drugNode = getOrAddDrugNode(graph, flatIdDrugMap, frequency.flatCompoundId,
                                                   frequency.stereoCompoundId);
            final Node termNode = getOrAddTermNode(graph, frequency.umlsConceptId, null, frequency.meddraUmlsConceptId,
                                                   frequency.sideEffectName);
            EdgeBuilder builder = graph.buildEdge().fromNode(drugNode).toNode(termNode);
            builder.withLabel("HAS_SIDE_EFFECT_FREQUENCY");
            builder.withProperty("frequency", frequency.frequency);
            builder.withProperty("frequency_lower_bound", frequency.frequencyLowerBound);
            builder.withProperty("frequency_upper_bound", frequency.frequencyUpperBound);
            if (frequency.placebo != null)
                builder.withProperty("placebo", frequency.placebo.equalsIgnoreCase("placebo"));
            builder.build();
        }
    }
}
