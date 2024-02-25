package de.unibi.agbi.biodwh2.trrust.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.trrust.TRRUSTDataSource;
import de.unibi.agbi.biodwh2.trrust.model.Annotation;
import de.unibi.agbi.biodwh2.trrust.model.Collection;
import de.unibi.agbi.biodwh2.trrust.model.Infon;
import de.unibi.agbi.biodwh2.trrust.model.Passage;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class TRRUSTGraphExporter extends GraphExporter<TRRUSTDataSource> {
    static final String TRANSCRIPTION_FACTOR_LABEL = "TranscriptionFactor";
    static final String GENE_LABEL = "Gene";
    static final String GENE_ID_KEY = "ncbi_gene_id";

    public TRRUSTGraphExporter(final TRRUSTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(TRANSCRIPTION_FACTOR_LABEL, GENE_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, GENE_ID_KEY, IndexDescription.Type.UNIQUE));
        try {
            if (speciesFilter.isSpeciesAllowed(SpeciesLookup.HOMO_SAPIENS.ncbiTaxId))
                exportCollection(graph, loadXmlFile(workspace, TRRUSTUpdater.HUMAN_FILE_NAME));
            if (speciesFilter.isSpeciesAllowed(SpeciesLookup.MUS_MUSCULUS.ncbiTaxId))
                exportCollection(graph, loadXmlFile(workspace, TRRUSTUpdater.MOUSE_FILE_NAME));
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private Collection loadXmlFile(final Workspace workspace, final String fileName) throws IOException {
        final var filePath = dataSource.resolveSourceFilePath(workspace, fileName).toFile();
        final FileInputStream inputStream = new FileInputStream(filePath);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        final XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(bufferedInputStream, Collection.class);
    }

    private void exportCollection(final Graph graph, final Collection collection) {
        for (final Passage passage : collection.document.passage)
            exportPassage(graph, passage);
    }

    private void exportPassage(final Graph graph, final Passage passage) {
        final Integer[] pmids = Arrays.stream(StringUtils.split(passage.infon.value, ';')).map(
                (s) -> Integer.valueOf(StringUtils.stripEnd(s, "e"))).toArray(Integer[]::new);
        final Node tfNode = getOrCreateNodeFromAnnotationId(graph, passage, "G1", TRANSCRIPTION_FACTOR_LABEL);
        final Node targetNode = getOrCreateNodeFromAnnotationId(graph, passage, "G2", GENE_LABEL);
        if (tfNode != null && targetNode != null) {
            final Annotation annotationE1 = getAnnotationWithId(passage, "E1");
            if (annotationE1 != null) {
                final String annotationValue = annotationE1.text;
                graph.addEdge(tfNode, targetNode, "REGULATES", "mode", annotationValue, "pmids", pmids);
            }
        }
    }

    private Node getOrCreateNodeFromAnnotationId(final Graph graph, final Passage passage, final String id,
                                                 final String label) {
        final Annotation annotation = getAnnotationWithId(passage, id);
        if (annotation == null)
            return null;
        final Optional<Infon> geneIdInfon = annotation.infon.stream().filter((i) -> "GeneID".equals(i.key)).findFirst();
        if (!geneIdInfon.isPresent())
            return null;
        final Integer tfGeneId = Integer.parseInt(geneIdInfon.get().value);
        Node node = graph.findNode(label, GENE_ID_KEY, tfGeneId);
        if (node == null)
            node = graph.addNode(label, GENE_ID_KEY, tfGeneId, "name", annotation.text);
        return node;
    }

    private Annotation getAnnotationWithId(final Passage passage, final String id) {
        for (final Annotation annotation : passage.annotation)
            if (id.equals(annotation.id))
                return annotation;
        return null;
    }
}
