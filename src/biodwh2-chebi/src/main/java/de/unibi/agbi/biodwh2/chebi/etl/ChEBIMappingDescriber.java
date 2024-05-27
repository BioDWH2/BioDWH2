package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class ChEBIMappingDescriber extends MappingDescriber {
    public ChEBIMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (ChEBIGraphExporter.COMPOUND_LABEL.equals(localMappingLabel))
            return describeCompound(node);
        return null;
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addIdentifier(IdentifierType.CHEBI, node.<Integer>getProperty(GraphExporter.ID_KEY));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] parts = StringUtils.split(xref, "|", 3);
                switch (parts[1].toLowerCase()) {
                    case "cas registry number":
                        description.addIdentifier(IdentifierType.CAS, parts[2]);
                        break;
                    case "drugbank accession":
                        description.addIdentifier(IdentifierType.DRUG_BANK, parts[2]);
                        break;
                    case "kegg drug accession":
                    case "kegg compound accession":
                        description.addIdentifier(IdentifierType.KEGG, parts[2]);
                        break;
                    case "drug central accession":
                        description.addIdentifier(IdentifierType.DRUG_CENTRAL, Integer.parseInt(parts[2]));
                        break;
                }
                // "Beilstein Registry Number", "BPDB accession", "ChemIDplus accession", "Chemspider accession",
                // "COMe accession", "ECMDB accession", "FAO/WHO standards accession", "FooDB accession",
                // "GlyGen accession", "GlyTouCan accession", "Gmelin Registry Number", "HMDB accession",
                // "KEGG GLYCAN accession", "KNApSAcK accession", "LINCS accession", "LIPID MAPS class accession",
                // "LIPID MAPS instance accession", "MetaCyc accession", "MolBase accession", "Patent accession",
                // "PDB accession", "PDBeChem accession", "Pesticides accession", "PPDB accession", "PPR",
                // "Pubchem accession", "Reaxys Registry Number", "RESID accession", "SMID accession", "UM-BBD compID",
                // "VSDB accession", "WebElements accession", "Wikipedia accession", "YMDB accession"
            }
        }
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                ChEBIGraphExporter.COMPOUND_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
