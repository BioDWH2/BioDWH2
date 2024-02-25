package de.unibi.agbi.biodwh2.uniprot.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;
import de.unibi.agbi.biodwh2.uniprot.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.groupingBy;

public class UniProtGraphExporter extends GraphExporter<UniProtDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(UniProtGraphExporter.class);
    private static final Map<String, Long> dbReferenceCitationNodeIdMap = new HashMap<>();

    public UniProtGraphExporter(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        dbReferenceCitationNodeIdMap.clear();
        graph.addIndex(IndexDescription.forNode("Organism", "id", IndexDescription.Type.UNIQUE));
        final File filePath = dataSource.resolveSourceFilePath(workspace, UniProtUpdater.HUMAN_SPROT_FILE_NAME)
                                        .toFile();
        if (!filePath.exists())
            throw new ExporterException("Failed to parse the file '" + UniProtUpdater.HUMAN_SPROT_FILE_NAME + "'");
        try (final GZIPInputStream zipStream = openZipInputStream(filePath)) {
            final XmlMapper xmlMapper = new XmlMapper();
            final FromXmlParser parser = FileUtils.createXmlParser(zipStream, xmlMapper);
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

    private void exportEntry(final Graph graph, final Entry entry) throws ExporterException {
        final NodeBuilder builder = graph.buildNode().withLabel("Protein");
        builder.withPropertyIfNotNull("dataset", entry.dataset);
        builder.withPropertyIfNotNull("created", entry.created);
        builder.withPropertyIfNotNull("modified", entry.modified);
        builder.withPropertyIfNotNull("version", entry.version);
        builder.withProperty("names", entry.name.toArray(new String[0]));
        builder.withProperty("accessions", entry.accession.toArray(new String[0]));
        if (entry.sequence != null) {
            builder.withPropertyIfNotNull("sequence", entry.sequence.value);
            builder.withPropertyIfNotNull("sequence_length", entry.sequence.length);
            builder.withPropertyIfNotNull("sequence_mass", entry.sequence.mass);
            builder.withPropertyIfNotNull("sequence_checksum", entry.sequence.checksum);
            builder.withPropertyIfNotNull("sequence_modified", entry.sequence.modified);
            builder.withPropertyIfNotNull("sequence_version", entry.sequence.version);
            builder.withPropertyIfNotNull("sequence_precursor", entry.sequence.precursor);
            builder.withPropertyIfNotNull("sequence_fragment", entry.sequence.fragment);
        }
        if (entry.proteinExistence != null)
            builder.withPropertyIfNotNull("existence", entry.proteinExistence.type);
        if (entry.keyword != null) {
            builder.withProperty("keywords", entry.keyword.stream().map((k) -> k.value).toArray(String[]::new));
            builder.withProperty("keyword_ids", entry.keyword.stream().map((k) -> k.id).toArray(String[]::new));
            // TODO: evidence
            for (final Keyword keyword : entry.keyword)
                if (keyword.evidence != null && keyword.evidence.length() > 0)
                    LOGGER.warn("Evidence for keyword '" + keyword.value + "' (" + keyword.id + ") not exported");
        }
        final Node node = builder.build();
        if (entry.gene != null) {
            for (final Gene gene : entry.gene) {
                // TODO
            }
        }
        if (entry.geneLocation != null) {
            for (final GeneLocation location : entry.geneLocation) {
                // TODO: in swiss-prot human only type is ever set and always "mitochondrion"
                // LOGGER.info(location.type + ", " + location.evidence + ", " + location.name);
            }
        }
        // TODO: Protein entry.protein
        // TODO: List<Organism> entry.organismHost
        // TODO: List<Comment> entry.comment
        // TODO: List<DbReference> entry.dbReference
        // TODO: List<Feature> entry.feature
        // TODO: List<Evidence> entry.evidence
        graph.addEdge(node, getOrCreateOrganism(graph, entry.organism), "HAS_SOURCE");
        for (final Reference reference : entry.reference) {
            final long citationNodeId = getOrCreateCitation(graph, reference.citation);
            final String[] scopes = reference.scope != null ? reference.scope.toArray(new String[0]) : new String[0];
            // TODO: reference.source
            graph.addEdge(node, citationNodeId, "REFERENCES", "key", Integer.parseInt(reference.key), "scopes", scopes);
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
        Map<String, List<OrganismName>> namesPerType = organism.name.stream().collect(groupingBy((o) -> o.type));
        for (final String nameType : namesPerType.keySet())
            builder.withProperty(nameType + "_names", namesPerType.get(nameType).stream().map((n) -> n.value)
                                                                  .toArray(String[]::new));
        ncbiId.ifPresent(r -> builder.withProperty("id", r.id));
        if (organism.lineage != null && organism.lineage.taxon != null && organism.lineage.taxon.size() > 0)
            builder.withProperty("lineage", organism.lineage.taxon.toArray(new String[0]));
        // TODO: evidence, molecule, property
        final String[] dbReferences = organism.dbReference != null ? organism.dbReference.stream().map(
                (r) -> r.type + ':' + r.id).toArray(String[]::new) : new String[0];
        builder.withProperty("db_references", dbReferences);
        return builder.build().getId();
    }

    /**
     * unpublished observations, patent, online journal article, thesis, book, journal article, submission
     */
    private long getOrCreateCitation(final Graph graph, final Citation citation) {
        // TODO: evidence, molecule, property
        final String[] dbReferences = citation.dbReference != null ? citation.dbReference.stream().map(
                (r) -> r.type + ':' + r.id).toArray(String[]::new) : new String[0];
        for (final String reference : dbReferences)
            if (dbReferenceCitationNodeIdMap.containsKey(reference))
                return dbReferenceCitationNodeIdMap.get(reference);
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
        if (citation.first != null && citation.last != null)
            builder.withProperty("pages", citation.first + '-' + citation.last);
        else if (citation.first != null)
            builder.withProperty("pages", citation.first);
        else if (citation.last != null)
            builder.withProperty("pages", citation.last);
        final String[] authors = nameListToArray(citation.authorList);
        if (authors.length > 0)
            builder.withPropertyIfNotNull("authors", authors);
        final String[] editors = nameListToArray(citation.editorList);
        if (editors.length > 0)
            builder.withPropertyIfNotNull("editors", editors);
        builder.withProperty("db_references", dbReferences);
        final long nodeId = builder.build().getId();
        for (final String reference : dbReferences) {
            final Long existingNodeId = dbReferenceCitationNodeIdMap.get(reference);
            if (existingNodeId != null && existingNodeId != nodeId) {
                LOGGER.warn("DBReference '" + reference + "' already exists for node id " + existingNodeId +
                            ", now found in node id " + nodeId);
            }
            dbReferenceCitationNodeIdMap.put(reference, nodeId);
        }
        return nodeId;
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
