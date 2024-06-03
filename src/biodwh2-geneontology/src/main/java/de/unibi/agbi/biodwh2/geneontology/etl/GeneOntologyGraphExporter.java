package de.unibi.agbi.biodwh2.geneontology.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyAnnotationsDataSource;
import de.unibi.agbi.biodwh2.geneontology.model.GAFEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;

public class GeneOntologyGraphExporter extends GraphExporter<GeneOntologyAnnotationsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(GeneOntologyGraphExporter.class);
    static final String DB_OBJECT_LABEL = "DBObject";

    public GeneOntologyGraphExporter(final GeneOntologyAnnotationsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DB_OBJECT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        return exportAnnotations(workspace, graph);
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
        try (final MappingIterator<GAFEntry> entries = FileUtils.openGzipTsv(workspace, dataSource, fileName,
                                                                             GAFEntry.class)) {
            while (entries.hasNext()) {
                final GAFEntry entry = entries.next();
                if (entry.db.charAt(0) != '!')
                    exportAnnotation(graph, entry);
            }
        }
    }

    private void exportAnnotation(final Graph graph, final GAFEntry entry) {
        final Node termNode = graph.findNode("Term", ID_KEY, entry.goId);
        // If referencing an obsolete term excluded via config file, just skip this annotation
        if (termNode == null)
            return;
        final Node databaseObject = getOrCreateDatabaseObject(graph, entry);
        final String[] qualifierParts = StringUtils.split(entry.qualifier, '|');
        final String qualifier;
        if (qualifierParts.length == 2)
            qualifier = qualifierParts[0].toUpperCase(Locale.US) + "_" + qualifierParts[1].toUpperCase(Locale.US);
        else
            qualifier = qualifierParts[0].toUpperCase(Locale.US);
        final EdgeBuilder builder = graph.buildEdge().fromNode(databaseObject).toNode(termNode).withLabel(qualifier);
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
        final String id = entry.geneProductFormId != null ? entry.geneProductFormId : entry.db + ':' + entry.dbObjectId;
        Node node = graph.findNode(DB_OBJECT_LABEL, ID_KEY, id);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(DB_OBJECT_LABEL);
            builder.withProperty(ID_KEY, id);
            builder.withProperty("db", entry.db);
            builder.withProperty("db_object_id", entry.dbObjectId);
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
