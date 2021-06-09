package de.unibi.agbi.biodwh2.geneontology.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;
import de.unibi.agbi.biodwh2.geneontology.model.GAFEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GeneOntologyGraphExporter extends OntologyGraphExporter<GeneOntologyDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneOntologyGraphExporter.class);
    private static final String DB_OBJECT_LABEL = "DBObject";

    public GeneOntologyGraphExporter(final GeneOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected String getOntologyFileName() {
        return "go.obo";
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DB_OBJECT_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        return super.exportGraph(workspace, graph) && exportAnnotations(workspace, graph);
    }

    private boolean exportAnnotations(final Workspace workspace, final Graph graph) throws ExporterException {
        try {
            exportAnnotationsFile(workspace, graph, GeneOntologyUpdater.GOA_HUMAN_FILE_NAME);
            exportAnnotationsFile(workspace, graph, GeneOntologyUpdater.GOA_HUMAN_COMPLEX_FILE_NAME);
            exportAnnotationsFile(workspace, graph, GeneOntologyUpdater.GOA_HUMAN_ISOFORM_FILE_NAME);
            exportAnnotationsFile(workspace, graph, GeneOntologyUpdater.GOA_HUMAN_RNA_FILE_NAME);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportAnnotationsFile(final Workspace workspace, final Graph graph,
                                       final String fileName) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting annotations file '" + fileName + "'...");
        for (final GAFEntry entry : getAnnotationReader(workspace, fileName))
            if (entry.db.charAt(0) != '!')
                exportAnnotation(graph, entry);
    }

    private Iterable<GAFEntry> getAnnotationReader(final Workspace workspace,
                                                   final String fileName) throws IOException {
        final MappingIterator<GAFEntry> entries = FileUtils.openGzipTsv(workspace, dataSource, fileName,
                                                                        GAFEntry.class);
        return () -> entries;
    }

    private void exportAnnotation(final Graph graph, final GAFEntry entry) {
        final Node termNode = graph.findNode("Term", ID_PROPERTY, entry.goId);
        // If referencing an obsolete term excluded via config file, just skip this annotation
        if (termNode == null)
            return;
        final Node databaseObject = getOrCreateDatabaseObject(graph, entry);
        final EdgeBuilder builder = graph.buildEdge().fromNode(databaseObject).toNode(termNode).withLabel(
                "HAS_ANNOTATION");
        builder.withProperty("qualifier", StringUtils.split(entry.qualifier, '|'));
        builder.withProperty("references", StringUtils.split(entry.dbReference, '|'));
        final String[] taxa = StringUtils.split(entry.taxon, '|');
        builder.withProperty("taxon", taxa[0]);
        if (taxa.length == 2)
            builder.withProperty("interaction_taxon", taxa[1]);
        builder.withProperty("aspect", entry.aspect);
        builder.withProperty("evidence_code", entry.evidenceCode);
        builder.withPropertyIfNotNull("with_or_from", entry.withOrFrom);
        builder.withProperty("date", entry.date);
        builder.withProperty("assigned_by", entry.assignedBy);
        builder.withPropertyIfNotNull("annotation_extensions", entry.annotationExtension);
        builder.build();
    }

    private Node getOrCreateDatabaseObject(final Graph graph, final GAFEntry entry) {
        final String id = entry.db + ':' + entry.dbObjectId;
        Node node = graph.findNode(DB_OBJECT_LABEL, ID_PROPERTY, id);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(DB_OBJECT_LABEL);
            builder.withProperty(ID_PROPERTY, id);
            builder.withProperty("symbol", entry.dbObjectSymbol);
            builder.withProperty("type", entry.dbObjectType);
            builder.withPropertyIfNotNull("name", entry.dbObjectName);
            builder.withPropertyIfNotNull("gene_product_form_id", entry.geneProductFormId);
            if (entry.dbObjectSynonym != null)
                builder.withProperty("synonyms", StringUtils.split(entry.dbObjectSynonym, '|'));
            node = builder.build();
        }
        return node;
    }
}
