package de.unibi.agbi.biodwh2.expasy.enzyme.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileEntry;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.expasy.enzyme.EnzymeDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnzymeGraphExporter extends GraphExporter<EnzymeDataSource> {
    static final String ENZYME_LABEL = "Enzyme";
    static final String PROTEIN_LABEL = "Protein";
    static final String ACCESSION_KEY = "accession";

    public EnzymeGraphExporter(final EnzymeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(ENZYME_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        try (final FlatFileReader reader = new FlatFileReader(
                FileUtils.openInput(workspace, dataSource, EnzymeUpdater.FILE_NAME), StandardCharsets.UTF_8)) {
            for (final FlatFileEntry entry : reader)
                exportEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportEntry(final Graph graph, final FlatFileEntry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel(ENZYME_LABEL);
        final List<FlatFileEntry.KeyValuePair> properties = entry.properties.get(0);
        final Optional<FlatFileEntry.KeyValuePair> id = properties.stream().filter((p) -> "ID".equals(p.key))
                                                                  .findFirst();
        if (id.isPresent()) {
            builder.withProperty(ID_KEY, id.get().value);
        } else {
            return;
        }
        final String[] descriptions = collectTypeValues(properties, "DE");
        if (descriptions.length > 0 && (descriptions[0].startsWith("Deleted entry") || descriptions[0].startsWith(
                "Transferred entry"))) {
            return;
        }
        if (descriptions.length == 1)
            builder.withProperty("official_name", descriptions[0]);
        else {
            builder.withProperty("descriptions", descriptions);
        }
        final String[] alternativeNames = collectTypeValues(properties, "AN");
        if (alternativeNames.length > 0)
            builder.withProperty("alternative_names", alternativeNames);
        final String[] comments = collectTypeValues(properties, "CC");
        if (comments.length > 0)
            builder.withProperty("comments", comments);
        final String[] catalyticActivities = collectTypeValues(properties, "CA");
        if (catalyticActivities.length > 0)
            builder.withProperty("catalyticActivities", catalyticActivities);
        final Node node = builder.build();
        for (final String swissProtXref : collectTypeValues(properties, "DR")) {
            final String[] xrefParts = StringUtils.split(swissProtXref, ';');
            for (final String xref : xrefParts) {
                final String[] proteinIds = StringUtils.split(xref, ',');
                final Node proteinNode = getOrCreateProteinNode(graph, proteinIds[0].trim(), proteinIds[1].trim());
                graph.addEdge(node, proteinNode, "HAS_XREF");
            }
        }
    }

    private String[] collectTypeValues(final List<FlatFileEntry.KeyValuePair> properties, final String key) {
        final List<String> lines = properties.stream().filter((p) -> key.equals(p.key)).map((p) -> p.value.trim())
                                             .collect(Collectors.toList());
        for (int i = 0; i < lines.size() - 1; i++) {
            if (!lines.get(i).endsWith(".") || lines.get(i).endsWith(" cf.")) {
                lines.set(i, lines.get(i) + lines.get(i + 1));
                lines.remove(i + 1);
            }
        }
        return lines.stream().map((l) -> l.substring(0, l.length() - 1)).toArray(String[]::new);
    }

    private Node getOrCreateProteinNode(final Graph graph, final String accession, final String name) {
        Node node = graph.findNode(PROTEIN_LABEL, ACCESSION_KEY, accession);
        if (node == null)
            node = graph.addNode(PROTEIN_LABEL, ACCESSION_KEY, accession, "name", name);
        return node;
    }
}
