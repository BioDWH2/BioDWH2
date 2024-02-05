package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class OpenTargetsUpdater extends Updater<OpenTargetsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(OpenTargetsUpdater.class);
    // private static final String DATA_VERSION_URL = "https://api.platform.opentargets.org/api/v4/graphql?query={meta{dataVersion{year,month,iteration}}}";
    private static final String FTP_BASE_URL = "https://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static final String BASELINE_EXPRESSION_FILE_NAME = "baselineExpression.parquet.zip";
    static final String DISEASE_TO_PHENOTYPE_FILE_NAME = "diseaseToPhenotype.parquet.zip";
    static final String DISEASES_FILE_NAME = "diseases.parquet.zip";
    static final String DRUG_WARNINGS_FILE_NAME = "drugWarnings.parquet.zip";
    static final String EVIDENCE_FILE_NAME = "evidence.parquet.zip";
    static final String FDA_FILE_NAME = "fda.parquet.zip";
    static final String GO_FILE_NAME = "go.parquet.zip";
    static final String HPO_FILE_NAME = "hpo.parquet.zip";
    static final String INDICATION_FILE_NAME = "indication.parquet.zip";
    static final String INTERACTION_FILE_NAME = "interaction.parquet.zip";
    static final String INTERACTION_EVIDENCE_FILE_NAME = "interactionEvidence.parquet.zip";
    static final String MECHANISM_OF_ACTION_FILE_NAME = "mechanismOfAction.parquet.zip";
    static final String MOLECULE_FILE_NAME = "molecule.parquet.zip";
    static final String MOUSE_PHENOTYPES_FILE_NAME = "mousePhenotypes.parquet.zip";
    static final String REACTOME_FILE_NAME = "reactome.parquet.zip";
    static final String TARGETS_FILE_NAME = "targets.parquet.zip";

    public OpenTargetsUpdater(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final var client = new HTTPFTPClient(FTP_BASE_URL);
        try {
            final var entries = client.listDirectory("diseases");
            for (final var entry : entries) {
                if ("_SUCCESS".equals(entry.name)) {
                    final var dateTime = LocalDateTime.parse(entry.modificationDate, DATE_TIME_FORMATTER);
                    return new Version(dateTime.getYear(), dateTime.getMonthValue());
                }
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        // final JsonNode json = loadDataVersionJson();
        // return parseVersion(json);
        return null;
    }

    // TODO: once graphql api is fixed
    // private JsonNode loadDataVersionJson() throws UpdaterException {
    //     final String source = getWebsiteSource(DATA_VERSION_URL, 5);
    //     return parseJsonSource(source);
    // }

    // private JsonNode parseJsonSource(final String source) throws UpdaterMalformedVersionException {
    //     final ObjectMapper mapper = new ObjectMapper();
    //     try {
    //         return mapper.readTree(source);
    //     } catch (IOException e) {
    //         throw new UpdaterMalformedVersionException(source, e);
    //     }
    // }

    // private Version parseVersion(final JsonNode json) throws UpdaterMalformedVersionException {
    //     final JsonNode dataNode = json.get("data");
    //     if (dataNode == null)
    //         throw new UpdaterMalformedVersionException(json.toString());
    //     final JsonNode metaNode = dataNode.get("meta");
    //     if (metaNode == null)
    //         throw new UpdaterMalformedVersionException(json.toString());
    //     final JsonNode dataVersionNode = metaNode.get("dataVersion");
    //     if (dataVersionNode == null)
    //         throw new UpdaterMalformedVersionException(json.toString());
    //     final int year = dataVersionNode.get("year").asInt();
    //     final int month = dataVersionNode.get("month").asInt();
    //     final int iteration = dataVersionNode.get("iteration").asInt();
    //     try {
    //         return new Version(year, month, iteration);
    //     } catch (NullPointerException | NumberFormatException e) {
    //         throw new UpdaterMalformedVersionException(dataVersionNode.toString(), e);
    //     }
    // }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final var client = new HTTPFTPClient(FTP_BASE_URL);
        try {
            // Not used: AOTFClickhouse, AOTFElasticsearch, errors, ebisearchAssociations, ebisearchEvidence,
            // searchDisease, searchDrug, searchTarget, associationByDatasourceDirect,
            // associationByDatasourceIndirect, associationByDatatypeDirect, associationByDatatypeIndirect,
            // associationByOverallDirect, associationByOverallIndirect
            downloadFiles(workspace, client, "baselineExpression");
            downloadFiles(workspace, client, "diseaseToPhenotype");
            downloadFiles(workspace, client, "diseases");
            downloadFiles(workspace, client, "drugWarnings");
            // TODO: evidence, fda
            downloadFiles(workspace, client, "go");
            downloadFiles(workspace, client, "hpo");
            downloadFiles(workspace, client, "indication");
            downloadFiles(workspace, client, "interaction");
            downloadFiles(workspace, client, "interactionEvidence");
            downloadFiles(workspace, client, "knownDrugsAggregated");
            downloadFiles(workspace, client, "mechanismOfAction");
            downloadFiles(workspace, client, "molecule");
            downloadFiles(workspace, client, "mousePhenotypes");
            downloadFiles(workspace, client, "reactome");
            downloadFiles(workspace, client, "targets");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    private void downloadFiles(final Workspace workspace, final HTTPFTPClient client,
                               final String directoryName) throws IOException {
        final HTTPFTPClient.Entry[] entries = client.listDirectory(directoryName);
        final String targetFilePath = dataSource.resolveSourceFilePath(workspace, directoryName + ".parquet.zip");
        final Optional<Integer> maxPartNumber = Arrays.stream(entries).map(e -> e.name).filter(
                n -> n.startsWith("part-")).map(n -> Integer.parseInt(StringUtils.split(n, '-')[1])).max(
                Integer::compareTo);
        if (LOGGER.isInfoEnabled()) {
            if (maxPartNumber.isPresent())
                LOGGER.info("Downloading " + directoryName + " in " + (maxPartNumber.get() + 1) + " parts...");
            else
                LOGGER.info("Downloading " + directoryName + "...");
        }
        try (final var outputStream = new ZipArchiveOutputStream(FileUtils.openOutput(targetFilePath))) {
            for (final HTTPFTPClient.Entry entry : entries) {
                if (entry.name.endsWith(".parquet")) {
                    final var stream = HTTPClient.getUrlInputStream(entry.fullUrl);
                    final var zipEntry = new ZipArchiveEntry(entry.name);
                    zipEntry.setSize(stream.contentLength);
                    outputStream.putArchiveEntry(zipEntry);
                    HTTPClient.downloadStream(stream, outputStream, null);
                    outputStream.closeArchiveEntry();
                }
            }
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                BASELINE_EXPRESSION_FILE_NAME, DISEASE_TO_PHENOTYPE_FILE_NAME, DISEASES_FILE_NAME,
                DRUG_WARNINGS_FILE_NAME, GO_FILE_NAME, HPO_FILE_NAME, INDICATION_FILE_NAME, INTERACTION_FILE_NAME,
                INTERACTION_EVIDENCE_FILE_NAME, MECHANISM_OF_ACTION_FILE_NAME, MOLECULE_FILE_NAME,
                MOUSE_PHENOTYPES_FILE_NAME, REACTOME_FILE_NAME, TARGETS_FILE_NAME
        };
    }
}
