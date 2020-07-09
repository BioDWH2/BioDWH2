package de.unibi.agbi.biodwh2.ncbi.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.PropertyContainer;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;
import de.unibi.agbi.biodwh2.ncbi.model.GeneAccession;
import de.unibi.agbi.biodwh2.ncbi.model.GeneGo;
import de.unibi.agbi.biodwh2.ncbi.model.GeneInfo;
import de.unibi.agbi.biodwh2.ncbi.model.GeneRelationship;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NCBIGraphExporter extends GraphExporter<NCBIDataSource> {
    private static final Logger logger = LoggerFactory.getLogger(NCBIGraphExporter.class);
    private Map<Long, Long> geneIdNodeIdMap;

    @Override
    protected boolean exportGraph(final Workspace workspace, final NCBIDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id");
        geneIdNodeIdMap = new HashMap<>();
        try {
            exportGeneDatabase(workspace, dataSource, graph);
        } catch (IOException e) {
            throw new ExporterException("Failed to export NCBI Gene database", e);
        }
        try {
            exportPubChemDatabase(workspace, dataSource, graph);
        } catch (IOException e) {
            throw new ExporterException("Failed to export NCBI PubChem database", e);
        }
        return true;
    }

    private void exportGeneDatabase(final Workspace workspace, final DataSource dataSource,
                                    final Graph graph) throws IOException {
        logger.info("Exporting gene_info.gz...");
        MappingIterator<GeneInfo> geneInfos = FileUtils.openGzipTsv(workspace, dataSource, "gene_info.gz",
                                                                    GeneInfo.class);
        while (geneInfos.hasNext()) {
            GeneInfo geneInfo = geneInfos.next();
            if (!geneInfo.taxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(geneInfo.geneId);
            Node geneNode = createNode(graph, "Gene");
            geneNode.setProperty("id", geneId);
            setPropertyIfNotDash(geneNode, "symbol", geneInfo.symbol);
            setPropertyIfNotDash(geneNode, "chromosome", geneInfo.chromosome);
            setPropertyIfNotDash(geneNode, "locus_tag", geneInfo.locusTag);
            setPropertyIfNotDash(geneNode, "type", geneInfo.typeOfGene);
            setPropertyIfNotDash(geneNode, "description", geneInfo.description);
            setArrayPropertyIfNotDash(geneNode, "synonyms", geneInfo.synonyms);
            setArrayPropertyIfNotDash(geneNode, "xrefs", geneInfo.dbXrefs);
            setArrayPropertyIfNotDash(geneNode, "feature_types", geneInfo.featureType);
            // TODO: mapLocation, symbolFromNomenclatureAuthority, fullNameFromNomenclatureAuthority,
            // TODO: nomenclatureStatus, otherDesignations
            geneIdNodeIdMap.put(geneId, geneNode.getId());
            graph.update(geneNode);
        }
        logger.info("Exporting gene2accession.gz...");
        MappingIterator<GeneAccession> accessions = FileUtils.openGzipTsv(workspace, dataSource, "gene2accession.gz",
                                                                          GeneAccession.class);
        while (accessions.hasNext()) {
            GeneAccession accession = accessions.next();
            if (!accession.taxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(accession.geneId);
            Node accessionNode = createAccessionNode(graph, accession);
            graph.addEdge(geneIdNodeIdMap.get(geneId), accessionNode, "HAS_ACCESSION");
        }
        logger.info("Exporting gene2go.gz...");
        MappingIterator<GeneGo> goAnnotations = FileUtils.openGzipTsv(workspace, dataSource, "gene2go.gz",
                                                                      GeneGo.class);
        while (goAnnotations.hasNext()) {
            GeneGo go = goAnnotations.next();
            if (!go.taxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(go.geneId);
            Node goTermNode = graph.findNode("GoTerm", "id", go.goId);
            if (goTermNode == null) {
                goTermNode = createNode(graph, "GoTerm");
                goTermNode.setProperty("id", go.goId);
                goTermNode.setProperty("category", go.category);
                goTermNode.setProperty("term", go.goTerm);
                graph.update(goTermNode);
            }
            Edge edge = graph.addEdge(geneIdNodeIdMap.get(geneId), goTermNode, "HAS_GO_TERM");
            setPropertyIfNotDash(edge, "evidence", go.evidence);
            setPropertyIfNotDash(edge, "qualifier", go.qualifier);
            setArrayPropertyIfNotDash(edge, "pubmed_ids", go.pubMedIds);
            graph.update(edge);
        }
        logger.info("Exporting gene_group.gz...");
        MappingIterator<GeneRelationship> groups = FileUtils.openGzipTsv(workspace, dataSource, "gene_group.gz",
                                                                         GeneRelationship.class);
        while (groups.hasNext()) {
            GeneRelationship group = groups.next();
            if (!group.taxonomyId.equals("9606") || !group.otherTaxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(group.geneId);
            long otherGeneId = Long.parseLong(group.otherGeneId);
            Edge edge = graph.addEdge(geneId, otherGeneId, "RELATED_TO");
            edge.setProperty("type", group.relationship);
            graph.update(edge);
        }
        logger.info("Exporting gene_orthologs.gz...");
        MappingIterator<GeneRelationship> orthologs = FileUtils.openGzipTsv(workspace, dataSource, "gene_orthologs.gz",
                                                                            GeneRelationship.class);
        while (orthologs.hasNext()) {
            GeneRelationship ortholog = orthologs.next();
            if (!ortholog.taxonomyId.equals("9606") || !ortholog.otherTaxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(ortholog.geneId);
            long otherGeneId = Long.parseLong(ortholog.otherGeneId);
            Edge edge = graph.addEdge(geneId, otherGeneId, "RELATED_TO");
            edge.setProperty("type", ortholog.relationship);
            graph.update(edge);
        }
        logger.info("Exporting gene2pubmed.gz...");
        MappingIterator<String[]> genePubMed = FileUtils.openGzipTsv(workspace, dataSource, "gene2pubmed.gz",
                                                                     String[].class);
        long lastGeneId = -1;
        Set<Long> currentPubMedIds = new HashSet<>();
        while (genePubMed.hasNext()) {
            String[] pubMedAnnotation = genePubMed.next();
            if (!pubMedAnnotation[0].equals("9606"))
                continue;
            long geneId = Long.parseLong(pubMedAnnotation[1]);
            if (geneId != lastGeneId) {
                if (lastGeneId != -1) {
                    Node geneNode = graph.getNode(geneIdNodeIdMap.get(lastGeneId));
                    geneNode.setProperty("pubmed_ids", currentPubMedIds.toArray(new Long[0]));
                    graph.update(geneNode);
                    currentPubMedIds.clear();
                }
                lastGeneId = geneId;
            }
            currentPubMedIds.add(Long.parseLong(pubMedAnnotation[2]));
        }
        if (currentPubMedIds.size() > 0) {
            Node geneNode = graph.getNode(geneIdNodeIdMap.get(lastGeneId));
            geneNode.setProperty("pubmed_ids", currentPubMedIds.toArray(new Long[0]));
            graph.update(geneNode);
        }
    }

    private Node createAccessionNode(final Graph graph, final GeneAccession accession) {
        Node accessionNode = createNode(graph, "Accession");
        setPropertyIfNotDash(accessionNode, "status", accession.status);
        setLongPropertyIfNotDash(accessionNode, "rna_nucleotide_gi", accession.rnaNucleotideGi);
        setPropertyIfNotDash(accessionNode, "rna_nucleotide_accession.version",
                             accession.rnaNucleotideAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "protein_gi", accession.proteinGi);
        setPropertyIfNotDash(accessionNode, "protein_accession.version", accession.proteinAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "genomic_nucleotide_gi", accession.genomicNucleotideGi);
        setPropertyIfNotDash(accessionNode, "genomic_nucleotide_accession.version",
                             accession.genomicNucleotideAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "mature_peptide_gi", accession.maturePeptideGi);
        setPropertyIfNotDash(accessionNode, "mature_peptide_accession.version",
                             accession.maturePeptideAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "start_position_on_the_genomic_accession",
                                 accession.startPositionOnTheGenomicAccession);
        setLongPropertyIfNotDash(accessionNode, "end_position_on_the_genomic_accession",
                                 accession.endPositionOnTheGenomicAccession);
        setPropertyIfNotDash(accessionNode, "assembly", accession.assembly);
        setPropertyIfNotDash(accessionNode, "orientation", accession.orientation);
        graph.update(accessionNode);
        return accessionNode;
    }

    private void setPropertyIfNotDash(final PropertyContainer container, final String propertyKey, final String value) {
        if (value != null && !"-".equals(value))
            container.setProperty(propertyKey, value);
    }

    private void setLongPropertyIfNotDash(final PropertyContainer container, final String propertyKey,
                                          final String value) {
        if (value != null && !"-".equals(value))
            container.setProperty(propertyKey, Long.parseLong(value));
    }

    private void setArrayPropertyIfNotDash(final PropertyContainer container, final String propertyKey,
                                           final String value) {
        if (value != null && !"-".equals(value) && value.trim().length() > 0)
            container.setProperty(propertyKey, StringUtils.split(value, "|"));
    }

    private void exportPubChemDatabase(final Workspace workspace, final DataSource dataSource,
                                       final Graph graph) throws IOException {
        String[] fileNames = dataSource.listSourceFiles(workspace);
        for (String fileName : fileNames)
            if (fileName.startsWith("Compound_") && fileName.endsWith(".sdf.gz")) {
                SdfReader reader = new SdfReader(FileUtils.openGzip(workspace, dataSource, fileName), "UTF-8");
                for (SdfEntry entry : reader)
                    createPubChemCompoundNode(graph, entry);
            }
    }

    private void createPubChemCompoundNode(final Graph graph, final SdfEntry entry) {
        Node node = createNode(graph, "Compound");
        node.setProperty("id", Long.parseLong(entry.properties.get("PUBCHEM_COMPOUND_CID")));
        node.setProperty("IUPAC_openeye_name", entry.properties.get("PUBCHEM_IUPAC_OPENEYE_NAME"));
        node.setProperty("IUPAC_cas_name", entry.properties.get("PUBCHEM_IUPAC_CAS_NAME"));
        node.setProperty("IUPAC_name_markup", entry.properties.get("PUBCHEM_IUPAC_NAME_MARKUP"));
        node.setProperty("IUPAC_name", entry.properties.get("PUBCHEM_IUPAC_NAME"));
        node.setProperty("IUPAC_systematic_name", entry.properties.get("PUBCHEM_IUPAC_SYSTEMATIC_NAME"));
        node.setProperty("IUPAC_traditional_name", entry.properties.get("PUBCHEM_IUPAC_TRADITIONAL_NAME"));
        node.setProperty("IUPAC_inchi", entry.properties.get("PUBCHEM_IUPAC_INCHI"));
        node.setProperty("IUPAC_inchi_key", entry.properties.get("PUBCHEM_IUPAC_INCHIKEY"));
        node.setProperty("IUPAC_openeye_canonical_smiles", entry.properties.get("PUBCHEM_OPENEYE_CAN_SMILES"));
        node.setProperty("IUPAC_openeye_iso_smiles", entry.properties.get("PUBCHEM_OPENEYE_ISO_SMILES"));
        graph.update(node);
    }
}
