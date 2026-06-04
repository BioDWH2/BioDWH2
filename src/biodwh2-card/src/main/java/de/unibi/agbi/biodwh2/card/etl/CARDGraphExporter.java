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

        exportEntries(graph);
        exportOntology(graph);
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
        // TODO : put this in a function?
        if (entry.modelSequences != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final String modelSequencesJson = mapper.writeValueAsString(entry.modelSequences);
                builder.withProperty("model_sequences", modelSequencesJson);
            } catch (JsonProcessingException e) {
                // Log and skip if serialization fails
            }
        }

        builder.build();
    }

    private void exportOntology(final Graph graph) {
        if (dataSource.aroTerms == null || dataSource.aroTerms.isEmpty())
            return;

        graph.addIndex(IndexDescription.forNode(ARO_LABEL, "aro_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ARO_LABEL, "category_aro_accession", IndexDescription.Type.NON_UNIQUE));
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
        builder.withPropertyIfNotNull("category_aro_accession", term.categoryAroAccession);
        if (!term.isA.isEmpty())
            builder.withProperty("aro_is_a", term.isA.toArray(new String[0]));
        if (!term.synonyms.isEmpty())
            builder.withProperty("aro_synonyms", term.synonyms.toArray(new String[0]));
        if (!term.xrefs.isEmpty())
            builder.withProperty("aro_xrefs", term.xrefs.toArray(new String[0]));
        builder.build();
    }

    private void exportARORelationships(final Graph graph, final Set<String> addedAroRelations, final AROTerm term) {
        if (term == null || StringUtils.isBlank(term.id) || (term.isA.isEmpty() && term.relationships.isEmpty()))
            return;

        final Node childNode = graph.findNode(ARO_LABEL, "aro_id", term.id);
        if (childNode == null)
            return;

        for (final String parentId : term.isA) {
            final Node parentNode = graph.findNode(ARO_LABEL, "aro_id", parentId);
            if (parentNode != null)
                addAroRelationship(graph, addedAroRelations, parentNode, childNode, "IS_A");
        }

        for (final AROTerm.Relationship relationship : term.relationships) {
            if (relationship == null || StringUtils.isBlank(relationship.name))
                continue;
            final Node targetNode = graph.findNode(ARO_LABEL, "aro_id", relationship.targetId);
            if (targetNode != null)
                addAroRelationship(graph, addedAroRelations, childNode, targetNode, relationship.name);
        }
    }

    private void addAroRelationship(final Graph graph, final Set<String> addedAroRelations, final Node sourceNode,
                                    final Node targetNode, final String relationName) {
        if (sourceNode == null || targetNode == null || StringUtils.isBlank(relationName))
            return;
        final String relationKey = sourceNode.getId() + "->" + targetNode.getId() + "#" + relationName;
        if (addedAroRelations.add(relationKey))
            graph.addEdge(sourceNode, targetNode, relationName);
    }

    private void exportModelAROCategories(final Graph graph) {
        if (dataSource.model_entries == null || dataSource.model_entries.isEmpty())
            return;

        final Set<String> addedCategoryRelations = new HashSet<>();
        for (final Entry entry : dataSource.model_entries)
            exportModelAROCategoryLinks(graph, entry, addedCategoryRelations);
    }

    private void exportModelAROCategoryLinks(final Graph graph, final Entry entry, final Set<String> addedRelations) {
        if (entry == null || StringUtils.isBlank(entry.modelId) || entry.aroCategory == null)
            return;

        final Node modelNode = graph.findNode("CARD_Model", "model_id", entry.modelId);
        if (modelNode == null)
            return;

        for (final Map.Entry<String, Entry.AROCategory> catEntry : entry.aroCategory.entrySet()) {
            final Entry.AROCategory category = catEntry.getValue();
            if (category == null || StringUtils.isBlank(category.categoryAroAccession))
                continue;
            // Normalize accession format: if it doesn't have ARO: prefix, add it for lookup
            final String accession = category.categoryAroAccession;
            final String normalizedAccession = accession.startsWith("ARO:") ? accession : "ARO:" + accession;
            final Node aroTermNode = graph.findNode("ARO_Term", "aro_id", normalizedAccession);
            if (aroTermNode == null)
                continue;
            final String relationKey = modelNode.getId() + "->" + aroTermNode.getId() + "#HAS_ARO_CATEGORY";
            if (addedRelations.add(relationKey))
                graph.addEdge(modelNode, aroTermNode, "HAS_ARO_CATEGORY");
        }
    }
}
