package de.unibi.agbi.biodwh2.uniprot.etl;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.groupingBy;

public class UniProtGraphExporter extends GraphExporter<UniProtDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(UniProtGraphExporter.class);
    private static final Map<String, Long> dbReferenceCitationNodeIdMap = new HashMap<>();
    static final String ORGANISM_LABEL = "Organism";
    static final String PROTEIN_LABEL = "Protein";
    static final String CITATION_LABEL = "Citation";

    public UniProtGraphExporter(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        dbReferenceCitationNodeIdMap.clear();
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final File filePath = dataSource.resolveSourceFilePath(workspace, UniProtUpdater.SPROT_FILE_NAME).toFile();
        if (!filePath.exists())
            throw new ExporterException("Failed to parse the file '" + UniProtUpdater.SPROT_FILE_NAME + "'");
        try (final GZIPInputStream stream = FileUtils.openGzip(filePath.toString())) {
            FileUtils.streamXmlList(stream, Entry.class, (protein -> exportEntry(graph, protein)));
        } catch (IOException | XMLStreamException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) throws ExporterException {
        final NodeBuilder builder = graph.buildNode().withLabel(PROTEIN_LABEL);
        builder.withPropertyIfNotNull("dataset", entry.dataset);
        builder.withPropertyIfNotNull("created", entry.created);
        builder.withPropertyIfNotNull("modified", entry.modified);
        builder.withPropertyIfNotNull("version", entry.version);
        builder.withProperty("names", entry.name.toArray(new String[0]));
        final var secondaryAccessions = entry.accession.stream().skip(1).toArray(String[]::new);
        builder.withProperty("accession", entry.accession.get(0));
        builder.withProperty("secondary_accessions", secondaryAccessions);
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
                if (keyword.evidence != null && !keyword.evidence.isEmpty())
                    LOGGER.warn("Evidence for keyword '{}' ({}) not exported", keyword.value, keyword.id);
        }
        if (entry.protein != null) {
            if (entry.protein.recommendedName != null) {
                final var recommendedName = entry.protein.recommendedName;
                // TODO: evidences
                if (recommendedName.fullName != null) {
                    builder.withPropertyIfNotNull("protein_recommended_name_full", recommendedName.fullName.value);
                }
                if (recommendedName.shortName != null && !recommendedName.shortName.isEmpty()) {
                    builder.withPropertyIfNotNull("protein_recommended_name_short",
                                                  recommendedName.shortName.stream().map((n) -> n.value)
                                                                           .toArray(String[]::new));
                }
                if (recommendedName.ecNumber != null && !recommendedName.ecNumber.isEmpty()) {
                    builder.withPropertyIfNotNull("protein_recommended_name_ec_numbers",
                                                  recommendedName.ecNumber.stream().map((ec) -> ec.value)
                                                                          .toArray(String[]::new));
                }
            }

            // TODO: List<Protein.AlternativeName> alternativeName
            // TODO: List<Protein.SubmittedName> submittedName
            // TODO: EvidencedString allergenName
            // TODO: EvidencedString biotechName
            // TODO: List<EvidencedString> cdAntigenName
            // TODO: List<EvidencedString> innName
            // TODO: List<Protein.Domain> domain
            // TODO: List<Protein.Component> component
        }
        // TODO: List<Organism> entry.organismHost
        // TODO: List<Comment> entry.comment
        // TODO: List<Evidence> entry.evidence
        if (entry.dbReference != null) {
            // TODO: evidence, molecule, property
            final String[] dbReferences = entry.dbReference.stream().map((r) -> r.type + ':' + r.id).toArray(
                    String[]::new);
            builder.withProperty("db_references", dbReferences);
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
        final Map<Integer, Long> referenceKeyNodeIdMap = new HashMap<>();
        for (final Reference reference : entry.reference) {
            final String[] scopes = reference.scope != null ? reference.scope.toArray(new String[0]) : new String[0];
            final int refKey = Integer.parseInt(reference.key);
            final var referenceNode = graph.addNode("Reference", "key", refKey, "scopes", scopes);
            referenceKeyNodeIdMap.put(refKey, referenceNode.getId());
            // TODO: reference.source
            // TODO: reference.evidence
            final long citationNodeId = getOrCreateCitation(graph, reference.citation);
            graph.addEdge(node, referenceNode, "REFERENCES");
            graph.addEdge(referenceNode, citationNodeId, "REFERENCES");
        }
        if (entry.feature != null) {
            for (final var feature : entry.feature) {
                // TODO:
                //  String original
                //  List<String> variation
                //  Ligand ligand
                //  LigandPart ligandPart
                //  String evidence
                final var featureBuilder = graph.buildNode().withLabel("Feature");
                featureBuilder.withPropertyIfNotNull(ID_KEY, feature.id);
                featureBuilder.withPropertyIfNotNull("description", feature.description);
                featureBuilder.withPropertyIfNotNull("type", feature.type);
                if (feature.location != null) {
                    featureBuilder.withPropertyIfNotNull("location_sequence", feature.location.sequence);
                    if (feature.location.position != null) {
                        featureBuilder.withPropertyIfNotNull("location_position", feature.location.position.position);
                        // TODO: status, evidence
                    }
                    if (feature.location.begin != null) {
                        featureBuilder.withPropertyIfNotNull("location_begin", feature.location.begin.position);
                        // TODO: status, evidence
                    }
                    if (feature.location.end != null) {
                        featureBuilder.withPropertyIfNotNull("location_end", feature.location.end.position);
                        // TODO: status, evidence
                    }
                }
                final var featureNode = featureBuilder.build();
                graph.addEdge(node, featureNode, "HAS_FEATURE");
                if (feature.ref != null) {
                    for (final String refKey : StringUtils.split(feature.ref, ' ')) {
                        final Long referenceNodeId = referenceKeyNodeIdMap.get(Integer.parseInt(refKey));
                        if (referenceNodeId != null)
                            graph.addEdge(featureNode, referenceNodeId, "REFERENCES");
                    }
                }
            }
        }
        graph.addEdge(node, getOrCreateOrganism(graph, entry.organism), "HAS_SOURCE");
    }

    private long getOrCreateOrganism(final Graph graph, final Organism organism) {
        final Optional<DbReference> ncbiId = organism.dbReference.stream().filter(r -> "NCBI Taxonomy".equals(r.type))
                                                                 .findFirst();
        if (ncbiId.isPresent()) {
            final Node node = graph.findNode(ORGANISM_LABEL, ID_KEY, ncbiId.get().id);
            if (node != null)
                return node.getId();
        }
        // TODO: evidence
        final NodeBuilder builder = graph.buildNode().withLabel(ORGANISM_LABEL);
        Map<String, List<OrganismName>> namesPerType = organism.name.stream().collect(groupingBy((o) -> o.type));
        for (final String nameType : namesPerType.keySet())
            builder.withProperty(nameType + "_names", namesPerType.get(nameType).stream().map((n) -> n.value)
                                                                  .toArray(String[]::new));
        ncbiId.ifPresent(r -> builder.withProperty(ID_KEY, r.id));
        if (organism.lineage != null && organism.lineage.taxon != null && !organism.lineage.taxon.isEmpty())
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
        final NodeBuilder builder = graph.buildNode().withLabel(CITATION_LABEL);
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
                LOGGER.warn("DBReference '{}' already exists for node id {}, now found in node id {}", reference,
                            existingNodeId, nodeId);
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
