package de.unibi.agbi.biodwh2.herb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.herb.HerbDataSource;
import de.unibi.agbi.biodwh2.herb.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class HerbGraphExporter extends GraphExporter<HerbDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(HerbGraphExporter.class);
    public static final String HERB_LABEL = "Herb";
    public static final String INGREDIENT_LABEL = "Ingredient";
    public static final String TARGET_LABEL = "Target";
    public static final String DISEASE_LABEL = "Disease";

    public HerbGraphExporter(final HerbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(HERB_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(INGREDIENT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportHerbs(workspace, graph);
        exportTargets(workspace, graph);
        exportDiseases(workspace, graph);
        exportIngredients(workspace, graph);
        exportExperiments(workspace, graph);
        exportReferences(workspace, graph);
        return true;
    }

    private void exportHerbs(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting herbs...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HerbUpdater.HERBS_FILE_NAME, Herb.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + HerbUpdater.HERBS_FILE_NAME + "'", e);
        }
    }

    private void exportTargets(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting targets...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HerbUpdater.TARGETS_FILE_NAME, Target.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + HerbUpdater.TARGETS_FILE_NAME + "'", e);
        }
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting diseases...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HerbUpdater.DISEASES_FILE_NAME, Disease.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + HerbUpdater.DISEASES_FILE_NAME + "'", e);
        }
    }

    private void exportIngredients(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting ingredients...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HerbUpdater.INGREDIENTS_FILE_NAME, Ingredient.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + HerbUpdater.INGREDIENTS_FILE_NAME + "'", e);
        }
    }

    private void exportExperiments(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting experiments...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HerbUpdater.EXPERIMENTS_FILE_NAME, Experiment.class,
                                        (experiment -> exportExperiment(graph, experiment)));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + HerbUpdater.EXPERIMENTS_FILE_NAME + "'", e);
        }
    }

    private void exportExperiment(final Graph graph, final Experiment experiment) {
        Node sourceNode = null;
        if (experiment.herbOrIngredient.equalsIgnoreCase("herb"))
            sourceNode = graph.findNode(HERB_LABEL, ID_KEY, experiment.herbOrIngredientId);
        else if (experiment.herbOrIngredient.equalsIgnoreCase("ingredient"))
            sourceNode = graph.findNode(INGREDIENT_LABEL, ID_KEY, experiment.herbOrIngredientId);
        if (sourceNode != null) {
            final Node node = graph.addNodeFromModel(experiment);
            graph.addEdge(sourceNode, node, "ASSOCIATED_WITH");
        } else
            graph.addNodeFromModel(experiment, "herb_or_ingredient_id", experiment.herbOrIngredientId);
    }

    private void exportReferences(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting references...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HerbUpdater.REFERENCES_FILE_NAME, Reference.class,
                                        (experiment -> {
                                            // TODO
                                        }));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + HerbUpdater.REFERENCES_FILE_NAME + "'", e);
        }
    }
}
