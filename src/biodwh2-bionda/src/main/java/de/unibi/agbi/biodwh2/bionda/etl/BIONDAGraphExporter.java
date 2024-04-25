package de.unibi.agbi.biodwh2.bionda.etl;

import de.unibi.agbi.biodwh2.bionda.BIONDADataSource;
import de.unibi.agbi.biodwh2.bionda.model.Entry;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class BIONDAGraphExporter extends GraphExporter<BIONDADataSource> {
    static final String DISEASE_LABEL = "Disease";
    static final String BIOMARKER_LABEL = "Biomarker";
    static final String PUBLICATION_LABEL = "Publication";
    static final String ASSOCIATION_LABEL = "Association";
    static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";

    public BIONDAGraphExporter(final BIONDADataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(BIOMARKER_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "other_id", IndexDescription.Type.UNIQUE));
        graph.beginEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, BIONDAUpdater.FILE_NAME, Entry.class,
                                        (entry) -> exportEntry(graph, entry));
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + BIONDAUpdater.FILE_NAME + "'", e);
        }
        graph.endEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        if ("ID".equalsIgnoreCase(entry.diseaseId))
            return;
        final var diseaseNode = getOrCreateDiseaseNode(graph, entry);
        final var biomarkerNode = getOrCreateBiomarkerNode(graph, entry);
        final var publicationNode = getOrCreatePublicationNode(graph, entry);
        final var builder = graph.buildNode(ASSOCIATION_LABEL);
        builder.withPropertyIfNotNull("sentence", entry.sentence);
        builder.withPropertyIfNotNull("sentence_unnormalized", entry.sentenceUnnormalized);
        builder.withPropertyIfNotNull("paper_type", entry.paperType);
        builder.withPropertyIfNotNull("mentions_in_pmid", entry.mentionsInPMID);
        builder.withPropertyIfNotNull("tps", entry.truePositives);
        builder.withPropertyIfNotNull("fps", entry.falsePositives);
        builder.withPropertyIfNotNull("fns", entry.falseNegatives);
        builder.withPropertyIfNotNull("tns", entry.trueNegatives);
        builder.withPropertyIfNotNull("Score", entry.score);
        final var associationNode = builder.build();
        graph.addEdge(diseaseNode, associationNode, ASSOCIATED_WITH_LABEL);
        graph.addEdge(biomarkerNode, associationNode, ASSOCIATED_WITH_LABEL);
        graph.addEdge(publicationNode, associationNode, ASSOCIATED_WITH_LABEL);
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final Entry entry) {
        final int id = Integer.parseInt(entry.diseaseId);
        Node node = graph.findNode(DISEASE_LABEL, ID_KEY, id);
        if (node == null)
            node = graph.addNode(DISEASE_LABEL, ID_KEY, id, "name", entry.disease);
        return node;
    }

    private Node getOrCreateBiomarkerNode(final Graph graph, final Entry entry) {
        Node node = graph.findNode(BIOMARKER_LABEL, ID_KEY, entry.markerId);
        if (node == null) {
            final var builder = graph.buildNode(BIOMARKER_LABEL);
            builder.withProperty(ID_KEY, entry.markerId);
            builder.withProperty("name", entry.marker);
            if (entry.uniProtEntry != null && !entry.uniProtEntry.contains("not"))
                builder.withProperty("uniprot_entry", entry.uniProtEntry);
            if (entry.uniProtProteinName != null && !entry.uniProtProteinName.contains("not"))
                builder.withProperty("uniprot_protein_name", entry.uniProtProteinName);
            node = builder.build();
        }
        return node;
    }

    private Node getOrCreatePublicationNode(final Graph graph, final Entry entry) {
        final boolean isProperPMID = NumberUtils.isCreatable(entry.pmid);
        Node node;
        if (isProperPMID)
            node = graph.findNode(PUBLICATION_LABEL, "pmid", Integer.parseInt(entry.pmid));
        else
            node = graph.findNode(PUBLICATION_LABEL, "other_id", entry.pmid);
        if (node == null)
            node = graph.addNode(PUBLICATION_LABEL, isProperPMID ? "pmid" : "other_id",
                                 isProperPMID ? Integer.parseInt(entry.pmid) : entry.pmid, "journal", entry.journal,
                                 "author", entry.author, "date", entry.date, "citations", entry.citations);
        return node;
    }
}
