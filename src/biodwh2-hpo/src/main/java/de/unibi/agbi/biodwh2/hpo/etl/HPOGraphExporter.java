package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.hpo.HPOAnnotationsDataSource;
import de.unibi.agbi.biodwh2.hpo.model.EvidenceCode;
import de.unibi.agbi.biodwh2.hpo.model.PhenotypeAnnotation;
import de.unibi.agbi.biodwh2.hpo.model.PhenotypeToGenesEntry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public final class HPOGraphExporter extends GraphExporter<HPOAnnotationsDataSource> {
    static final String GENE_LABEL = "Gene";
    static final String DISEASE_LABEL = "Disease";
    static final String ASSOCIATION_LABEL = "Association";
    static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";

    private boolean omimLicensed = false;

    public HPOGraphExporter(final HPOAnnotationsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        omimLicensed = dataSource.getBooleanProperty(workspace, "omimLicensed");
        exportAnnotations(workspace, graph);
        return true;
    }

    private void exportAnnotations(final Workspace workspace, final Graph graph) throws ExporterException {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HPOUpdater.ANNOTATIONS_FILE_NAME,
                                        PhenotypeAnnotation.class, (entry) -> exportPhenotypeAnnotation(graph, entry));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export HPO annotations", e);
        }
        final Map<Long, Map<Long, Long>> associationNodeMap = new HashMap<>();
        graph.beginEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, HPOUpdater.PHENOTYPE_TO_GENES_FILE_NAME,
                                        PhenotypeToGenesEntry.class,
                                        (entry) -> exportPhenotypeGeneAssociation(graph, entry, associationNodeMap));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export HPO annotations", e);
        }
        graph.endEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
    }

    private void exportPhenotypeAnnotation(final Graph graph, final PhenotypeAnnotation entry) {
        final Long termNodeId = getOrCreateOntologyProxyTerm(graph, entry.hpoId);
        if (termNodeId == null || !isPhenotypeAnnotationAllowed(entry))
            return;
        final Node diseaseNode = getOrCreateDiseaseNode(graph, entry.databaseId, entry.diseaseName);
        final EdgeBuilder builder = graph.buildEdge().fromNode(termNodeId).toNode(diseaseNode);
        builder.withLabel("NOT".equalsIgnoreCase(entry.qualifier) ? "NOT_ASSOCIATED_WITH" : ASSOCIATED_WITH_LABEL);
        builder.withPropertyIfNotNull("reference", entry.reference);
        builder.withPropertyIfNotNull("evidence", entry.evidence.name());
        builder.withPropertyIfNotNull("onset", entry.onset);
        builder.withPropertyIfNotNull("frequency", entry.frequency);
        builder.withPropertyIfNotNull("sex", entry.sex);
        builder.withPropertyIfNotNull("modifier", entry.modifier);
        builder.withPropertyIfNotNull("aspect", entry.aspect);
        builder.withPropertyIfNotNull("biocuration", StringUtils.split(entry.biocuration, ';'));
        builder.build();
    }

    private boolean isPhenotypeAnnotationAllowed(final PhenotypeAnnotation entry) {
        return entry.evidence != EvidenceCode.IEA || omimLicensed;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final String diseaseId, final String diseaseName) {
        Node node = graph.findNode(DISEASE_LABEL, ID_KEY, diseaseId);
        if (node == null)
            node = graph.addNode(DISEASE_LABEL, ID_KEY, diseaseId, "names",
                                 new HashSet<>(Collections.singletonList(diseaseName)));
        else if (diseaseName != null) {
            // Add name if not already added to the disease node
            Set<String> names = node.getProperty("names");
            if (names == null)
                names = new HashSet<>();
            if (!names.contains(diseaseName)) {
                names.add(diseaseName);
                node.setProperty("names", names);
                graph.update(node);
            }
        }
        return node;
    }

    private void exportPhenotypeGeneAssociation(final Graph graph, final PhenotypeToGenesEntry entry,
                                                final Map<Long, Map<Long, Long>> associationNodeMap) {
        final Long termNodeId = getOrCreateOntologyProxyTerm(graph, entry.hpoId);
        if (termNodeId == null)
            return;
        final Map<Long, Long> geneAssociationNodeMap = associationNodeMap.computeIfAbsent(termNodeId,
                                                                                          (hpoId) -> new HashMap<>());
        final Node geneNode = getOrCreateGeneNode(graph, entry.ncbiGeneId, entry.geneSymbol);
        Long associationNodeId = geneAssociationNodeMap.get(geneNode.getId());
        if (associationNodeId == null) {
            associationNodeId = graph.addNode(ASSOCIATION_LABEL).getId();
            geneAssociationNodeMap.put(geneNode.getId(), associationNodeId);
            graph.addEdge(termNodeId, associationNodeId, ASSOCIATED_WITH_LABEL);
            graph.addEdge(geneNode, associationNodeId, ASSOCIATED_WITH_LABEL);
        }
        if (StringUtils.isNotEmpty(entry.diseaseId)) {
            final Node diseaseNode = getOrCreateDiseaseNode(graph, entry.diseaseId, null);
            graph.addEdge(diseaseNode, associationNodeId, ASSOCIATED_WITH_LABEL);
        }
    }

    private Node getOrCreateGeneNode(final Graph graph, final Integer id, final String symbol) {
        Node node = graph.findNode(GENE_LABEL, ID_KEY, id);
        if (node == null)
            node = graph.addNode(GENE_LABEL, ID_KEY, id, "symbol", symbol);
        return node;
    }
}
