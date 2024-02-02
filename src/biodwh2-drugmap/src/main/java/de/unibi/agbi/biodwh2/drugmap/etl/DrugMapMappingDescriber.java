package de.unibi.agbi.biodwh2.drugmap.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class DrugMapMappingDescriber extends MappingDescriber {
    public DrugMapMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (DrugMapGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (DrugMapGraphExporter.THERAPEUTIC_TARGET_LABEL.equals(localMappingLabel))
            return describeTherapeuticTarget(node);
        if (DrugMapGraphExporter.TRANSPORTER_LABEL.equals(localMappingLabel))
            return describeTransporter(node);
        if (DrugMapGraphExporter.METABOLIZING_ENZYME_LABEL.equals(localMappingLabel))
            return describeEnzyme(node);
        if (DrugMapGraphExporter.PATHWAY_LABEL.equals(localMappingLabel))
            return describePathway(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("iupac_name"));
        final String chebiId = node.getProperty("chebi_id");
        if (chebiId != null)
            description.addIdentifier(IdentifierType.CHEBI, Integer.parseInt(chebiId.split(":")[1]));
        // pubchem_cid, pubchem_cids, cas_number
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTherapeuticTarget(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_id"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_accession"));
        description.addIdentifier(IdentifierType.EC_NUMBER, node.<String>getProperty("ec_number"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTransporter(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_id"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_accession"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeEnzyme(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_accession"));
        description.addIdentifier(IdentifierType.EC_NUMBER, node.<String>getProperty("ec_number"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePathway(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        description.addName(node.getProperty("name"));
        final String source = node.getProperty("source");
        if (source != null) {
            switch (source) {
                case "KEGG":
                    description.addIdentifier(IdentifierType.KEGG, node.<String>getProperty(GraphExporter.ID_KEY));
                    break;
                case "Reactome":
                    description.addIdentifier(IdentifierType.REACTOME, node.<String>getProperty(GraphExporter.ID_KEY));
                    break;
            }
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1)
            return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                DrugMapGraphExporter.DRUG_LABEL, DrugMapGraphExporter.THERAPEUTIC_TARGET_LABEL,
                DrugMapGraphExporter.TRANSPORTER_LABEL, DrugMapGraphExporter.METABOLIZING_ENZYME_LABEL,
                DrugMapGraphExporter.PATHWAY_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        final var drugTargetMapping = new PathMapping().add(DrugMapGraphExporter.DRUG_LABEL,
                                                            DrugMapGraphExporter.TARGETS_LABEL,
                                                            DrugMapGraphExporter.THERAPEUTIC_TARGET_LABEL);
        final var drugTransporterMapping = new PathMapping().add(DrugMapGraphExporter.DRUG_LABEL,
                                                                 DrugMapGraphExporter.TARGETS_LABEL,
                                                                 DrugMapGraphExporter.TRANSPORTER_LABEL);
        final var drugEnzymeMapping = new PathMapping().add(DrugMapGraphExporter.DRUG_LABEL,
                                                            DrugMapGraphExporter.TARGETS_LABEL,
                                                            DrugMapGraphExporter.METABOLIZING_ENZYME_LABEL);
        return new PathMapping[]{drugTargetMapping, drugTransporterMapping, drugEnzymeMapping};
    }
}
