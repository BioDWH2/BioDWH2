package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class GeneOntologyMappingDescriber extends MappingDescriber {
    private final Map<String, NodeMappingDescription.NodeType> typeNodeTypeMap;

    public GeneOntologyMappingDescriber(final DataSource dataSource) {
        super(dataSource);
        typeNodeTypeMap = new HashMap<>();
        typeNodeTypeMap.put("protein", NodeMappingDescription.NodeType.PROTEIN);
        typeNodeTypeMap.put("protein_complex", NodeMappingDescription.NodeType.PROTEIN);
        typeNodeTypeMap.put("hammerhead_ribozyme", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("ribozyme", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("antisense_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("guide_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("lnc_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("miRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("ncRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("piRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("RNase_MRP_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("RNase_P_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("rRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("scRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("snoRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("snRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("SRP_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("telomerase_RNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("tRNA", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("primary_transcript", NodeMappingDescription.NodeType.RNA);
        typeNodeTypeMap.put("transcript", NodeMappingDescription.NodeType.RNA);
        // gene_product is not further specified and a fallback, therefore a precise mapping is difficult
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (GeneOntologyGraphExporter.DB_OBJECT_LABEL.equals(localMappingLabel))
            return describeDBObject(node);
        return null;
    }

    private NodeMappingDescription[] describeDBObject(final Node node) {
        final String type = node.getProperty("type");
        if (typeNodeTypeMap.containsKey(type))
            return new NodeMappingDescription[]{createDescription(node, typeNodeTypeMap.get(type))};
        return null;
    }

    private NodeMappingDescription createDescription(final Node node, final NodeMappingDescription.NodeType type) {
        final NodeMappingDescription description = new NodeMappingDescription(type);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        final String id = node.getProperty("id");
        final String[] idParts = StringUtils.split(id, ":", 2);
        if (idParts != null) {
            switch (idParts[0]) {
                case "UniProtKB":
                    description.addIdentifier(IdentifierType.UNIPROT_KB, idParts[1]);
                    break;
                case "ComplexPortal":
                    description.addIdentifier("ComplexPortal", idParts[1]);
                    break;
                case "RNAcentral":
                    description.addIdentifier("RNAcentral", idParts[1]);
                    break;
            }
        }
        return description;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{GeneOntologyGraphExporter.DB_OBJECT_LABEL};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
