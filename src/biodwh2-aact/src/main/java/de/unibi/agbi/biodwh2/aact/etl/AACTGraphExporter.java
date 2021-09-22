package de.unibi.agbi.biodwh2.aact.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.aact.AACTDataSource;
import de.unibi.agbi.biodwh2.aact.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AACTGraphExporter extends GraphExporter<AACTDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AACTGraphExporter.class);
    static final String STUDY_LABEL = "Study";
    public static final String COUNTRY_LABEL = "Country";
    public static final String REFERENCE_LABEL = "Reference";

    public AACTGraphExporter(final AACTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(STUDY_LABEL, "nct_id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pmid", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "doi", false, IndexDescription.Type.UNIQUE));
        final String filePath = dataSource.resolveSourceFilePath(workspace, AACTUpdater.DUMP_FILE_NAME);
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterFormatException("Failed to parse the file '" + AACTUpdater.DUMP_FILE_NAME + "'");
        /*exportStudies(workspace, graph);
        exportCountries(workspace, graph);
        exportBriefSummaries(workspace, graph);
        exportDetailedDescriptions(workspace, graph);*/
        exportStudyReferences(workspace, graph);
        exportLinks(workspace, graph);
        return true;
    }

    private void exportStudies(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting studies...");
        final Map<String, Set<String>> keywordsPerStudy = collectKeywordsPerStudy(workspace);
        final MappingIterator<Study> studies = parseZipPsvFile(workspace, "studies.txt", Study.class);
        while (studies.hasNext()) {
            final Study study = studies.next();
            final NodeBuilder builder = graph.buildNode().withLabel(STUDY_LABEL);
            builder.withModel(study);
            if (keywordsPerStudy.containsKey(study.nctId))
                builder.withProperty("keywords", keywordsPerStudy.get(study.nctId).toArray(new String[0]));
            builder.build();
        }
    }

    private Map<String, Set<String>> collectKeywordsPerStudy(final Workspace workspace) {
        final Map<String, Set<String>> result = new HashMap<>();
        final MappingIterator<Keyword> keywords = parseZipPsvFile(workspace, "keywords.txt", Keyword.class);
        while (keywords.hasNext()) {
            final Keyword keyword = keywords.next();
            if (!result.containsKey(keyword.nctId))
                result.put(keyword.nctId, new HashSet<>());
            result.get(keyword.nctId).add(keyword.name);
        }
        return result;
    }

    private <T> MappingIterator<T> parseZipPsvFile(final Workspace workspace, final String fileName,
                                                   final Class<T> type) {
        try {
            final ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource, AACTUpdater.DUMP_FILE_NAME);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null)
                if (fileName.equals(zipEntry.getName()))
                    return FileUtils.openSeparatedValuesFile(zipInputStream, type, '|', true);
        } catch (IOException e) {
            throw new ExporterFormatException(
                    "Failed to parse the file '" + fileName + "' in '" + AACTUpdater.DUMP_FILE_NAME + "'", e);
        }
        throw new ExporterFormatException(
                "Failed to parse the file '" + fileName + "' missing in '" + AACTUpdater.DUMP_FILE_NAME + "'");
    }

    private void exportCountries(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting countries...");
        final Map<String, Long> countryNameNodeIdMap = new HashMap<>();
        final MappingIterator<Country> countries = parseZipPsvFile(workspace, "countries.txt", Country.class);
        while (countries.hasNext()) {
            final Country country = countries.next();
            if (!countryNameNodeIdMap.containsKey(country.name)) {
                final Node node = graph.addNode(COUNTRY_LABEL, "name", country.name);
                countryNameNodeIdMap.put(country.name, node.getId());
            }
            if ("t".equalsIgnoreCase(country.removed))
                graph.addEdge(graph.findNode(STUDY_LABEL, "nct_id", country.nctId),
                              countryNameNodeIdMap.get(country.name), "HAS_COUNTRY", "removed", true);
            else
                graph.addEdge(graph.findNode(STUDY_LABEL, "nct_id", country.nctId),
                              countryNameNodeIdMap.get(country.name), "HAS_COUNTRY");
        }
    }

    private void exportBriefSummaries(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting brief summaries...");
        final MappingIterator<BriefSummary> briefSummaries = parseZipPsvFile(workspace, "brief_summaries.txt",
                                                                             BriefSummary.class);
        while (briefSummaries.hasNext()) {
            final BriefSummary summary = briefSummaries.next();
            final Node node = graph.addNode("BriefSummary", "description", summary.description);
            graph.addEdge(graph.findNode(STUDY_LABEL, "nct_id", summary.nctId), node, "HAS_SUMMARY");
        }
    }

    private void exportDetailedDescriptions(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting detailed descriptions...");
        final MappingIterator<DetailedDescription> details = parseZipPsvFile(workspace, "detailed_descriptions.txt",
                                                                             DetailedDescription.class);
        while (details.hasNext()) {
            final DetailedDescription detail = details.next();
            final Node node = graph.addNode("DetailedDescription", "description", detail.description);
            graph.addEdge(graph.findNode(STUDY_LABEL, "nct_id", detail.nctId), node, "HAS_DESCRIPTION");
        }
    }

    private void exportStudyReferences(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting study references...");
        final MappingIterator<StudyReference> references = parseZipPsvFile(workspace, "study_references.txt",
                                                                           StudyReference.class);
        while (references.hasNext()) {
            final StudyReference reference = references.next();
            final String[] dois = IdentifierUtils.extractDois(reference.citation);
            final Node node = getOrCreateReference(graph, reference.citation, dois != null ? dois[0] : null,
                                                   reference.pmid);
            //graph.addEdge(graph.findNode(STUDY_LABEL, "nct_id", reference.nctId), node, "HAS_REFERENCE", "type",
            //              reference.referenceType);
        }
    }

    private Node getOrCreateReference(final Graph graph, final String citation, final String doi, final Integer pmid) {
        Node node = null;
        if (doi != null)
            node = graph.findNode(REFERENCE_LABEL, "doi", doi);
        if (node == null && pmid != null)
            node = graph.findNode(REFERENCE_LABEL, "pmid", pmid);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(REFERENCE_LABEL);
            builder.withPropertyIfNotNull("citation", citation);
            builder.withPropertyIfNotNull("doi", doi);
            builder.withPropertyIfNotNull("pmid", pmid);
            node = builder.build();
        }
        return node;
    }

    private void exportLinks(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting links...");
        final Map<String, Set<String>> linksPerStudy = new HashMap<>();
        final MappingIterator<Link> links = parseZipPsvFile(workspace, "links.txt", Link.class);
        while (links.hasNext()) {
            final Link link = links.next();
            final String doi = getDoiFromUrl(link.url);
            final Integer pmid = getPubMedIdFromUrl(link.url);
            if (doi != null || pmid != null || link.description.contains("PubMed ID")) {
                System.out.println(">>> " + link.url + "|" + link.description);
                final Node node = getOrCreateReference(graph, null, doi, pmid);
                //graph.addEdge(graph.findNode(STUDY_LABEL, "nct_id", link.nctId), node, "HAS_REFERENCE", "type", "link", "description", link.description);
            } else {
                if (!linksPerStudy.containsKey(link.nctId))
                    linksPerStudy.put(link.nctId, new HashSet<>());
                linksPerStudy.get(link.nctId).add(link.url + "|" + link.description);
            }
        }
        /*
        for (final String nctId : linksPerStudy.keySet()) {
            final Node node = graph.findNode(STUDY_LABEL, "nct_id", nctId);
            node.setProperty("links", linksPerStudy.get(nctId).toArray(new String[0]));
            graph.update(node);
        }
         */
    }

    private String getDoiFromUrl(final String url) {
        if (url == null)
            return null;
        final String lowerUrl = url.toLowerCase(Locale.ROOT);
        if (!lowerUrl.contains("doi.org/"))
            return null;
        final String[] parts = StringUtils.splitByWholeSeparator(lowerUrl, "doi.org/");
        if (parts.length <= 1)
            return null;
        return parts[1].trim();
    }

    private Integer getPubMedIdFromUrl(final String url) {
        if (url == null)
            return null;
        final String lowerUrl = url.toLowerCase(Locale.ROOT);
        if (!lowerUrl.contains("pubmed.ncbi.nlm.nih.gov/"))
            return null;
        final String[] parts = StringUtils.splitByWholeSeparator(lowerUrl, "pubmed.ncbi.nlm.nih.gov/");
        if (parts.length <= 1)
            return null;
        int endIndex = 0;
        while (endIndex < parts[1].length() && Character.isDigit(parts[1].charAt(endIndex))) {
            endIndex++;
        }
        if (endIndex == 0)
            return null;
        return Integer.parseInt(parts[1].substring(0, endIndex));
    }
}
