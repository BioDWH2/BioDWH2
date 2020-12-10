package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import de.unibi.agbi.biodwh2.dgidb.model.Category;
import de.unibi.agbi.biodwh2.dgidb.model.Drug;
import de.unibi.agbi.biodwh2.dgidb.model.Gene;
import de.unibi.agbi.biodwh2.dgidb.model.Interaction;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DGIdbGraphExporter extends GraphExporter<DGIdbDataSource> {
    public DGIdbGraphExporter(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys("chembl_id", "entrez_id");
        for (Drug drug : dataSource.drugs.stream().distinct().collect(Collectors.toList()))
            createNodeFromModel(graph, drug);
        for (Gene gene : dataSource.genes.stream().distinct().collect(Collectors.toList()))
            createNodeFromModel(graph, gene);
        Map<String, Long> categoryNodeIdMap = new HashMap<>();
        for (Category category : dataSource.categories) {
            if (!categoryNodeIdMap.containsKey(category.category)) {
                Node categoryNode = createNode(graph, "GeneCategory");
                categoryNodeIdMap.put(category.category, categoryNode.getId());
            }
            Node gene = graph.findNode("Gene", "claim_name", category.entrezGeneSymbol);
            Edge edge = graph.addEdge(gene, categoryNodeIdMap.get(category.category), "IN_CATEGORY");
            edge.setProperty("sources", StringUtils.split(category.categorySources, ","));
        }
        for (Interaction interaction : dataSource.interactions) {
            Node drugNode = graph.findNode("Drug", "claim_name", interaction.drugClaimName, "chembl_id",
                                           interaction.drugChemblId, "name", interaction.drugName);
            Node geneNode = graph.findNode("Gene", "claim_name", interaction.geneClaimName, "entrez_id",
                                           interaction.entrezId, "name", interaction.geneName);
            Edge edge = graph.addEdge(drugNode, geneNode, "TARGETS");
            edge.setProperty("claim_source", interaction.interactionClaimSource);
            edge.setProperty("types", StringUtils.split(interaction.interactionTypes));
            edge.setProperty("pmids", StringUtils.split(interaction.pmids, ","));
        }
        return true;
    }
}
