package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.gene2phenotype.Gene2PhenotypeDataSource;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;
import org.apache.commons.lang3.StringUtils;

public class Gene2PhenotypeGraphExporter extends GraphExporter<Gene2PhenotypeDataSource> {
    static final String PUBLICATION_LABEL = "Publication";
    static final String GENE_LABEL = "Gene";
    static final String PHENOTYPE_LABEL = "Phenotype";
    static final String DISEASE_LABEL = "Disease";

    public Gene2PhenotypeGraphExporter(final Gene2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "hgnc_symbol", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PHENOTYPE_LABEL, "hpo_id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "mim", false, IndexDescription.Type.UNIQUE));
        exportGeneDiseasePairs(graph);
        return true;
    }

    private void exportGeneDiseasePairs(final Graph graph) {
        for (final GeneDiseasePair gdp : dataSource.geneDiseasePairs)
            exportGeneDiseasePair(graph, gdp);
    }

    private void exportGeneDiseasePair(final Graph graph, final GeneDiseasePair gdp) {
        final Node associationNode = graph.addNode("Association");
        associationNode.setProperty("confidence", gdp.diseaseConfidence.getValue());
        associationNode.setProperty("allelic_requirement", gdp.allelicRequirement);
        associationNode.setProperty("mutation_consequence", gdp.mutationConsequence);
        associationNode.setProperty("panel", gdp.panel.getValue());
        associationNode.setProperty("entry_date", gdp.entryDate);
        associationNode.setProperty("organ_specificity_list", StringUtils.split(gdp.organSpecificityList, ';'));
        graph.update(associationNode);
        final Node geneNode = getOrCreateGeneNode(graph, gdp);
        graph.addEdge(geneNode, associationNode, "MUTATES");
        final Node diseaseNode = getOrCreateDiseaseNode(graph, gdp.diseaseName, gdp.diseaseMim);
        graph.addEdge(associationNode, diseaseNode, "CAUSES");
        if (gdp.phenotypes != null)
            for (final String hpoId : StringUtils.split(gdp.phenotypes, ';'))
                graph.addEdge(associationNode, getOrCreatePhenotypeNode(graph, hpoId), "SHOWS");
        if (gdp.pmids != null)
            for (final String pmid : StringUtils.split(gdp.pmids, ';'))
                graph.addEdge(associationNode, getOrCreatePublicationNode(graph, pmid), "HAS_REFERENCE");
    }

    private Node getOrCreateGeneNode(final Graph graph, final GeneDiseasePair gdp) {
        Node node = graph.findNode(GENE_LABEL, "hgnc_symbol", gdp.geneSymbol);
        if (node == null)
            node = graph.addNode(GENE_LABEL, "hgnc_symbol", gdp.geneSymbol, "hgnc_id", gdp.hgncId, "mim", gdp.geneMim,
                                 "previous_symbols", StringUtils.split(gdp.prevSymbols, ';'));
        return node;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final String name, final String mim) {
        Node node = graph.findNode(DISEASE_LABEL, "mim", mim);
        if (node == null)
            node = graph.addNode(DISEASE_LABEL, "name", name, "mim", mim);
        return node;
    }

    private Node getOrCreatePhenotypeNode(final Graph graph, final String hpoId) {
        Node node = graph.findNode(PHENOTYPE_LABEL, "hpo_id", hpoId);
        if (node == null)
            node = graph.addNode(PHENOTYPE_LABEL, "hpo_id", hpoId);
        return node;
    }

    private Node getOrCreatePublicationNode(final Graph graph, final String pmid) {
        Node node = graph.findNode(PUBLICATION_LABEL, "pmid", pmid);
        if (node == null)
            node = graph.addNode(PUBLICATION_LABEL, "pmid", pmid);
        return node;
    }
}
