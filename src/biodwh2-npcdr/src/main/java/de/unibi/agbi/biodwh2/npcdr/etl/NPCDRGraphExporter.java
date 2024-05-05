package de.unibi.agbi.biodwh2.npcdr.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.npcdr.NPCDRDataSource;
import de.unibi.agbi.biodwh2.npcdr.model.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NPCDRGraphExporter extends GraphExporter<NPCDRDataSource> {
    public static final String NATURAL_PRODUCT_LABEL = "NaturalProduct";
    public static final String DRUG_LABEL = "Drug";
    public static final String TARGET_LABEL = "Target";
    public static final String CELL_LINE_LABEL = "CellLine";
    public static final String TAXON_LABEL = "Taxon";
    public static final String COMBINATION_LABEL = "Combination";
    public static final String DISEASE_LABEL = "Disease";
    static final String NCBI_TAX_ID_KEY = "ncbi_taxid";

    private final Map<String, Long> diseaseNameNodeIdMap = new HashMap<>();

    public NPCDRGraphExporter(final NPCDRDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(NATURAL_PRODUCT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CELL_LINE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(COMBINATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TAXON_LABEL, NCBI_TAX_ID_KEY, IndexDescription.Type.UNIQUE));
        exportNaturalProducts(workspace, graph);
        exportDrugs(workspace, graph);
        exportNaturalProductTargets(workspace, graph);
        exportDrugTargets(workspace, graph);
        exportCellLines(workspace, graph);
        exportNaturalProductSources(workspace, graph);
        exportCombinations(workspace, graph);
        exportCombinationClinical(workspace, graph);
        exportDrugClinical(workspace, graph);
        exportNaturalProductClinical(workspace, graph);
        exportCombinationEffectAndExperimentModel(workspace, graph);
        exportMoleculeRegulatedByCombination(workspace, graph);
        exportMoleculeRegulationTypeAndInfo(workspace, graph);
        diseaseNameNodeIdMap.clear();
        return false;
    }

    private void exportNaturalProducts(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.NP_FILE_NAME, NaturalProduct.class,
                                        (entry) -> exportNaturalProduct(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.NP_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProduct(final Graph graph, final NaturalProduct entry) {
        Integer chebi = null;
        if (StringUtils.isNotEmpty(entry.chebi) && !".".equals(entry.chebi))
            chebi = Integer.parseInt(StringUtils.split(entry.chebi, ":", 2)[1].strip());
        Integer pubChemCID = null;
        if (StringUtils.isNotEmpty(entry.pubChemCID) && !".".equals(entry.pubChemCID))
            pubChemCID = Integer.parseInt(StringUtils.split(entry.pubChemCID, ":", 2)[1].strip());
        if (chebi != null && pubChemCID != null)
            graph.addNodeFromModel(entry, "chebi", chebi, "pubchem_cid", pubChemCID);
        else if (chebi != null)
            graph.addNodeFromModel(entry, "chebi", chebi);
        else if (pubChemCID != null)
            graph.addNodeFromModel(entry, "pubchem_cid", pubChemCID);
        else
            graph.addNodeFromModel(entry);
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.DRUG_FILE_NAME, Drug.class, (entry) -> {
                Integer chebi = null;
                if (StringUtils.isNotEmpty(entry.chebi) && !".".equals(entry.chebi) && !entry.chebi.contains("CHEMBL"))
                    chebi = Integer.parseInt(StringUtils.split(entry.chebi, ":", 2)[1].strip());
                Integer pubChemCID = null;
                if (StringUtils.isNotEmpty(entry.pubChemCID) && !".".equals(entry.pubChemCID))
                    pubChemCID = Integer.parseInt(StringUtils.split(entry.pubChemCID, ":", 2)[1].strip());
                if (chebi != null && pubChemCID != null)
                    graph.addNodeFromModel(entry, "chebi", chebi, "pubchem_cid", pubChemCID);
                else if (chebi != null)
                    graph.addNodeFromModel(entry, "chebi", chebi);
                else if (pubChemCID != null)
                    graph.addNodeFromModel(entry, "pubchem_cid", pubChemCID);
                else
                    graph.addNodeFromModel(entry);
            });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.DRUG_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProductTargets(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.NP_TARGET_FILE_NAME,
                                        NaturalProductTarget.class,
                                        (entry) -> exportNaturalProductTarget(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.NP_TARGET_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProductTarget(final Graph graph, final NaturalProductTarget entry) {
        final Integer[] geneIds = parseGeneIds(entry.geneId);
        final String[] keggIds = parseKeggIds(entry.keggId);
        if (geneIds != null && keggIds != null)
            graph.addNodeFromModel(entry, "gene_ids", geneIds, "kegg_ids", keggIds);
        else if (geneIds != null)
            graph.addNodeFromModel(entry, "gene_ids", geneIds);
        else if (keggIds != null)
            graph.addNodeFromModel(entry, "kegg_ids", keggIds);
        else
            graph.addNodeFromModel(entry);
    }

    private Integer[] parseGeneIds(final String value) {
        Integer[] geneIds = null;
        if (StringUtils.isNotEmpty(value) && !".".equals(value)) {
            final String[] parts = StringUtils.split(value, ";");
            geneIds = new Integer[parts.length];
            for (int i = 0; i < parts.length; i++)
                geneIds[i] = Integer.parseInt(parts[i].replace("GeneID:", "").strip());
        }
        return geneIds;
    }

    private String[] parseKeggIds(final String value) {
        String[] keggIds = null;
        if (StringUtils.isNotEmpty(value) && !".".equals(value)) {
            keggIds = StringUtils.split(value, ";");
            for (int i = 0; i < keggIds.length; i++)
                keggIds[i] = keggIds[i].strip();
        }
        return keggIds;
    }

    private void exportDrugTargets(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.DRUG_TARGET_FILE_NAME, DrugTarget.class,
                                        (entry) -> exportDrugTarget(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.DRUG_TARGET_FILE_NAME + "'", e);
        }
    }

    private void exportDrugTarget(final Graph graph, final DrugTarget entry) {
        if (graph.findNode(TARGET_LABEL, ID_KEY, entry.proteinId) != null)
            return;
        final Integer[] geneIds = parseGeneIds(entry.geneId);
        final String[] keggIds = parseKeggIds(entry.keggId);
        if (geneIds != null && keggIds != null)
            graph.addNodeFromModel(entry, "gene_ids", geneIds, "kegg_ids", keggIds);
        else if (geneIds != null)
            graph.addNodeFromModel(entry, "gene_ids", geneIds);
        else if (keggIds != null)
            graph.addNodeFromModel(entry, "kegg_ids", keggIds);
        else
            graph.addNodeFromModel(entry);
    }

    private void exportCellLines(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.CELL_LINE_FILE_NAME, CellLine.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.CELL_LINE_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProductSources(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.NP_SOURCE_FILE_NAME,
                                        NaturalProductSource.class,
                                        (entry) -> exportNaturalProductSource(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.NP_SOURCE_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProductSource(final Graph graph, final NaturalProductSource entry) {
        final var naturalProductNode = graph.findNode(NATURAL_PRODUCT_LABEL, ID_KEY, entry.naturalProductId);
        if (naturalProductNode == null)
            return;
        final var taxonNodes = new Node[]{
                getOrCreateTaxon(graph, parseNCBITaxId(entry.superkingdomOrKingdomId), entry.superkingdomOrKingdomName,
                                 "Kingdom", null), getOrCreateTaxon(graph, parseNCBITaxId(entry.phylumId),
                                                                    entry.phylumName, "Phylum",
                                                                    parseNCBITaxId(entry.superkingdomOrKingdomId)),
                getOrCreateTaxon(graph, parseNCBITaxId(entry.classId), entry.className, "Class",
                                 parseNCBITaxId(entry.phylumId)), getOrCreateTaxon(graph, parseNCBITaxId(entry.orderId),
                                                                                   entry.orderName, "Class",
                                                                                   parseNCBITaxId(entry.classId)),
                getOrCreateTaxon(graph, parseNCBITaxId(entry.familyId), entry.familyName, "Class",
                                 parseNCBITaxId(entry.orderId)), getOrCreateTaxon(graph, parseNCBITaxId(entry.genusId),
                                                                                  entry.genusName, "Class",
                                                                                  parseNCBITaxId(entry.familyId)),
                getOrCreateTaxon(graph, parseNCBITaxId(entry.speciesId), entry.speciesName, "Class",
                                 parseNCBITaxId(entry.genusId))
        };
        for (int i = taxonNodes.length - 1; i >= 0; i--) {
            if (taxonNodes[i] != null) {
                graph.addEdge(naturalProductNode, taxonNodes[i], "HAS_SOURCE");
                break;
            }
        }
    }

    private Integer parseNCBITaxId(final String value) {
        if (StringUtils.isEmpty(value) || ".".equals(value))
            return null;
        return Integer.parseInt(value.strip());
    }

    private Node getOrCreateTaxon(final Graph graph, final Integer id, final String name, final String type,
                                  final Integer parentId) {
        if (id == null)
            return null;
        Node taxonNode = graph.findNode(TAXON_LABEL, NCBI_TAX_ID_KEY, id);
        if (taxonNode == null) {
            taxonNode = graph.addNode(TAXON_LABEL, NCBI_TAX_ID_KEY, id, "name", name, "type", type);
            if (parentId != null)
                graph.addEdge(graph.findNode(TAXON_LABEL, NCBI_TAX_ID_KEY, parentId), taxonNode, "HAS_CHILD");
        }
        return taxonNode;
    }

    private void exportCombinations(final Workspace workspace, final Graph graph) {
        try {
            final Set<String> alreadyAddedCombinationIds = new HashSet<>();
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.PAIR_INFO_FILE_NAME, Pair.class,
                                        (entry) -> exportCombination(graph, entry, alreadyAddedCombinationIds));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.PAIR_INFO_FILE_NAME + "'", e);
        }
    }

    private void exportCombination(final Graph graph, final Pair entry, final Set<String> alreadyAddedCombinationIds) {
        if (alreadyAddedCombinationIds.contains(entry.id))
            return;
        alreadyAddedCombinationIds.add(entry.id);
        final var combinationNode = graph.addNode(COMBINATION_LABEL, ID_KEY, entry.id);
        for (final String drugId : StringUtils.split(entry.drugId, "_"))
            graph.addEdge(graph.findNode(DRUG_LABEL, ID_KEY, drugId), combinationNode, "PART_OF");
        for (final String naturalProductId : StringUtils.split(entry.naturalProductId, "_")) {
            final var naturalProductNode = graph.findNode(NATURAL_PRODUCT_LABEL, ID_KEY, naturalProductId);
            if (naturalProductNode != null)
                graph.addEdge(naturalProductNode, combinationNode, "PART_OF");
        }
    }

    private void exportCombinationClinical(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.CLINICAL_ICD_FILE_NAME,
                                        CombinationClinical.class, (entry) -> {
                        final Node combinationNode = graph.findNode(COMBINATION_LABEL, ID_KEY, entry.combinationId);
                        final var diseaseNode = getOrCreateDiseaseNode(graph, entry.icdCode, entry.disease);
                        graph.addEdge(combinationNode, diseaseNode, "INDICATES", "clinical_status",
                                      entry.clinicalStatus);
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.CLINICAL_ICD_FILE_NAME + "'", e);
        }
    }

    private Long getOrCreateDiseaseNode(final Graph graph, final String icdCode, final String name) {
        if (icdCode == null || StringUtils.containsIgnoreCase(icdCode, "N.A.")) {
            Long nodeId = diseaseNameNodeIdMap.get(name);
            if (nodeId == null) {
                nodeId = graph.addNode(DISEASE_LABEL, "name", name).getId();
                diseaseNameNodeIdMap.put(name, nodeId);
            }
            return nodeId;
        }
        final String icdCodeWithoutPrefix = icdCode.replace("ICD-11:", "").strip();
        var node = graph.findNode(DISEASE_LABEL, ID_KEY, icdCodeWithoutPrefix);
        if (node == null) {
            node = graph.addNode(DISEASE_LABEL, ID_KEY, icdCodeWithoutPrefix, "name", name);
            diseaseNameNodeIdMap.put(name, node.getId());
        }
        return node.getId();
    }

    private void exportDrugClinical(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.DRUG_CLINICAL_ICD_FILE_NAME,
                                        DrugClinical.class, (entry) -> {
                        final Node drugNode = graph.findNode(DRUG_LABEL, ID_KEY, entry.drugId);
                        final var diseaseNode = getOrCreateDiseaseNode(graph, entry.icdCode, entry.disease);
                        graph.addEdge(drugNode, diseaseNode, "INDICATES", "clinical_status", entry.clinicalStatus);
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.DRUG_CLINICAL_ICD_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProductClinical(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.NP_CLINICAL_ICD_FILE_NAME,
                                        NaturalProductClinical.class,
                                        (entry) -> exportNaturalProductClinical(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.NP_CLINICAL_ICD_FILE_NAME + "'", e);
        }
    }

    private void exportNaturalProductClinical(final Graph graph, final NaturalProductClinical entry) {
        final Node naturalProductNode = graph.findNode(DRUG_LABEL, ID_KEY, entry.naturalProductId);
        if (naturalProductNode == null)
            return;
        final var diseaseNode = getOrCreateDiseaseNode(graph, entry.icdCode, entry.disease);
        graph.addEdge(naturalProductNode, diseaseNode, "INDICATES", "clinical_status", entry.clinicalStatus);
    }

    private void exportCombinationEffectAndExperimentModel(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.EFFECT_EXPERIMENT_FILE_NAME,
                                        CombinationEffectAndExperimentModel.class, (entry) -> {
                        // TODO
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.EFFECT_EXPERIMENT_FILE_NAME + "'", e);
        }
    }

    private void exportMoleculeRegulatedByCombination(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.MOLECULE_REGULATED_FILE_NAME,
                                        MoleculeRegulatedByCombination.class, (entry) -> {
                        // TODO
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.MOLECULE_REGULATED_FILE_NAME + "'", e);
        }
    }

    private void exportMoleculeRegulationTypeAndInfo(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, NPCDRUpdater.MOLECULE_REGULATION_TYPE_FILE_NAME,
                                        MoleculeRegulationTypeAndInfo.class, (entry) -> {
                        // TODO
                    });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + NPCDRUpdater.MOLECULE_REGULATION_TYPE_FILE_NAME + "'",
                                        e);
        }
    }
}
