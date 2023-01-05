package de.unibi.agbi.biodwh2.cmaup.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.cmaup.CMAUPDataSource;
import de.unibi.agbi.biodwh2.cmaup.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class CMAUPGraphExporter extends GraphExporter<CMAUPDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(CMAUPGraphExporter.class);

    public CMAUPGraphExporter(final CMAUPDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Plant", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Ingredient", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Target", "id", false, IndexDescription.Type.UNIQUE));
        final String[] fileNames = dataSource.listSourceFiles(workspace);
        for (final Plant plant : openTsvFile(workspace, fileNames, "_Plants.txt", Plant.class, true)) {
            graph.addNodeFromModel(plant);
        }
        // _Ingredients_All.txt has a malformed header row
        for (final Ingredient ingredient : openTsvFile(workspace, fileNames, "_Ingredients_onlyActive.txt",
                                                       Ingredient.class, true)) {
            graph.addNodeFromModel(ingredient);
        }
        for (final Target target : openTsvFile(workspace, fileNames, "_Targets.txt", Target.class, true)) {
            graph.addNodeFromModel(target);
        }
        for (final PlantIngredientAssociation association : openTsvFile(workspace, fileNames,
                                                                        "_Plant_Ingredient_Associations_onlyActiveIngredients.txt",
                                                                        PlantIngredientAssociation.class, false)) {
            final Node plantNode = graph.findNode("Plant", "id", association.plantId);
            final Node ingredientNode = graph.findNode("Ingredient", "id", association.ingredientId);
            graph.addEdge(plantNode, ingredientNode, "CONTAINS");
        }
        for (final IngredientTargetAssociation association : openTsvFile(workspace, fileNames,
                                                                         "_Ingredient_Target_Associations_ActivityValues_References.txt",
                                                                         IngredientTargetAssociation.class, true)) {
            final Node ingredientNode = graph.findNode("Ingredient", "id", association.ingredientId);
            final Node targetNode = graph.findNode("Target", "id", association.targetId);
            // Skipping ingredient-target associations where the ingredient is not active
            if (ingredientNode == null)
                continue;
            // Skipping targets not in targets file
            if (targetNode == null)
                continue;
            final EdgeBuilder builder = graph.buildEdge().withLabel("TARGETS").fromNode(ingredientNode).toNode(
                    targetNode);
            builder.withPropertyIfNotNull("activity_type", association.activityType);
            builder.withPropertyIfNotNull("activity_value", association.activityValue);
            builder.withPropertyIfNotNull("activity_relationship", association.activityRelationship);
            builder.withPropertyIfNotNull("activity_unit", association.activityUnit);
            builder.withPropertyIfNotNull("reference_id", association.referenceId);
            builder.withPropertyIfNotNull("reference_id_type", association.referenceIdType);
            builder.withPropertyIfNotNull("reference_id_others", association.referenceIdOthers);
            builder.build();
        }
        return true;
    }

    private <T> Iterable<T> openTsvFile(final Workspace workspace, final String[] fileNames,
                                        final String fileNameSuffix, final Class<T> typeClass,
                                        final boolean withHeader) {
        final Optional<String> fileName = Arrays.stream(fileNames).filter((x) -> x.endsWith(fileNameSuffix))
                                                .findFirst();
        if (!fileName.isPresent())
            throw new ExporterException("File with suffix " + fileNameSuffix + " not found");
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName.get());
        try {
            final MappingIterator<T> iterator = withHeader ? FileUtils.openTsvWithHeader(workspace, dataSource,
                                                                                         fileName.get(), typeClass) :
                                                FileUtils.openTsv(workspace, dataSource, fileName.get(), typeClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
