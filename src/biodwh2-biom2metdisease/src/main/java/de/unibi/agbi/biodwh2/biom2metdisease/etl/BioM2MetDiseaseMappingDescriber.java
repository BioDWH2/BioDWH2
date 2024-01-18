package de.unibi.agbi.biodwh2.biom2metdisease.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class BioM2MetDiseaseMappingDescriber extends MappingDescriber {
    public BioM2MetDiseaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (BioM2MetDiseaseGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (BioM2MetDiseaseGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        if (BioM2MetDiseaseGraphExporter.SPECIES_LABEL.equals(localMappingLabel))
            return describeSpecies(node);
        if (BioM2MetDiseaseGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel))
            return describePublication(node);
        if (BioM2MetDiseaseGraphExporter.BIOMOLECULE_LABEL.equals(localMappingLabel))
            return describeBiomolecule(node);
        return null;
    }

    private NodeMappingDescription[] describeBiomolecule(final Node node) {
        final String category = node.getProperty("category");
        if ("microRNA".equals(category)) {
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
            description.addIdentifier(IdentifierType.MIRBASE, node.<String>getProperty("id"));
            description.addName(node.getProperty("name"));
            return new NodeMappingDescription[]{description};
        }
        if ("metabolite".equals(category)) {
            String[] ids = node.<String>getProperty("id").split(";");
            NodeMappingDescription[] descriptions = new NodeMappingDescription[ids.length];
            for (int i = 0; i < ids.length; i++) {
                descriptions[i] = new NodeMappingDescription(NodeMappingDescription.NodeType.METABOLITE);
                descriptions[i].addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(ids[i]));
            }
            return descriptions;
        }
        if ("small molecule".equals(category)) {
            String[] ids = node.<String>getProperty("id").split(";");
            NodeMappingDescription[] descriptions = new NodeMappingDescription[ids.length];
            for (int i = 0; i < ids.length; i++) {
                //TODO NodeType?
                descriptions[i] = new NodeMappingDescription(NodeMappingDescription.NodeType.METABOLITE);
                descriptions[i].addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(ids[i]));
            }
            return descriptions;
        }
        if ("drug".equals(category)) {
            String[] ids = node.<String>getProperty("id").split(";");
            NodeMappingDescription[] descriptions = new NodeMappingDescription[ids.length];
            for (int i = 0; i < ids.length; i++) {
                descriptions[i] = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
                descriptions[i].addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(ids[i]));
            }
            return descriptions;
        }
        return null;
    }

    private NodeMappingDescription[] describePublication(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, Integer.parseInt(node.getProperty("pubmed_id")));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeSpecies(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        SpeciesLookup.Entry species = SpeciesLookup.getByScientificName(node.getProperty("species"));
        if (species != null) {
            description.addIdentifier(IdentifierType.NCBI_TAXON, species.ncbiTaxId);
            description.addName(species.scientificName);
            return new NodeMappingDescription[]{description};
        } else {
            return new NodeMappingDescription[]{};
        }
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addIdentifier(IdentifierType.ICD10, node.<String>getProperty("icd10_classification"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                BioM2MetDiseaseGraphExporter.GENE_LABEL, BioM2MetDiseaseGraphExporter.BIOMOLECULE_LABEL,
                BioM2MetDiseaseGraphExporter.DISEASE_LABEL, BioM2MetDiseaseGraphExporter.PUBLICATION_LABEL,
                BioM2MetDiseaseGraphExporter.SPECIES_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
