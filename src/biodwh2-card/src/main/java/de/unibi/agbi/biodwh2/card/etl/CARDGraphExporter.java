package de.unibi.agbi.biodwh2.card.etl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.card.CARDDataSource;
import de.unibi.agbi.biodwh2.card.model.CARD_Model;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CARDGraphExporter extends GraphExporter<CARDDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(CARDGraphExporter.class);

    // Node labels
    private static final String CARD_MODEL_LABEL = "CARD_Model";

    // Property keys
    private static final String MODEL_ID_KEY = "model_id";
    private static final String MODEL_NAME_KEY = "model_name";
    private static final String MODEL_TYPE_KEY = "model_type";
    private static final String MODEL_TYPE_ID_KEY = "model_type_id";
    private static final String MODEL_DESCRIPTION_KEY = "model_description";
    private static final String ARO_ACCESSION_KEY = "ARO_accession";
    private static final String ARO_ID_KEY = "ARO_id";
    private static final String ARO_NAME_KEY = "ARO_name";
    private static final String ARO_DESCRIPTION_KEY = "ARO_description";
    private static final String CARD_SHORT_NAME_KEY = "CARD_short_name";

    // Relationship labels
    private static final String HAS_ARO_CATEGORY_LABEL = "HAS_ARO_CATEGORY";

    // Constants
    private static final String ARO_PREFIX = "ARO:";
    private static final String MODEL_PARAM_KEY = "model_param";
    private static final String MODEL_SEQUENCES_KEY = "model_sequences";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CARDGraphExporter(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(CARD_MODEL_LABEL, MODEL_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CARD_MODEL_LABEL, MODEL_NAME_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CARD_MODEL_LABEL, ARO_ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CARD_MODEL_LABEL, ARO_NAME_KEY, IndexDescription.Type.UNIQUE));

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting CARD models...");
        exportEntries(graph);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting model ARO category links...");
        exportModelAROCategories(graph);

        return true;
    }

    private void exportEntries(final Graph graph) {
        if (dataSource.model_entries == null)
            return;
        for (final CARD_Model entry : dataSource.model_entries)
            exportCARDModel(graph, entry);
    }

    private void exportCARDModel(final Graph graph, final CARD_Model entry) {
        final NodeBuilder builder = graph.buildNode().withLabel(CARD_MODEL_LABEL);

        builder.withProperty(MODEL_ID_KEY, entry.modelId);
        builder.withPropertyIfNotNull(MODEL_NAME_KEY, entry.modelName);
        builder.withPropertyIfNotNull(MODEL_TYPE_KEY, entry.modelType);
        builder.withPropertyIfNotNull(MODEL_TYPE_ID_KEY, entry.modelTypeId);
        builder.withPropertyIfNotNull(MODEL_DESCRIPTION_KEY, entry.modelDescription);

        builder.withPropertyIfNotNull(ARO_ACCESSION_KEY, entry.aroAccession);
        builder.withPropertyIfNotNull(ARO_ID_KEY, entry.aroId);
        builder.withPropertyIfNotNull(ARO_NAME_KEY, entry.aroName);
        builder.withPropertyIfNotNull(ARO_DESCRIPTION_KEY, entry.aroDescription);

        builder.withPropertyIfNotNull(CARD_SHORT_NAME_KEY, entry.cardShortName);

        if (entry.modelParam != null) {
            try {
                final String modelParamJson = objectMapper.writeValueAsString(entry.modelParam);
                builder.withProperty(MODEL_PARAM_KEY, modelParamJson);
            } catch (JsonProcessingException e) {
                // Log and skip if serialization fails
            }
        }

        if (entry.modelSequences != null) {
            try {
                final String modelSequencesJson = objectMapper.writeValueAsString(entry.modelSequences);
                builder.withProperty(MODEL_SEQUENCES_KEY, modelSequencesJson);
            } catch (JsonProcessingException e) {
                // Log and skip if serialization fails
            }
        }

        builder.build();
    }

    private void exportModelAROCategories(final Graph graph) {
        if (dataSource.model_entries == null || dataSource.model_entries.isEmpty())
            return;

        for (final CARD_Model entry : dataSource.model_entries)
            exportModelAROCategoryLinks(graph, entry);
    }

    private void exportModelAROCategoryLinks(final Graph graph, final CARD_Model entry) {
        if (entry == null || StringUtils.isBlank(entry.modelId) || entry.aroCategory == null)
            return;

        final Node modelNode = graph.findNode(CARD_MODEL_LABEL, MODEL_ID_KEY, entry.modelId);
        if (modelNode == null)
            return;

        for (final Map.Entry<String, CARD_Model.AROCategory> catEntry : entry.aroCategory.entrySet()) {
            final CARD_Model.AROCategory category = catEntry.getValue();
            if (category == null || StringUtils.isBlank(category.categoryAroAccession))
                continue;

            // Normalize accession format to match the ARO ontology term id (e.g. "ARO:3000000")
            final String accession = category.categoryAroAccession;
            final String normalizedAccession = accession.startsWith(ARO_PREFIX) ? accession : ARO_PREFIX + accession;
            final Long termNodeId = getOrCreateOntologyProxyTerm(graph, normalizedAccession);

            if (!graph.containsEdge(HAS_ARO_CATEGORY_LABEL, modelNode.getId(), termNodeId))
                graph.addEdge(modelNode.getId(), termNodeId, HAS_ARO_CATEGORY_LABEL);
        }
    }
}