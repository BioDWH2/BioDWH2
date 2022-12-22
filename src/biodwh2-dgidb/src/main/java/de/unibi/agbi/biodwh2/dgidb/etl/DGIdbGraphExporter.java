package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import de.unibi.agbi.biodwh2.dgidb.model.Category;
import de.unibi.agbi.biodwh2.dgidb.model.Drug;
import de.unibi.agbi.biodwh2.dgidb.model.Gene;
import de.unibi.agbi.biodwh2.dgidb.model.Interaction;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DGIdbGraphExporter extends GraphExporter<DGIdbDataSource> {
    static final String DRUG_LABEL = "Drug";
    static final String GENE_LABEL = "Gene";

    public DGIdbGraphExporter(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "chembl_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "entrez_id", IndexDescription.Type.UNIQUE));
        exportDrugs(graph);
        exportGenes(graph);
        exportCategories(graph);
        exportInteractions(graph);
        return true;
    }

    private void exportDrugs(final Graph graph) {
        for (final Drug drug : dataSource.drugs.stream().distinct().collect(Collectors.toList()))
            graph.addNodeFromModel(drug);
    }

    private void exportGenes(final Graph graph) {
        for (final Gene gene : dataSource.genes.stream().distinct().collect(Collectors.toList()))
            graph.addNodeFromModel(gene);
    }

    private void exportCategories(final Graph graph) {
        final Map<String, Long> categoryNodeIdMap = new HashMap<>();
        for (final Category category : dataSource.categories) {
            if (!categoryNodeIdMap.containsKey(category.category)) {
                final Node categoryNode = graph.addNode("GeneCategory");
                categoryNodeIdMap.put(category.category, categoryNode.getId());
            }
            final Node gene = graph.findNode(GENE_LABEL, "claim_name", category.entrezGeneSymbol);
            graph.addEdge(gene, categoryNodeIdMap.get(category.category), "IN_CATEGORY", "sources",
                          StringUtils.split(category.categorySources, ","));
        }
    }

    private void exportInteractions(final Graph graph) {
        for (final Interaction interaction : dataSource.interactions) {
            final Node drugNode = graph.findNode(DRUG_LABEL, "claim_name", interaction.drugClaimName, "chembl_id",
                                                 interaction.drugChemblId, "name", interaction.drugName);
            final Node geneNode = graph.findNode(GENE_LABEL, "claim_name", interaction.geneClaimName, "entrez_id",
                                                 interaction.entrezId, "name", interaction.geneName);
            final Integer[] pmids = Arrays.stream(StringUtils.split(interaction.pmids, ",")).map(Integer::parseInt)
                                          .toArray(Integer[]::new);
            graph.addEdge(drugNode, geneNode, "TARGETS", "claim_source", interaction.interactionClaimSource, "types",
                          StringUtils.split(interaction.interactionTypes), "pmids", pmids);
        }
    }
}
