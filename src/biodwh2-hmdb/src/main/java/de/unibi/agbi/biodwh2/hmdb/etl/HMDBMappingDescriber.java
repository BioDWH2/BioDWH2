package de.unibi.agbi.biodwh2.hmdb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class HMDBMappingDescriber extends MappingDescriber {
    public HMDBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HMDBGraphExporter.REFERENCE_LABEL.equals(localMappingLabel))
            return describeReferenceNode(node);
        if (HMDBGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDiseaseNode(node);
        if (HMDBGraphExporter.PATHWAY_LABEL.equals(localMappingLabel))
            return describePathwayNode(node);
        if (HMDBGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProteinNode(node);
        if (HMDBGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGeneNode(node);
        if (HMDBGraphExporter.METABOLITE_LABEL.equals(localMappingLabel))
            return describeMetaboliteNode(node);
        return null;
    }

    private NodeMappingDescription[] describeReferenceNode(final Node node) {
        final Integer pubmedId = node.getProperty("pubmed_id");
        if (pubmedId == null)
            return null;
        final PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
        description.pubmedId = pubmedId;
        description.addIdentifier(IdentifierType.PUBMED_ID, pubmedId);
        final String citation = node.getProperty("text");
        if (citation != null) {
            final String[] dois = IdentifierUtils.extractDOIs(citation);
            if (dois != null) {
                if (dois.length == 1) {
                    description.doi = dois[0];
                }
                for (final String doi : dois)
                    description.addIdentifier(IdentifierType.DOI, doi);
            }
            description.addName(citation);
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDiseaseNode(final Node node) {
        final Integer omimId = node.getProperty("omim_id");
        if (omimId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addIdentifier(IdentifierType.OMIM, omimId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePathwayNode(final Node node) {
        final String keggMapId = node.getProperty("kegg_map_id");
        final String smpdbId = node.getProperty("smpdb_id");
        if (keggMapId == null && smpdbId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        description.addIdentifier(IdentifierType.KEGG, keggMapId);
        description.addIdentifier(IdentifierType.SMPDB, smpdbId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProteinNode(final Node node) {
        final String accession = node.getProperty("accession");
        final String uniprotId = node.getProperty("uniprot_id");
        final String genbankProteinId = node.getProperty("genbank_protein_id");
        if (accession == null && uniprotId == null && genbankProteinId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        // description.addIdentifier("HMDB", accession);
        description.addIdentifier(IdentifierType.UNIPROT_KB, uniprotId);
        // description.addIdentifier(IdentifierType.GENBANK, genbankProteinId);
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("uniprot_name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMetaboliteNode(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.METABOLITE);
        // description.addIdentifier("HMDB", accession);
        description.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        description.addIdentifier(IdentifierType.KEGG, node.<String>getProperty("kegg_id"));
        description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.<Integer>getProperty("pubchem_compound_id"));
        description.addIdentifier(IdentifierType.CHEMSPIDER, node.<Integer>getProperty("chemspider_id"));
        description.addIdentifier(IdentifierType.CHEBI, node.<Integer>getProperty("chebi_id"));
        // foodb_id, pdb_id, phenol_explorer_compound_id, knapsack_id, biocyc_id, bigg_id, wikipedia_id, vmh_id,
        // metlin_id, fbonto_id, cas_registry_number
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("iupac_name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGeneNode(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        final String hgncId = node.getProperty("hgnc_id");
        if (StringUtils.isNotEmpty(hgncId)) {
            description.addIdentifier(IdentifierType.HGNC_ID,
                                      Integer.parseInt(StringUtils.strip(hgncId.trim(), "HGNC:")));
        }
        description.addIdentifier(IdentifierType.GENBANK, node.<String>getProperty("genbank_gene_id"));
        description.addIdentifier(IdentifierType.GENE_CARD, node.<String>getProperty("genecard_id"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1) {
            if (edges[0].getLabel().endsWith(HMDBGraphExporter.TRANSLATES_TO_LABEL))
                return new PathMappingDescription(PathMappingDescription.EdgeType.TRANSLATES_TO);
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                HMDBGraphExporter.REFERENCE_LABEL, HMDBGraphExporter.DISEASE_LABEL, HMDBGraphExporter.PATHWAY_LABEL,
                HMDBGraphExporter.PROTEIN_LABEL, HMDBGraphExporter.GENE_LABEL, HMDBGraphExporter.METABOLITE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(HMDBGraphExporter.GENE_LABEL, HMDBGraphExporter.TRANSLATES_TO_LABEL,
                                      HMDBGraphExporter.PROTEIN_LABEL, EdgeDirection.FORWARD)
        };
    }
}
