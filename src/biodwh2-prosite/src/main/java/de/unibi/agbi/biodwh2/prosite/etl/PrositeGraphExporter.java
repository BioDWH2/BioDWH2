package de.unibi.agbi.biodwh2.prosite.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileEntry;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.prosite.PrositeDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrositeGraphExporter extends GraphExporter<PrositeDataSource> {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}-)?[A-Z]{3}-\\d{4}");
    static final String ACCESSION_KEY = "accession";

    public PrositeGraphExporter(final PrositeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Pattern", ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Matrix", ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        try (final FlatFileReader reader = new FlatFileReader(
                FileUtils.openInput(workspace, dataSource, PrositeUpdater.PROSITE_FILE_NAME), StandardCharsets.UTF_8)) {
            for (final FlatFileEntry entry : reader)
                exportPrositeEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        try (final FlatFileReader reader = new FlatFileReader(
                FileUtils.openInput(workspace, dataSource, PrositeUpdater.PRORULE_FILE_NAME), StandardCharsets.UTF_8)) {
            for (final FlatFileEntry entry : reader)
                exportProruleEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportPrositeEntry(final Graph graph, final FlatFileEntry entry) {
        final List<FlatFileEntry.KeyValuePair> properties = entry.properties.get(0);
        final String[] id = collectTypeValues(properties, "ID");
        if (id.length == 0)
            return;
        final String[] idParts = StringUtils.split(id[0], ";", 2);
        final String type = idParts[1].replace(".", "").trim();
        final String label = type.charAt(0) + type.substring(1).toLowerCase(Locale.ROOT);
        final NodeBuilder builder = graph.buildNode().withLabel(label);
        builder.withProperty(ID_KEY, idParts[0].trim());
        final String[] accessions = collectTypeValues(properties, "AC");
        builder.withProperty(ACCESSION_KEY, accessions[0].replace(";", "").trim());
        final Matcher dtMatcher = DATE_PATTERN.matcher(collectTypeValues(properties, "DT")[0]);
        dtMatcher.find();
        builder.withProperty("creation_date", dtMatcher.group(0));
        dtMatcher.find();
        builder.withProperty("data_update_date", dtMatcher.group(0));
        dtMatcher.find();
        builder.withProperty("info_update_date", dtMatcher.group(0));
        // TODO
        builder.build();
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

    private void exportProruleEntry(final Graph graph, final FlatFileEntry entry) {
        // TODO
    }
}
