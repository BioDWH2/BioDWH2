package de.unibi.agbi.biodwh2.ttd.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class TTDMappingDescriber extends MappingDescriber {
    public TTDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (TTDGraphExporter.PATHWAY_LABEL.equals(localMappingLabel))
            return describePathway(node);
        if (TTDGraphExporter.TARGET_LABEL.equals(localMappingLabel))
            return describeTarget(node);
        if (TTDGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (TTDGraphExporter.COMPOUND_LABEL.equals(localMappingLabel))
            return describeCompound(node);
        if (TTDGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describePathway(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        description.addName(node.getProperty("name"));
        final String source = node.getProperty("source");
        final String id = node.getProperty("id");
        if (source != null && id != null) {
            switch (source) {
                case "Reactome":
                    description.addIdentifier(IdentifierType.REACTOME, id);
                    break;
                case "KEGG":
                    description.addIdentifier(IdentifierType.KEGG, id);
                    break;
                case "WikiPathway":
                    // "WP2446"
                    break;
                case "PathWhiz Pathway":
                    // "PW000565"
                    break;
                case "Pathway Interact":
                    // "trkrpathway", "rb_1pathway"
                    break;
                case "PANTHER Pathway":
                    // "P00008"
                    break;
                case "NetPathway":
                    // "NetPath_14"
                    break;
                case "BioCyc":
                    // "PWY66-395"
                    break;
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTarget(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        final String[] uniprotIds = node.getProperty("uniprot_ids");
        if (uniprotIds != null)
            for (final String uniprotId : uniprotIds)
                description.addIdentifier(IdentifierType.UNIPROT_KB, uniprotId);
        // gene_names, pdb_structures, ec_number
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        final String casNumber = node.getProperty("cas_number");
        if (casNumber != null)
            description.addIdentifier(IdentifierType.CAS, StringUtils.replace(casNumber, "CAS", "").trim());
        final String[] pubchemCompoundIds = node.getProperty("pubchem_cids");
        if (pubchemCompoundIds != null)
            for (final String pubchemCompoundId : pubchemCompoundIds)
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(pubchemCompoundId));
        // chebi_id, inchi_key
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        final String pubchemCompoundId = node.getProperty("pubchem_cid");
        if (pubchemCompoundId != null)
            description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(pubchemCompoundId));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        final String[] icd10Ids = node.getProperty("ICD10");
        if (icd10Ids != null)
            for (final String icd10 : icd10Ids)
                description.addIdentifier(IdentifierType.ICD10, icd10);
        final String icd11Id = node.getProperty("ICD11");
        if (icd11Id != null && !icd11Id.contains("-"))
            description.addIdentifier(IdentifierType.ICD11, icd11Id);
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                TTDGraphExporter.PATHWAY_LABEL, TTDGraphExporter.TARGET_LABEL, TTDGraphExporter.DRUG_LABEL,
                TTDGraphExporter.COMPOUND_LABEL, TTDGraphExporter.DISEASE_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1) {
            if (edges[0].getLabel().endsWith(TTDGraphExporter.TARGETS_LABEL))
                return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
            if (edges[0].getLabel().endsWith(TTDGraphExporter.INDICATES_LABEL))
                return new PathMappingDescription(PathMappingDescription.EdgeType.INDICATES);
        }
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(TTDGraphExporter.DRUG_LABEL, TTDGraphExporter.TARGETS_LABEL,
                                      TTDGraphExporter.TARGET_LABEL, EdgeDirection.FORWARD), new PathMapping().add(
                TTDGraphExporter.DRUG_LABEL, TTDGraphExporter.INDICATES_LABEL, TTDGraphExporter.DISEASE_LABEL,
                EdgeDirection.FORWARD)
        };
    }
}
