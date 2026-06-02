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
import de.unibi.agbi.biodwh2.card.model.Entry;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public final class CARDGraphExporter extends GraphExporter<CARDDataSource> {
    private final Set<String> indexedCategoryLabels = new HashSet<>();

    public CARDGraphExporter(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("CARD_Model", "model_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("CARD_Model", "model_name", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("CARD_Model", "ARO_accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("CARD_Model", "ARO_name", IndexDescription.Type.UNIQUE));
        for (final Entry entry : dataSource.entries)
            exportEntry(graph, entry);
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        // Use CARD_Model label to match the index descriptions
        final NodeBuilder builder = graph.buildNode().withLabel("CARD_Model");

        // Core identification properties
        builder.withProperty("model_id", entry.modelId);
        builder.withPropertyIfNotNull("model_name", entry.modelName);
        builder.withPropertyIfNotNull("model_type", entry.modelType);
        builder.withPropertyIfNotNull("model_type_id", entry.modelTypeId);
        builder.withPropertyIfNotNull("model_description", entry.modelDescription);

        // ARO properties
        builder.withPropertyIfNotNull("ARO_accession", entry.aroAccession);
        builder.withPropertyIfNotNull("ARO_id", entry.aroId);
        builder.withPropertyIfNotNull("ARO_name", entry.aroName);
        builder.withPropertyIfNotNull("ARO_description", entry.aroDescription);

        // CARD-specific properties
        builder.withPropertyIfNotNull("CARD_short_name", entry.cardShortName);

        // Serialize nested structures to JSON strings for content preservation
        if (entry.modelParam != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final String modelParamJson = mapper.writeValueAsString(entry.modelParam);
                builder.withProperty("model_param", modelParamJson);
            } catch (JsonProcessingException e) {
                // Log and skip if serialization fails
            }
        }

        if (entry.modelSequences != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final String modelSequencesJson = mapper.writeValueAsString(entry.modelSequences);
                builder.withProperty("model_sequences", modelSequencesJson);
            } catch (JsonProcessingException e) {
                // Log and skip if serialization fails
            }
        }

        final Node node = builder.build();

        // Export ARO categories as first-class nodes and connect them to the CARD_Model
        exportAROCategories(graph, entry, node);
    }

    /**
     * Export ARO categories of a CARD model as separate nodes and connect them to the model node.
     */
    private void exportAROCategories(final Graph graph, final Entry entry, final Node modelNode) {
        if (entry.aroCategory == null)
            return;

        for (final Map.Entry<String, Entry.AROCategory> catEntry : entry.aroCategory.entrySet()) {
            final Entry.AROCategory cat = catEntry.getValue();
            if (cat == null)
                continue;

            final String label = sanitizeCategoryLabel(cat.categoryAroClassName);
            final String identity = getCategoryIdentity(catEntry.getKey(), cat);
            final Node catNode = getOrCreateCategoryNode(graph, label, identity, cat, catEntry.getKey());

            // Connect CARD_Model -> ARO category
            graph.addEdge(modelNode, catNode, "HAS_ARO_CATEGORY");
        }
    }

    private Node getOrCreateCategoryNode(final Graph graph, final String label, final String identity,
                                         final Entry.AROCategory category, final String key) {
        if (!indexedCategoryLabels.contains(label)) {
            graph.addIndex(IndexDescription.forNode(label, "category_aro_identity", IndexDescription.Type.UNIQUE));
            indexedCategoryLabels.add(label);
        }

        Node node = graph.findNode(label, "category_aro_identity", identity);
        if (node != null)
            return node;

        final NodeBuilder catBuilder = graph.buildNode().withLabel(label);
        catBuilder.withProperty("category_aro_identity", identity);
        catBuilder.withPropertyIfNotNull("category_aro_accession", category.categoryAroAccession);
        catBuilder.withPropertyIfNotNull("category_aro_cvterm_id", category.categoryAroCvtermId);
        catBuilder.withPropertyIfNotNull("category_aro_name", category.categoryAroName);
        catBuilder.withPropertyIfNotNull("category_aro_description", category.categoryAroDescription);
        // keep the class name as a property as well
        catBuilder.withPropertyIfNotNull("category_aro_class_name", category.categoryAroClassName);
        // include original map key to help with uniqueness/debugging
        catBuilder.withPropertyIfNotNull("_key", key);

        return catBuilder.build();
    }

    private String getCategoryIdentity(final String key, final Entry.AROCategory category) {
        if (category.categoryAroCvtermId != null && !category.categoryAroCvtermId.isEmpty())
            return category.categoryAroCvtermId;
        if (category.categoryAroAccession != null && !category.categoryAroAccession.isEmpty())
            return category.categoryAroAccession;
        return key;
    }

    private String sanitizeCategoryLabel(final String className) {
        String label = "ARO_Category";
        if (className != null && !className.isEmpty()) {
            // sanitize label: keep letters, numbers and underscores, replace other chars with underscore
            label = className.replaceAll("[^A-Za-z0-9_]", "_");
            // ensure label does not start with a digit
            if (!label.isEmpty() && Character.isDigit(label.charAt(0)))
                label = "C_" + label;
        }
        return label;
    }
}
