package de.unibi.agbi.biodwh2.hmdb.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.hmdb.HMDBDataSource;
import de.unibi.agbi.biodwh2.hmdb.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HMDBGraphExporter extends GraphExporter<HMDBDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(HMDBGraphExporter.class);
    static final String PATHWAY_LABEL = "Pathway";
    public static final String PFAM_LABEL = "Pfam";
    public static final String GO_CLASS_LABEL = "GOClass";
    public static final String ONTOLOGY_TERM_LABEL = "OntologyTerm";
    static final String DISEASE_LABEL = "Disease";
    static final String REFERENCE_LABEL = "Reference";
    public static final String PROTEIN_LABEL = "Protein";
    public static final String GENE_LABEL = "Gene";
    static final String METABOLITE_LABEL = "Metabolite";
    static final String TRANSLATES_TO_LABEL = "TRANSLATES_TO";
    private final Map<String, Long> referenceTextNodeIdMap = new HashMap<>();
    private final Map<String, Long> diseaseNameNodeIdMap = new HashMap<>();
    private final Map<String, Long> pathwayNameNodeIdMap = new HashMap<>();
    private final Map<String, Long> proteinMetaboliteLinkNodeIdMap = new HashMap<>();
    private final Map<String, List<Long>> proteinMetaboliteLinkCache = new HashMap<>();
    private final Map<String, MetaboliteStructure> metaboliteStructureMap = new HashMap<>();

    public HMDBGraphExporter(final HMDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        referenceTextNodeIdMap.clear();
        diseaseNameNodeIdMap.clear();
        pathwayNameNodeIdMap.clear();
        proteinMetaboliteLinkNodeIdMap.clear();
        metaboliteStructureMap.clear();
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pubmed_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(METABOLITE_LABEL, "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, "smpdb_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, "kegg_map_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "hgnc_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GO_CLASS_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PFAM_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ONTOLOGY_TERM_LABEL, "term", IndexDescription.Type.UNIQUE));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting proteins...");
        exportProteins(workspace, graph);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Extracting structures...");
        extractStructureInformation(workspace);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting metabolites...");
        exportMetabolites(workspace, graph);
        return true;
    }

    private void exportProteins(final Workspace workspace, final Graph graph) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, HMDBUpdater.PROTEINS_XML_FILE_NAME);
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to find file '" + HMDBUpdater.PROTEINS_XML_FILE_NAME + "'");
        try {
            final int[] counter = new int[]{1};
            FileUtils.forEachZipEntry(zipFile, ".xml", (stream, entry) -> {
                final XmlMapper xmlMapper = new XmlMapper();
                final FromXmlParser parser = FileUtils.createXmlParser(stream, xmlMapper);
                // Skip the first structure token which is the root HMDB node
                //noinspection UnusedAssignment
                JsonToken token = parser.nextToken();
                while ((token = parser.nextToken()) != null)
                    if (token.isStructStart()) {
                        if (counter[0] % 1_000 == 0 && LOGGER.isInfoEnabled())
                            LOGGER.info("Exporting proteins progress " + counter[0]);
                        counter[0]++;
                        exportProtein(graph, xmlMapper.readValue(parser, Protein.class));
                    }
            });
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to parse the file '" + HMDBUpdater.PROTEINS_XML_FILE_NAME + "'",
                                              e);
        }
    }

    private void exportProtein(final Graph graph, final Protein protein) {
        final NodeBuilder builder = graph.buildNode().withLabel(PROTEIN_LABEL).withModel(protein);
        if (protein.proteinProperties != null)
            builder.withModel(protein.proteinProperties);
        final Node proteinNode = builder.build();
        for (final Reference reference : protein.generalReferences)
            graph.addEdge(proteinNode, getOrCreateReferenceNode(graph, reference), "HAS_REFERENCE");
        if (protein.pathways != null && !protein.pathways.isEmpty())
            for (final Pathway pathway : protein.pathways)
                graph.addEdge(proteinNode, getOrCreatePathwayNode(graph, pathway), "ASSOCIATED_WITH");
        if (protein.goClassifications != null)
            for (final GOClass goClass : protein.goClassifications)
                graph.addEdge(proteinNode, getOrCreateGOClassNode(graph, goClass), "HAS_GO_CLASS");
        if (protein.proteinProperties != null && protein.proteinProperties.pfams != null)
            for (final Pfam pfam : protein.proteinProperties.pfams)
                graph.addEdge(proteinNode, getOrCreatePfamNode(graph, pfam), "HAS_PFAM");
        if (protein.metaboliteReferences != null) {
            for (final MetaboliteReference reference : protein.metaboliteReferences) {
                if (reference.reference != null) {
                    final String key = protein.accession + "|" + reference.metabolite.accession;
                    Long associationNodeId = proteinMetaboliteLinkNodeIdMap.get(key);
                    if (associationNodeId == null) {
                        associationNodeId = graph.addNode("ProteinMetaboliteLink").getId();
                        proteinMetaboliteLinkNodeIdMap.put(key, associationNodeId);
                    }
                    if (!proteinMetaboliteLinkCache.containsKey(reference.metabolite.accession))
                        proteinMetaboliteLinkCache.put(reference.metabolite.accession, new ArrayList<>());
                    proteinMetaboliteLinkCache.get(reference.metabolite.accession).add(associationNodeId);
                    final Long referenceNodeId = getOrCreateReferenceNode(graph, reference.reference);
                    graph.addEdge(proteinNode, associationNodeId, "ASSOCIATED_WITH");
                    graph.addEdge(associationNodeId, referenceNodeId, "HAS_REFERENCE");
                }
            }
        }
        final Node geneNode = getOrCreateGeneNode(graph, protein);
        graph.addEdge(geneNode, proteinNode, TRANSLATES_TO_LABEL);
        // ignored because bidirectional: metaboliteAssociations
    }

    private Node getOrCreateGeneNode(final Graph graph, final Protein protein) {
        Node node = null;
        if (StringUtils.isNotEmpty(protein.hgncId)) {
            if (protein.hgncId.startsWith("GNC:"))
                protein.hgncId = 'H' + protein.hgncId;
            node = graph.findNode(GENE_LABEL, "hgnc_id", protein.hgncId);
        }
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(GENE_LABEL);
            builder.withPropertyIfNotNull("hgnc_id", protein.hgncId);
            builder.withPropertyIfNotNull("name", protein.geneName);
            builder.withPropertyIfNotNull("genbank_gene_id", protein.genbankGeneId);
            builder.withPropertyIfNotNull("genecard_id", protein.genecardId);
            builder.withPropertyIfNotNull("geneatlas_id", protein.geneatlasId);
            if (protein.geneProperties != null)
                builder.withModel(protein.geneProperties);
            node = builder.build();
        }
        return node;
    }

    private Node getOrCreateGOClassNode(final Graph graph, final GOClass goClass) {
        final Node node = graph.findNode(GO_CLASS_LABEL, ID_KEY, goClass.goId);
        return node == null ? graph.addNodeFromModel(goClass) : node;
    }

    private Node getOrCreatePfamNode(final Graph graph, final Pfam pfam) {
        final Node node = graph.findNode(PFAM_LABEL, ID_KEY, pfam.pfamId);
        return node == null ? graph.addNodeFromModel(pfam) : node;
    }

    private void extractStructureInformation(final Workspace workspace) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, HMDBUpdater.STRUCTURES_SDF_FILE_NAME);
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to find file '" + HMDBUpdater.STRUCTURES_SDF_FILE_NAME + "'");
        try {
            final int[] counter = new int[]{1};
            FileUtils.forEachZipEntry(zipFile, ".xml", (stream, zipEntry) -> {
                final SdfReader reader = new SdfReader(stream, StandardCharsets.UTF_8);
                for (final SdfEntry entry : reader) {
                    if (counter[0] % 10_000 == 0 && LOGGER.isInfoEnabled())
                        LOGGER.info("Processing structures progress " + counter[0]);
                    counter[0]++;
                    final MetaboliteStructure structure = new MetaboliteStructure();
                    structure.structure = entry.getConnectionTable();
                    structure.jchemAcceptorCount = getSdfIntegerPropertyOrNull(entry, "JCHEM_ACCEPTOR_COUNT");
                    structure.jchemAtomCount = getSdfIntegerPropertyOrNull(entry, "JCHEM_ATOM_COUNT");
                    structure.jchemAveragePolarizability = entry.properties.get("JCHEM_AVERAGE_POLARIZABILITY");
                    structure.jchemBioavailability = entry.properties.get("JCHEM_BIOAVAILABILITY");
                    structure.jchemDonorCount = getSdfIntegerPropertyOrNull(entry, "JCHEM_DONOR_COUNT");
                    structure.jchemFormalCharge = entry.properties.get("JCHEM_FORMAL_CHARGE");
                    structure.jchemGhoseFilter = entry.properties.get("JCHEM_GHOSE_FILTER");
                    structure.jchemIupac = entry.properties.get("JCHEM_IUPAC");
                    structure.alogpsLogp = entry.properties.get("ALOGPS_LOGP");
                    structure.jchemLogp = entry.properties.get("JCHEM_LOGP");
                    structure.alogpsLogs = entry.properties.get("ALOGPS_LOGS");
                    structure.jchemMddrLikeRule = entry.properties.get("JCHEM_MDDR_LIKE_RULE");
                    structure.jchemNumberOfRings = entry.properties.get("JCHEM_NUMBER_OF_RINGS");
                    structure.jchemPhysiologicalCharge = entry.properties.get("JCHEM_PHYSIOLOGICAL_CHARGE");
                    structure.jchemPkaStrongestAcidic = entry.properties.get("JCHEM_PKA_STRONGEST_ACIDIC");
                    structure.jchemPkaStrongestBasic = entry.properties.get("JCHEM_PKA_STRONGEST_BASIC");
                    structure.jchemPolarSurfaceArea = entry.properties.get("JCHEM_POLAR_SURFACE_AREA");
                    structure.jchemRefractivity = entry.properties.get("JCHEM_REFRACTIVITY");
                    structure.jchemRotatableBondCount = getSdfIntegerPropertyOrNull(entry,
                                                                                    "JCHEM_ROTATABLE_BOND_COUNT");
                    structure.jchemRuleOfFive = entry.properties.get("JCHEM_RULE_OF_FIVE");
                    structure.alogpsSolubility = entry.properties.get("ALOGPS_SOLUBILITY");
                    structure.jchemTraditionalIupac = entry.properties.get("JCHEM_TRADITIONAL_IUPAC");
                    structure.jchemVeberRule = entry.properties.get("JCHEM_VEBER_RULE");
                    metaboliteStructureMap.put(entry.properties.get("HMDB_ID"), structure);
                }
            });
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to parse the file '" + HMDBUpdater.STRUCTURES_SDF_FILE_NAME + "'",
                                              e);
        }
    }

    private Integer getSdfIntegerPropertyOrNull(final SdfEntry entry, final String key) {
        final String value = entry.properties.get(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    private void exportMetabolites(final Workspace workspace, final Graph graph) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, HMDBUpdater.METABOLITES_XML_FILE_NAME);
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to find file '" + HMDBUpdater.METABOLITES_XML_FILE_NAME + "'");
        try {
            final int[] counter = new int[]{1};
            FileUtils.forEachZipEntry(zipFile, ".xml", (stream, entry) -> {
                final XmlMapper xmlMapper = new XmlMapper();
                final FromXmlParser parser = FileUtils.createXmlParser(stream, xmlMapper);
                // Skip the first structure token which is the root HMDB node
                //noinspection UnusedAssignment
                JsonToken token = parser.nextToken();
                while ((token = parser.nextToken()) != null)
                    if (token.isStructStart()) {
                        if (counter[0] % 10_000 == 0 && LOGGER.isInfoEnabled())
                            LOGGER.info("Exporting metabolites progress " + counter[0]);
                        counter[0]++;
                        exportMetabolite(graph, xmlMapper.readValue(parser, Metabolite.class));
                    }
            });
        } catch (Exception e) {
            throw new ExporterFormatException(
                    "Failed to parse the file '" + HMDBUpdater.METABOLITES_XML_FILE_NAME + "'", e);
        }
    }

    private void exportMetabolite(final Graph graph, final Metabolite metabolite) {
        final NodeBuilder builder = graph.buildNode().withLabel(METABOLITE_LABEL).withModel(metabolite);
        final MetaboliteStructure structure = metaboliteStructureMap.get(metabolite.accession);
        if (structure != null) {
            builder.withModel(structure);
            metaboliteStructureMap.remove(metabolite.accession);
        }
        if (metabolite.spectra != null && !metabolite.spectra.isEmpty())
            builder.withProperty("spectra", metabolite.spectra.stream().map(s -> s.type + ':' + s.spectrumId)
                                                              .collect(Collectors.joining(";")));
        if (metabolite.biologicalProperties != null) {
            if (metabolite.biologicalProperties.cellularLocations != null &&
                !metabolite.biologicalProperties.cellularLocations.isEmpty())
                builder.withProperty("cellular_locations",
                                     metabolite.biologicalProperties.cellularLocations.toArray(new String[0]));
            if (metabolite.biologicalProperties.biospecimenLocations != null &&
                !metabolite.biologicalProperties.biospecimenLocations.isEmpty())
                builder.withProperty("biospecimen_locations",
                                     metabolite.biologicalProperties.biospecimenLocations.toArray(new String[0]));
            if (metabolite.biologicalProperties.tissueLocations != null &&
                !metabolite.biologicalProperties.tissueLocations.isEmpty())
                builder.withProperty("tissue_locations",
                                     metabolite.biologicalProperties.tissueLocations.toArray(new String[0]));
        }
        if (metabolite.experimentalProperties != null && !metabolite.experimentalProperties.isEmpty())
            builder.withProperty("experimental_properties",
                                 getPropertiesListAsString(metabolite.experimentalProperties));
        if (metabolite.predictedProperties != null && !metabolite.predictedProperties.isEmpty())
            builder.withProperty("predicted_properties", getPropertiesListAsString(metabolite.predictedProperties));
        final Node metaboliteNode = builder.build();
        for (final Reference reference : metabolite.generalReferences)
            graph.addEdge(metaboliteNode, getOrCreateReferenceNode(graph, reference), "HAS_REFERENCE");
        exportMetaboliteConcentrations(graph, metabolite, metaboliteNode);
        if (metabolite.proteinAssociations != null)
            for (final ProteinAssociation protein : metabolite.proteinAssociations)
                graph.addEdge(metaboliteNode, getOrCreateProteinNode(graph, protein), "ASSOCIATED_WITH");
        if (metabolite.biologicalProperties != null && metabolite.biologicalProperties.pathways != null &&
            !metabolite.biologicalProperties.pathways.isEmpty()) {
            for (final Pathway pathway : metabolite.biologicalProperties.pathways)
                graph.addEdge(metaboliteNode, getOrCreatePathwayNode(graph, pathway), "ASSOCIATED_WITH");
        }
        if (metabolite.diseases != null)
            for (final Disease disease : metabolite.diseases)
                graph.addEdge(metaboliteNode, getOrCreateDiseaseNode(graph, disease), "ASSOCIATED_WITH");
        if (metabolite.ontology != null)
            for (final OntologyTerm term : metabolite.ontology)
                graph.addEdge(metaboliteNode, getOrCreateOntologyTerm(graph, term), "HAS_TERM");
        if (metabolite.taxonomy != null) {
            // TODO
        }
        final List<Long> proteinMetaboliteLinkNodeIds = proteinMetaboliteLinkCache.get(metabolite.accession);
        if (proteinMetaboliteLinkNodeIds != null)
            for (final Long associationNodeId : proteinMetaboliteLinkNodeIds)
                graph.addEdge(metaboliteNode.getId(), associationNodeId, "ASSOCIATED_WITH");
    }

    private Long getOrCreateReferenceNode(final Graph graph, final Reference reference) {
        if (reference.pubmedId != null) {
            Node node = graph.findNode(REFERENCE_LABEL, "pubmed_id", reference.pubmedId);
            if (node == null) {
                node = graph.addNode(REFERENCE_LABEL, "pubmed_id", reference.pubmedId, "text", reference.referenceText);
            }
            return node.getId();
        }
        Long nodeId = referenceTextNodeIdMap.get(reference.referenceText);
        if (nodeId != null) {
            return nodeId;
        }
        nodeId = graph.addNode(REFERENCE_LABEL, "text", reference.referenceText).getId();
        referenceTextNodeIdMap.put(reference.referenceText, nodeId);
        return nodeId;
    }

    private void exportMetaboliteConcentrations(final Graph graph, final Metabolite metabolite,
                                                final Node metaboliteNode) {
        for (final Concentration concentration : metabolite.normalConcentrations) {
            final Node concentrationNode = graph.addNodeFromModel(concentration);
            graph.addEdge(metaboliteNode, concentrationNode, "HAS_CONCENTRATION", "type", "normal");
            if (concentration.references != null)
                for (final Reference reference : concentration.references)
                    graph.addEdge(concentrationNode, getOrCreateReferenceNode(graph, reference), "HAS_REFERENCE");
        }
        for (final Concentration concentration : metabolite.abnormalConcentrations) {
            final Node concentrationNode = graph.addNodeFromModel(concentration);
            if (concentration.references != null)
                for (final Reference reference : concentration.references)
                    graph.addEdge(concentrationNode, getOrCreateReferenceNode(graph, reference), "HAS_REFERENCE");
            graph.addEdge(metaboliteNode, concentrationNode, "HAS_CONCENTRATION", "type", "abnormal");
        }
    }

    private long getOrCreateProteinNode(final Graph graph, final ProteinAssociation protein) {
        Node node = graph.findNode(PROTEIN_LABEL, "accession", protein.proteinAccession);
        if (node == null)
            node = graph.addNodeFromModel(protein);
        return node.getId();
    }

    private String getPropertiesListAsString(final List<Property> properties) {
        return properties.stream().map(p -> p.kind + ':' + p.value + ':' + p.source).collect(Collectors.joining(";"));
    }

    private Long getOrCreatePathwayNode(final Graph graph, final Pathway pathway) {
        Long nodeId = null;
        if (pathway.keggMapId != null) {
            final Node node = graph.findNode(PATHWAY_LABEL, "kegg_map_id", pathway.keggMapId);
            nodeId = node != null ? node.getId() : null;
        }
        if (nodeId == null && pathway.smpdbId != null) {
            final Node node = graph.findNode(PATHWAY_LABEL, "smpdb_id", pathway.smpdbId);
            nodeId = node != null ? node.getId() : null;
        }
        if (nodeId == null)
            nodeId = pathwayNameNodeIdMap.get(pathway.name);
        if (nodeId == null) {
            if (pathway.keggMapId != null && pathway.smpdbId != null)
                nodeId = graph.addNode(PATHWAY_LABEL, "name", pathway.name, "kegg_map_id", pathway.keggMapId,
                                       "smpdb_id", pathway.smpdbId).getId();
            else if (pathway.smpdbId != null)
                nodeId = graph.addNode(PATHWAY_LABEL, "name", pathway.name, "smpdb_id", pathway.smpdbId).getId();
            else if (pathway.keggMapId != null)
                nodeId = graph.addNode(PATHWAY_LABEL, "name", pathway.name, "kegg_map_id", pathway.keggMapId).getId();
            else
                nodeId = graph.addNode(PATHWAY_LABEL, "name", pathway.name).getId();
            pathwayNameNodeIdMap.put(pathway.name, nodeId);
        }
        return nodeId;
    }

    private Long getOrCreateDiseaseNode(final Graph graph, final Disease disease) {
        Long nodeId = null;
        if (disease.omimId != null) {
            final Node node = graph.findNode(DISEASE_LABEL, "omim_id", disease.omimId);
            nodeId = node != null ? node.getId() : null;
        }
        if (nodeId == null)
            nodeId = diseaseNameNodeIdMap.get(disease.name);
        if (nodeId == null) {
            if (disease.omimId != null)
                nodeId = graph.addNode(DISEASE_LABEL, "name", disease.name, "omim_id", disease.omimId).getId();
            else
                nodeId = graph.addNode(DISEASE_LABEL, "name", disease.name).getId();
            diseaseNameNodeIdMap.put(disease.name, nodeId);
        }
        return nodeId;
    }

    private Node getOrCreateOntologyTerm(final Graph graph, final OntologyTerm term) {
        Node node = graph.findNode(ONTOLOGY_TERM_LABEL, "term", term.term);
        if (node == null)
            node = graph.addNodeFromModel(term);
        if (term.descendants != null) {
            for (final OntologyTerm child : term.descendants) {
                final Node childNode = getOrCreateOntologyTerm(graph, child);
                if (!graph.containsEdge("HAS_CHILD", node.getId(), childNode.getId()))
                    graph.addEdge(node, childNode, "HAS_CHILD");
            }
        }
        return node;
    }
}
