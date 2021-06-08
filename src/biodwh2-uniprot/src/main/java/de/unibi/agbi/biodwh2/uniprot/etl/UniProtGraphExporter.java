package de.unibi.agbi.biodwh2.uniprot.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;
import de.unibi.agbi.biodwh2.uniprot.model.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class UniProtGraphExporter extends GraphExporter<UniProtDataSource> {
    public UniProtGraphExporter(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Organism", "id", IndexDescription.Type.UNIQUE));
        final String filePath = dataSource.resolveSourceFilePath(workspace, "uniprot_sprot_human.xml.gz");
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to parse the file 'uniprot_sprot_human.xml.gz'");
        try {
            final GZIPInputStream zipStream = openZipInputStream(zipFile);
            final XmlMapper xmlMapper = new XmlMapper();
            final FromXmlParser parser = createXmlParser(zipStream, xmlMapper);
            // Skip the first structure token which is the root UniProt node
            //noinspection UnusedAssignment
            JsonToken token = parser.nextToken();
            while ((token = parser.nextToken()) != null)
                if (token.isStructStart())
                    exportEntry(graph, xmlMapper.readValue(parser, Entry.class));
        } catch (IOException | XMLStreamException e) {
            throw new ExporterFormatException(e);
        }
        return false;
    }

    private static GZIPInputStream openZipInputStream(final File file) throws IOException {
        final FileInputStream inputStream = new FileInputStream(file);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return new GZIPInputStream(bufferedInputStream);
    }

    private FromXmlParser createXmlParser(final InputStream stream,
                                          final XmlMapper xmlMapper) throws IOException, XMLStreamException {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(stream,
                                                                                   StandardCharsets.UTF_8.name());
        return xmlMapper.getFactory().createParser(streamReader);
    }

    private void exportEntry(final Graph graph, final Entry entry) throws ExporterException {
        final NodeBuilder builder = graph.buildNode().withLabel("Protein");
        builder.withPropertyIfNotNull("dataset", entry.dataset);
        builder.withPropertyIfNotNull("created", entry.created);
        builder.withPropertyIfNotNull("modified", entry.modified);
        builder.withPropertyIfNotNull("version", entry.version);
        builder.withPropertyIfNotNull("names", entry.name);
        builder.withPropertyIfNotNull("accessions", entry.accession);
        // TODO: protein, gene
        final Node node = builder.build();
        graph.addEdge(node, getOrCreateOrganism(graph, entry.organism), "HAS_SOURCE");
        final Map<String, Long> referenceKeyCitationNodeIdMap = new HashMap<>();
        for (final Reference reference : entry.reference) {
            final long citationNodeId = getOrCreateCitation(graph, reference.citation);
            referenceKeyCitationNodeIdMap.put(reference.key, citationNodeId);
            // TODO: scope, source
            graph.addEdge(node, citationNodeId, "REFERENCES");
        }
    }

    private long getOrCreateOrganism(final Graph graph, final Organism organism) {
        final Optional<DbReference> ncbiId = organism.dbReference.stream().filter(r -> "NCBI Taxonomy".equals(r.type))
                                                                 .findFirst();
        if (ncbiId.isPresent()) {
            final Node node = graph.findNode("Organism", "id", ncbiId.get().id);
            if (node != null)
                return node.getId();
        }
        // TODO: evidence
        final NodeBuilder builder = graph.buildNode().withLabel("Organism");
        for (final OrganismName name : organism.name) {
            // TODO: duplicate types
            builder.withProperty(name.type + "_name", name.value);
        }
        ncbiId.ifPresent(r -> builder.withProperty("id", r.id));
        for (final DbReference reference : organism.dbReference) {
            // TODO: evidence, molecule, property
            // TODO: add non ncbi ids
        }
        if (organism.lineage != null && organism.lineage.taxon != null && organism.lineage.taxon.size() > 0)
            builder.withProperty("lineage", organism.lineage.taxon.toArray(new String[0]));
        return builder.build().getId();
    }

    /**
     * unpublished observations, patent, online journal article, thesis, book, journal article, submission
     */
    private long getOrCreateCitation(final Graph graph, final Citation citation) {
        // TODO: dbreference
        final NodeBuilder builder = graph.buildNode().withLabel("Citation");
        builder.withPropertyIfNotNull("title", citation.title);
        builder.withPropertyIfNotNull("type", citation.type);
        builder.withPropertyIfNotNull("date", citation.date);
        builder.withPropertyIfNotNull("journal", citation.name);
        builder.withPropertyIfNotNull("publisher", citation.publisher);
        builder.withPropertyIfNotNull("city", citation.city);
        builder.withPropertyIfNotNull("number", citation.number);
        builder.withPropertyIfNotNull("institute", citation.institute);
        builder.withPropertyIfNotNull("locator", citation.locator);
        builder.withPropertyIfNotNull("country", citation.country);
        builder.withPropertyIfNotNull("volume", citation.volume);
        builder.withPropertyIfNotNull("pages", citation.first + '-' + citation.last);
        final String[] authors = nameListToArray(citation.authorList);
        if (authors.length > 0)
            builder.withPropertyIfNotNull("authors", authors);
        final String[] editors = nameListToArray(citation.editorList);
        if (editors.length > 0)
            builder.withPropertyIfNotNull("editors", editors);
        return builder.build().getId();
    }

    private String[] nameListToArray(final NameList list) {
        final List<String> names = new ArrayList<>();
        if (list != null) {
            if (list.person != null)
                names.addAll(list.person.stream().map(a -> a.name).collect(Collectors.toList()));
            if (list.consortium != null)
                names.addAll(list.consortium.stream().map(a -> a.name).collect(Collectors.toList()));
        }
        return names.toArray(new String[0]);
    }
}
