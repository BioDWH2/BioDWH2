package de.unibi.agbi.biodwh2.interpro.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.interpro.InterProDataSource;
import de.unibi.agbi.biodwh2.interpro.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class InterProGraphExporter extends GraphExporter<InterProDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(InterProGraphExporter.class);
    private static final String CLASSIFICATION_LABEL = "Classification";
    static final String PUBLICATION_LABEL = "Publication";
    static final String DOMAIN_LABEL = "Domain";

    public InterProGraphExporter(final InterProDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DOMAIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ActiveSite", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("BindingSite", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ConservedSite", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Family", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("HomologousSuperfamily", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("PTM", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Repeat", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CLASSIFICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        try {
            exportInterProDB(workspace, graph);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportInterProDB(final Workspace workspace, final Graph graph) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Loading InterPro " + InterProUpdater.FILE_NAME + "...");
        final InterproDB db = loadInterProDB(workspace);
        exportDBInfo(graph, db);
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
    }

    private InterproDB loadInterProDB(final Workspace workspace) throws IOException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, InterProUpdater.FILE_NAME);
        final FileInputStream inputStream = new FileInputStream(filePath);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        final GZIPInputStream zipStream = new GZIPInputStream(bufferedInputStream);
        final XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(zipStream, InterproDB.class);
    }

    private void exportDBInfo(final Graph graph, final InterproDB db) {
        for (final DBInfo info : db.release.dbInfo)
            graph.addNode("DBInfo", "name", info.dbName, "version", info.version, "entry_count", info.entryCount,
                          "file_date", info.fileDate);
    }

    private void exportEntry(final Graph graph, final Interpro entry, final Map<String, Long> idToNodeIdMap) {
        // taxonomy_distribution is currently ignored, as all entries only contain "root" with varying protein count
        final NodeBuilder builder = graph.buildNode().withLabel(getLabelForEntryType(entry.type));
        builder.withProperty(ID_KEY, entry.id);
        builder.withPropertyIfNotNull("short_name", entry.shortName);
        builder.withPropertyIfNotNull("protein_count", entry.proteinCount);
        builder.withPropertyIfNotNull("name", entry.name);
        // builder.withPropertyIfNotNull("abstract", entry._abstract);
        if (entry.structureDbLinks != null && entry.structureDbLinks.size() > 0) {
            final String[] structureDbLinks = new String[entry.structureDbLinks.size()];
            for (int i = 0; i < entry.structureDbLinks.size(); i++) {
                final DBXref xref = entry.structureDbLinks.get(i);
                structureDbLinks[i] = xref.db + ':' + xref.dbKey;
            }
            builder.withProperty("structure_db_links", structureDbLinks);
        }
        if (entry.externalDocList != null && entry.externalDocList.size() > 0) {
            final String[] externalDocList = new String[entry.externalDocList.size()];
            for (int i = 0; i < entry.externalDocList.size(); i++) {
                final DBXref xref = entry.externalDocList.get(i);
                externalDocList[i] = xref.db + ':' + xref.dbKey;
            }
            builder.withProperty("external_docs", externalDocList);
        }
        if (entry.keySpecies != null && entry.keySpecies.size() > 0) {
            final String[] keySpeciesNames = new String[entry.keySpecies.size()];
            final Integer[] keySpeciesNCBITaxIds = new Integer[entry.keySpecies.size()];
            final Integer[] keySpeciesProteinCounts = new Integer[entry.keySpecies.size()];
            for (int i = 0; i < entry.keySpecies.size(); i++) {
                final TaxonData taxon = entry.keySpecies.get(i);
                final SpeciesLookup.Entry taxonEntry = SpeciesLookup.getByScientificName(taxon.name);
                keySpeciesNames[i] = taxon.name;
                keySpeciesNCBITaxIds[i] = taxonEntry != null ? taxonEntry.ncbiTaxId : null;
                keySpeciesProteinCounts[i] = taxon.proteinsCount;
            }
            builder.withProperty("key_species", keySpeciesNames);
            builder.withProperty("key_species_ncbi_taxids", keySpeciesNCBITaxIds);
            builder.withProperty("key_species_protein_counts", keySpeciesProteinCounts);
        }
        if (entry.memberList != null && entry.memberList.size() > 0) {
            final String[] membersDbLinks = new String[entry.memberList.size()];
            final String[] membersNames = new String[entry.memberList.size()];
            final int[] membersProteinCounts = new int[entry.memberList.size()];
            for (int i = 0; i < entry.memberList.size(); i++) {
                final DBXref xref = entry.memberList.get(i);
                membersDbLinks[i] = xref.db + ':' + xref.dbKey;
                membersNames[i] = xref.name;
                membersProteinCounts[i] = xref.proteinCount;
            }
            builder.withProperty("members", membersDbLinks);
            builder.withProperty("members_names", membersNames);
            builder.withProperty("members_protein_counts", membersProteinCounts);
        }
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

    /**
     * Convert an InterPro type such as "Homologous_superfamily" to a graph label "HomologousSuperfamily"
     */
    private String getLabelForEntryType(String type) {
        int index = 0;
        while ((index = type.indexOf("_", index)) != -1)
            type = type.substring(0, index) + Character.toUpperCase(type.charAt(index + 1)) + type.substring(index + 2);
        return type;
    }

    private Node getOrCreatePublication(final Graph graph, final Publication publication) {
        Node node = graph.findNode(PUBLICATION_LABEL, ID_KEY, publication.id);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(PUBLICATION_LABEL);
            builder.withProperty(ID_KEY, publication.id);
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
        Node node = graph.findNode(CLASSIFICATION_LABEL, ID_KEY, classification.id);
        if (node == null) {
            node = graph.addNode(CLASSIFICATION_LABEL, ID_KEY, classification.id, "type", classification.classType,
                                 "category", classification.category, "description", classification.description);
        }
        return node;
    }
}
