package de.unibi.agbi.biodwh2.interpro.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.interpro.InterProDataSource;
import de.unibi.agbi.biodwh2.interpro.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class InterProGraphExporter extends GraphExporter<InterProDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterProGraphExporter.class);
    private static final String CLASSIFICATION_LABEL = "Classification";
    static final String PUBLICATION_LABEL = "Publication";

    public InterProGraphExporter(final InterProDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Domain", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ActiveSite", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("BindingSite", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ConservedSite", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Family", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("HomologousSuperfamily", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("PTM", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Repeat", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CLASSIFICATION_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Loading InterPro " + InterProUpdater.FILE_NAME + "...");
            final InterproDB db = loadInterProDB(workspace);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting entries...");
            final Map<String, Long> idToNodeIdMap = new HashMap<>();
            for (final Interpro entry : db.interpro)
                exportEntry(graph, entry, idToNodeIdMap);
            // Export the entry hierarchy
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting entry hierarchy...");
            for (final Interpro entry : db.interpro)
                if (entry.parentList != null)
                    for (final RelRef parentRef : entry.parentList)
                        graph.addEdge(idToNodeIdMap.get(entry.id), idToNodeIdMap.get(parentRef.iprRef), "CHILD_OF");
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private InterproDB loadInterProDB(final Workspace workspace) throws IOException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, InterProUpdater.FILE_NAME);
        final FileInputStream inputStream = new FileInputStream(filePath);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        final GZIPInputStream zipStream = new GZIPInputStream(bufferedInputStream);
        final XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(zipStream, InterproDB.class);
    }

    private void exportEntry(final Graph graph, final Interpro entry, final Map<String, Long> idToNodeIdMap) {
        // taxonomy_distribution is currently ignored, as all entries only contain "root" with varying protein count
        String[] externalDocList = null;
        if (entry.externalDocList != null) {
            externalDocList = new String[entry.externalDocList.size()];
            for (int i = 0; i < entry.externalDocList.size(); i++) {
                final DBXref xref = entry.externalDocList.get(i);
                externalDocList[i] = xref.db + ':' + xref.dbKey;
            }
        }
        String[] structureDbLinks = null;
        if (entry.structureDbLinks != null) {
            structureDbLinks = new String[entry.structureDbLinks.size()];
            for (int i = 0; i < entry.structureDbLinks.size(); i++) {
                final DBXref xref = entry.structureDbLinks.get(i);
                structureDbLinks[i] = xref.db + ':' + xref.dbKey;
            }
        }
        String[] keySpeciesNames = null;
        int[] keySpeciesProteinCounts = null;
        if (entry.keySpecies != null) {
            keySpeciesNames = new String[entry.keySpecies.size()];
            keySpeciesProteinCounts = new int[entry.keySpecies.size()];
            for (int i = 0; i < entry.keySpecies.size(); i++) {
                final TaxonData taxon = entry.keySpecies.get(i);
                keySpeciesNames[i] = taxon.name;
                keySpeciesProteinCounts[i] = taxon.proteinsCount;
            }
        }
        String[] membersDbLinks = null;
        String[] membersNames = null;
        int[] membersProteinCounts = null;
        if (entry.memberList != null) {
            membersDbLinks = new String[entry.memberList.size()];
            membersNames = new String[entry.memberList.size()];
            membersProteinCounts = new int[entry.memberList.size()];
            for (int i = 0; i < entry.memberList.size(); i++) {
                final DBXref xref = entry.memberList.get(i);
                membersDbLinks[i] = xref.db + ':' + xref.dbKey;
                membersNames[i] = xref.name;
                membersProteinCounts[i] = xref.proteinCount;
            }
        }
        final NodeBuilder builder = graph.buildNode().withLabel(getLabelForEntryType(entry.type));
        builder.withProperty("id", entry.id);
        builder.withPropertyIfNotNull("short_name", entry.shortName);
        builder.withPropertyIfNotNull("protein_count", entry.proteinCount);
        builder.withPropertyIfNotNull("name", entry.name);
        builder.withPropertyIfNotNull("structure_db_links", structureDbLinks);
        builder.withPropertyIfNotNull("external_docs", externalDocList);
        builder.withPropertyIfNotNull("key_species", keySpeciesNames);
        builder.withPropertyIfNotNull("key_species_protein_counts", keySpeciesProteinCounts);
        builder.withPropertyIfNotNull("members", membersDbLinks);
        builder.withPropertyIfNotNull("members_names", membersNames);
        builder.withPropertyIfNotNull("members_protein_counts", membersProteinCounts);
        // builder.withPropertyIfNotNull("abstract", entry._abstract);
        final Node node = builder.build();
        idToNodeIdMap.put(entry.id, node.getId());
        if (entry.pubList != null) {
            for (final Publication publication : entry.pubList) {
                final Node publicationNode = getOrCreatePublication(graph, publication);
                graph.addEdge(node, publicationNode, "HAS_REFERENCE");
            }
        }
        if (entry.classList != null) {
            for (final Classification classification : entry.classList) {
                final Node classificationNode = getOrCreateClassification(graph, classification);
                graph.addEdge(node, classificationNode, "HAS_CLASS");
            }
        }
    }

    private String getLabelForEntryType(String type) {
        int index = 0;
        while ((index = type.indexOf("_", index)) != -1) {
            type = type.substring(0, index) + ("" + type.charAt(index + 1)).toUpperCase(Locale.ROOT) + type.substring(
                    index + 2);
        }
        return type;
    }

    private Node getOrCreatePublication(final Graph graph, final Publication publication) {
        Node node = graph.findNode(PUBLICATION_LABEL, "id", publication.id);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(PUBLICATION_LABEL);
            builder.withProperty("id", publication.id);
            builder.withPropertyIfNotNull("authors", publication.authorList);
            builder.withPropertyIfNotNull("title", publication.title);
            builder.withPropertyIfNotNull("book_title", publication.bookTitle);
            builder.withPropertyIfNotNull("journal", publication.journal);
            builder.withPropertyIfNotNull("year", publication.year);
            builder.withPropertyIfNotNull("url", publication.url);
            builder.withPropertyIfNotNull("doi_url", publication.doiUrl);
            if (publication.location != null) {
                builder.withPropertyIfNotNull("pages", publication.location.pages);
                builder.withPropertyIfNotNull("volume", publication.location.volume);
                builder.withPropertyIfNotNull("issue", publication.location.issue);
            }
            if (publication.dbXref != null) {
                if ("PUBMED".equalsIgnoreCase(publication.dbXref.db)) {
                    builder.withPropertyIfNotNull("pmid", Integer.parseInt(publication.dbXref.dbKey));
                } else {
                    builder.withPropertyIfNotNull("xref_db", publication.dbXref.db);
                    builder.withPropertyIfNotNull("xref_dbkey", publication.dbXref.dbKey);
                }
            }
            node = builder.build();
        }
        return node;
    }

    private Node getOrCreateClassification(final Graph graph, final Classification classification) {
        Node node = graph.findNode(CLASSIFICATION_LABEL, "id", classification.id);
        if (node == null) {
            node = graph.addNode(CLASSIFICATION_LABEL, "id", classification.id, "type", classification.classType,
                                 "category", classification.category, "description", classification.description);
        }
        return node;
    }
}
