package de.unibi.agbi.biodwh2.biom2metdisease.etl;

import de.unibi.agbi.biodwh2.biom2metdisease.BioM2MetDiseaseDataSource;
import de.unibi.agbi.biodwh2.biom2metdisease.model.Associations;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public class BioM2MetDiseaseGraphExporter extends GraphExporter<BioM2MetDiseaseDataSource> {
    static final String BIOMOLECULE_LABEL = "Biomolecule";
    static final String GENE_LABEL = "Gene";
    static final String SPECIES_LABEL = "Species";
    static final String DISEASE_LABEL = "Disease";
    static final String PUBLICATION_LABEL = "Publication";
    private static final String ASSOCIATION_NODE_LABEL = "Association";
    private static final String TISSUE_LABEL = "Tissue";
    static final String ASSOCIATED_WITH = "ASSOCIATED_WITH";

    public BioM2MetDiseaseGraphExporter(final BioM2MetDiseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(BIOMOLECULE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TISSUE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ASSOCIATION_NODE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportAssociations(graph);
        return true;
    }

    private void exportAssociations(final Graph graph) {
        for (final Associations association : dataSource.associations)
            exportAssociation(graph, association);
    }

    private void exportAssociation(final Graph graph, final Associations association) {
        final Node publication = findOrCreateNodePublication(graph, association);
        final Node species = findOrCreateNodeSpecies(graph, association);
        final Node gene = findOrCreateNodeGene(graph, association);
        final Node biomolecule = findOrCreateNodeBiomolecule(graph, association);
        final Node disease = findOrCreateNodeDisease(graph, association);
        final Node tissue = findOrCreateNodeTissue(graph, association);
        final Node associationNode = graph.addNode(ASSOCIATION_NODE_LABEL, "description", association.description);
        graph.addEdge(associationNode, publication, "PUBLISHED_IN");
        graph.addEdge(associationNode, species, ASSOCIATED_WITH);
        graph.addEdge(associationNode, gene, ASSOCIATED_WITH, "expression_direction", association.expressionDirection);
        graph.addEdge(associationNode, biomolecule, ASSOCIATED_WITH, "experimental_method",
                      association.experimentalMethod, "experimental_method_classification",
                      association.experimentalMethodClassification, "throughput", association.throughput);
        graph.addEdge(associationNode, disease, ASSOCIATED_WITH);
        graph.addEdge(associationNode, tissue, ASSOCIATED_WITH);
    }

    private Node findOrCreateNodeTissue(final Graph graph, final Associations a) {
        Node node = graph.findNode(TISSUE_LABEL, "tissue", a.tissue);
        if (node == null) {
            node = graph.addNode(TISSUE_LABEL, "tissue", a.tissue);
        }
        return node;
    }

    private Node findOrCreateNodePublication(final Graph graph, final Associations a) {
        Node node = graph.findNode(PUBLICATION_LABEL, "pubmed_id", a.pubmedID, "title", a.referenceTitle, "year",
                                   a.year);
        if (node == null) {
            node = graph.addNode(PUBLICATION_LABEL, "pubmed_id", a.pubmedID, "title", a.referenceTitle, "year", a.year);
        }
        return node;
    }

    private Node findOrCreateNodeDisease(final Graph graph, final Associations a) {
        Node node = graph.findNode(DISEASE_LABEL, "icd10_classification", a.icd10Classification, "name", a.diseaseName,
                                   "disease_ontology", a.diseaseOntology);
        if (node == null) {
            node = graph.addNode(DISEASE_LABEL, "icd10_classification", a.icd10Classification, "name", a.diseaseName,
                                 "disease_ontology", a.diseaseOntology, "tissue", a.tissue);
        }
        return node;
    }

    private Node findOrCreateNodeSpecies(final Graph graph, final Associations a) {
        Node node = graph.findNode(SPECIES_LABEL, "species", a.species);
        if (node == null) {
            node = graph.addNode(SPECIES_LABEL, "species", a.species);
        }
        return node;
    }

    private Node findOrCreateNodeGene(final Graph graph, final Associations a) {
        Node node = graph.findNode(GENE_LABEL, ID_KEY, a.interactionGeneSymbol);
        if (node == null) {
            node = graph.addNode(GENE_LABEL, ID_KEY, a.interactionGeneSymbol);
        }
        return node;
    }

    private Node findOrCreateNodeBiomolecule(final Graph graph, final Associations a) {
        Node node = graph.findNode(BIOMOLECULE_LABEL, ID_KEY, a.biomoleculeID);
        if (node == null) {
            node = graph.addNode(BIOMOLECULE_LABEL, ID_KEY, a.biomoleculeID, "name", a.biomoleculeName, "category",
                                 a.category);
        }
        return node;
    }
}
