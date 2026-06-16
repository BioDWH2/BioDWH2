package de.unibi.agbi.biodwh2.card.etl;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CARDGraphExporter extends GraphExporter<CARDDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(CARDGraphExporter.class);

    // Node labels
    private static final String AMR_MODEL_LABEL = "AMRModel";
    private static final String PROTEIN_LABEL = "Protein";
    private static final String DNA_SEQUENCE_LABEL = "DNASequence";
    private static final String TAXON_LABEL = "Taxon";

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
    private static final String ACCESSION_KEY = "accession";
    private static final String SEQUENCE_KEY = "sequence";
    private static final String FMIN_KEY = "fmin";
    private static final String FMAX_KEY = "fmax";
    private static final String STRAND_KEY = "strand";
    private static final String PARTIAL_KEY = "partial";
    private static final String NCBI_TAXID_KEY = "ncbi_taxid";
    private static final String NAME_KEY = "name";
    private static final String CVTERM_ID_KEY = "cvterm_id";
    private static final String BLASTP_BIT_SCORE_KEY = "blastp_bit_score";
    private static final String BLASTN_BIT_SCORE_KEY = "blastn_bit_score";
    private static final String SNP_KEY = "snp";

    // Relationship labels
    private static final String HAS_ARO_CATEGORY_LABEL = "HAS_ARO_CATEGORY";
    private static final String HAS_PROTEIN_LABEL = "HAS_PROTEIN";
    private static final String HAS_DNA_SEQUENCE_LABEL = "HAS_DNA_SEQUENCE";
    private static final String IN_TAXON_LABEL = "IN_TAXON";
    private static final String HAS_CORE_GENE_LABEL = "HAS_CORE_GENE";
    private static final String HAS_REGULATORY_GENE_LABEL = "HAS_REGULATORY_GENE";
    private static final String HAS_ACCESSORY_GENE_LABEL = "HAS_ACCESSORY_GENE";
    private static final String HAS_COMPONENT_LABEL = "HAS_COMPONENT";

    // model_param param_type_id dispatch values
    private static final String PARAM_TYPE_ID_BLASTP_BIT_SCORE = "40725";
    private static final String PARAM_TYPE_ID_BLASTN_BIT_SCORE = "41093";
    private static final String PARAM_TYPE_ID_SNP = "36301";
    private static final String PARAM_TYPE_ID_GENE_ORDER = "40297";
    private static final String PARAM_TYPE_ID_EFFLUX_COMPONENTS = "41141";
    private static final String SNP_PARAM_KEY = "snp";

    // Constants
    private static final String ARO_PREFIX = "ARO:";

    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public CARDGraphExporter(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(AMR_MODEL_LABEL, MODEL_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(AMR_MODEL_LABEL, MODEL_NAME_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(AMR_MODEL_LABEL, ARO_ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(AMR_MODEL_LABEL, ARO_NAME_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(AMR_MODEL_LABEL, ARO_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DNA_SEQUENCE_LABEL, ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TAXON_LABEL, NCBI_TAXID_KEY, IndexDescription.Type.UNIQUE));

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting CARD models...");
        exportEntries(graph);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting model ARO category links...");
        exportModelAROCategories(graph);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting meta-model dependency links...");
        exportModelDependencies(graph);

        return true;
    }

    private void exportEntries(final Graph graph) {
        if (dataSource.model_entries == null)
            return;
        for (final CARD_Model entry : dataSource.model_entries)
            exportCARDModel(graph, entry);
    }

    private void exportCARDModel(final Graph graph, final CARD_Model entry) {
        final NodeBuilder builder = graph.buildNode().withLabel(AMR_MODEL_LABEL);

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

        applyModelParams(builder, entry);

        final Node modelNode = builder.build();

        exportModelSequences(graph, modelNode, entry);
    }

    /**
     * Stores the {@code snp} and blast-score {@code model_param} objects verbatim as JSON-string properties on the model
     * node, leaving downstream extraction to the user. Gene-order and efflux-pump-component params encode pointers to
     * other models and are handled separately in {@link #exportModelDependencies(Graph)}.
     */
    private void applyModelParams(final NodeBuilder builder, final CARD_Model entry) {
        if (entry.modelParam == null)
            return;

        for (final Map.Entry<String, CARD_Model.ModelParam> paramEntry : entry.modelParam.entrySet()) {
            final CARD_Model.ModelParam param = paramEntry.getValue();
            if (param == null)
                continue;
            final String typeId = param.paramTypeId;
            if (PARAM_TYPE_ID_BLASTP_BIT_SCORE.equals(typeId))
                applyParamAsJson(builder, BLASTP_BIT_SCORE_KEY, param);
            else if (PARAM_TYPE_ID_BLASTN_BIT_SCORE.equals(typeId))
                applyParamAsJson(builder, BLASTN_BIT_SCORE_KEY, param);
            else if (PARAM_TYPE_ID_SNP.equals(typeId) || SNP_PARAM_KEY.equalsIgnoreCase(paramEntry.getKey()))
                // The meta-models carry an snp block without param_type_id (only evidence buckets), hence the key
                // fallback.
                applyParamAsJson(builder, SNP_KEY, param);
        }
    }

    private void applyParamAsJson(final NodeBuilder builder, final String key, final CARD_Model.ModelParam param) {
        try {
            builder.withProperty(key, objectMapper.writeValueAsString(param));
        } catch (JsonProcessingException e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Failed to serialize model_param '{}', skipping property", key, e);
        }
    }

    private void exportModelSequences(final Graph graph, final Node modelNode, final CARD_Model entry) {
        if (entry.modelSequences == null || entry.modelSequences.sequence == null)
            return;

        for (final CARD_Model.ModelSequence modelSequence : entry.modelSequences.sequence.values()) {
            if (modelSequence == null)
                continue;

            final Long proteinNodeId = getOrCreateProtein(graph, modelSequence.proteinSequence);
            if (proteinNodeId != null && !graph.containsEdge(HAS_PROTEIN_LABEL, modelNode.getId(), proteinNodeId))
                graph.addEdge(modelNode.getId(), proteinNodeId, HAS_PROTEIN_LABEL);

            final Long dnaNodeId = getOrCreateDnaSequence(graph, modelSequence.dnaSequence);
            if (dnaNodeId != null && !graph.containsEdge(HAS_DNA_SEQUENCE_LABEL, modelNode.getId(), dnaNodeId))
                graph.addEdge(modelNode.getId(), dnaNodeId, HAS_DNA_SEQUENCE_LABEL);

            final Long taxonNodeId = getOrCreateTaxon(graph, modelSequence.ncbiTaxonomy);
            if (taxonNodeId != null && !graph.containsEdge(IN_TAXON_LABEL, modelNode.getId(), taxonNodeId))
                graph.addEdge(modelNode.getId(), taxonNodeId, IN_TAXON_LABEL);
        }
    }

    private Long getOrCreateProtein(final Graph graph, final CARD_Model.ProteinSequence proteinSequence) {
        if (proteinSequence == null || StringUtils.isBlank(proteinSequence.accession))
            return null;

        final Node existing = graph.findNode(PROTEIN_LABEL, ACCESSION_KEY, proteinSequence.accession);
        if (existing != null)
            return existing.getId();

        return graph.buildNode().withLabel(PROTEIN_LABEL).withProperty(ACCESSION_KEY, proteinSequence.accession)
                    .withPropertyIfNotNull(SEQUENCE_KEY, proteinSequence.sequence).build().getId();
    }

    private Long getOrCreateDnaSequence(final Graph graph, final CARD_Model.DnaSequence dnaSequence) {
        if (dnaSequence == null || StringUtils.isBlank(dnaSequence.accession))
            return null;

        final Node existing = graph.findNode(DNA_SEQUENCE_LABEL, ACCESSION_KEY, dnaSequence.accession);
        if (existing != null)
            return existing.getId();

        return graph.buildNode().withLabel(DNA_SEQUENCE_LABEL).withProperty(ACCESSION_KEY, dnaSequence.accession)
                    .withPropertyIfNotNull(SEQUENCE_KEY, dnaSequence.sequence)
                    .withPropertyIfNotNull(FMIN_KEY, dnaSequence.fmin).withPropertyIfNotNull(FMAX_KEY, dnaSequence.fmax)
                    .withPropertyIfNotNull(STRAND_KEY, dnaSequence.strand)
                    .withPropertyIfNotNull(PARTIAL_KEY, dnaSequence.partial).build().getId();
    }

    private Long getOrCreateTaxon(final Graph graph, final CARD_Model.NcbiTaxonomy ncbiTaxonomy) {
        if (ncbiTaxonomy == null || StringUtils.isBlank(ncbiTaxonomy.ncbiTaxonomyId))
            return null;

        final Integer ncbiTaxId = Integer.parseInt(ncbiTaxonomy.ncbiTaxonomyId.strip());
        final Node existing = graph.findNode(TAXON_LABEL, NCBI_TAXID_KEY, ncbiTaxId);
        if (existing != null)
            return existing.getId();

        return graph.buildNode().withLabel(TAXON_LABEL).withProperty(NCBI_TAXID_KEY, ncbiTaxId)
                    .withPropertyIfNotNull(NAME_KEY, ncbiTaxonomy.ncbiTaxonomyName)
                    .withPropertyIfNotNull(CVTERM_ID_KEY, ncbiTaxonomy.ncbiTaxonomyCvtermId).build().getId();
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

        final Node modelNode = graph.findNode(AMR_MODEL_LABEL, MODEL_ID_KEY, entry.modelId);
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

    /**
     * Wires the dependency edges of the meta-model types. Runs after all model nodes have been created, so a referenced
     * model is always present regardless of iteration order.
     */
    private void exportModelDependencies(final Graph graph) {
        if (dataSource.model_entries == null)
            return;
        for (final CARD_Model entry : dataSource.model_entries)
            exportModelDependencyLinks(graph, entry);
    }

    private void exportModelDependencyLinks(final Graph graph, final CARD_Model entry) {
        if (entry == null || StringUtils.isBlank(entry.modelId) || entry.modelParam == null)
            return;

        final Node modelNode = graph.findNode(AMR_MODEL_LABEL, MODEL_ID_KEY, entry.modelId);
        if (modelNode == null)
            return;

        for (final CARD_Model.ModelParam param : entry.modelParam.values()) {
            if (param == null || param.paramTypeId == null)
                continue;
            if (PARAM_TYPE_ID_GENE_ORDER.equals(param.paramTypeId))
                exportGeneClusterLinks(graph, modelNode, param.paramValue);
            else if (PARAM_TYPE_ID_EFFLUX_COMPONENTS.equals(param.paramTypeId))
                exportEffluxComponentLinks(graph, modelNode, param.paramValue);
        }
    }

    private void exportGeneClusterLinks(final Graph graph, final Node modelNode, final Object paramValue) {
        final Set<String> geneOrders = new LinkedHashSet<>();
        collectStringValues(paramValue, geneOrders);
        for (final String geneOrder : geneOrders) {
            for (final String token : StringUtils.split(geneOrder, ',')) {
                final String[] parts = StringUtils.split(token.strip(), ":", 2);
                if (parts.length != 2)
                    continue;
                final String edgeLabel = geneTypeEdgeLabel(parts[0].strip());
                if (edgeLabel == null) {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Skipping unknown gene cluster gene type in token '{}'", token);
                    continue;
                }
                linkToModel(graph, modelNode, ARO_ID_KEY, parts[1].strip(), edgeLabel);
            }
        }
    }

    private void exportEffluxComponentLinks(final Graph graph, final Node modelNode, final Object paramValue) {
        final Set<String> componentLists = new LinkedHashSet<>();
        collectStringValues(paramValue, componentLists);
        for (final String componentList : componentLists)
            for (final String componentModelId : StringUtils.split(componentList, ','))
                linkToModel(graph, modelNode, MODEL_ID_KEY, componentModelId.strip(), HAS_COMPONENT_LABEL);
    }

    private void linkToModel(final Graph graph, final Node modelNode, final String targetKey, final String targetValue,
                             final String edgeLabel) {
        if (StringUtils.isBlank(targetValue))
            return;
        final Node targetNode = graph.findNode(AMR_MODEL_LABEL, targetKey, targetValue);
        if (targetNode == null) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Referenced model {}={} not found, skipping {} edge", targetKey, targetValue, edgeLabel);
            return;
        }
        if (targetNode.getId() == modelNode.getId())
            return;
        if (!graph.containsEdge(edgeLabel, modelNode.getId(), targetNode.getId()))
            graph.addEdge(modelNode.getId(), targetNode.getId(), edgeLabel);
    }

    private String geneTypeEdgeLabel(final String geneType) {
        switch (geneType) {
            case "C":
                return HAS_CORE_GENE_LABEL;
            case "R":
                return HAS_REGULATORY_GENE_LABEL;
            case "A":
                return HAS_ACCESSORY_GENE_LABEL;
            default:
                return null;
        }
    }

    /**
     * Coerces a polymorphic {@code model_param} value into its contained string values. Bit-score-style values are plain
     * strings; snp / gene-order / efflux-component values are maps keyed by an arbitrary parameter instance id.
     */
    private void collectStringValues(final Object value, final Set<String> target) {
        if (value instanceof String) {
            if (StringUtils.isNotBlank((String) value))
                target.add((String) value);
        } else if (value instanceof Map<?, ?>) {
            for (final Object mapValue : ((Map<?, ?>) value).values())
                if (mapValue instanceof String && StringUtils.isNotBlank((String) mapValue))
                    target.add((String) mapValue);
        }
    }
}