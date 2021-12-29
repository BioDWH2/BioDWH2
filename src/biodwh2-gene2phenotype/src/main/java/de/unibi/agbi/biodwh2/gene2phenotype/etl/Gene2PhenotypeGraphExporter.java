package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
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
        for (final GeneDiseasePair association : dataSource.geneDiseasePairs)
            exportGeneDiseasePair(graph, association);
    }

    private void exportGeneDiseasePair(final Graph graph, final GeneDiseasePair association) {
        final NodeBuilder builder = graph.buildNode().withLabel("Association");
        builder.withPropertyIfNotNull("confidence_category", association.confidenceCategory);
        builder.withPropertyIfNotNull("allelic_requirement", association.allelicRequirement);
        builder.withPropertyIfNotNull("mutation_consequence", association.mutationConsequence);
        builder.withPropertyIfNotNull("cross_cutting_modifier", association.crossCuttingModifier);
        builder.withPropertyIfNotNull("mutation_consequence_flag", association.mutationConsequenceFlag);
        builder.withPropertyIfNotNull("panel", association.panel);
        builder.withPropertyIfNotNull("entry_date", association.entryDate);
        builder.withPropertyIfNotNull("organ_specificity", StringUtils.split(association.organSpecificityList, ';'));
        final Node associationNode = builder.build();
        final Node geneNode = getOrCreateGeneNode(graph, association.geneSymbol, association.hgncId,
                                                  association.geneMim, association.prevSymbols);
        graph.addEdge(geneNode, associationNode, "ASSOCIATED_WITH");
        final Node diseaseNode = getOrCreateDiseaseNode(graph, association.diseaseName, association.diseaseMim);
        graph.addEdge(associationNode, diseaseNode, "ASSOCIATED_WITH");
        if (association.phenotypes != null)
            for (final String hpoId : StringUtils.split(association.phenotypes, ';'))
                graph.addEdge(associationNode, getOrCreatePhenotypeNode(graph, hpoId), "SHOWS");
        if (association.pmids != null)
            for (final String pmid : StringUtils.split(association.pmids, ';'))
                graph.addEdge(associationNode, getOrCreatePublicationNode(graph, pmid), "HAS_REFERENCE");
    }

    private Node getOrCreateGeneNode(final Graph graph, final String symbol, final Integer hgncId, final String mim,
                                     final String prevSymbols) {
        final Integer mimNumber = tryParseMim(mim);
        Node node = graph.findNode(GENE_LABEL, "hgnc_symbol", symbol);
        if (node == null) {
            final String[] prevSymbolsArray = StringUtils.split(prevSymbols, ';');
            if (mimNumber != null)
                node = graph.addNode(GENE_LABEL, "hgnc_symbol", symbol, "hgnc_id", hgncId, "mim", mimNumber,
                                     "previous_symbols", prevSymbolsArray);
            else
                node = graph.addNode(GENE_LABEL, "hgnc_symbol", symbol, "hgnc_id", hgncId, "previous_symbols",
                                     prevSymbolsArray);
        }
        return node;
    }

    private Integer tryParseMim(final String mim) {
        try {
            return Integer.parseInt(mim);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final String name, final String mim) {
        final Integer mimNumber = tryParseMim(mim);
        Node node;
        if (mimNumber == null) {
            node = graph.findNode(DISEASE_LABEL, "name", name);
            if (node == null)
                node = graph.addNode(DISEASE_LABEL, "name", name);
        } else {
            node = graph.findNode(DISEASE_LABEL, "mim", mimNumber);
            if (node == null)
                node = graph.addNode(DISEASE_LABEL, "name", name, "mim", mimNumber);
        }
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
