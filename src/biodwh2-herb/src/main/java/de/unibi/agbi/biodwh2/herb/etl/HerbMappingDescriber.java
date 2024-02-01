package de.unibi.agbi.biodwh2.herb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class HerbMappingDescriber extends MappingDescriber {
    public HerbMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HerbGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        if (HerbGraphExporter.TARGET_LABEL.equals(localMappingLabel))
            return describeTarget(node);
        if (HerbGraphExporter.INGREDIENT_LABEL.equals(localMappingLabel))
            return describeIngredient(node);
        if (HerbGraphExporter.REFERENCE_LABEL.equals(localMappingLabel))
            return describeReference(node);
        return null;
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.UMLS_CUI, node.<String>getProperty("disgenet_id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTarget(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("gene_name"));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] idParts = StringUtils.split(xref.trim(), ":", 2);
                switch (idParts[0].toLowerCase()) {
                    case "omim":
                        description.addIdentifier(IdentifierType.OMIM, Integer.parseInt(idParts[1]));
                        break;
                    case "hgnc":
                        description.addIdentifier(IdentifierType.HGNC_ID, Integer.parseInt(idParts[1].split(":")[1]));
                        break;
                    case "ensembl":
                        description.addIdentifier(IdentifierType.ENSEMBL, idParts[1]);
                        break;
                    // MGI:MGI:99827, miRBase:MI00000, IMGT/GENE-DB:VPREB3
                }
            }
        }
        return description.hasIdentifiers() ? new NodeMappingDescription[]{description} : null;
    }

    private NodeMappingDescription[] describeIngredient(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        String casNumber = node.getProperty("cas_id");
        if (casNumber != null) {
            if (casNumber.contains(";"))
                casNumber = casNumber.split(";")[0];
            if (IdentifierUtils.isCasNumber(casNumber))
                description.addIdentifier(IdentifierType.CAS, casNumber);
        }
        description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.<Integer>getProperty("pubchem_id"));
        description.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        return description.hasIdentifiers() ? new NodeMappingDescription[]{description} : null;
    }

    private NodeMappingDescription[] describeReference(final Node node) {
        final var description = new PublicationNodeMappingDescription();
        description.doi = node.getProperty("doi");
        description.pubmedId = node.getProperty("pubmed_id");
        description.addIdentifier(IdentifierType.DOI, description.doi);
        description.addIdentifier(IdentifierType.PUBMED_ID, description.pubmedId);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                HerbGraphExporter.DISEASE_LABEL, HerbGraphExporter.TARGET_LABEL, HerbGraphExporter.INGREDIENT_LABEL,
                HerbGraphExporter.REFERENCE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
