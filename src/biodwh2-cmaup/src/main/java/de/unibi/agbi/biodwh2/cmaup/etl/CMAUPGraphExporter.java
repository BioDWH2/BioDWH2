package de.unibi.agbi.biodwh2.cmaup.etl;

import de.unibi.agbi.biodwh2.cmaup.CMAUPDataSource;
import de.unibi.agbi.biodwh2.cmaup.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CMAUPGraphExporter extends GraphExporter<CMAUPDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(CMAUPGraphExporter.class);
    public static final String PLANT_LABEL = "Plant";
    public static final String INGREDIENT_LABEL = "Ingredient";
    public static final String TARGET_LABEL = "Target";
    public static final String TARGETS_LABEL = "TARGETS";

    public CMAUPGraphExporter(final CMAUPDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PLANT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(INGREDIENT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportPlants(workspace, graph);
        exportIngredients(workspace, graph);
        exportTargets(workspace, graph);
        exportPlantIngredientAssociations(workspace, graph);
        exportIngredientTargetAssociations(workspace, graph);
        return true;
    }

    private void exportPlants(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting plants...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, CMAUPUpdater.PLANTS_FILE_NAME, Plant.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + CMAUPUpdater.PLANTS_FILE_NAME + "'", e);
        }
    }

    private void exportIngredients(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting ingredients...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, CMAUPUpdater.INGREDIENTS_ONLY_ACTIVE_FILE_NAME,
                                        Ingredient.class, graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + CMAUPUpdater.INGREDIENTS_ONLY_ACTIVE_FILE_NAME + "'", e);
        }
    }

    private void exportTargets(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting targets...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, CMAUPUpdater.TARGETS_FILE_NAME, Target.class,
                                        graph::addNodeFromModel);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + CMAUPUpdater.TARGETS_FILE_NAME + "'", e);
        }
    }

    private void exportPlantIngredientAssociations(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting plant ingredient associations...");
        try {
            FileUtils.openTsv(workspace, dataSource, CMAUPUpdater.PLANT_INGREDIENT_ASSOCIATIONS_ONLY_ACTIVE_FILE_NAME,
                              PlantIngredientAssociation.class, ((entry) -> {
                        final Node plantNode = graph.findNode(PLANT_LABEL, ID_KEY, entry.plantId);
                        final Node ingredientNode = graph.findNode(INGREDIENT_LABEL, ID_KEY, entry.ingredientId);
                        graph.addEdge(plantNode, ingredientNode, "CONTAINS");
                    }));
        } catch (IOException e) {
            throw new ExporterException(
                    "Failed to export '" + CMAUPUpdater.PLANT_INGREDIENT_ASSOCIATIONS_ONLY_ACTIVE_FILE_NAME + "'", e);
        }
    }

    private void exportIngredientTargetAssociations(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting ingredient target associations...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, CMAUPUpdater.INGREDIENT_TARGET_ASSOCIATIONS_FILE_NAME,
                                        IngredientTargetAssociation.class, ((entry) -> {
                        final Node ingredientNode = graph.findNode(INGREDIENT_LABEL, ID_KEY, entry.ingredientId);
                        final Node targetNode = graph.findNode(TARGET_LABEL, ID_KEY, entry.targetId);
                        // Skipping ingredient-target associations where the ingredient is not active and targets not in targets file
                        if (ingredientNode != null && targetNode != null)
                            graph.addEdgeFromModel(ingredientNode, targetNode, entry);
                    }));
        } catch (IOException e) {
            throw new ExporterException(
                    "Failed to export '" + CMAUPUpdater.INGREDIENT_TARGET_ASSOCIATIONS_FILE_NAME + "'", e);
        }
    }
}
