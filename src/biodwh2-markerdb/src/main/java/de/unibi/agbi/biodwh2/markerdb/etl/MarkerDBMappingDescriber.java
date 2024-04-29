package de.unibi.agbi.biodwh2.markerdb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MarkerDBMappingDescriber extends MappingDescriber {
    private static final Pattern RS_NUMBER_PATTERN = Pattern.compile("rs\\d+");

    public MarkerDBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (MarkerDBGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (MarkerDBGraphExporter.CHEMICAL_LABEL.equals(localMappingLabel))
            return describeChemical(node);
        if (MarkerDBGraphExporter.SEQUENCE_VARIANT_LABEL.equals(localMappingLabel))
            return describeSequenceVariant(node);
        if (MarkerDBGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final String uniProtId = node.getProperty("uniprot_id");
        if (uniProtId == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, uniProtId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeChemical(final Node node) {
        final String hmdbId = node.getProperty("hmdb_id");
        if (hmdbId == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addIdentifier(IdentifierType.HMDB, hmdbId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeSequenceVariant(final Node node) {
        final String variationId = node.getProperty("variation");
        if (variationId == null)
            return null;
        final var matcher = RS_NUMBER_PATTERN.matcher(variationId);
        if (!matcher.find())
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE_VARIANT);
        description.addIdentifier(IdentifierType.DB_SNP, matcher.group(0));
        final String name = StringUtils.strip(StringUtils.replace(variationId, matcher.group(0), ""), " \t()");
        if (!name.isEmpty())
            description.addName(name);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final List<NodeMappingDescription> descriptions = new ArrayList<>();
        final String entrezGeneId = node.getProperty("entrez_gene_id");
        final String geneSymbol = node.getProperty("gene_symbol");
        if (entrezGeneId != null || geneSymbol != null) {
            final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
            if (entrezGeneId != null)
                description.addIdentifier(IdentifierType.NCBI_GENE, Integer.parseInt(entrezGeneId));
            if (geneSymbol != null)
                description.addIdentifier(IdentifierType.HGNC_SYMBOL, geneSymbol);
            descriptions.add(description);
        }
        final String variationId = node.getProperty("variation");
        if (variationId != null) {
            final var matcher = RS_NUMBER_PATTERN.matcher(variationId);
            if (matcher.find()) {
                final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE_VARIANT);
                description.addIdentifier(IdentifierType.DB_SNP, matcher.group(0));
                final String name = StringUtils.strip(StringUtils.replace(variationId, matcher.group(0), ""), " \t()");
                if (!name.isEmpty())
                    description.addName(name);
                descriptions.add(description);
            }
        }
        return !descriptions.isEmpty() ? descriptions.toArray(new NodeMappingDescription[0]) : null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                MarkerDBGraphExporter.GENE_LABEL, MarkerDBGraphExporter.SEQUENCE_VARIANT_LABEL,
                MarkerDBGraphExporter.CHEMICAL_LABEL, MarkerDBGraphExporter.PROTEIN_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
