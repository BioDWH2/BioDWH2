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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;


public final class CARDGraphExporter extends GraphExporter<CARDDataSource> {
    private static final String ARO_LABEL = "ARO_Term";
    private final Set<String> indexedCategoryLabels = new HashSet<>();

    public CARDGraphExporter(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("CARD_Model", "model_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("CARD_Model", "model_name", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("CARD_Model", "ARO_accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("CARD_Model", "ARO_name", IndexDescription.Type.UNIQUE));

        for (final Entry entry : dataSource.entries)
            exportEntry(graph, entry);
        exportOntology(graph);
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final Node node = exportCARDModel(graph, entry);
        exportAROCategories(graph, entry, node);
    }

    private Node exportCARDModel(final Graph graph, final Entry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("CARD_Model");

        builder.withProperty("model_id", entry.modelId);
        builder.withPropertyIfNotNull("model_name", entry.modelName);
        builder.withPropertyIfNotNull("model_type", entry.modelType);
        builder.withPropertyIfNotNull("model_type_id", entry.modelTypeId);
        builder.withPropertyIfNotNull("model_description", entry.modelDescription);

        builder.withPropertyIfNotNull("ARO_accession", entry.aroAccession);
        builder.withPropertyIfNotNull("ARO_id", entry.aroId);
        builder.withPropertyIfNotNull("ARO_name", entry.aroName);
        builder.withPropertyIfNotNull("ARO_description", entry.aroDescription);

        builder.withPropertyIfNotNull("CARD_short_name", entry.cardShortName);

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

        return builder.build();
    }

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

            graph.addEdge(modelNode, catNode, "HAS_ARO_CATEGORY");
        }
    }

    private String sanitizeCategoryLabel(final String className) {
        String label = "ARO_Category";
        if (className != null && !className.isEmpty()) {
            label = className.replaceAll("[^A-Za-z0-9_]", "_");
            if (!label.isEmpty() && Character.isDigit(label.charAt(0)))
                label = "C_" + label;
        }
        return label;
    }

    private String getCategoryIdentity(final String key, final Entry.AROCategory category) {
        if (category.categoryAroCvtermId != null && !category.categoryAroCvtermId.isEmpty())
            return category.categoryAroCvtermId;
        if (category.categoryAroAccession != null && !category.categoryAroAccession.isEmpty())
            return category.categoryAroAccession;
        return key;
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
        catBuilder.withPropertyIfNotNull("category_aro_class_name", category.categoryAroClassName);
        catBuilder.withPropertyIfNotNull("_key", key);

        return catBuilder.build();
    }

    private void exportOntology(final Graph graph) {
        if (dataSource.aroTerms == null || dataSource.aroTerms.isEmpty())
            return;

        graph.addIndex(IndexDescription.forNode(ARO_LABEL, "aro_id", IndexDescription.Type.UNIQUE));
        for (final AROTerm term : dataSource.aroTerms.values())
            exportAROTerm(graph, term);

        final Set<String> addedAroRelations = new HashSet<>();
        for (final AROTerm term : dataSource.aroTerms.values())
            exportARORelationships(graph, addedAroRelations, term);
    }

    private void exportAROTerm(final Graph graph, final AROTerm term) {
        if (term == null || StringUtils.isBlank(term.id))
            return;

        final Node existingNode = graph.findNode(ARO_LABEL, "aro_id", term.id);
        if (existingNode != null)
            return;

        final NodeBuilder builder = graph.buildNode().withLabel(ARO_LABEL);
        builder.withProperty("aro_id", term.id);
        builder.withPropertyIfNotNull("aro_name", term.name);
        builder.withPropertyIfNotNull("aro_namespace", term.namespace);
        builder.withPropertyIfNotNull("aro_def", term.def);
        if (!term.isA.isEmpty())
            builder.withProperty("aro_is_a", term.isA.toArray(new String[0]));
        if (!term.synonyms.isEmpty())
            builder.withProperty("aro_synonyms", term.synonyms.toArray(new String[0]));
        if (!term.xrefs.isEmpty())
            builder.withProperty("aro_xrefs", term.xrefs.toArray(new String[0]));
        builder.build();
    }

    private void exportARORelationships(final Graph graph, final Set<String> addedAroRelations, final AROTerm term) {
        if (term == null || StringUtils.isBlank(term.id) || term.isA.isEmpty())
            return;

        final Node childNode = graph.findNode(ARO_LABEL, "aro_id", term.id);
        if (childNode == null)
            return;

        for (final String parentId : term.isA) {
            if (StringUtils.isBlank(parentId))
                continue;
            final Node parentNode = graph.findNode(ARO_LABEL, "aro_id", parentId);
            if (parentNode == null)
                continue;
            final String relationKey = parentNode.getId() + "->" + childNode.getId();
            if (addedAroRelations.add(relationKey))
                graph.addEdge(parentNode, childNode, "IS_A");
        }
    }
}
