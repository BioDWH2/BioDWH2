package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import de.unibi.agbi.biodwh2.dgidb.model.Category;
import de.unibi.agbi.biodwh2.dgidb.model.Drug;
import de.unibi.agbi.biodwh2.dgidb.model.Gene;
import de.unibi.agbi.biodwh2.dgidb.model.Interaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class DGIdbGraphExporter extends GraphExporter<DGIdbDataSource> {
    private static class CategoryLink {
        public Long categoryNodeId;
        public String sourceDBName;
        public String sourceDBVersion;

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CategoryLink that = (CategoryLink) o;
            return Objects.equals(categoryNodeId, that.categoryNodeId) && Objects.equals(sourceDBName,
                                                                                         that.sourceDBName) &&
                   Objects.equals(sourceDBVersion, that.sourceDBVersion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryNodeId, sourceDBName, sourceDBVersion);
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(DGIdbGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String GENE_LABEL = "Gene";
    public static final String INTERACTS_WITH_LABEL = "INTERACTS_WITH";

    public DGIdbGraphExporter(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportDrugs(workspace, graph);
        final Map<String, List<CategoryLink>> geneNameCategoriesMap = exportCategories(workspace, graph);
        exportGenes(workspace, graph, geneNameCategoriesMap);
        exportInteractions(workspace, graph);
        return true;
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drugs...");
        final Map<String, List<Drug>> conceptIdDrugsMap = new HashMap<>();
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, DGIdbUpdater.DRUGS_FILE_NAME, Drug.class, (entry) -> {
                if (entry.conceptId != null && !"NULL".equals(entry.conceptId))
                    conceptIdDrugsMap.computeIfAbsent(entry.conceptId, (x) -> new ArrayList<>()).add(entry);
            });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DGIdbUpdater.DRUGS_FILE_NAME + "'", e);
        }
        for (final var entry : conceptIdDrugsMap.entrySet())
            exportDrug(graph, entry.getKey(), entry.getValue());
    }

    private void exportDrug(final Graph graph, final String id, final List<Drug> drugs) {
        final Map<String, List<Drug>> propertyKeyDrugsMap = new HashMap<>();
        for (final var drug : drugs)
            propertyKeyDrugsMap.computeIfAbsent(drug.nomenclature, (x) -> new ArrayList<>()).add(drug);
        final NodeBuilder builder = graph.buildNode(DRUG_LABEL);
        builder.withProperty(ID_KEY, id);
        builder.withProperty("name", drugs.get(0).drugName);
        builder.withProperty("sources", drugs.stream().map(this::drugSourceToString).distinct().toArray(String[]::new));
        addSingularPluralDrugProperty(builder, propertyKeyDrugsMap, "Drugs@FDA ID", "drugs_at_fda_id");
        addSingularPluralDrugProperty(builder, propertyKeyDrugsMap, "Primary Drug Name", "primary_drug_name");
        addSingularPluralDrugProperty(builder, propertyKeyDrugsMap, "Primary Name", "primary_name");
        builder.build();
    }

    private String drugSourceToString(final Drug d) {
        return d.nomenclature + '|' + d.drugClaimName + '|' + d.approved + '|' + d.immunotherapy + '|' +
               d.antiNeoplastic + '|' + d.sourceDBName + '|' + d.sourceDBVersion;
    }

    private static void addSingularPluralDrugProperty(final NodeBuilder builder,
                                                      final Map<String, List<Drug>> propertyKeyEntriesMap,
                                                      final String key, final String propertyKey) {
        final List<Drug> entries = propertyKeyEntriesMap.get(key);
        if (entries == null)
            return;
        final String[] values = entries.stream().map((x) -> x.drugClaimName).distinct().toArray(String[]::new);
        if (values.length > 1)
            builder.withProperty(propertyKey + 's', values);
        else if (values.length == 1)
            builder.withProperty(propertyKey, values[0]);
    }

    private Map<String, List<CategoryLink>> exportCategories(final Workspace workspace, final Graph graph) {
        final Map<String, List<CategoryLink>> result = new HashMap<>();
        final Map<String, Long> categoryNameNodeIdMap = new HashMap<>();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting categories...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, DGIdbUpdater.CATEGORIES_FILE_NAME, Category.class,
                                        (entry) -> {
                                            Long nodeId = categoryNameNodeIdMap.get(entry.category);
                                            if (nodeId == null) {
                                                nodeId = graph.addNode("Category", "name", entry.category).getId();
                                                categoryNameNodeIdMap.put(entry.category, nodeId);
                                            }
                                            final var categoryLink = new CategoryLink();
                                            categoryLink.categoryNodeId = nodeId;
                                            categoryLink.sourceDBName = entry.sourceDBName;
                                            categoryLink.sourceDBVersion = entry.sourceDBVersion;
                                            result.computeIfAbsent(entry.name, (x) -> new ArrayList<>()).add(
                                                    categoryLink);
                                        });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DGIdbUpdater.CATEGORIES_FILE_NAME + "'", e);
        }
        return result;
    }

    private void exportGenes(final Workspace workspace, final Graph graph,
                             final Map<String, List<CategoryLink>> geneNameCategoriesMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting genes...");
        final Map<String, List<Gene>> conceptIdGenesMap = new HashMap<>();
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, DGIdbUpdater.GENES_FILE_NAME, Gene.class, (entry) -> {
                if (entry.conceptId != null && !"NULL".equals(entry.conceptId))
                    conceptIdGenesMap.computeIfAbsent(entry.conceptId, (x) -> new ArrayList<>()).add(entry);
            });
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DGIdbUpdater.GENES_FILE_NAME + "'", e);
        }
        for (final var entry : conceptIdGenesMap.entrySet())
            exportGene(graph, geneNameCategoriesMap, entry.getKey(), entry.getValue());
    }

    private void exportGene(final Graph graph, final Map<String, List<CategoryLink>> geneNameCategoriesMap,
                            final String id, final List<Gene> genes) {
        final Map<String, List<Gene>> propertyKeyGenesMap = new HashMap<>();
        for (final var gene : genes)
            propertyKeyGenesMap.computeIfAbsent(gene.nomenclature, (x) -> new ArrayList<>()).add(gene);
        final NodeBuilder builder = graph.buildNode(GENE_LABEL);
        builder.withProperty(ID_KEY, id);
        builder.withProperty("name", genes.get(0).geneClaimName);
        builder.withProperty("sources", genes.stream().map(this::geneSourceToString).distinct().toArray(String[]::new));
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "Ensembl Gene ID", "ensembl_gene_id");
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "Gene Name", "gene_name");
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "Gene Symbol", "gene_symbol");
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "NCBI Gene Name", "ncbi_gene_name");
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "OncoKB Gene Name", "oncokb_gene_name");
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "Primary Gene Name", "primary_gene_name");
        addSingularPluralGeneProperty(builder, propertyKeyGenesMap, "UniProtKB ID", "uniprotkb_id");
        final Node node = builder.build();
        final List<CategoryLink> categoryLinks = new ArrayList<>();
        for (final var gene : genes) {
            final var links = geneNameCategoriesMap.get(gene.name);
            if (links != null)
                categoryLinks.addAll(links);
        }
        categoryLinks.stream().distinct().forEach(
                (link) -> graph.addEdge(node, link.categoryNodeId, "HAS_CATEGORY", "source_db", link.sourceDBName,
                                        "source_db_version", link.sourceDBVersion));
    }

    private String geneSourceToString(final Gene g) {
        return g.nomenclature + '|' + g.name + '|' + g.sourceDBName + '|' + g.sourceDBVersion;
    }

    private static void addSingularPluralGeneProperty(final NodeBuilder builder,
                                                      final Map<String, List<Gene>> propertyKeyEntriesMap,
                                                      final String key, final String propertyKey) {
        final List<Gene> entries = propertyKeyEntriesMap.get(key);
        if (entries == null)
            return;
        final String[] values = entries.stream().map((x) -> x.name).distinct().toArray(String[]::new);
        if (values.length > 1)
            builder.withProperty(propertyKey + 's', values);
        else if (values.length == 1)
            builder.withProperty(propertyKey, values[0]);
    }

    private void exportInteractions(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting interactions...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, DGIdbUpdater.INTERACTIONS_FILE_NAME, Interaction.class,
                                        (entry) -> exportInteraction(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DGIdbUpdater.INTERACTIONS_FILE_NAME + "'", e);
        }
    }

    private void exportInteraction(final Graph graph, final Interaction interaction) {
        if (interaction.gene_concept_id != null && !"NULL".equals(interaction.gene_concept_id) &&
            interaction.drug_concept_id != null && !"NULL".equals(interaction.drug_concept_id)) {
            final Node drugNode = graph.findNode(DRUG_LABEL, ID_KEY, interaction.drug_concept_id);
            final Node geneNode = graph.findNode(GENE_LABEL, ID_KEY, interaction.gene_concept_id);
            graph.addEdgeFromModel(drugNode, geneNode, interaction);
        }
    }
}
