package de.unibi.agbi.biodwh2.ttd.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.XlsxMappingIterator;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.ttd.TTDDataSource;
import de.unibi.agbi.biodwh2.ttd.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TTDGraphExporter extends GraphExporter<TTDDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TTDGraphExporter.class);
    public static final String PATHWAY_LABEL = "Pathway";
    public static final String TARGET_LABEL = "Target";
    public static final String DRUG_LABEL = "Drug";
    public static final String COMPOUND_LABEL = "Compound";
    public static final String DISEASE_LABEL = "Disease";
    public static final String BIOMARKER_LABEL = "Biomarker";

    public TTDGraphExporter(final TTDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(BIOMARKER_LABEL, "id", IndexDescription.Type.UNIQUE));
        final Set<String> setOfPathwayIds = new HashSet<>();
        final Map<String, Target> idTargetMap = new HashMap<>();
        final Map<String, Drug> idDrugMap = new HashMap<>();
        final Map<String, List<String>> dictionaryTargetIdToPathways = new HashMap<>();
        final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo = new HashMap<>();
        // prepare drug information
        try {
            extractDrugsFromFlatFile(workspace, idDrugMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug", e);
        }
        try {
            extractDrugFlatFileCrossRef(workspace, idDrugMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug Cross", e);
        }
        try {
            extractDrugFlatFileSynonyms(workspace, idDrugMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug synonyms", e);
        }
        try {
            extractDrugInfoFromDrugDiseaseFlatFile(workspace, idDrugMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug disease", e);
        }
        //prepare Target information
        try {
            extractTargetFlatFile(workspace, graph, setOfPathwayIds, idTargetMap, dictionaryTargetIdToPathways,
                                  dictionaryTargetIdToDrugsToEdgeInfo, idDrugMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target", e);
        }
        try {
            extractTargetFlatFileUniProt(workspace, idTargetMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target uniprot", e);
        }
        try {
            extractTargetNameFromTargetDiseaseFlatFile(workspace, idTargetMap);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target disease", e);
        }
        addTargetToData(graph, idTargetMap);
        Set<String> setOfTargetIds = new HashSet<>(idTargetMap.keySet());
        idTargetMap.clear();
        // drug target edges
        final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToCompoundToEdgeInfo = exportTargetCompoundActivityTSV(
                workspace, graph, idDrugMap, setOfTargetIds, dictionaryTargetIdToDrugsToEdgeInfo);
        addDrugToData(graph, idDrugMap);
        final Set<String> setOfDrugIds = new HashSet<>(idDrugMap.keySet());
        idDrugMap.clear();
        exportTargetDrugExcel(workspace, graph, setOfDrugIds, setOfTargetIds, dictionaryTargetIdToDrugsToEdgeInfo);
        addTargetDrugEdges(graph, dictionaryTargetIdToDrugsToEdgeInfo, DRUG_LABEL);
        addTargetDrugEdges(graph, dictionaryTargetIdToCompoundToEdgeInfo, COMPOUND_LABEL);
        // pathway and target-pathway edges
        addTargetPathwayToData(graph, dictionaryTargetIdToPathways);
        exportPathwayKeggTargetTSV(workspace, graph, TTDUpdater.KEGG_PATHWAY_TO_TARGET_TSV, setOfPathwayIds,
                                   setOfTargetIds, dictionaryTargetIdToPathways);
        exportPathwayWikiTargetTSV(workspace, graph, TTDUpdater.WIKI_PATHWAY_TO_TARGET_TSV, setOfPathwayIds,
                                   setOfTargetIds, dictionaryTargetIdToPathways);
        dictionaryTargetIdToPathways.clear();
        // prepare disease and biomarker
        final Set<String> setOfDiseaseIds = exportBiomarkerDiseaseTSV(workspace, graph);
        // edges to disease
        try {
            extractTargetDiseaseFlatFile(workspace, graph, setOfDiseaseIds, setOfTargetIds);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target disease", e);
        }
        try {
            extractDrugDiseaseFlatFile(workspace, graph, setOfDiseaseIds);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File drug disease", e);
        }
        return true;
    }

    private void extractDrugsFromFlatFile(final Workspace workspace,
                                          final Map<String, Drug> idDrugMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_RAW_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String id = entry.getFirst("DRUG__ID");
            if (id != null) {
                final Drug drug = new Drug();
                drug.id = id;
                drug.tradeName = entry.getFirst("TRADNAME");
                drug.company = entry.getFirst("DRUGCOMP");
                drug.type = entry.getFirst("DRUGTYPE");
                drug.inchi = entry.getFirst("DRUGINCH");
                drug.inchiKey = entry.getFirst("DRUGINKE");
                drug.canonicalSmiles = entry.getFirst("DRUGSMIL");
                drug.highestStatus = entry.getFirst("HIGHSTAT");
                drug.adiID = entry.getFirst("DRUADIID");
                drug.therapeuticClass = entry.getFirst("THERCLAS");
                drug.drugClass = entry.getFirst("DRUGCLAS");
                drug.compoundClass = entry.getFirst("COMPCLAS");
                idDrugMap.put(id, drug);
            }
        }
    }

    /**
     * Parse tsv into an iterator of a given class.
     */
    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, fileName,
                                                                                    typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    /**
     * Go through biomarker disease tsv and generate biomarker and disease with the additional biomarker-disease edges.
     */
    private Set<String> exportBiomarkerDiseaseTSV(final Workspace workspace, final Graph graph) {
        final Set<String> setOfBiomarkerIds = new HashSet<>();
        final Set<String> setOfDiseaseIds = new HashSet<>();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Biomarker-Disease TSV ...");
        boolean foundHeader = false;
        for (final BiomarkerDisease entry : parseTsvFile(workspace, BiomarkerDisease.class,
                                                         TTDUpdater.BIOMARKER_DISEASE_TSV)) {
            final String targetId = entry.biomarkerId;
            if (targetId.equals("BiomarkerID")) {
                foundHeader = true;
                continue;
            }
            if (!foundHeader)
                continue;
            final String biomarkerId = entry.biomarkerId;
            final Node biomarker;
            if (!setOfBiomarkerIds.contains(biomarkerId)) {
                biomarker = graph.addNode(BIOMARKER_LABEL, "name", entry.biomarkerName, "id", biomarkerId);
                setOfBiomarkerIds.add(biomarkerId);
            } else {
                biomarker = graph.findNode(BIOMARKER_LABEL, "id", biomarkerId);
            }
            final String diseaseId = entry.icd11.replace("ICD-11: ", "");
            final Node disease;
            if (!setOfDiseaseIds.contains(diseaseId)) {
                setOfDiseaseIds.add(diseaseId);
                final NodeBuilder builder = graph.buildNode().withLabel(DISEASE_LABEL);
                builder.withProperty("id", diseaseId);
                builder.withProperty("ICD11", diseaseId.split(", "));
                builder.withProperty("name", entry.diseaseName);
                if (!".".equals(entry.icd9)) {
                    String[] icd9s = entry.icd9.replace("ICD-9: ", "").split(", ");
                    builder.withProperty("ICD9", icd9s);
                }
                if (!".".equals(entry.icd10)) {
                    String[] icd10s = entry.icd10.replace("ICD-10: ", "").split(", ");
                    builder.withProperty("ICD10", icd10s);
                }
                disease = builder.build();
            } else
                disease = graph.findNode(DISEASE_LABEL, "id", diseaseId);
            graph.addEdge(biomarker, disease, "ASSOCIATED_WITH");
        }
        return setOfDiseaseIds;
    }

    /**
     * Open the target-disease flat file and parse with TTD reader. The target is checked if exist or generate new and
     * for all disease the node is checked or created and a new edge is generated.
     */
    private boolean extractTargetDiseaseFlatFile(final Workspace workspace, final Graph graph,
                                                 final Set<String> setOfDiseaseIds,
                                                 final Set<String> setOfTargetIds) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Disease Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_DISEASE_FLAT_FILE),
                StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String targetId = entry.getFirst("TARGETID");
            if (targetId == null)
                continue;
            final Node target;
            if (!setOfTargetIds.contains(targetId)) {
                target = graph.addNode(TARGET_LABEL, "name", entry.getFirst("TARGNAME"), "id", targetId);
                setOfTargetIds.add(targetId);
            } else
                target = graph.findNode(TARGET_LABEL, "id", targetId);

            for (String diseaseInfo : entry.properties.get("INDICATI")) {
                String[] splitDiseaseInfo = diseaseInfo.split("\t");
                String diseaseNameId = splitDiseaseInfo[1];
                String[] splitDiseaseNameID = diseaseNameId.split(" \\[");
                String diseaseId = splitDiseaseNameID[1].substring(0, splitDiseaseNameID[1].length() - 1).replace(
                        "ICD-11: ", "");
                String clincalStatus = splitDiseaseInfo[0];
                Node disease;
                if (!setOfDiseaseIds.contains(diseaseId)) {
                    disease = graph.addNode(DISEASE_LABEL, "name", splitDiseaseInfo[1], "id", diseaseId, "ICD11",
                                            diseaseId.split(", "));
                    setOfDiseaseIds.add(diseaseId);
                } else
                    disease = graph.findNode(DISEASE_LABEL, "id", diseaseId);
                graph.addEdge(target, disease, "ASSOCIATED_WITH", "clinical_status", clincalStatus, "disease_name",
                              splitDiseaseInfo[1]);
            }
        }
        return true;
    }

    /**
     * Open the drug-disease flat file and parse with TTD reader. The  target is checked if exist or generate new and
     * for all disease the node is checked or created and a new edge is generated.
     */
    private boolean extractDrugDiseaseFlatFile(final Workspace workspace, final Graph graph,
                                               final Set<String> setOfDiseaseIds) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Disease Flat File ...");
        final FlatFileWithoutIDTTDReader reader = new FlatFileWithoutIDTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_DISEASE_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String drugId = entry.getFirst("TTDDRUID");
            if (drugId == null)
                continue;
            final Node drug = graph.findNode(DRUG_LABEL, "id", drugId);
            for (final String diseaseInfo : entry.properties.get("INDICATI")) {
                String[] splitDiseaseInfo = diseaseInfo.split(" \\[");
                String[] splitDiseaseIdAndClinicalStatus = splitDiseaseInfo[1].split("] ");
                String diseaseId = splitDiseaseIdAndClinicalStatus[0].replace("ICD-11: ", "");
                String clincalStatus = splitDiseaseIdAndClinicalStatus[1];
                Node disease;
                if (!setOfDiseaseIds.contains(diseaseId)) {
                    disease = graph.addNode(DISEASE_LABEL, "name", splitDiseaseInfo[0], "id", diseaseId, "ICD11",
                                            diseaseId.split(", "));
                    setOfDiseaseIds.add(diseaseId);
                } else
                    disease = graph.findNode(DISEASE_LABEL, "id", diseaseId);
                graph.addEdge(drug, disease, "INDICATES", "clinical_status", clincalStatus, "disease_name",
                              splitDiseaseInfo[0]);
            }
        }
        return true;
    }

    private Node findOrCreatePathwayNode(final Graph graph, final Set<String> setOfPathwayIds, final String pathwayId,
                                         final String name, final String source) {
        if (!setOfPathwayIds.contains(pathwayId)) {
            Node node = graph.addNode(PATHWAY_LABEL, "name", name, "id", pathwayId, "source", source);
            setOfPathwayIds.add(pathwayId);
            return node;
        }
        return graph.findNode(PATHWAY_LABEL, "id", pathwayId);
    }

    /**
     * go through kegg-target tsv file and if pair do not exist already add to graph. Also, if a node (target/pathway)
     * do not exist generate a new node.
     */
    private void exportPathwayKeggTargetTSV(final Workspace workspace, final Graph graph, String fileName,
                                            Set<String> setOfPathwayIds, Set<String> setOfTargetIds,
                                            final Map<String, List<String>> dictionaryTargetIdToPathways) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export KEGG pathway Target TSV ...");
        boolean foundHeader = false;
        for (final KEGGPathwayToTarget entry : parseTsvFile(workspace, KEGGPathwayToTarget.class, fileName)) {
            final String targetId = entry.ttdId;
            if (targetId.equals("TTDID")) {
                foundHeader = true;
                continue;
            }
            if (!foundHeader)
                continue;
            final String pathwayId = entry.keggPathwayId;
            if (dictionaryTargetIdToPathways.containsKey(targetId) && dictionaryTargetIdToPathways.get(targetId)
                                                                                                  .contains(
                                                                                                          pathwayId)) {
                continue;
            }
            if (!dictionaryTargetIdToPathways.containsKey(targetId))
                dictionaryTargetIdToPathways.put(targetId, new ArrayList<>());
            dictionaryTargetIdToPathways.get(targetId).add(pathwayId);
            Node node = findOrCreatePathwayNode(graph, setOfPathwayIds, pathwayId, entry.keggPathwayName, "KEGG");
            Node targetNode;
            if (!setOfTargetIds.contains(targetId)) {
                targetNode = graph.addNode(TARGET_LABEL, "id", targetId);
                setOfTargetIds.add(targetId);
            } else {
                targetNode = graph.findNode(TARGET_LABEL, "id", targetId);
            }
            graph.addEdge(targetNode, node, "ASSOCIATED_WITH");
        }
    }

    /**
     * go through Wiki-target tsv file and if pair do not exist already add to graph. Also, if a node (target/pathway)
     * do not exist generate a new node.
     */
    private void exportPathwayWikiTargetTSV(final Workspace workspace, final Graph graph, String fileName,
                                            Set<String> setOfPathwayIds, Set<String> setOfTargetIds,
                                            final Map<String, List<String>> dictionaryTargetIdToPathways) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Wiki pathway Target TSV ...");
        boolean foundHeader = false;
        for (final WikiPathwayToTarget entry : parseTsvFile(workspace, WikiPathwayToTarget.class, fileName)) {
            final String targetId = entry.ttdId;
            if (targetId.equals("TTDID")) {
                foundHeader = true;
                continue;
            }
            if (!foundHeader)
                continue;
            final String pathwayId = entry.wikiPathwayId;
            if (dictionaryTargetIdToPathways.containsKey(targetId) && dictionaryTargetIdToPathways.get(targetId)
                                                                                                  .contains(
                                                                                                          pathwayId)) {
                continue;
            }
            final Node node = findOrCreatePathwayNode(graph, setOfPathwayIds, pathwayId, entry.wikiPathwayName,
                                                      "WikiPathway");
            final Node targetNode;
            if (!setOfTargetIds.contains(targetId)) {
                targetNode = graph.addNode(TARGET_LABEL, "id", targetId);
                setOfTargetIds.add(targetId);
            } else
                targetNode = graph.findNode(TARGET_LABEL, "id", targetId);
            graph.addEdge(targetNode, node, "ASSOCIATED_WITH");
        }
    }

    /**
     * add target-pathway edges to graph.
     */
    private void addTargetPathwayToData(final Graph graph,
                                        final Map<String, List<String>> dictionaryTargetIdToPathways) {
        for (Map.Entry<String, List<String>> entry : dictionaryTargetIdToPathways.entrySet()) {
            Node target = graph.findNode(TARGET_LABEL, "id", entry.getKey());
            for (String pathwayId : entry.getValue()) {
                Node pathway = graph.findNode(PATHWAY_LABEL, "id", pathwayId);
                graph.addEdge(target, pathway, "ASSOCIATED_WITH");
            }
        }
    }

    private void prepareEdgeInformationForDrugCompoundToTarget(final Graph graph,
                                                               final Map<String, Drug> dictionaryNodeIdToDrug,
                                                               final Set<String> setOfTargetIds, final String targetId,
                                                               final String nodeId,
                                                               final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToNodeToEdgeInfo,
                                                               final TargetCompoundActivity entry) {
        TargetDrugEdge targetDrugEdge = new TargetDrugEdge();
        if (dictionaryTargetIdToNodeToEdgeInfo.containsKey(targetId) && dictionaryTargetIdToNodeToEdgeInfo.get(targetId)
                                                                                                          .containsKey(
                                                                                                                  nodeId))
            targetDrugEdge = dictionaryTargetIdToNodeToEdgeInfo.get(targetId).get(nodeId);
        else if (dictionaryTargetIdToNodeToEdgeInfo.containsKey(targetId))
            dictionaryTargetIdToNodeToEdgeInfo.get(targetId).put(nodeId, targetDrugEdge);
        else {
            dictionaryTargetIdToNodeToEdgeInfo.put(targetId, new HashMap<>());
            dictionaryTargetIdToNodeToEdgeInfo.get(targetId).put(nodeId, targetDrugEdge);
        }
        if (dictionaryTargetIdToNodeToEdgeInfo.containsKey(targetId)) {
            if (targetDrugEdge.activity != null) {
                System.out.println("different edges have the same pair");
            }
        }
        targetDrugEdge.activity = entry.activity;
        if (!setOfTargetIds.contains(targetId)) {
            graph.addNode(TARGET_LABEL, "id", targetId);
            setOfTargetIds.add(targetId);
        }
        if (!dictionaryNodeIdToDrug.containsKey(nodeId)) {
            Drug drug = new Drug();
            if (nodeId.startsWith("D")) {
                drug.id = nodeId;
                drug.pubChemCID = new String[]{entry.pubchemCID};
            } else {
                graph.addNode(COMPOUND_LABEL, "id", nodeId, "pubchem_cid", entry.pubchemCID);
            }
            dictionaryNodeIdToDrug.put(nodeId, drug);
        } else if (nodeId.startsWith("D")) {
            if (dictionaryNodeIdToDrug.get(nodeId).pubChemCID == null) {
                dictionaryNodeIdToDrug.get(nodeId).pubChemCID = new String[]{entry.pubchemCID};
            }
        }
    }

    /**
     * Go through target-compound activity tsv file and update the dictionary target id to drug id to model
     * TargetDrugEdge. If target/drug do not exist it is added to graph.
     */
    private Map<String, Map<String, TargetDrugEdge>> exportTargetCompoundActivityTSV(final Workspace workspace,
                                                                                     final Graph graph,
                                                                                     final Map<String, Drug> idDrugMap,
                                                                                     final Set<String> setOfTargetIds,
                                                                                     final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Drug activity TSV ...");
        final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToCompoundToEdgeInfo = new HashMap<>();
        final Map<String, Drug> dictionaryCompoundIdToDrug = new HashMap<>();
        for (final TargetCompoundActivity entry : parseTsvFile(workspace, TargetCompoundActivity.class,
                                                               TTDUpdater.TARGET_COMPOUND_ACTIVITY_TSV)) {

            final String targetId = entry.ttdTargetId;
            final String drugId = entry.ttdDrugId;
            if (drugId.startsWith("D")) {
                prepareEdgeInformationForDrugCompoundToTarget(graph, idDrugMap, setOfTargetIds, targetId, drugId,
                                                              dictionaryTargetIdToDrugsToEdgeInfo, entry);
            } else {
                prepareEdgeInformationForDrugCompoundToTarget(graph, dictionaryCompoundIdToDrug, setOfTargetIds,
                                                              targetId, drugId, dictionaryTargetIdToCompoundToEdgeInfo,
                                                              entry);
            }
        }
        return dictionaryTargetIdToCompoundToEdgeInfo;
    }

    /**
     * Go through the dictionary from target id to drug id to TargetDrugEdge Model and add edge to graph.
     */
    private void addTargetDrugEdges(final Graph graph,
                                    final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo,
                                    final String fromLabel) {
        for (Map.Entry<String, Map<String, TargetDrugEdge>> entry : dictionaryTargetIdToDrugsToEdgeInfo.entrySet()) {
            Node target = graph.findNode(TARGET_LABEL, "id", entry.getKey());
            Map<String, TargetDrugEdge> drugToEdgeModel = entry.getValue();
            for (String drugId : drugToEdgeModel.keySet()) {
                Node drug = graph.findNode(fromLabel, "id", drugId);
                graph.buildEdge().withLabel("TARGETS").fromNode(drug).toNode(target).withModel(
                        drugToEdgeModel.get(drugId)).build();
            }
        }
    }

    private <T> List<T> tryLoadXlsxTable(final Workspace workspace, final String fileName,
                                         final Class<T> type) throws ParserException {
        try {
            return loadXlsxTable(workspace, fileName, type);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException)
                throw new ParserFileNotFoundException(fileName);
            else
                throw new ParserFormatException(e);
        }
    }

    private <T> List<T> loadXlsxTable(final Workspace workspace, final String fileName,
                                      final Class<T> type) throws IOException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        final FileInputStream file = new FileInputStream(filePath);
        final XlsxMappingIterator<T> mappingIterator = new XlsxMappingIterator<>(type, file);
        final List<T> result = new ArrayList<>();
        while (mappingIterator.hasNext())
            result.add(mappingIterator.next());
        return result;
    }

    /**
     * Go through the target exel file and update the different edge information or add new pairs.
     */
    private void exportTargetDrugExcel(final Workspace workspace, final Graph graph, final Set<String> setOfDrugIds,
                                       final Set<String> setOfTargetIds,
                                       final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Drug mapping Excel ...");
        try {
            for (final DrugTargetMapping entry : tryLoadXlsxTable(workspace, TTDUpdater.TARGET_COMPOUND_MAPPIN_XLSX,
                                                                  DrugTargetMapping.class)) {
                final String targetId = entry.targetId;
                final String drugId = entry.drugId;
                TargetDrugEdge targetDrugEdge = new TargetDrugEdge();
                if (dictionaryTargetIdToDrugsToEdgeInfo.containsKey(targetId) &&
                    dictionaryTargetIdToDrugsToEdgeInfo.get(targetId).containsKey(drugId))
                    targetDrugEdge = dictionaryTargetIdToDrugsToEdgeInfo.get(targetId).get(drugId);
                else if (dictionaryTargetIdToDrugsToEdgeInfo.containsKey(targetId))
                    dictionaryTargetIdToDrugsToEdgeInfo.get(targetId).put(drugId, targetDrugEdge);
                else {
                    dictionaryTargetIdToDrugsToEdgeInfo.put(targetId, new HashMap<>());
                    dictionaryTargetIdToDrugsToEdgeInfo.get(targetId).put(drugId, targetDrugEdge);
                }
                if (targetDrugEdge.moa != null) {
                    System.out.println("different edges have the same pair");
                }
                targetDrugEdge.moa = entry.moa;
                if (!setOfTargetIds.contains(targetId)) {
                    graph.addNode(TARGET_LABEL, "id", targetId);
                    setOfTargetIds.add(targetId);
                }
                if (!setOfDrugIds.contains(drugId)) {
                    graph.addNode(DRUG_LABEL, "id", drugId);
                    setOfDrugIds.add(drugId);
                }
            }
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTargetToData(final Graph graph, final Map<String, Target> idTargetMap) {
        for (final Target target : idTargetMap.values())
            graph.addNodeFromModel(target);
    }

    /**
     * This goes through all pathway of a target of a given source and add them to the nodes and update the dictionary
     * with target id to pathway ids.
     */
    private void extractPathwayInformation(final Graph graph, List<String> listOfPathways,
                                           final Set<String> setOfPathwayIds, final String source,
                                           final String targetId,
                                           final Map<String, List<String>> dictionaryTargetIdToPathways) {
        final List<String> pathwayIds = dictionaryTargetIdToPathways.get(targetId);
        for (String pathwayInfo : listOfPathways) {
            final String[] separatedPathwayInfo = pathwayInfo.split(":");
            final String pathwayId = separatedPathwayInfo[0];
            pathwayIds.add(pathwayId);
            if (!setOfPathwayIds.contains(pathwayId)) {
                graph.addNode(PATHWAY_LABEL, "name", separatedPathwayInfo[1], "id", pathwayId, "source", source);
                setOfPathwayIds.add(pathwayId);
            }
        }
    }

    /**
     * Go through the target flat file with the flat file reader. Extract Target information and edges to drug and
     * pathways. Also add pathway and drug information.
     */
    private boolean extractTargetFlatFile(final Workspace workspace, final Graph graph,
                                          final Set<String> setOfPathwayIds, final Map<String, Target> idTargetMap,
                                          final Map<String, List<String>> dictionaryTargetIdToPathways,
                                          final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo,
                                          final Map<String, Drug> idDrugMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export raw Target Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_RAW_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TARGETID") == null)
                continue;
            String targetId = entry.getFirst("TARGETID");
            Target target = targetFromFlatEntry(entry);
            idTargetMap.put(targetId, target);
            dictionaryTargetIdToPathways.put(targetId, new ArrayList<>());
            if (entry.properties.get("DRUGINFO") != null) {
                dictionaryTargetIdToDrugsToEdgeInfo.put(targetId, new HashMap<>());
                for (String drugInfo : entry.properties.get("DRUGINFO")) {
                    String[] splitDrugInfo = drugInfo.split("\t");
                    String drugId = splitDrugInfo[0];
                    if (!idDrugMap.containsKey(drugId)) {
                        Drug drug = new Drug();
                        drug.name = splitDrugInfo[1];
                        drug.id = drugId;
                        drug.highestStatus = splitDrugInfo[2];
                        idDrugMap.put(drugId, drug);
                    }
                    TargetDrugEdge targetDrugEdge = new TargetDrugEdge();
                    targetDrugEdge.highestClinicalStatus = splitDrugInfo[2];
                    dictionaryTargetIdToDrugsToEdgeInfo.get(targetId).put(drugId, new TargetDrugEdge());
                }
            }
            if (entry.properties.get("KEGGPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("KEGGPATH"), setOfPathwayIds, "KEGG", targetId,
                                          dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("WIKIPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("WIKIPATH"), setOfPathwayIds, "WikiPathway",
                                          targetId, dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("WHIZPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("WHIZPATH"), setOfPathwayIds, "PathWhiz Pathway",
                                          targetId, dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("REACPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("REACPATH"), setOfPathwayIds, "Reactome",
                                          targetId, dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("NET_PATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("NET_PATH"), setOfPathwayIds, "NetPathway",
                                          targetId, dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("INTEPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("INTEPATH"), setOfPathwayIds, "Pathway Interact",
                                          targetId, dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("PANTPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("PANTPATH"), setOfPathwayIds, "PANTHER Pathway",
                                          targetId, dictionaryTargetIdToPathways);
            }
            if (entry.properties.get("BIOCPATH") != null) {
                extractPathwayInformation(graph, entry.properties.get("BIOCPATH"), setOfPathwayIds, "BioCyc", targetId,
                                          dictionaryTargetIdToPathways);
            }
        }
        return true;
    }

    /**
     * Extract the target information from the target raw file and add to target model.
     */
    private Target targetFromFlatEntry(final FlatFileTTDEntry entry) {
        final Target target = new Target();
        target.id = entry.getFirst("TARGETID");
        target.name = entry.getFirst("TARGNAME");
        target.formerTargetId = entry.getFirst("FORMERID");
        target.uniProtID = entry.getFirst("UNIPROID");
        target.geneName = entry.getFirst("GENENAME");
        target.type = entry.getFirst("TARGTYPE");
        if (entry.properties.get("SYNONYMS") != null)
            target.synonyms = entry.getFirst("SYNONYMS").split("; ");
        target.function = entry.getFirst("FUNCTION");
        if (entry.properties.get("PDBSTRUC") != null)
            target.pdbStructures = entry.getFirst("PDBSTRUC").split("; ");
        target.ecNumber = entry.getFirst("ECNUMBER");
        target.sequence = entry.getFirst("SEQUENCE");
        target.biochemicalClass = entry.getFirst("BIOCLASS");
        return target;
    }

    /**
     * Open the target-disease flat file and parse with TTD reader. The target information are extract and updated.
     */
    private void extractTargetNameFromTargetDiseaseFlatFile(final Workspace workspace,
                                                            final Map<String, Target> idTargetMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Disease Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_DISEASE_FLAT_FILE),
                StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String id = entry.getFirst("TARGETID");
            if (id != null) {
                final Target target = getOrCreateTarget(id, idTargetMap);
                target.name = entry.getFirst("TARGNAME");
            }
        }
    }

    private Target getOrCreateTarget(final String id, final Map<String, Target> idTargetMap) {
        Target target = idTargetMap.get(id);
        if (target == null) {
            target = new Target();
            target.id = id;
            idTargetMap.put(id, target);
        }
        return target;
    }

    /**
     * Open the target-uniprot flat file and add the information to the existing targets or generate new targets.
     */
    private void extractTargetFlatFileUniProt(final Workspace workspace,
                                              final Map<String, Target> idTargetMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-UniProt Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_UNIPORT_FLAT_FILE),
                StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String id = entry.getFirst("TARGETID");
            if (id != null) {
                final Target target = getOrCreateTarget(id, idTargetMap);
                target.name = entry.getFirst("TARGNAME");
                target.type = entry.getFirst("TARGTYPE");
                target.uniProtID = entry.getFirst("UNIPROID");
            }
        }
    }

    private void addDrugToData(final Graph graph, final Map<String, Drug> idDrugMap) {
        for (final Drug drug : idDrugMap.values())
            graph.addNodeFromModel(drug);
    }

    /**
     * Open the drug-disease flat file and parse with TTD flat file reader specific for this flat file. The dictionary
     * drug id to drug update the name.
     */
    private void extractDrugInfoFromDrugDiseaseFlatFile(final Workspace workspace,
                                                        final Map<String, Drug> idDrugMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Disease Flat File ...");
        final FlatFileWithoutIDTTDReader reader = new FlatFileWithoutIDTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_DISEASE_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String drugId = entry.getFirst("TTDDRUID");
            if (drugId != null) {
                final Drug drug = getOrCreateDrug(drugId, idDrugMap);
                drug.name = entry.getFirst("DRUGNAME");
            }
        }
    }

    private Drug getOrCreateDrug(final String id, final Map<String, Drug> idDrugMap) {
        Drug drug = idDrugMap.get(id);
        if (drug == null) {
            drug = new Drug();
            drug.id = id;
            idDrugMap.put(id, drug);
        }
        return drug;
    }

    /**
     * Open the flat file drug-cross reference and add the information to the existing drugs or generate new drugs.
     */
    private void extractDrugFlatFileCrossRef(final Workspace workspace,
                                             final Map<String, Drug> idDrugMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Crossref Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_CROSSREF_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String drugId = entry.getFirst("TTDDRUID");
            if (drugId != null)
                drugCrossRefFromFlatEntry(entry, drugId, idDrugMap);
        }
    }

    /**
     * Generate for not existing drug ids a Drug node with identifier. The get the drug model from the map with
     * identifier and add information from drug-cross reference file. Some are arrays and some are only string.
     */
    private void drugCrossRefFromFlatEntry(final FlatFileTTDEntry entry, final String id,
                                           final Map<String, Drug> idDrugMap) {
        Drug drug = getOrCreateDrug(id, idDrugMap);
        drug.name = entry.getFirst("DRUGNAME");
        drug.casNumber = entry.getFirst("CASNUMBE");
        drug.formular = entry.getFirst("D_FOMULA");
        if (entry.properties.get("PUBCHCID") != null)
            drug.pubChemCID = entry.getFirst("PUBCHCID").split("; ");
        if (entry.properties.get("PUBCHSID") != null)
            drug.pubChemSID = entry.getFirst("PUBCHSID").split("; ");
        drug.chebiId = entry.getFirst("ChEBI_ID");
        if (entry.properties.get("SUPDRATC") != null)
            drug.superDrugATC = entry.getFirst("SUPDRATC").split("; ");
        drug.superDrugCas = entry.getFirst("SUPDRCAS");
    }

    /**
     * Open the drug-synonym flat file and parse with TTD reader. The information of the file are added to the existing
     * drug or generate new drugs.
     */
    private void extractDrugFlatFileSynonyms(final Workspace workspace,
                                             final Map<String, Drug> idDrugMap) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Synonyms Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_SYNONYMS_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            final String drugId = entry.getFirst("TTDDRUID");
            if (drugId != null)
                drugSynonymsFromFlatEntry(entry, drugId, idDrugMap);
        }
    }

    /**
     * Generate for not existing drug ids a Drug node with identifier. The get the drug model from the map with
     * identifier and add name and synonym information to model from file Drug-synonyms.
     */
    private void drugSynonymsFromFlatEntry(final FlatFileTTDEntry entry, final String id,
                                           final Map<String, Drug> idDrugMap) {
        final Drug drug = getOrCreateDrug(id, idDrugMap);
        drug.name = entry.getFirst("DRUGNAME");
        drug.synonyms = entry.properties.get("SYNONYMS").toArray(new String[0]);
    }
}
