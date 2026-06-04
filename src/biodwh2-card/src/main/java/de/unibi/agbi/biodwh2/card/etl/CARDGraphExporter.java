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
import de.unibi.agbi.biodwh2.card.model.AROTerm;
import de.unibi.agbi.biodwh2.card.model.Entry;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CARDGraphExporter extends GraphExporter<CARDDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(CARDGraphExporter.class);

    // Node labels
    private static final String CARD_MODEL_LABEL = "CARD_Model";
    private static final String ARO_TERM_LABEL = "ARO_Term";

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
    private static final String ARO_ID_PROPERTY_KEY = "aro_id";
    private static final String ARO_NAME_PROPERTY_KEY = "aro_name";
    private static final String ARO_NAMESPACE_KEY = "aro_namespace";
    private static final String ARO_DEF_KEY = "aro_def";
    private static final String CATEGORY_ARO_ACCESSION_KEY = "category_aro_accession";
    private static final String ARO_IS_A_KEY = "aro_is_a";
    private static final String ARO_SYNONYMS_KEY = "aro_synonyms";
    private static final String ARO_XREFS_KEY = "aro_xrefs";

    // Relationship labels
    private static final String IS_A_LABEL = "IS_A";
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
        graph.addIndex(IndexDescription.forNode(ARO_TERM_LABEL, ARO_ID_PROPERTY_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ARO_TERM_LABEL, CATEGORY_ARO_ACCESSION_KEY, IndexDescription.Type.NON_UNIQUE));

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting CARD models...");
        exportEntries(graph);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting ARO ontology...");
        exportOntology(graph);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting model ARO category links...");
        exportModelAROCategories(graph);

        return true;
    }

    private void exportEntries(final Graph graph) {
        if (dataSource.model_entries == null)
            return;
        for (final Entry entry : dataSource.model_entries)
            exportEntry(graph, entry);
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        exportCARDModel(graph, entry);
    }

    private void exportCARDModel(final Graph graph, final Entry entry) {
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

    private void exportOntology(final Graph graph) {
        if (dataSource.aroTerms == null || dataSource.aroTerms.isEmpty())
            return;

        for (final AROTerm term : dataSource.aroTerms.values())
            exportAROTerm(graph, term);

        for (final AROTerm term : dataSource.aroTerms.values())
            exportARORelationships(graph, term);
    }

    private void exportAROTerm(final Graph graph, final AROTerm term) {
        if (term == null || StringUtils.isBlank(term.id))
            return;

        final Node existingNode = graph.findNode(ARO_TERM_LABEL, ARO_ID_PROPERTY_KEY, term.id);
        if (existingNode != null)
            return;

        final NodeBuilder builder = graph.buildNode().withLabel(ARO_TERM_LABEL);
        builder.withProperty(ARO_ID_PROPERTY_KEY, term.id);
        builder.withPropertyIfNotNull(ARO_NAME_PROPERTY_KEY, term.name);
        builder.withPropertyIfNotNull(ARO_NAMESPACE_KEY, term.namespace);
        builder.withPropertyIfNotNull(ARO_DEF_KEY, term.def);
        builder.withPropertyIfNotNull(CATEGORY_ARO_ACCESSION_KEY, term.categoryAroAccession);
        if (!term.isA.isEmpty())
            builder.withProperty(ARO_IS_A_KEY, term.isA.toArray(new String[0]));
        if (!term.synonyms.isEmpty())
            builder.withProperty(ARO_SYNONYMS_KEY, term.synonyms.toArray(new String[0]));
        if (!term.xrefs.isEmpty())
            builder.withProperty(ARO_XREFS_KEY, term.xrefs.toArray(new String[0]));
        builder.build();
    }

    private void exportARORelationships(final Graph graph, final AROTerm term) {
        if (term == null || StringUtils.isBlank(term.id) || (term.isA.isEmpty() && term.relationships.isEmpty()))
            return;

        final Node childNode = graph.findNode(ARO_TERM_LABEL, ARO_ID_PROPERTY_KEY, term.id);
        if (childNode == null)
            return;

        for (final String parentId : term.isA) {
            final Node parentNode = graph.findNode(ARO_TERM_LABEL, ARO_ID_PROPERTY_KEY, parentId);
            if (parentNode != null && !graph.containsEdge(IS_A_LABEL, parentNode, childNode))
                graph.addEdge(parentNode, childNode, IS_A_LABEL);
        }

        for (final AROTerm.Relationship relationship : term.relationships) {
            if (relationship == null || StringUtils.isBlank(relationship.name))
                continue;
            final Node targetNode = graph.findNode(ARO_TERM_LABEL, ARO_ID_PROPERTY_KEY, relationship.targetId);
            final String relationshipLabel = relationship.name.toUpperCase();
            if (targetNode != null && !graph.containsEdge(relationshipLabel, childNode, targetNode))
                graph.addEdge(childNode, targetNode, relationshipLabel);
        }
    }

    private void exportModelAROCategories(final Graph graph) {
        if (dataSource.model_entries == null || dataSource.model_entries.isEmpty())
            return;

        for (final Entry entry : dataSource.model_entries)
            exportModelAROCategoryLinks(graph, entry);
    }

    private void exportModelAROCategoryLinks(final Graph graph, final Entry entry) {
        if (entry == null || StringUtils.isBlank(entry.modelId) || entry.aroCategory == null)
            return;

        final Node modelNode = graph.findNode(CARD_MODEL_LABEL, MODEL_ID_KEY, entry.modelId);
        if (modelNode == null)
            return;

        for (final Map.Entry<String, Entry.AROCategory> catEntry : entry.aroCategory.entrySet()) {
            final Entry.AROCategory category = catEntry.getValue();
            if (category == null || StringUtils.isBlank(category.categoryAroAccession))
                continue;

            // Normalize accession format: ensure ARO: prefix
            final String accession = category.categoryAroAccession;
            final String normalizedAccession = accession.startsWith(ARO_PREFIX) ? accession : ARO_PREFIX + accession;
            final Node aroTermNode = graph.findNode(ARO_TERM_LABEL, ARO_ID_PROPERTY_KEY, normalizedAccession);
            if (aroTermNode == null)
                continue;

            if (!graph.containsEdge(HAS_ARO_CATEGORY_LABEL, modelNode, aroTermNode))
                graph.addEdge(modelNode, aroTermNode, HAS_ARO_CATEGORY_LABEL);
        }
    }
}
