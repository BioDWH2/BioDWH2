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
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, "identifier", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, "identifier", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "identifier", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, "identifier", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "identifier", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(BIOMARKER_LABEL, "identifier", IndexDescription.Type.UNIQUE));
        final Set<String> setOfPathwayIds = new HashSet<>();
        final Map<String, Target> dictionaryTargetIdToTarget = new HashMap<>();
        final Map<String, Drug> dictionaryDrugIdToDrug = new HashMap<>();
        final Map<String, List<String>> dictionaryTargetIdToPathways = new HashMap<>();
        final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo = new HashMap<>();
        // prepare drug information
        try {
            extractDrugFlatFile(workspace, dictionaryDrugIdToDrug);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug", e);
        }
        try {
            extractDrugFlatFileCrossRef(workspace, dictionaryDrugIdToDrug);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug Cross", e);
        }
        try {
            extractDrugFlatFileSynonyms(workspace, dictionaryDrugIdToDrug);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug synonyms", e);
        }
        try {
            extractDrugInfoFromDrugDiseaseFlatFile(workspace, dictionaryDrugIdToDrug);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug disease", e);
        }
        //prepare Target information
        try {
            extractTargetFlatFile(workspace, graph, setOfPathwayIds, dictionaryTargetIdToTarget,
                                  dictionaryTargetIdToPathways, dictionaryTargetIdToDrugsToEdgeInfo,
                                  dictionaryDrugIdToDrug);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target", e);
        }
        try {
            extractTargetFlatFileUniprot(workspace, dictionaryTargetIdToTarget);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target uniprot", e);
        }
        try {
            extractTargetNameFromTargetDiseaseFlatFile(workspace, dictionaryTargetIdToTarget);
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target disease", e);
        }
        addTargetToData(graph, dictionaryTargetIdToTarget);
        Set<String> setOfTargetIds = new HashSet<>(dictionaryTargetIdToTarget.keySet());
        dictionaryTargetIdToTarget.clear();
        // drug target edges
        final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToCompoundToEdgeInfo = exportTargetCompoundActivityTSV(
                workspace, graph, dictionaryDrugIdToDrug, setOfTargetIds, dictionaryTargetIdToDrugsToEdgeInfo);
        addDrugToData(graph, dictionaryDrugIdToDrug);
        final Set<String> setOfDrugIds = new HashSet<>(dictionaryDrugIdToDrug.keySet());
        dictionaryDrugIdToDrug.clear();
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
            if (!foundHeader) {
                continue;
            }
            final String biomarkerId = entry.biomarkerId;
            Node biomarker;
            if (!setOfBiomarkerIds.contains(biomarkerId)) {
                biomarker = graph.addNode(BIOMARKER_LABEL, "name", entry.biomarkerName, "identifier", biomarkerId);
                setOfBiomarkerIds.add(biomarkerId);
            } else {
                biomarker = graph.findNode(BIOMARKER_LABEL, "identifier", biomarkerId);
            }
            final String diseaseId = entry.icd11.replace("ICD-11: ", "");
            Node disease;
            if (!setOfDiseaseIds.contains(diseaseId)) {
                setOfDiseaseIds.add(diseaseId);
                final NodeBuilder builder = graph.buildNode().withLabel(DISEASE_LABEL);
                builder.withProperty("identifier", diseaseId);
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
            } else {
                disease = graph.findNode(DISEASE_LABEL, "identifier", diseaseId);
            }
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
            if (entry.properties.get("TARGETID") == null)
                continue;
            String targetId = entry.properties.get("TARGETID").get(0);
            Node target = null;

            if (!setOfTargetIds.contains(targetId)) {
                target = graph.addNode(TARGET_LABEL, "name", entry.properties.get("TARGNAME").get(0), "identifier",
                                       targetId);
                setOfTargetIds.add(targetId);
            } else
                target = graph.findNode(TARGET_LABEL, "identifier", targetId);

            for (String diseaseInfo : entry.properties.get("INDICATI")) {
                String[] splitDiseaseInfo = diseaseInfo.split("\t");
                String diseaseNameId = splitDiseaseInfo[1];
                String[] splitDiseaseNameID = diseaseNameId.split(" \\[");
                String diseaseId = splitDiseaseNameID[1].substring(0, splitDiseaseNameID[1].length() - 1).replace(
                        "ICD-11: ", "");
                String clincalStatus = splitDiseaseInfo[0];
                Node disease;
                if (!setOfDiseaseIds.contains(diseaseId)) {
                    disease = graph.addNode(DISEASE_LABEL, "name", splitDiseaseInfo[1], "identifier", diseaseId,
                                            "ICD11", diseaseId.split(", "));
                    setOfDiseaseIds.add(diseaseId);
                } else
                    disease = graph.findNode(DISEASE_LABEL, "identifier", diseaseId);
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
            if (entry.properties.get("TTDDRUID") == null)
                continue;
            String drugId = entry.properties.get("TTDDRUID").get(0);
            Node drug = graph.findNode(DRUG_LABEL, "identifier", drugId);

            for (String diseaseInfo : entry.properties.get("INDICATI")) {
                String[] splitDiseaseInfo = diseaseInfo.split(" \\[");
                String[] splitDiseaseIdAndClinicalStatus = splitDiseaseInfo[1].split("] ");
                String diseaseId = splitDiseaseIdAndClinicalStatus[0].replace("ICD-11: ", "");
                String clincalStatus = splitDiseaseIdAndClinicalStatus[1];
                Node disease;
                if (!setOfDiseaseIds.contains(diseaseId)) {
                    disease = graph.addNode(DISEASE_LABEL, "name", splitDiseaseInfo[0], "identifier", diseaseId,
                                            "ICD11", diseaseId.split(", "));
                    setOfDiseaseIds.add(diseaseId);
                } else
                    disease = graph.findNode(DISEASE_LABEL, "identifier", diseaseId);
                graph.addEdge(drug, disease, "INDICATES", "clinical_status", clincalStatus, "disease_name",
                              splitDiseaseInfo[0]);
            }
        }
        return true;
    }

    private Node findOrCreatePathwayNode(final Graph graph, final Set<String> setOfPathwayIds, final String pathwayId,
                                         final String name, final String source) {
        if (!setOfPathwayIds.contains(pathwayId)) {
            Node node = graph.addNode(PATHWAY_LABEL, "name", name, "identifier", pathwayId, "source", source);
            setOfPathwayIds.add(pathwayId);
            return node;
        }
        return graph.findNode(PATHWAY_LABEL, "identifier", pathwayId);
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
            if (!foundHeader) {
                continue;
            }
            final String pathwayId = entry.keggPathwayId;
            if (dictionaryTargetIdToPathways.containsKey(targetId) && dictionaryTargetIdToPathways.get(targetId)
                                                                                                  .contains(pathwayId))
                continue;
            if (!dictionaryTargetIdToPathways.containsKey(targetId))
                dictionaryTargetIdToPathways.put(targetId, new ArrayList<>());
            dictionaryTargetIdToPathways.get(targetId).add(pathwayId);
            Node node = findOrCreatePathwayNode(graph, setOfPathwayIds, pathwayId, entry.keggPathwayName, "KEGG");
            Node targetNode;
            if (!setOfTargetIds.contains(targetId)) {
                targetNode = graph.addNode(TARGET_LABEL, "identifier", targetId);
                setOfTargetIds.add(targetId);
            } else {
                targetNode = graph.findNode(TARGET_LABEL, "identifier", targetId);
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
            if (!foundHeader) {
                continue;
            }
            final String pathwayId = entry.wikiPathwayId;
            if (dictionaryTargetIdToPathways.containsKey(targetId) && dictionaryTargetIdToPathways.get(targetId)
                                                                                                  .contains(
                                                                                                          pathwayId)) {
                continue;
            }
            Node node = findOrCreatePathwayNode(graph, setOfPathwayIds, pathwayId, entry.wikiPathwayName,
                                                "WikiPathway");
            Node targetNode;
            if (!setOfTargetIds.contains(targetId)) {
                targetNode = graph.addNode(TARGET_LABEL, "identifier", targetId);
                setOfTargetIds.add(targetId);
            } else {
                targetNode = graph.findNode(TARGET_LABEL, "identifier", targetId);
            }
            graph.addEdge(targetNode, node, "ASSOCIATED_WITH");
        }
    }

    /**
     * add target-pathway edges to graph.
     */
    private void addTargetPathwayToData(final Graph graph,
                                        final Map<String, List<String>> dictionaryTargetIdToPathways) {
        for (Map.Entry<String, List<String>> entry : dictionaryTargetIdToPathways.entrySet()) {
            Node target = graph.findNode(TARGET_LABEL, "identifier", entry.getKey());
            for (String pathwayId : entry.getValue()) {
                Node pathway = graph.findNode(PATHWAY_LABEL, "identifier", pathwayId);
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
            graph.addNode(TARGET_LABEL, "identifier", targetId);
            setOfTargetIds.add(targetId);
        }
        if (!dictionaryNodeIdToDrug.containsKey(nodeId)) {
            Drug drug = new Drug();
            if (nodeId.startsWith("D")) {
                drug.identifier = nodeId;
                drug.pubChemCID = new String[]{entry.pubchemCID};
            } else {
                graph.addNode(COMPOUND_LABEL, "identifier", nodeId, "pubchem_cid", entry.pubchemCID);
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
                                                                                     final Map<String, Drug> dictionaryDrugIdToDrug,
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
                prepareEdgeInformationForDrugCompoundToTarget(graph, dictionaryDrugIdToDrug, setOfTargetIds, targetId,
                                                              drugId, dictionaryTargetIdToDrugsToEdgeInfo, entry);
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
            Node target = graph.findNode(TARGET_LABEL, "identifier", entry.getKey());
            Map<String, TargetDrugEdge> drugToEdgeModel = entry.getValue();
            for (String drugId : drugToEdgeModel.keySet()) {
                Node drug = graph.findNode(fromLabel, "identifier", drugId);
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
     * Go through the target exel file and update the differnt edge information or add new pairs.
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
                    graph.addNode(TARGET_LABEL, "identifier", targetId);
                    setOfTargetIds.add(targetId);
                }
                if (!setOfDrugIds.contains(drugId)) {
                    graph.addNode(DRUG_LABEL, "identifier", drugId);
                    setOfDrugIds.add(drugId);
                }
            }
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * add target nodes to graph.
     */
    private void addTargetToData(final Graph graph, final Map<String, Target> dictionaryTargetIdToTarget) {
        for (final Target target : dictionaryTargetIdToTarget.values())
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
        List<String> pathwayIds = dictionaryTargetIdToPathways.get(targetId);
        for (String pathwayInfo : listOfPathways) {
            String[] separatedPathwayInfo = pathwayInfo.split(":");
            final String pathwayId = separatedPathwayInfo[0];
            pathwayIds.add(pathwayId);
            if (!setOfPathwayIds.contains(pathwayId)) {
                graph.addNode(PATHWAY_LABEL, "name", separatedPathwayInfo[1], "identifier", pathwayId, "source",
                              source);
                setOfPathwayIds.add(pathwayId);
            }
        }
    }

    /**
     * Go through the target flat file with the flat file reader. Extract Target information and edges to drug and
     * pathways. Also add pathway and drug information.
     */
    private boolean extractTargetFlatFile(final Workspace workspace, final Graph graph,
                                          final Set<String> setOfPathwayIds,
                                          final Map<String, Target> dictionaryTargetIdToTarget,
                                          final Map<String, List<String>> dictionaryTargetIdToPathways,
                                          final Map<String, Map<String, TargetDrugEdge>> dictionaryTargetIdToDrugsToEdgeInfo,
                                          final Map<String, Drug> dictionaryDrugIdToDrug) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export raw Target Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_RAW_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TARGETID") == null)
                continue;
            String targetId = entry.properties.get("TARGETID").get(0);
            Target target = targetFromFlatEntry(entry);
            dictionaryTargetIdToTarget.put(targetId, target);
            dictionaryTargetIdToPathways.put(targetId, new ArrayList<>());
            if (entry.properties.get("DRUGINFO") != null) {
                dictionaryTargetIdToDrugsToEdgeInfo.put(targetId, new HashMap<>());
                for (String drugInfo : entry.properties.get("DRUGINFO")) {
                    String[] splitDrugInfo = drugInfo.split("\t");
                    String drugId = splitDrugInfo[0];
                    if (!dictionaryDrugIdToDrug.containsKey(drugId)) {
                        Drug drug = new Drug();
                        drug.name = splitDrugInfo[1];
                        drug.identifier = drugId;
                        drug.highestStatus = splitDrugInfo[2];
                        dictionaryDrugIdToDrug.put(drugId, drug);
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
        target.identifier = entry.properties.get("TARGETID").get(0);
        target.name = entry.properties.get("TARGNAME").get(0);
        if (entry.properties.get("FORMERID") != null)
            target.formerTargetId = entry.properties.get("FORMERID").get(0);
        if (entry.properties.get("UNIPROID") != null)
            target.uniProtID = entry.properties.get("UNIPROID").get(0);
        if (entry.properties.get("GENENAME") != null)
            target.geneName = entry.properties.get("GENENAME").get(0);
        if (entry.properties.get("TARGTYPE") != null)
            target.type = entry.properties.get("TARGTYPE").get(0);
        if (entry.properties.get("SYNONYMS") != null)
            target.synonyms = entry.properties.get("SYNONYMS").get(0).split("; ");
        if (entry.properties.get("FUNCTION") != null)
            target.function = entry.properties.get("FUNCTION").get(0);
        if (entry.properties.get("PDBSTRUC") != null)
            target.pdbStructurs = entry.properties.get("PDBSTRUC").get(0).split("; ");
        if (entry.properties.get("ECNUMBER") != null)
            target.ecNumber = entry.properties.get("ECNUMBER").get(0);
        if (entry.properties.get("SEQUENCE") != null)
            target.sequence = entry.properties.get("SEQUENCE").get(0);
        if (entry.properties.get("BIOCLASS") != null)
            target.biochemicalClass = entry.properties.get("BIOCLASS").get(0);
        return target;
    }

    /**
     * Open the target-disease flat file and parse with TTD reader. The target information are extract and updated.
     */
    private boolean extractTargetNameFromTargetDiseaseFlatFile(final Workspace workspace,
                                                               final Map<String, Target> dictionaryTargetIdToTarget) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Disease Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_DISEASE_FLAT_FILE),
                StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TARGETID") == null)
                continue;
            String targetId = entry.properties.get("TARGETID").get(0);
            if (!dictionaryTargetIdToTarget.containsKey(targetId)) {
                final Target target = new Target();
                target.identifier = targetId;
                dictionaryTargetIdToTarget.put(targetId, target);
            }
            Target target = dictionaryTargetIdToTarget.get(targetId);
            target.name = entry.properties.get("TARGNAME").get(0);
        }
        return true;
    }

    /**
     * Open the target-uniprot flat file  and add the information to the existing targets or generate new targets.
     */
    private boolean extractTargetFlatFileUniprot(final Workspace workspace,
                                                 final Map<String, Target> dictionaryTargetIdToTarget) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-UniProt Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_UNIPORT_FLAT_FILE),
                StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TARGETID") != null) {
                String targetId = entry.properties.get("TARGETID").get(0);
                TargetUniprotFromFlatEntry(entry, targetId, dictionaryTargetIdToTarget);
            }
        }
        return true;
    }

    /**
     * Generate for not existing target ids a Target node with identifier. The get the drug model from the map with
     * identifier and add name,type and uniprot information to model from file target-uniprot.
     */
    private void TargetUniprotFromFlatEntry(final FlatFileTTDEntry entry, final String identifier,
                                            final Map<String, Target> dictionaryTargetIdToTarget) {
        if (!dictionaryTargetIdToTarget.containsKey(identifier)) {
            final Target target = new Target();
            target.identifier = identifier;
            dictionaryTargetIdToTarget.put(identifier, target);
        }
        Target target = dictionaryTargetIdToTarget.get(identifier);
        target.name = entry.properties.get("TARGNAME").get(0);
        target.type = entry.properties.get("TARGTYPE").get(0);
        target.uniProtID = entry.properties.get("UNIPROID").get(0);
    }

    /**
     * add drug nodes to graph.
     */
    private void addDrugToData(final Graph graph, final Map<String, Drug> dictionaryDrugIdToDrug) {
        for (final Drug drug : dictionaryDrugIdToDrug.values())
            graph.addNodeFromModel(drug);
    }

    /**
     * Open the drug flat file and fill the map of identifier to drug with the file information.
     */
    private boolean extractDrugFlatFile(final Workspace workspace,
                                        final Map<String, Drug> dictionaryDrugIdToDrug) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_RAW_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("DRUG__ID") != null) {
                String drugId = entry.properties.get("DRUG__ID").get(0);
                Drug drug = DrugFromFlatEntry(entry);
                dictionaryDrugIdToDrug.put(drugId, drug);
            }
        }
        return true;
    }

    /**
     * Extract the Drug information from  drug download and add information into drug model.
     */
    private Drug DrugFromFlatEntry(final FlatFileTTDEntry entry) {
        final Drug drug = new Drug();
        drug.identifier = entry.properties.get("DRUG__ID").get(0);
        if (entry.properties.get("TRADNAME") != null)
            drug.tradeName = entry.properties.get("TRADNAME").get(0);
        if (entry.properties.get("DRUGCOMP") != null)
            drug.company = entry.properties.get("DRUGCOMP").get(0);
        if (entry.properties.get("DRUGTYPE") != null)
            drug.type = entry.properties.get("DRUGTYPE").get(0);
        if (entry.properties.get("DRUGINCH") != null)
            drug.inchi = entry.properties.get("DRUGINCH").get(0);
        if (entry.properties.get("DRUGINKE") != null)
            drug.inchikey = entry.properties.get("DRUGINKE").get(0);
        if (entry.properties.get("DRUGSMIL") != null)
            drug.canonicalSmiles = entry.properties.get("DRUGSMIL").get(0);
        if (entry.properties.get("HIGHSTAT") != null)
            drug.highestStatus = entry.properties.get("HIGHSTAT").get(0);
        if (entry.properties.get("DRUADIID") != null)
            drug.adiID = entry.properties.get("DRUADIID").get(0);
        if (entry.properties.get("THERCLAS") != null)
            drug.therapeuticClass = entry.properties.get("THERCLAS").get(0);
        if (entry.properties.get("DRUGCLAS") != null)
            drug.drugClass = entry.properties.get("DRUGCLAS").get(0);
        if (entry.properties.get("COMPCLAS") != null)
            drug.compoundClass = entry.properties.get("COMPCLAS").get(0);
        return drug;
    }

    /**
     * Open the drug-disease flat file and parse with TTD flat file reader specific for this flat file. The dictionary
     * drug id to drug update the name.
     */
    private boolean extractDrugInfoFromDrugDiseaseFlatFile(final Workspace workspace,
                                                           final Map<String, Drug> dictionaryDrugIdToDrug) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Disease Flat File ...");
        final FlatFileWithoutIDTTDReader reader = new FlatFileWithoutIDTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_DISEASE_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TTDDRUID") == null)
                continue;
            String drugId = entry.properties.get("TTDDRUID").get(0);
            if (!dictionaryDrugIdToDrug.containsKey(drugId)) {
                final Drug drug = new Drug();
                drug.identifier = drugId;
                dictionaryDrugIdToDrug.put(drugId, drug);
            }
            Drug drug = dictionaryDrugIdToDrug.get(drugId);
            drug.name = entry.properties.get("DRUGNAME").get(0);
        }
        return true;
    }

    /**
     * Open the flat file drug-cross reference and add the information to the existing drugs or generate new drugs.
     */
    private boolean extractDrugFlatFileCrossRef(final Workspace workspace,
                                                final Map<String, Drug> dictionaryDrugIdToDrug) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Crossref Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_CROSSREF_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TTDDRUID") != null) {
                String drugId = entry.properties.get("TTDDRUID").get(0);
                DrugCrossRefFromFlatEntry(entry, drugId, dictionaryDrugIdToDrug);
            }
        }
        return true;
    }

    /**
     * Generate for not existing drug ids a Drug node with identifier. The get the drug model from the map with
     * identifier and add information from drug-cross reference file. Some are arrays and some are only string.
     */
    private void DrugCrossRefFromFlatEntry(final FlatFileTTDEntry entry, final String identifier,
                                           final Map<String, Drug> dictionaryDrugIdToDrug) {
        if (!dictionaryDrugIdToDrug.containsKey(identifier)) {
            final Drug drug = new Drug();
            drug.identifier = identifier;
            dictionaryDrugIdToDrug.put(identifier, drug);
        }
        Drug drug = dictionaryDrugIdToDrug.get(identifier);
        drug.name = entry.properties.get("DRUGNAME").get(0);
        if (entry.properties.get("CASNUMBE") != null)
            drug.casNumber = entry.properties.get("CASNUMBE").get(0);
        if (entry.properties.get("D_FOMULA") != null)
            drug.formular = entry.properties.get("D_FOMULA").get(0);
        if (entry.properties.get("PUBCHCID") != null)
            drug.pubChemCID = entry.properties.get("PUBCHCID").get(0).split("; ");
        if (entry.properties.get("PUBCHSID") != null)
            drug.pubChemSID = entry.properties.get("PUBCHSID").get(0).split("; ");
        if (entry.properties.get("ChEBI_ID") != null)
            drug.chEBIid = entry.properties.get("ChEBI_ID").get(0);
        if (entry.properties.get("SUPDRATC") != null)
            drug.superDrugATC = entry.properties.get("SUPDRATC").get(0).split("; ");
        if (entry.properties.get("SUPDRCAS") != null)
            drug.superDrugCas = entry.properties.get("SUPDRCAS").get(0);
    }

    /**
     * Open the drug-synonym flat file and parse with TTD reader. The information of the file are added to the existing
     * drug or generate new drugs.
     */
    private boolean extractDrugFlatFileSynonyms(final Workspace workspace,
                                                final Map<String, Drug> dictionaryDrugIdToDrug) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Synonyms Flat File ...");
        final FlatFileTTDReader reader = new FlatFileTTDReader(
                FileUtils.openInput(workspace, dataSource, TTDUpdater.DRUG_SYNONYMS_FLAT_FILE), StandardCharsets.UTF_8);
        for (final FlatFileTTDEntry entry : reader) {
            if (entry.properties.get("TTDDRUID") != null) {
                String drugId = entry.properties.get("TTDDRUID").get(0);
                DrugSynonymsFromFlatEntry(entry, drugId, dictionaryDrugIdToDrug);
            }
        }
        return true;
    }

    /**
     * Generate for not existing drug ids a Drug node with identifier. The get the drug model from the map with
     * identifier and add name a synonyms information to model from file Drug-synonyms.
     */
    private void DrugSynonymsFromFlatEntry(final FlatFileTTDEntry entry, final String identifier,
                                           final Map<String, Drug> dictionaryDrugIdToDrug) {
        if (!dictionaryDrugIdToDrug.containsKey(identifier)) {
            final Drug drug = new Drug();
            drug.identifier = identifier;
            dictionaryDrugIdToDrug.put(identifier, drug);
        }
        Drug drug = dictionaryDrugIdToDrug.get(identifier);
        drug.name = entry.properties.get("DRUGNAME").get(0);
        drug.synonyms = entry.properties.get("SYNONYMS").toArray(new String[0]);
    }
}
