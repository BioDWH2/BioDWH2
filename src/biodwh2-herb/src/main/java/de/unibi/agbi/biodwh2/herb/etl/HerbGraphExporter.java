package de.unibi.agbi.biodwh2.herb.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.herb.HerbDataSource;
import de.unibi.agbi.biodwh2.herb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HerbGraphExporter extends GraphExporter<HerbDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HerbGraphExporter.class);

    public HerbGraphExporter(final HerbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Herb", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Ingredient", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Target", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Disease", "id", false, IndexDescription.Type.UNIQUE));
        for (final Herb herb : parseTsvFile(workspace, Herb.class, "HERB_herb_info.txt"))
            graph.addNodeFromModel(herb);
        for (final Target target : parseTsvFile(workspace, Target.class, "HERB_target_info.txt"))
            graph.addNodeFromModel(target);
        for (final Disease disease : parseTsvFile(workspace, Disease.class, "HERB_disease_info.txt"))
            graph.addNodeFromModel(disease);
        for (final Ingredient ingredient : parseTsvFile(workspace, Ingredient.class, "HERB_ingredient_info.txt"))
            graph.addNodeFromModel(ingredient);
        for (final Experiment experiment : parseTsvFile(workspace, Experiment.class, "HERB_experiment_info.txt")) {
            Node sourceNode = null;
            if (experiment.herbOrIngredient.equalsIgnoreCase("herb")) {
                sourceNode = graph.findNode("Herb", "id", experiment.herbOrIngredientId);
            } else if (experiment.herbOrIngredient.equalsIgnoreCase("ingredient")) {
                sourceNode = graph.findNode("Ingredient", "id", experiment.herbOrIngredientId);
            }
            if (sourceNode != null) {
                final Node node = graph.addNodeFromModel(experiment);
                graph.addEdge(sourceNode, node, "ASSOCIATED_WITH");
            } else
                graph.addNodeFromModel(experiment, "herb_or_ingredient_id", experiment.herbOrIngredientId);
        }
        /*
        for (final Reference reference : parseTsvFile(workspace, Reference.class, "HERB_reference_info.txt")) {
            // TODO
        }
        */
        return true;
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeader(workspace, dataSource, fileName,
                                                                      typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }
}
