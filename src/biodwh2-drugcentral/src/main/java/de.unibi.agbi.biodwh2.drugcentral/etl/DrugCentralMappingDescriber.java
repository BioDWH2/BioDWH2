package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class DrugCentralMappingDescriber extends MappingDescriber {
    public DrugCentralMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Reference".equalsIgnoreCase(localMappingLabel))
            return describeReference(node);
        if ("ParentDrugMolecule".equalsIgnoreCase(localMappingLabel))
            return describeParentDrugMolecule(graph, node);
        if ("Structure".equalsIgnoreCase(localMappingLabel))
            return describeStructure(graph, node);
        if ("ActiveIngredient".equalsIgnoreCase(localMappingLabel))
            return describeActiveIngredient(graph, node);
        if ("OMOPConcept".equalsIgnoreCase(localMappingLabel))
            return describeOMOPConcept(node);
        if ("DrugLabel".equalsIgnoreCase(localMappingLabel))
            return describeDrugLabel(node);
        if ("PDB".equalsIgnoreCase(localMappingLabel))
            return describeProtein(node);
        return null;
    }

    private NodeMappingDescription[] describeReference(final Node node) {
        final String type = node.getProperty("type");
        if ("JOURNAL ARTICLE".equalsIgnoreCase(type)) {
            final NodeMappingDescription description = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.PUBLICATION);
            description.addName(node.getProperty("title"));
            description.addName(generateAMACitation(node));
            description.addIdentifier(IdentifierType.PUBMED_ID, node.<String>getProperty("pmid"));
            description.addIdentifier(IdentifierType.DOI, node.<String>getProperty("doi"));
            return new NodeMappingDescription[]{description};
        }
        return null;
    }

    private String generateAMACitation(final Node node) {
        final String authors = node.getProperty("authors");
        final String title = node.getProperty("title");
        final String volume = node.getProperty("volume");
        final String year = node.getProperty("dp_year");
        final String journal = node.getProperty("journal");
        final String pages = node.getProperty("pages");
        final String issue = node.getProperty("issue");
        final String doi = node.getProperty("doi");
        final StringBuilder builder = new StringBuilder();
        if (authors != null)
            builder.append(authors);
        builder.append(". ").append(title).append(" ").append(journal).append(". ").append(year);
        if (volume != null || issue != null || pages != null) {
            builder.append(';');
            if (volume != null)
                builder.append(volume);
            if (issue != null)
                builder.append('(').append(issue).append(')');
            if (pages != null)
                builder.append(':').append(pages);
            builder.append('.');
        }
        if (doi != null)
            builder.append(" doi:").append(doi).append('.');
        return builder.toString();
    }

    private NodeMappingDescription[] describeParentDrugMolecule(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_reg_no"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeStructure(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_reg_no"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeActiveIngredient(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("substance_name"));
        description.addIdentifier(IdentifierType.UNII, node.<String>getProperty("substance_unii"));
        final NodeMappingDescription activeMoietyDescription = new NodeMappingDescription(
                NodeMappingDescription.NodeType.COMPOUND);
        activeMoietyDescription.addName(node.getProperty("active_moiety_name"));
        activeMoietyDescription.addIdentifier(IdentifierType.UNII, node.<String>getProperty("active_moiety_unii"));
        return new NodeMappingDescription[]{description, activeMoietyDescription};
    }

    private NodeMappingDescription[] describeOMOPConcept(final Node node) {
        final String cuiSemanticType = node.getProperty("cui_semantic_type");
        if (cuiSemanticType != null) {
            final NodeMappingDescription description;
            switch (cuiSemanticType) {
                // T001 Organism
                case "T002": // Plant
                case "T204": // Eukaryote
                case "T007": // Bacterium
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
                    description.addNames(node.getProperty("concept_name"), node.getProperty("snomed_full_name"));
                    description.addIdentifier(IdentifierType.SNOMED_CT, node.<String>getProperty("snomed_concept_id"));
                    description.addIdentifier(IdentifierType.UMLS_CUI, node.<String>getProperty("umls_cui"));
                    return new NodeMappingDescription[]{description};
                case "T167": // Substance
                case "T109": // Organic Chemical
                case "T131": // Hazardous or Poisonous Substance
                case "T121": // Pharmacologic Substance
                case "T130": // Indicator, Reagent, or Diagnostic Aid
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
                    description.addNames(node.getProperty("concept_name"), node.getProperty("snomed_full_name"));
                    description.addIdentifier(IdentifierType.SNOMED_CT, node.<String>getProperty("snomed_concept_id"));
                    description.addIdentifier(IdentifierType.UMLS_CUI, node.<String>getProperty("umls_cui"));
                    return new NodeMappingDescription[]{description};
                case "T047": // Disease or Syndrome
                case "T191": // Neoplastic Process
                case "T048": // Mental or Behavioral Dysfunction
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
                    description.addNames(node.getProperty("concept_name"), node.getProperty("snomed_full_name"));
                    description.addIdentifier(IdentifierType.SNOMED_CT, node.<String>getProperty("snomed_concept_id"));
                    description.addIdentifier(IdentifierType.UMLS_CUI, node.<String>getProperty("umls_cui"));
                    return new NodeMappingDescription[]{description};
            }
        }
        return null;
    }

    private NodeMappingDescription[] describeDrugLabel(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.DRUG_LABEL);
        description.addName(node.getProperty("title") + " (" + node.getProperty("assigned_entity") + ")");
        description.addIdentifier(IdentifierType.FDA_SPL, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.PROTEIN_DATA_BANK, node.<String>getProperty("pdb"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("accession"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                "Reference", "ParentDrugMolecule", "Structure", "ActiveIngredient", "OMOPConcept", "DrugLabel", "PDB"
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[][]{
                {"Structure", "INDICATION", "OMOPConcept"}, {"Structure", "CONTRAINDICATION", "OMOPConcept"}
        };
    }
}
