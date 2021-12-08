package de.unibi.agbi.biodwh2.omim.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.omim.OMIMDataSource;
import de.unibi.agbi.biodwh2.omim.model.GeneMap2;
import de.unibi.agbi.biodwh2.omim.model.MIMTitles;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OMIMGraphExporter extends GraphExporter<OMIMDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OMIMGraphExporter.class);
    public static final String GENE_LABEL = "Gene";
    static final String PHENOTYPE_LABEL = "Phenotype";
    static final String MIM_NUMBER_KEY = "mim_number";
    private static final Pattern LONG_PHENOTYPE_PATTERN = Pattern.compile("^(.*),\\s(\\d{6})\\s\\((\\d)\\)(|, (.*))$");
    private static final Pattern SHORT_PHENOTYPE_PATTERN = Pattern.compile("^(.*)\\((\\d)\\)(|, (.*))$");

    public OMIMGraphExporter(final OMIMDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, MIM_NUMBER_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PHENOTYPE_LABEL, MIM_NUMBER_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, MIMTitles> mimNumberToTitles = loadMimTitles(workspace);
        exportGeneMap2(workspace, graph, mimNumberToTitles);
        return true;
    }

    private Map<String, MIMTitles> loadMimTitles(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Loading MIM titles...");
        final Map<String, MIMTitles> result = new HashMap<>();
        for (final MIMTitles entry : parseTsvFile(workspace, MIMTitles.class, OMIMUpdater.MIMTITLES_FILENAME))
            if (!entry.prefix.equalsIgnoreCase("Caret"))
                result.put(entry.mimNumber, entry);
        return result;
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, fileName,
                                                                                    typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void exportGeneMap2(final Workspace workspace, final Graph graph,
                                final Map<String, MIMTitles> mimNumberToTitles) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export GeneMap2...");
        for (final GeneMap2 entry : parseTsvFile(workspace, GeneMap2.class, OMIMUpdater.GENEMAP2_FILENAME)) {
            final MIMTitles titles = mimNumberToTitles.get(entry.mimNumber);
            final NodeBuilder builder = graph.buildNode().withLabel(GENE_LABEL).withModel(entry);
            if (titles != null && isTitlesForGene(titles.prefix)) {
                builder.withPropertyIfNotNull("included_titles", getTitlesArray(titles.includedTitles));
                builder.withPropertyIfNotNull("alternative_titles", getTitlesArray(titles.alternativeTitle));
                builder.withPropertyIfNotNull("preferred_title", titles.preferredTitle);
            }
            final Node node = builder.build();
            if (entry.phenotypes != null)
                exportEntryPhenotypes(graph, mimNumberToTitles, node, entry);
        }
    }

    private boolean isTitlesForGene(final String titlePrefix) {
        return titlePrefix.equalsIgnoreCase("Asterisk") || titlePrefix.equalsIgnoreCase("Plus");
    }

    private String[] getTitlesArray(final String titles) {
        if (titles == null || StringUtils.isEmpty(titles))
            return null;
        return Arrays.stream(StringUtils.splitByWholeSeparator(titles, ";;")).map(String::trim).toArray(String[]::new);
    }

    private void exportEntryPhenotypes(final Graph graph, final Map<String, MIMTitles> mimNumberToTitles,
                                       final Node geneNode, final GeneMap2 entry) {
        for (final String phenotype : StringUtils.split(entry.phenotypes, ';')) {
            final Matcher longMatcher = LONG_PHENOTYPE_PATTERN.matcher(phenotype.trim());
            final Matcher shortMatcher = SHORT_PHENOTYPE_PATTERN.matcher(phenotype.trim());
            Node phenotypeNode = null;
            if (longMatcher.find()) {
                phenotypeNode = getOrCreatePhenotypeNode(graph, mimNumberToTitles, longMatcher.group(2),
                                                         longMatcher.group(1).trim(), longMatcher.group(3),
                                                         longMatcher.group(5));
            } else if (shortMatcher.find()) {
                phenotypeNode = getOrCreatePhenotypeNode(graph, mimNumberToTitles, entry.mimNumber,
                                                         shortMatcher.group(1).trim(), shortMatcher.group(2),
                                                         shortMatcher.group(3));
            } else
                LOGGER.warn("Failed to match phenotype for value '" + phenotype.trim() + "'");
            if (phenotypeNode != null)
                graph.addEdge(geneNode, phenotypeNode, "ASSOCIATED_WITH");
        }
    }

    private Node getOrCreatePhenotypeNode(final Graph graph, final Map<String, MIMTitles> mimNumberToTitles,
                                          final String mimNumber, String name, final String mappingKey,
                                          final String inheritance) {
        Node node = graph.findNode(PHENOTYPE_LABEL, MIM_NUMBER_KEY, mimNumber);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(PHENOTYPE_LABEL);
            builder.withProperty(MIM_NUMBER_KEY, mimNumber);
            // A question mark, before the phenotype name indicates that the relationship between the phenotype
            // and gene is provisional. More details about this relationship are provided in the comment field of the
            // map and in the gene and phenotype OMIM entries.
            if (name.startsWith("?")) {
                name = name.substring(1);
                builder.withProperty("provisional", true);
            }
            // Braces, {}, indicate mutations that contribute to susceptibility to multifactorial disorders
            // (e.g., diabetes, asthma) or to susceptibility to infection (e.g., malaria).
            if (name.startsWith("{") && name.endsWith("}")) {
                name = name.substring(1, name.length() - 1);
                builder.withProperty("susceptibility_mutation", true);
            }
            // Brackets, [], indicate "nondiseases," mainly genetic variations that lead to apparently abnormal
            // laboratory test values (e.g., dysalbuminemic euthyroidal hyperthyroxinemia).
            if (name.startsWith("[") && name.endsWith("]")) {
                name = name.substring(1, name.length() - 1);
                builder.withProperty("nondisease", true);
            }
            builder.withPropertyIfNotNull("name", name);
            // 1 - the disorder is placed on the map based on its association with a gene, but the underlying defect is not known.
            // 2 - the disorder has been placed on the map by linkage; no mutation has been found.
            // 3 - the molecular basis for the disorder is known; a mutation has been found in the gene.
            // 4 - a contiguous gene deletion or duplication syndrome, multiple genes are deleted or duplicated causing the phenotype.
            builder.withPropertyIfNotNull("mapping_key", mappingKey != null ? Integer.parseInt(mappingKey) : null);
            builder.withPropertyIfNotNull("inheritance", inheritance);
            final MIMTitles titles = mimNumberToTitles.get(mimNumber);
            if (titles != null && isTitlesForPhenotype(titles.prefix)) {
                builder.withPropertyIfNotNull("included_titles", getTitlesArray(titles.includedTitles));
                builder.withPropertyIfNotNull("alternative_titles", getTitlesArray(titles.alternativeTitle));
                builder.withPropertyIfNotNull("preferred_title", titles.preferredTitle);
            }
            node = builder.build();
        }
        return node;
    }

    private boolean isTitlesForPhenotype(final String titlePrefix) {
        return titlePrefix.equalsIgnoreCase("Number Sign") || titlePrefix.equalsIgnoreCase("Percent") ||
               titlePrefix.equalsIgnoreCase("NULL");
    }
}
