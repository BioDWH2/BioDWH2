package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import de.unibi.agbi.biodwh2.dgidb.model.Drug;
import de.unibi.agbi.biodwh2.dgidb.model.Gene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class DGIdbGraphExporter extends GraphExporter<DGIdbDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(DGIdbGraphExporter.class);
    public static final String DRUG_LABEL = "Drug";
    public static final String GENE_LABEL = "Gene";

    public DGIdbGraphExporter(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        // graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "chembl_id", IndexDescription.Type.UNIQUE));
        exportDrugs(workspace, graph);
        exportGenes(workspace, graph);
        exportCategories(workspace, graph);
        exportInteractions(workspace, graph);
        return false;
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drugs...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, DGIdbUpdater.DRUGS_FILE_NAME, Drug.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DGIdbUpdater.DRUGS_FILE_NAME + "'", e);
        }
    }

    private void exportGenes(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting genes...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, DGIdbUpdater.GENES_FILE_NAME, Gene.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DGIdbUpdater.GENES_FILE_NAME + "'", e);
        }
    }

    private void exportCategories(final Workspace workspace, final Graph graph) {
        // final Map<String, Long> categoryNodeIdMap = new HashMap<>();
        // for (final Category category : dataSource.categories) {
        //     if (!categoryNodeIdMap.containsKey(category.category)) {
        //         final Node categoryNode = graph.addNode("GeneCategory");
        //         categoryNodeIdMap.put(category.category, categoryNode.getId());
        //     }
        //     final Node gene = graph.findNode(GENE_LABEL, "claim_name", category.entrezGeneSymbol);
        //     graph.addEdge(gene, categoryNodeIdMap.get(category.category), "IN_CATEGORY", "sources",
        //                   StringUtils.split(category.categorySources, ","));
        // }
    }

    private void exportInteractions(final Workspace workspace, final Graph graph) {
        // for (final Interaction interaction : dataSource.interactions) {
        //     final Node drugNode = graph.findNode(DRUG_LABEL, "claim_name", interaction.drugClaimName, "chembl_id",
        //                                          interaction.drugChemblId, "name", interaction.drugName);
        //     final Node geneNode = graph.findNode(GENE_LABEL, "claim_name", interaction.geneClaimName, "entrez_id",
        //                                          interaction.entrezId, "name", interaction.geneName);
        //     final Integer[] pmids = Arrays.stream(StringUtils.split(interaction.pmids, ",")).map(Integer::parseInt)
        //                                   .toArray(Integer[]::new);
        //     graph.addEdge(drugNode, geneNode, "TARGETS", "claim_source", interaction.interactionClaimSource, "types",
        //                   StringUtils.split(interaction.interactionTypes), "pmids", pmids);
        // }
    }
}
