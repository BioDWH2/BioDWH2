package de.unibi.agbi.biodwh2.uniprot.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class UniProtMappingDescriber extends MappingDescriber {
    public UniProtMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (UniProtGraphExporter.ORGANISM_LABEL.equals(localMappingLabel))
            return describeOrganism(node);
        if (UniProtGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (UniProtGraphExporter.CITATION_LABEL.equals(localMappingLabel))
            return describeCitation(node);
        return null;
    }

    private NodeMappingDescription[] describeOrganism(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addNames(node.<String[]>getProperty("common_names"));
        description.addNames(node.<String[]>getProperty("scientific_names"));
        final var xrefs = node.<String[]>getProperty("db_references");
        if (xrefs != null) {
            for (final String id : xrefs) {
                final String[] parts = StringUtils.split(id, ":", 2);
                switch (parts[0].toLowerCase(Locale.ROOT)) {
                    case "ncbi taxonomy":
                        description.addIdentifier(IdentifierType.NCBI_TAXON, Integer.parseInt(parts[1]));
                        break;
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addNames(node.<String[]>getProperty("names"));
        final var accessions = node.<String[]>getProperty("accessions");
        if (accessions != null)
            for (final String accession : accessions)
                description.addIdentifier(IdentifierType.UNIPROT_KB, accession);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeCitation(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
        // TODO: description.addName(CitationUtils.getAMACitation());
        final var xrefs = node.<String[]>getProperty("db_references");
        if (xrefs != null) {
            for (final String id : xrefs) {
                final String[] parts = StringUtils.split(id, ":", 2);
                switch (parts[0].toLowerCase(Locale.ROOT)) {
                    case "doi":
                        description.addIdentifier(IdentifierType.DOI, parts[1]);
                        break;
                    case "pubmed":
                        description.addIdentifier(IdentifierType.PUBMED_ID, Integer.parseInt(parts[1]));
                        break;
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                UniProtGraphExporter.ORGANISM_LABEL, UniProtGraphExporter.PROTEIN_LABEL,
                UniProtGraphExporter.CITATION_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
