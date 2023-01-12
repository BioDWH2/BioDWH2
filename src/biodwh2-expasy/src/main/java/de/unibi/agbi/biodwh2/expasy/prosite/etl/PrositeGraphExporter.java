package de.unibi.agbi.biodwh2.expasy.prosite.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileEntry;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.expasy.UniRuleDataClass;
import de.unibi.agbi.biodwh2.expasy.UniRuleEntry;
import de.unibi.agbi.biodwh2.expasy.UniRuleReader;
import de.unibi.agbi.biodwh2.expasy.prosite.PrositeDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * https://prosite.expasy.org/prosuser.html
 * <p/>
 * https://prosite.expasy.org/prorule_details.html
 */
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
        graph.addIndex(IndexDescription.forNode("Rule", ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        /*try (final FlatFileReader reader = new FlatFileReader(
                FileUtils.openInput(workspace, dataSource, PrositeUpdater.PROSITE_FILE_NAME), StandardCharsets.UTF_8)) {
            for (final FlatFileEntry entry : reader)
                exportPrositeEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }*/
        try (final UniRuleReader reader = new UniRuleReader(
                FileUtils.openInput(workspace, dataSource, PrositeUpdater.PRORULE_FILE_NAME), StandardCharsets.UTF_8)) {
            for (final UniRuleEntry entry : reader)
                exportProRuleEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportPrositeEntry(final Graph graph, final FlatFileEntry entry) {
        final List<FlatFileEntry.KeyValuePair> properties = entry.properties.get(0);
        final String[] id = collectTypeValuesJoinedLines(properties, "ID");
        if (id.length == 0)
            return;
        final String[] idParts = StringUtils.split(id[0], ";", 2);
        final String type = idParts[1].replace(".", "").trim();
        final String label = type.charAt(0) + type.substring(1).toLowerCase(Locale.ROOT);
        final NodeBuilder builder = graph.buildNode().withLabel(label);
        builder.withProperty(ID_KEY, idParts[0].trim());
        final String accession = collectTypeValues(properties, "AC")[0].replace(";", "").trim();
        builder.withProperty(ACCESSION_KEY, accession);
        final Matcher dtMatcher = DATE_PATTERN.matcher(collectTypeValues(properties, "DT")[0]);
        if (dtMatcher.find()) {
            builder.withProperty("creation_date", dtMatcher.group(0));
            if (dtMatcher.find()) {
                builder.withProperty("data_update_date", dtMatcher.group(0));
                if (dtMatcher.find())
                    builder.withProperty("info_update_date", dtMatcher.group(0));
            }
        }
        builder.withProperty("description", collectTypeValuesJoinedLines(properties, "DE")[0]);
        final String[] patterns = collectTypeValues(properties, "PA");
        if (patterns.length == 1)
            builder.withProperty("pattern", patterns[0]);
        else if (patterns.length > 1)
            builder.withProperty("patterns", patterns);
        final String[] matrix = collectTypeValues(properties, "MA");
        if (matrix.length > 0)
            builder.withProperty("matrix", matrix);
        builder.withProperty("post_processing", collectTypeValues(properties, "PP"));
        builder.withProperty("numerical_results", collectTypeValues(properties, "NR"));
        final String[] comments = collectSemicolonValues(properties, "CC");
        final List<String> sites = new ArrayList<>();
        final List<String> featureKeyDescriptionPairs = new ArrayList<>();
        for (final String comment : comments) {
            final String[] parts = StringUtils.split(comment, "=", 2);
            final String commentValue = parts[1].trim();
            switch (parts[0]) {
                case "/TAXO-RANGE":
                    builder.withProperty("taxonomic_range", commentValue);
                    break;
                case "/MAX-REPEAT":
                    builder.withProperty("max_repeat", Integer.parseInt(commentValue));
                    break;
                case "/SITE":
                    sites.add(commentValue);
                    break;
                case "/SKIP-FLAG":
                    builder.withProperty("skip_flag", "TRUE".equalsIgnoreCase(commentValue));
                    break;
                case "/VERSION":
                    builder.withProperty("version", Integer.parseInt(commentValue));
                    break;
                case "/AUTHOR":
                    builder.withProperty("author", commentValue);
                    break;
                case "/MATRIX_TYPE":
                    builder.withProperty("matrix_type", commentValue);
                    break;
                case "/SCALING_DB":
                    builder.withProperty("scaling_db", commentValue);
                    break;
                case "/FT_KEY":
                    featureKeyDescriptionPairs.add(commentValue);
                    break;
                case "/FT_DESC":
                    String key = featureKeyDescriptionPairs.get(featureKeyDescriptionPairs.size() - 1);
                    featureKeyDescriptionPairs.set(featureKeyDescriptionPairs.size() - 1, key + "=" + commentValue);
                    break;
            }
        }
        if (sites.size() > 0)
            builder.withProperty("sites", sites.toArray(new String[0]));
        if (featureKeyDescriptionPairs.size() > 0)
            builder.withProperty("feature_key_descriptions", featureKeyDescriptionPairs.toArray(new String[0]));
        for (final String uniProtReference : collectSemicolonValues(properties, "DR")) {
            final String[] parts = StringUtils.split(uniProtReference, ',');
            final String uniProtAccession = parts[0].trim();
            final String uniProtName = parts[1].trim();
            // T - true positive, P - 'potential' hit, N - false negative, ? - unknown, F - false positive
            final String uniProtFlag = parts[2].trim();
            // TODO
        }
        builder.withProperty("uniprot_xrefs", collectSemicolonValues(properties, "DR"));
        builder.withProperty("pdb_xrefs", collectSemicolonValues(properties, "3D"));
        collectSemicolonValues(properties, "PR"); // TODO
        final String docId = collectTypeValues(properties, "DO")[0].replace(";", "").trim(); // TODO
        builder.build();
    }

    private String[] collectTypeValues(final List<FlatFileEntry.KeyValuePair> properties, final String key) {
        return properties.stream().filter((p) -> key.equals(p.key)).map((p) -> p.value.trim()).toArray(String[]::new);
    }

    private String[] collectTypeValuesJoinedLines(final List<FlatFileEntry.KeyValuePair> properties, final String key) {
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

    private String[] collectSemicolonValues(final List<FlatFileEntry.KeyValuePair> properties, final String key) {
        final List<String> result = new ArrayList<>();
        for (final FlatFileEntry.KeyValuePair pair : properties) {
            if (key.equals(pair.key)) {
                for (final String part : StringUtils.split(pair.value, ';')) {
                    result.add(part.trim());
                }
            }
        }
        return result.toArray(new String[0]);
    }

    private void exportProRuleEntry(final Graph graph, final UniRuleEntry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Rule");
        builder.withProperty(ACCESSION_KEY, entry.accession);
        if (entry.secondaryAccessions != null && entry.secondaryAccessions.length > 0)
            builder.withProperty("secondary_accessions", entry.secondaryAccessions);
        builder.withProperty("data_classes", Arrays.stream(entry.dataClasses).map(UniRuleDataClass::getValue)
                                                   .toArray(String[]::new));
        // TODO
        builder.build();
    }
}
