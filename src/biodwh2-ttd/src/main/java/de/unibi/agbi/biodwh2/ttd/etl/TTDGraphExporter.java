package de.unibi.agbi.biodwh2.ttd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.collections.MappingIterable;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.XlsxMappingIterator;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.ttd.TTDDataSource;
import de.unibi.agbi.biodwh2.ttd.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TTDGraphExporter extends GraphExporter<TTDDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(TTDGraphExporter.class);
    static final String PATHWAY_LABEL = "Pathway";
    static final String TARGET_LABEL = "Target";
    static final String DRUG_LABEL = "Drug";
    static final String COMPOUND_LABEL = "Compound";
    static final String DISEASE_LABEL = "Disease";
    static final String BIOMARKER_LABEL = "Biomarker";
    static final String TARGETS_LABEL = "TARGETS";
    static final String INDICATES_LABEL = "INDICATES";

    public TTDGraphExporter(final TTDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(BIOMARKER_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        // prepare drug information
        final Map<String, Drug> idDrugMap = new HashMap<>();
        extractDrugsFromFlatFile(workspace, idDrugMap);
        extractDrugCrossRefsFromFlatFile(workspace, idDrugMap);
        extractDrugSynonymsFromFlatFile(workspace, idDrugMap);
        extractDrugInfoFromDrugDiseaseFlatFile(workspace, idDrugMap);
        //prepare Target information
        final Map<String, Target> idTargetMap = new HashMap<>();
        final Map<String, Set<String>> targetIdPathwayMap = new HashMap<>();
        final Map<String, Map<String, TargetDrugEdge>> targetIdDrugEdgeMap = new HashMap<>();
        extractTargetFlatFile(workspace, graph, idTargetMap, targetIdPathwayMap, targetIdDrugEdgeMap, idDrugMap);
        extractTargetFlatFileUniProt(workspace, idTargetMap);
        extractTargetNameFromTargetDiseaseFlatFile(workspace, idTargetMap);
        createTargetNodes(graph, idTargetMap);
        idTargetMap.clear();
        // drug target edges
        final Map<String, Map<String, TargetDrugEdge>> targetIdCompoundEdgeInfoMap = exportTargetCompoundActivityTSV(
                workspace, graph, idDrugMap, targetIdDrugEdgeMap);
        createDrugNodes(graph, idDrugMap);
        idDrugMap.clear();
        exportTargetDrugExcel(workspace, graph, targetIdDrugEdgeMap);
        addTargetDrugEdges(graph, targetIdDrugEdgeMap, DRUG_LABEL);
        addTargetDrugEdges(graph, targetIdCompoundEdgeInfoMap, COMPOUND_LABEL);
        // pathway and target-pathway edges
        addTargetPathwayToData(graph, targetIdPathwayMap);
        exportPathwayKeggTargetTSV(workspace, graph, targetIdPathwayMap);
        exportPathwayWikiTargetTSV(workspace, graph, targetIdPathwayMap);
        targetIdPathwayMap.clear();
        // prepare disease and biomarker
        exportBiomarkerDiseaseTSV(workspace, graph);
        // edges to disease
        exportTargetDiseaseFlatFile(workspace, graph);
        exportDrugDiseaseFlatFile(workspace, graph);
        return true;
    }

    private void extractDrugsFromFlatFile(final Workspace workspace, final Map<String, Drug> idDrugMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.DRUG_RAW_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = entry.getFirst("DRUG__ID");
                if (id == null)
                    continue;
                final Drug drug = new Drug(id);
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
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export TTD Flat File Drug", e);
        }
    }

    private FlatFileTTDReader openFlatFile(final Workspace workspace, final String fileName) throws IOException {
        return new FlatFileTTDReader(FileUtils.openInput(workspace, dataSource, fileName), StandardCharsets.UTF_8);
    }

    private void extractDrugCrossRefsFromFlatFile(final Workspace workspace, final Map<String, Drug> idDrugMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Crossref Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.DRUG_CROSSREF_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = entry.getFirst("TTDDRUID");
                if (id == null)
                    continue;
                final Drug drug = getOrCreateDrug(id, idDrugMap);
                drug.name = entry.getFirst("DRUGNAME");
                drug.casNumber = entry.getFirst("CASNUMBE");
                drug.formula = entry.getFirst("D_FOMULA");
                if (entry.properties.get("PUBCHCID") != null)
                    drug.pubChemCID = getFlatFileEntryArray(entry, "PUBCHCID");
                if (entry.properties.get("PUBCHSID") != null)
                    drug.pubChemSID = getFlatFileEntryArray(entry, "PUBCHSID");
                drug.chebiId = entry.getFirst("ChEBI_ID");
                if (entry.properties.get("SUPDRATC") != null)
                    drug.superDrugATC = getFlatFileEntryArray(entry, "SUPDRATC");
                drug.superDrugCas = entry.getFirst("SUPDRCAS");
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug Cross", e);
        }
    }

    private Drug getOrCreateDrug(final String id, final Map<String, Drug> idDrugMap) {
        Drug drug = idDrugMap.get(id);
        if (drug == null) {
            drug = new Drug(id);
            idDrugMap.put(id, drug);
        }
        return drug;
    }

    private String[] getFlatFileEntryArray(final FlatFileTTDEntry entry, final String key) {
        final String[] result = StringUtils.split(entry.getFirst(key), ';');
        for (int i = 0; i < result.length; i++)
            result[i] = result[i].trim();
        return result;
    }

    /**
     * Get or create drugs from the drug-synonyms flat file and add the drug name and synonyms.
     */
    private void extractDrugSynonymsFromFlatFile(final Workspace workspace, final Map<String, Drug> idDrugMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Synonyms Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.DRUG_SYNONYMS_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String drugId = entry.getFirst("TTDDRUID");
                if (drugId != null) {
                    final Drug drug = getOrCreateDrug(drugId, idDrugMap);
                    drug.name = drug.name != null ? drug.name : entry.getFirst("DRUGNAME");
                    drug.synonyms = entry.properties.get("SYNONYMS").toArray(new String[0]);
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug synonyms", e);
        }
    }

    /**
     * Get or create drugs from the drug-disease flat file and add the drug name.
     */
    private void extractDrugInfoFromDrugDiseaseFlatFile(final Workspace workspace, final Map<String, Drug> idDrugMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Disease Flat File ...");
        try (FlatFileWithoutIDTTDReader reader = openFlatFileWithoutId(workspace, TTDUpdater.DRUG_DISEASE_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String drugId = entry.getFirst("TTDDRUID");
                if (drugId != null) {
                    final Drug drug = getOrCreateDrug(drugId, idDrugMap);
                    drug.name = drug.name != null ? drug.name : entry.getFirst("DRUGNAME");
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Drug disease", e);
        }
    }

    private FlatFileWithoutIDTTDReader openFlatFileWithoutId(final Workspace workspace,
                                                             final String fileName) throws IOException {
        return new FlatFileWithoutIDTTDReader(FileUtils.openInput(workspace, dataSource, fileName),
                                              StandardCharsets.UTF_8);
    }

    /**
     * Go through the target flat file with the flat file reader. Extract Target information and edges to drug and
     * pathways. Also add pathway and drug information.
     */
    private void extractTargetFlatFile(final Workspace workspace, final Graph graph,
                                       final Map<String, Target> idTargetMap,
                                       final Map<String, Set<String>> targetIdPathwayMap,
                                       final Map<String, Map<String, TargetDrugEdge>> targetIdToDrugsToEdgeInfoMap,
                                       final Map<String, Drug> idDrugMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export raw Target Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.TARGET_RAW_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                if (entry.properties.get("TARGETID") == null)
                    continue;
                final String targetId = entry.getFirst("TARGETID");
                final Target target = targetFromFlatEntry(entry);
                idTargetMap.put(targetId, target);
                if (entry.properties.get("DRUGINFO") != null) {
                    final Map<String, TargetDrugEdge> drugEdgeMap = new HashMap<>();
                    targetIdToDrugsToEdgeInfoMap.put(targetId, drugEdgeMap);
                    for (final String drugInfo : entry.properties.get("DRUGINFO")) {
                        final String[] splitDrugInfo = StringUtils.split(drugInfo, '\t');
                        final String drugId = splitDrugInfo[0];
                        final Drug drug = getOrCreateDrug(drugId, idDrugMap);
                        drug.name = drug.name != null ? drug.name : splitDrugInfo[1];
                        drug.highestStatus = drug.highestStatus != null ? drug.highestStatus : splitDrugInfo[2];
                        final TargetDrugEdge targetDrugEdge = new TargetDrugEdge();
                        targetDrugEdge.highestClinicalStatus = splitDrugInfo[2];
                        drugEdgeMap.put(drugId, targetDrugEdge);
                    }
                }
                final Set<String> targetPathwayIds = new HashSet<>();
                targetIdPathwayMap.put(targetId, targetPathwayIds);
                extractPathwayInformation(graph, entry, "KEGGPATH", "KEGG", targetPathwayIds);
                extractPathwayInformation(graph, entry, "WIKIPATH", "WikiPathway", targetPathwayIds);
                extractPathwayInformation(graph, entry, "WHIZPATH", "PathWhiz Pathway", targetPathwayIds);
                extractPathwayInformation(graph, entry, "REACPATH", "Reactome", targetPathwayIds);
                extractPathwayInformation(graph, entry, "NET_PATH", "NetPathway", targetPathwayIds);
                extractPathwayInformation(graph, entry, "INTEPATH", "Pathway Interact", targetPathwayIds);
                extractPathwayInformation(graph, entry, "PANTPATH", "PANTHER Pathway", targetPathwayIds);
                extractPathwayInformation(graph, entry, "BIOCPATH", "BioCyc", targetPathwayIds);
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target", e);
        }
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
            target.synonyms = getFlatFileEntryArray(entry, "SYNONYMS");
        target.function = entry.getFirst("FUNCTION");
        if (entry.properties.get("PDBSTRUC") != null)
            target.pdbStructures = getFlatFileEntryArray(entry, "PDBSTRUC");
        target.ecNumber = entry.getFirst("ECNUMBER");
        target.sequence = entry.getFirst("SEQUENCE");
        target.biochemicalClass = entry.getFirst("BIOCLASS");
        return target;
    }

    /**
     * Goes through all pathways of a target of a given source and add them to the nodes and update the dictionary with
     * target id to pathway ids.
     */
    private void extractPathwayInformation(final Graph graph, final FlatFileTTDEntry entry, final String key,
                                           final String source, final Set<String> targetPathwayIds) {
        if (entry.properties.get(key) != null) {
            for (final String pathwayInfo : entry.properties.get(key)) {
                final String[] separatedPathwayInfo = StringUtils.split(pathwayInfo, ":", 2);
                final String pathwayId = separatedPathwayInfo[0];
                targetPathwayIds.add(pathwayId);
                getOrCreatePathwayNode(graph, pathwayId, separatedPathwayInfo[1], source);
            }
        }
    }

    private Node getOrCreatePathwayNode(final Graph graph, final String pathwayId, final String name,
                                        final String source) {
        Node node = graph.findNode(PATHWAY_LABEL, ID_KEY, pathwayId);
        if (node == null)
            node = graph.addNode(PATHWAY_LABEL, ID_KEY, pathwayId, "name", name, "source", source);
        return node;
    }

    /**
     * Open the target-UniProt flat file and add the information to the existing targets or generate new targets.
     */
    private void extractTargetFlatFileUniProt(final Workspace workspace, final Map<String, Target> idTargetMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-UniProt Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.TARGET_UNIPORT_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = entry.getFirst("TARGETID");
                if (id != null) {
                    final Target target = getOrCreateTarget(id, idTargetMap);
                    target.name = entry.getFirst("TARGNAME");
                    target.type = entry.getFirst("TARGTYPE");
                    target.uniProtID = entry.getFirst("UNIPROID");
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target UniProt", e);
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
     * Open the target-disease flat file and parse with TTD reader. The target information are extract and updated.
     */
    private void extractTargetNameFromTargetDiseaseFlatFile(final Workspace workspace,
                                                            final Map<String, Target> idTargetMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Disease Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.TARGET_DISEASE_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = entry.getFirst("TARGETID");
                if (id != null) {
                    final Target target = getOrCreateTarget(id, idTargetMap);
                    target.name = entry.getFirst("TARGNAME");
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target disease", e);
        }
    }

    private void createTargetNodes(final Graph graph, final Map<String, Target> idTargetMap) {
        for (final Target target : idTargetMap.values())
            graph.addNodeFromModel(target);
    }

    /**
     * Go through biomarker disease tsv and generate biomarker and disease with the additional biomarker-disease edges.
     */
    private void exportBiomarkerDiseaseTSV(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Biomarker-Disease TSV ...");
        boolean foundHeader = false;
        try (MappingIterable<BiomarkerDisease> entries = parseTsvFile(workspace, BiomarkerDisease.class,
                                                                      TTDUpdater.BIOMARKER_DISEASE_TSV)) {
            for (final BiomarkerDisease entry : entries) {
                final String targetId = entry.biomarkerId;
                if (targetId.equals("BiomarkerID")) {
                    foundHeader = true;
                    continue;
                }
                if (!foundHeader)
                    continue;
                Node biomarker = graph.findNode(BIOMARKER_LABEL, ID_KEY, entry.biomarkerId);
                if (biomarker == null)
                    biomarker = graph.addNode(BIOMARKER_LABEL, ID_KEY, entry.biomarkerId, "name", entry.biomarkerName);
                final String diseaseId = entry.icd11.replace("ICD-11: ", "");
                Node disease = graph.findNode(DISEASE_LABEL, ID_KEY, diseaseId);
                if (disease == null) {
                    final NodeBuilder builder = graph.buildNode().withLabel(DISEASE_LABEL);
                    builder.withProperty(ID_KEY, diseaseId);
                    builder.withProperty("ICD11", diseaseId);
                    builder.withProperty("name", entry.diseaseName);
                    if (!".".equals(entry.icd9)) {
                        final String[] icd9s = StringUtils.splitByWholeSeparator(entry.icd9.replace("ICD-9: ", ""),
                                                                                 ", ");
                        builder.withProperty("ICD9", icd9s);
                    }
                    if (!".".equals(entry.icd10)) {
                        final String[] icd10s = StringUtils.splitByWholeSeparator(entry.icd10.replace("ICD-10: ", ""),
                                                                                  ", ");
                        builder.withProperty("ICD10", icd10s);
                    }
                    disease = builder.build();
                }
                graph.addEdge(biomarker, disease, "ASSOCIATED_WITH");
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + TTDUpdater.BIOMARKER_DISEASE_TSV + "'", e);
        }
    }

    private <T> MappingIterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                                final String fileName) throws IOException {
        return new MappingIterable<>(
                FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, fileName, typeVariableClass));
    }

    /**
     * Open the target-disease flat file and parse with TTD reader. The target is checked if exist or generate new and
     * for all disease the node is checked or created and a new edge is generated.
     */
    private void exportTargetDiseaseFlatFile(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Disease Flat File ...");
        try (FlatFileTTDReader reader = openFlatFile(workspace, TTDUpdater.TARGET_DISEASE_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String targetId = entry.getFirst("TARGETID");
                if (targetId == null)
                    continue;
                final Node targetNode = getOrCreateTargetNode(graph, targetId, entry.getFirst("TARGNAME"));
                for (String diseaseInfo : entry.properties.get("INDICATI")) {
                    final String[] splitDiseaseInfo = StringUtils.split(diseaseInfo, '\t');
                    // some ICD11 has no [ ] around the id
                    final String diseaseId;
                    if (splitDiseaseInfo[2].startsWith("["))
                        diseaseId = splitDiseaseInfo[2].substring(1, splitDiseaseInfo[2].length() - 1).replace(
                                "ICD-11: ", "");
                    else
                        diseaseId = splitDiseaseInfo[2].replace("ICD-11: ", "");

                    final String clincalStatus = splitDiseaseInfo[0];
                    Node disease = graph.findNode(DISEASE_LABEL, ID_KEY, diseaseId);
                    if (disease == null) {
                        disease = graph.addNode(DISEASE_LABEL, ID_KEY, diseaseId, "name", splitDiseaseInfo[1], "ICD11",
                                                diseaseId);
                    }
                    graph.addEdge(targetNode, disease, "ASSOCIATED_WITH", "clinical_status", clincalStatus,
                                  "disease_name", splitDiseaseInfo[1]);
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File Target disease", e);
        }
    }

    private Node getOrCreateTargetNode(final Graph graph, final String id, final String name) {
        Node node = graph.findNode(TARGET_LABEL, ID_KEY, id);
        if (node == null) {
            if (name != null)
                node = graph.addNode(TARGET_LABEL, ID_KEY, id, "name", name);
            else
                node = graph.addNode(TARGET_LABEL, ID_KEY, id);
        }
        return node;
    }

    /**
     * Open the drug-disease flat file and parse with TTD reader. The  target is checked if exist or generate new and
     * for all disease the node is checked or created and a new edge is generated.
     */
    private void exportDrugDiseaseFlatFile(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Drug-Disease Flat File ...");
        try (FlatFileWithoutIDTTDReader reader = openFlatFileWithoutId(workspace, TTDUpdater.DRUG_DISEASE_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String drugId = entry.getFirst("TTDDRUID");
                if (drugId == null)
                    continue;
                final Node drug = graph.findNode(DRUG_LABEL, ID_KEY, drugId);
                for (final String diseaseInfo : entry.properties.get("INDICATI")) {
                    final String[] splitDiseaseInfo = StringUtils.split(diseaseInfo, '\t');


                    final String diseaseId = splitDiseaseInfo[1].replace("ICD-11: ", "");
                    final String clinicalStatus = splitDiseaseInfo[2];
                    Node disease = graph.findNode(DISEASE_LABEL, ID_KEY, diseaseId);
                    if (disease == null) {
                        disease = graph.addNode(DISEASE_LABEL, ID_KEY, diseaseId, "name", splitDiseaseInfo[0], "ICD11",
                                                diseaseId);
                    }
                    graph.addEdge(drug, disease, INDICATES_LABEL, "clinical_status", clinicalStatus, "disease_name",
                                  splitDiseaseInfo[0]);
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to export TTD Flat File drug disease", e);
        }
    }

    /**
     * go through kegg-target tsv file and if pair do not exist already add to graph. Also, if a node (target/pathway)
     * do not exist generate a new node.
     */
    private void exportPathwayKeggTargetTSV(final Workspace workspace, final Graph graph,
                                            final Map<String, Set<String>> targetIdPathwayMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export KEGG pathway Target TSV ...");
        boolean foundHeader = false;
        try (MappingIterable<KEGGPathwayToTarget> entries = parseTsvFile(workspace, KEGGPathwayToTarget.class,
                                                                         TTDUpdater.KEGG_PATHWAY_TO_TARGET_TSV)) {
            for (final KEGGPathwayToTarget entry : entries) {
                final String targetId = entry.ttdId;
                if (targetId.equals("TTDID")) {
                    foundHeader = true;
                    continue;
                }
                if (!foundHeader)
                    continue;
                final String pathwayId = entry.keggPathwayId;
                final Set<String> pathwayList = targetIdPathwayMap.computeIfAbsent(targetId, k -> new HashSet<>());
                if (!pathwayList.contains(pathwayId)) {
                    pathwayList.add(pathwayId);
                    Node node = getOrCreatePathwayNode(graph, pathwayId, entry.keggPathwayName, "KEGG");
                    graph.addEdge(getOrCreateTargetNode(graph, targetId, null), node, "ASSOCIATED_WITH");
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + TTDUpdater.KEGG_PATHWAY_TO_TARGET_TSV + "'", e);
        }
    }

    /**
     * go through Wiki-target tsv file and if pair do not exist already add to graph. Also, if a node (target/pathway)
     * do not exist generate a new node.
     */
    private void exportPathwayWikiTargetTSV(final Workspace workspace, final Graph graph,
                                            final Map<String, Set<String>> targetIdPathwayMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Wiki pathway Target TSV ...");
        boolean foundHeader = false;
        try (MappingIterable<WikiPathwayToTarget> entries = parseTsvFile(workspace, WikiPathwayToTarget.class,
                                                                         TTDUpdater.WIKI_PATHWAY_TO_TARGET_TSV)) {
            for (final WikiPathwayToTarget entry : entries) {
                final String targetId = entry.ttdId;
                if (targetId.equals("TTDID")) {
                    foundHeader = true;
                    continue;
                }
                if (!foundHeader)
                    continue;
                final String pathwayId = entry.wikiPathwayId;
                final Set<String> pathwayList = targetIdPathwayMap.computeIfAbsent(targetId, k -> new HashSet<>());
                if (!pathwayList.contains(pathwayId)) {
                    pathwayList.add(pathwayId);
                    final Node node = getOrCreatePathwayNode(graph, pathwayId, entry.wikiPathwayName, "WikiPathway");
                    graph.addEdge(getOrCreateTargetNode(graph, targetId, null), node, "ASSOCIATED_WITH");
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + TTDUpdater.WIKI_PATHWAY_TO_TARGET_TSV + "'", e);
        }
    }

    /**
     * add target-pathway edges to graph.
     */
    private void addTargetPathwayToData(final Graph graph, final Map<String, Set<String>> targetIdPathwayMap) {
        for (Map.Entry<String, Set<String>> entry : targetIdPathwayMap.entrySet()) {
            Node target = graph.findNode(TARGET_LABEL, ID_KEY, entry.getKey());
            for (final String pathwayId : entry.getValue()) {
                final Node pathway = graph.findNode(PATHWAY_LABEL, ID_KEY, pathwayId);
                graph.addEdge(target, pathway, "ASSOCIATED_WITH");
            }
        }
    }

    private void prepareEdgeInformationForDrugCompoundToTarget(final Graph graph,
                                                               final Map<String, Drug> dictionaryNodeIdToDrug,
                                                               final String targetId, final String nodeId,
                                                               final Map<String, Map<String, TargetDrugEdge>> targetIdNodeEdgeMap,
                                                               final TargetCompoundActivity entry) {
        targetIdNodeEdgeMap.computeIfAbsent(targetId, k -> new HashMap<>());
        TargetDrugEdge targetDrugEdge = targetIdNodeEdgeMap.get(targetId).get(nodeId);
        if (targetDrugEdge == null) {
            targetDrugEdge = new TargetDrugEdge();
            targetIdNodeEdgeMap.get(targetId).put(nodeId, targetDrugEdge);
        }
        if (targetDrugEdge.activity != null)
            LOGGER.warn("different edges have the same pair");
        targetDrugEdge.activity = entry.activity;
        getOrCreateTargetNode(graph, targetId, null);
        final boolean isDrugId = nodeId.startsWith("D");
        if (!dictionaryNodeIdToDrug.containsKey(nodeId)) {
            if (!isDrugId)
                graph.addNode(COMPOUND_LABEL, ID_KEY, nodeId, "pubchem_cid", entry.pubchemCID);
            dictionaryNodeIdToDrug.put(nodeId, new Drug(nodeId));
        }
        if (isDrugId) {
            final Drug drug = dictionaryNodeIdToDrug.get(nodeId);
            if (drug.pubChemCID == null)
                drug.pubChemCID = new String[]{entry.pubchemCID};
        }
    }

    /**
     * Go through target-compound activity tsv file and update the dictionary target id to drug id to model
     * TargetDrugEdge. If target/drug do not exist it is added to graph.
     */
    private Map<String, Map<String, TargetDrugEdge>> exportTargetCompoundActivityTSV(final Workspace workspace,
                                                                                     final Graph graph,
                                                                                     final Map<String, Drug> idDrugMap,
                                                                                     final Map<String, Map<String, TargetDrugEdge>> targetIdDrugEdgeMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Drug activity TSV ...");
        final Map<String, Map<String, TargetDrugEdge>> targetIdCompoundEdgeMap = new HashMap<>();
        final Map<String, Drug> compoundIdDrugMap = new HashMap<>();
        try (MappingIterable<TargetCompoundActivity> entries = parseTsvFile(workspace, TargetCompoundActivity.class,
                                                                            TTDUpdater.TARGET_COMPOUND_ACTIVITY_TSV)) {
            for (final TargetCompoundActivity entry : entries) {

                final String targetId = entry.ttdTargetId;
                final String drugId = entry.ttdDrugId;
                if (drugId.startsWith("D")) {
                    prepareEdgeInformationForDrugCompoundToTarget(graph, idDrugMap, targetId, drugId,
                                                                  targetIdDrugEdgeMap, entry);
                } else {
                    prepareEdgeInformationForDrugCompoundToTarget(graph, compoundIdDrugMap, targetId, drugId,
                                                                  targetIdCompoundEdgeMap, entry);
                }
            }
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + TTDUpdater.TARGET_COMPOUND_ACTIVITY_TSV + "'",
                                        e);
        }
        return targetIdCompoundEdgeMap;
    }

    /**
     * Go through the dictionary from target id to drug id to TargetDrugEdge Model and add edge to graph.
     */
    private void addTargetDrugEdges(final Graph graph,
                                    final Map<String, Map<String, TargetDrugEdge>> targetIdCompoundEdgeInfoMap,
                                    final String fromLabel) {
        for (final Map.Entry<String, Map<String, TargetDrugEdge>> entry : targetIdCompoundEdgeInfoMap.entrySet()) {
            final Node target = graph.findNode(TARGET_LABEL, ID_KEY, entry.getKey());
            final Map<String, TargetDrugEdge> drugToEdgeModel = entry.getValue();
            for (final String drugId : drugToEdgeModel.keySet()) {
                final Node drug = graph.findNode(fromLabel, ID_KEY, drugId);
                graph.buildEdge().withLabel(TARGETS_LABEL).fromNode(drug).toNode(target).withModel(
                        drugToEdgeModel.get(drugId)).build();
            }
        }
    }

    /**
     * Go through the target exel file and update the different edge information or add new pairs.
     */
    private void exportTargetDrugExcel(final Workspace workspace, final Graph graph,
                                       final Map<String, Map<String, TargetDrugEdge>> targetIdNodeEdgeMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export Target-Drug mapping Excel ...");
        try (InputStream stream = FileUtils.openInput(workspace, dataSource, TTDUpdater.TARGET_COMPOUND_MAPPIN_XLSX);
             XlsxMappingIterator<DrugTargetMapping> mappingIterator = new XlsxMappingIterator<>(DrugTargetMapping.class,
                                                                                                stream)) {
            while (mappingIterator.hasNext()) {
                final DrugTargetMapping entry = mappingIterator.next();
                final String targetId = entry.targetId;
                final String drugId = entry.drugId;
                targetIdNodeEdgeMap.computeIfAbsent(targetId, k -> new HashMap<>());
                final TargetDrugEdge targetDrugEdge = targetIdNodeEdgeMap.get(targetId).computeIfAbsent(drugId,
                                                                                                        k -> new TargetDrugEdge());
                if (targetDrugEdge.moa != null)
                    LOGGER.warn("different edges have the same pair");
                targetDrugEdge.moa = entry.moa;
                getOrCreateTargetNode(graph, targetId, null);
                if (graph.findNode(DRUG_LABEL, ID_KEY, drugId) == null)
                    graph.addNode(DRUG_LABEL, ID_KEY, drugId);
            }
        } catch (IOException e) {
            if (e instanceof FileNotFoundException)
                throw new ExporterException(new ParserFileNotFoundException(TTDUpdater.TARGET_COMPOUND_MAPPIN_XLSX));
            else
                throw new ExporterFormatException(e);
        }
    }

    private void createDrugNodes(final Graph graph, final Map<String, Drug> idDrugMap) {
        for (final Drug drug : idDrugMap.values())
            graph.addNodeFromModel(drug);
    }
}
