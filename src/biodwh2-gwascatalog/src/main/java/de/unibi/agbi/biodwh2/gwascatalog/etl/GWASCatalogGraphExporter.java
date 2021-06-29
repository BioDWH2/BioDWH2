package de.unibi.agbi.biodwh2.gwascatalog.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.gwascatalog.GWASCatalogDataSource;
import de.unibi.agbi.biodwh2.gwascatalog.model.Study;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public final class GWASCatalogGraphExporter extends GraphExporter<GWASCatalogDataSource> {
    static final String PUBLICATION_LABEL = "Publication";
    static final String STUDY_LABEL = "Study";
    static final String TRAIT_LABEL = "Trait";

    public GWASCatalogGraphExporter(final GWASCatalogDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(STUDY_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TRAIT_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        try {
            final MappingIterator<Study> studies = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource,
                                                                                             GWASCatalogUpdater.STUDIES_FILE_NAME,
                                                                                             Study.class);
            exportStudies(graph, studies);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportStudies(final Graph graph, final MappingIterator<Study> studies) {
        while (studies.hasNext())
            exportStudy(graph, studies.next());
    }

    private void exportStudy(final Graph graph, final Study study) {
        final NodeBuilder builder = graph.buildNode().withLabel(STUDY_LABEL);
        builder.withProperty("id", study.studyAccession);
        builder.withProperty("date_added", study.dateAddedToCatalog);
        builder.withProperty("initial_sample_size", study.initialSampleSize);
        builder.withProperty("replication_sample_size", study.replicationSampleSize);
        builder.withProperty("association_count", study.associationCount);
        builder.withPropertyIfNotNull("genotyping_technology", study.genotypingTechnology);
        builder.withPropertyIfNotNull("platform", study.platform);
        builder.withPropertyIfNotNull("disease_or_trait", study.diseaseOrTrait);
        builder.withPropertyIfNotNull("mapped_traits", study.mappedTrait);
        final Node node = builder.build();
        final Node publicationNode = getOrCreatePublication(graph, study);
        graph.addEdge(node, publicationNode, "HAS_REFERENCE");
        // TODO: add names to traits when list separator is fixed
        if (study.mappedTraitUri != null) {
            final String[] traitUris = StringUtils.split(study.mappedTraitUri, ',');
            for (int i = 0; i < traitUris.length; i++) {
                final Node traitNode = getOrCreateTrait(graph, traitUris[i].trim());
                graph.addEdge(node, traitNode, "STUDIES");
            }
        }
    }

    private Node getOrCreatePublication(final Graph graph, final Study study) {
        Node node = graph.findNode(PUBLICATION_LABEL, "pmid", study.pubmedId);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(PUBLICATION_LABEL);
            builder.withProperty("pmid", study.pubmedId);
            builder.withProperty("first_author", study.firstAuthor);
            builder.withProperty("date_published", study.datePublished);
            builder.withProperty("journal", study.journal);
            builder.withProperty("title", study.studyTitle);
            node = builder.build();
        }
        return node;
    }

    private Node getOrCreateTrait(final Graph graph, final String id) {
        Node node = graph.findNode(TRAIT_LABEL, "id", id);
        if (node == null)
            node = graph.addNode(TRAIT_LABEL, "id", id);
        return node;
    }
}
