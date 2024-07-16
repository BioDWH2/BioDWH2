package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.CitationUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;

public class DrugCentralMappingDescriber extends MappingDescriber {
    public DrugCentralMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (DrugCentralGraphExporter.REFERENCE_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeReference(node);
        if (DrugCentralGraphExporter.PARENT_DRUG_MOLECULE_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeParentDrugMolecule(node);
        if (DrugCentralGraphExporter.STRUCTURE_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeStructure(graph, node);
        if (DrugCentralGraphExporter.ACTIVE_INGREDIENT_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeActiveIngredient(node);
        if (DrugCentralGraphExporter.OMOP_CONCEPT_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeOMOPConcept(node);
        if (DrugCentralGraphExporter.DRUG_LABEL_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeDrugLabel(node);
        if (DrugCentralGraphExporter.PDB_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeProtein(node);
        if (DrugCentralGraphExporter.TARGET_COMPONENT_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeTargetComponent(node);
        return null;
    }

    private NodeMappingDescription[] describeReference(final Node node) {
        final String type = node.getProperty("type");
        if ("JOURNAL ARTICLE".equalsIgnoreCase(type)) {
            final PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
            description.pubmedId = node.getProperty("pmid");
            description.setDOI(node.getProperty("doi"));
            description.addName(node.getProperty("title"));
            description.addName(generateAMACitation(node));
            description.addIdentifier(IdentifierType.PUBMED_ID, description.pubmedId);
            description.addIdentifier(IdentifierType.DOI, description.doi);
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
        return CitationUtils.getAMACitation(authors, title, volume, year, journal, pages, issue, doi);
    }

    private NodeMappingDescription[] describeParentDrugMolecule(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_reg_no"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeStructure(final Graph graph, final Node node) {
        final NodeMappingDescription compoundDescription = new NodeMappingDescription(
                NodeMappingDescription.NodeType.COMPOUND);
        compoundDescription.addName(node.getProperty("name"));
        compoundDescription.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_reg_no"));
        final NodeMappingDescription drugDescription = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        drugDescription.addName(node.getProperty("name"));
        drugDescription.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_reg_no"));
        final Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getId(), "DrugCentral_HAS_IDENTIFIER",
                                                                    EdgeDirection.FORWARD);
        for (final Long nodeId : nodeIds) {
            final Node adjacentNode = graph.getNode(nodeId);
            final String type = adjacentNode.getProperty("type");
            final String identifier = adjacentNode.getProperty("identifier");
            if ("DRUGBANK_ID".equals(type)) {
                compoundDescription.addIdentifier(IdentifierType.DRUG_BANK, identifier);
                drugDescription.addIdentifier(IdentifierType.DRUG_BANK, identifier);
            } else if ("UNII".equals(type)) {
                compoundDescription.addIdentifier(IdentifierType.UNII, identifier);
                drugDescription.addIdentifier(IdentifierType.UNII, identifier);
            }
        }
        return new NodeMappingDescription[]{compoundDescription, drugDescription};
    }

    private NodeMappingDescription[] describeActiveIngredient(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("substance_name"));
        description.addIdentifier(IdentifierType.UNII, node.<String>getProperty("substance_unii"));
        final NodeMappingDescription activeMoietyDescription = new NodeMappingDescription(
                NodeMappingDescription.NodeType.DRUG);
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
                    description.addIdentifier(IdentifierType.SNOMED_CT, node.<Long>getProperty("snomed_concept_id"));
                    description.addIdentifier(IdentifierType.UMLS_CUI, node.<String>getProperty("umls_cui"));
                    return new NodeMappingDescription[]{description};
                case "T167": // Substance
                case "T109": // Organic Chemical
                case "T131": // Hazardous or Poisonous Substance
                case "T121": // Pharmacologic Substance
                case "T130": // Indicator, Reagent, or Diagnostic Aid
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
                    description.addNames(node.getProperty("concept_name"), node.getProperty("snomed_full_name"));
                    description.addIdentifier(IdentifierType.SNOMED_CT, node.<Long>getProperty("snomed_concept_id"));
                    description.addIdentifier(IdentifierType.UMLS_CUI, node.<String>getProperty("umls_cui"));
                    return new NodeMappingDescription[]{description};
                case "T047": // Disease or Syndrome
                case "T191": // Neoplastic Process
                case "T048": // Mental or Behavioral Dysfunction
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
                    description.addNames(node.getProperty("concept_name"), node.getProperty("snomed_full_name"));
                    description.addIdentifier(IdentifierType.SNOMED_CT, node.<Long>getProperty("snomed_concept_id"));
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

    private NodeMappingDescription[] describeTargetComponent(final Node node) {
        final String organism = node.getProperty("organism");
        if (!"homo sapiens".equalsIgnoreCase(organism))
            return null;
        final NodeMappingDescription geneDescription = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        geneDescription.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("gene"));
        geneDescription.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty("geneid"));
        final NodeMappingDescription proteinDescription = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PROTEIN);
        proteinDescription.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("accession"));
        return new NodeMappingDescription[]{geneDescription, proteinDescription};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                DrugCentralGraphExporter.REFERENCE_LABEL, DrugCentralGraphExporter.PARENT_DRUG_MOLECULE_LABEL,
                DrugCentralGraphExporter.STRUCTURE_LABEL, DrugCentralGraphExporter.ACTIVE_INGREDIENT_LABEL,
                DrugCentralGraphExporter.OMOP_CONCEPT_LABEL, DrugCentralGraphExporter.DRUG_LABEL_LABEL,
                DrugCentralGraphExporter.PDB_LABEL, DrugCentralGraphExporter.TARGET_COMPONENT_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1) {
            if (edges[0].getLabel().endsWith("INDICATION"))
                return new PathMappingDescription(PathMappingDescription.EdgeType.INDICATES);
            if (edges[0].getLabel().endsWith("CONTRAINDICATION"))
                return new PathMappingDescription(PathMappingDescription.EdgeType.CONTRAINDICATES);
        }
        if (edges.length == 3) {
            if (edges[1].getLabel().endsWith("INTERACTS"))
                return new PathMappingDescription(PathMappingDescription.EdgeType.INTERACTS);
            if (edges[1].getLabel().endsWith("HAS_TARGET"))
                return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        }
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        final PathMapping interactionPathMapping = new PathMapping();
        interactionPathMapping.add("Structure", "BELONGS_TO", "DrugClass", EdgeDirection.FORWARD);
        interactionPathMapping.add("DrugClass", "INTERACTS", "DrugClass", EdgeDirection.FORWARD);
        interactionPathMapping.add("DrugClass", "BELONGS_TO", "Structure", EdgeDirection.BACKWARD);
        final PathMapping targetsPathMapping = new PathMapping();
        targetsPathMapping.add("Structure", "TARGETS", "Bioactivity", EdgeDirection.FORWARD);
        targetsPathMapping.add("Bioactivity", "HAS_TARGET", "Target", EdgeDirection.FORWARD);
        targetsPathMapping.add("Target", "COMPONENT_OF", "TargetComponent", EdgeDirection.BACKWARD);
        return new PathMapping[]{
                new PathMapping().add("Structure", "INDICATION", "OMOPConcept", EdgeDirection.FORWARD),
                new PathMapping().add("Structure", "CONTRAINDICATION", "OMOPConcept", EdgeDirection.FORWARD),
                interactionPathMapping, targetsPathMapping
        };
    }
}
