package de.unibi.agbi.biodwh2.gencc.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.gencc.GenCCDataSource;
import de.unibi.agbi.biodwh2.gencc.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class GenCCGraphExporter extends GraphExporter<GenCCDataSource> {
    static final String GENE_LABEL = "Gene";
    static final String DISEASE_LABEL = "Disease";

    public GenCCGraphExporter(final GenCCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportEntries(workspace, graph);
        return true;
    }

    private void exportEntries(final Workspace workspace, final Graph graph) {
        try (final MappingIterator<Entry> iterator = FileUtils.openTsvWithHeader(workspace, dataSource,
                                                                                 GenCCUpdater.FILE_NAME, Entry.class)) {
            while (iterator.hasNext())
                exportEntry(graph, iterator.next());
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + GenCCUpdater.FILE_NAME + "'", e);
        }
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final Node gene = getOrCreateGeneNode(graph, entry);
        final Node disease = getOrCreateDiseaseNode(graph, entry);
        final EdgeBuilder builder = graph.buildEdge().fromNode(gene).toNode(disease).withLabel("ASSOCIATED_WITH");
        builder.withModel(entry);
        if (StringUtils.isNotBlank(entry.submittedAsPmids)) {
            // Cleanup this field if malformed by the submitter
            String pmidsText = StringUtils.replace(entry.submittedAsPmids.trim(), "\u00A0", " ");
            pmidsText = StringUtils.replaceChars(pmidsText, "{[]}\"", "");
            pmidsText = StringUtils.replace(pmidsText, "PMID", "");
            pmidsText = StringUtils.replace(pmidsText, "PMLID", "");
            pmidsText = StringUtils.replace(pmidsText, "_", ", ");
            pmidsText = StringUtils.replace(pmidsText, ";", ", ");
            if (!pmidsText.equals("0") && !pmidsText.equals("--") && !pmidsText.equalsIgnoreCase("NULL") &&
                !pmidsText.equalsIgnoreCase("neant")) {
                final Integer[] pmids = Arrays.stream(StringUtils.split(pmidsText, ", ")).map(this::parsePMIDInteger)
                                              .filter(Objects::nonNull).toArray(Integer[]::new);
                builder.withProperty("submitted_as_pmids", pmids);
            }
        }
        builder.build();
    }

    private Integer parsePMIDInteger(final String value) {
        if (StringUtils.isNotBlank(value)) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private Node getOrCreateGeneNode(final Graph graph, final Entry entry) {
        Node node = graph.findNode(GENE_LABEL, ID_KEY, entry.geneCurie);
        if (node == null)
            node = graph.addNode(GENE_LABEL, ID_KEY, entry.geneCurie, "symbol", entry.geneSymbol);
        return node;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final Entry entry) {
        Node node = graph.findNode(DISEASE_LABEL, ID_KEY, entry.diseaseCurie);
        if (node == null)
            node = graph.addNode(DISEASE_LABEL, ID_KEY, entry.diseaseCurie, "title", entry.diseaseTitle);
        return node;
    }
}
