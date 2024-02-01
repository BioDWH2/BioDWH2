package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class DGIdbMappingDescriber extends MappingDescriber {
    public DGIdbMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (DGIdbGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (DGIdbGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        final String id = node.getProperty(GraphExporter.ID_KEY);
        final String[] idParts = StringUtils.split(id, ":", 2);
        switch (idParts[0].toLowerCase()) {
            case "chembl":
                description.addIdentifier(IdentifierType.CHEMBL, idParts[1]);
                break;
            case "drugbank":
                description.addIdentifier(IdentifierType.DRUG_BANK, idParts[1]);
                break;
            case "rxcui":
                description.addIdentifier(IdentifierType.RX_NORM_CUI, Integer.parseInt(idParts[1]));
                break;
            default:
                // TODO: "chemidplus", "drugsatfda.anda", "drugsatfda.nda", "hemonc", "iuphar.ligand", "ncit", "wikidata"
                return null;
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        final String id = node.getProperty(GraphExporter.ID_KEY);
        final String[] idParts = StringUtils.split(id, ":", 2);
        switch (idParts[0].toLowerCase()) {
            case "ensembl":
                description.addIdentifier(IdentifierType.ENSEMBL, idParts[1]);
                break;
            case "hgnc":
                description.addIdentifier(IdentifierType.HGNC_ID, Integer.parseInt(idParts[1]));
                break;
            case "ncbigene":
                description.addIdentifier(IdentifierType.NCBI_GENE, Integer.parseInt(idParts[1]));
                break;
            default:
                return null;
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1 && edges[0].getLabel().endsWith(DGIdbGraphExporter.INTERACTS_WITH_LABEL))
            return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{DGIdbGraphExporter.DRUG_LABEL, DGIdbGraphExporter.GENE_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(DGIdbGraphExporter.DRUG_LABEL, DGIdbGraphExporter.INTERACTS_WITH_LABEL,
                                      DGIdbGraphExporter.GENE_LABEL)
        };
    }
}
