package de.unibi.agbi.biodwh2.opentargets.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

public class OpenTargetsUpdater extends Updater<OpenTargetsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(OpenTargetsUpdater.class);
    private static final String DATA_VERSION_URL = "https://api.platform.opentargets.org/api/v4/graphql?query={meta{dataVersion{year,month,iteration}}}";
    private static final String FTP_BASE_URL = "https://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/json/";
    static final String BASELINE_EXPRESSION_FILE_NAME = "baselineExpression.json.gz";
    static final String DISEASE_TO_PHENOTYPE_FILE_NAME = "diseaseToPhenotype.json.gz";
    static final String DISEASES_FILE_NAME = "diseases.json.gz";
    static final String DRUG_WARNINGS_FILE_NAME = "drugWarnings.json.gz";
    static final String EVIDENCE_FILE_NAME = "evidence.json.gz";
    static final String FDA_FILE_NAME = "fda.json.gz";
    static final String GO_FILE_NAME = "go.json.gz";
    static final String HPO_FILE_NAME = "hpo.json.gz";
    static final String INDICATION_FILE_NAME = "indication.json.gz";
    static final String INTERACTION_FILE_NAME = "interaction.json.gz";
    static final String INTERACTION_EVIDENCE_FILE_NAME = "interactionEvidence.json.gz";
    static final String MECHANISM_OF_ACTION_FILE_NAME = "mechanismOfAction.json.gz";
    static final String MOLECULE_FILE_NAME = "molecule.json.gz";
    static final String MOUSE_PHENOTYPES_FILE_NAME = "mousePhenotypes.json.gz";
    static final String REACTOME_FILE_NAME = "reactome.json.gz";
    static final String TARGETS_FILE_NAME = "targets.json.gz";

    public OpenTargetsUpdater(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final JsonNode json = loadDataVersionJson();
        return parseVersion(json);
    }

    private JsonNode loadDataVersionJson() throws UpdaterException {
        String source = "";
        int tries = 0;
        while (tries < 10) {
            try {
                source = HTTPClient.getWebsiteSource(DATA_VERSION_URL);
                break;
            } catch (UnknownHostException e) {
                tries++;
                if (tries == 10)
                    throw new UpdaterConnectionException(e);
                else
                    trySleep(1000);
            } catch (IOException e) {
                throw new UpdaterConnectionException(e);
            }
        }
        return parseJsonSource(source);
    }

    private void trySleep(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    private JsonNode parseJsonSource(final String source) throws UpdaterMalformedVersionException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(source);
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    private Version parseVersion(final JsonNode json) throws UpdaterMalformedVersionException {
        final JsonNode dataNode = json.get("data");
        if (dataNode == null)
            throw new UpdaterMalformedVersionException(json.toString());
        final JsonNode metaNode = dataNode.get("meta");
        if (metaNode == null)
            throw new UpdaterMalformedVersionException(json.toString());
        final JsonNode dataVersionNode = metaNode.get("dataVersion");
        if (dataVersionNode == null)
            throw new UpdaterMalformedVersionException(json.toString());
        final int year = dataVersionNode.get("year").asInt();
        final int month = dataVersionNode.get("month").asInt();
        final int iteration = dataVersionNode.get("iteration").asInt();
        try {
            return new Version(year, month, iteration);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(dataVersionNode.toString(), e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final HTTPFTPClient client = new HTTPFTPClient(FTP_BASE_URL);
        try {
            final AnonymousFTPClient ftpClient = new AnonymousFTPClient();
            ftpClient.connect("ftp.ebi.ac.uk");
            // Not used: AOTFClickhouse, AOTFElasticsearch, errors, ebisearchAssociations, ebisearchEvidence,
            // searchDisease, searchDrug, searchTarget, associationByDatasourceDirect,
            // associationByDatasourceIndirect, associationByDatatypeDirect, associationByDatatypeIndirect,
            // associationByOverallDirect, associationByOverallIndirect
            downloadFiles(workspace, client, ftpClient, "baselineExpression");
            downloadFiles(workspace, client, ftpClient, "diseaseToPhenotype");
            downloadFiles(workspace, client, ftpClient, "diseases");
            downloadFiles(workspace, client, ftpClient, "drugWarnings");
            // TODO: evidence, fda
            downloadFiles(workspace, client, ftpClient, "go");
            downloadFiles(workspace, client, ftpClient, "hpo");
            downloadFiles(workspace, client, ftpClient, "indication");
            downloadFiles(workspace, client, ftpClient, "interaction");
            downloadFiles(workspace, client, ftpClient, "interactionEvidence");
            downloadFiles(workspace, client, ftpClient, "knownDrugsAggregated");
            downloadFiles(workspace, client, ftpClient, "mechanismOfAction");
            downloadFiles(workspace, client, ftpClient, "molecule");
            downloadFiles(workspace, client, ftpClient, "mousePhenotypes");
            downloadFiles(workspace, client, ftpClient, "reactome");
            downloadFiles(workspace, client, ftpClient, "targets");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    private void downloadFiles(final Workspace workspace, final HTTPFTPClient client,
                               final AnonymousFTPClient ftpClient, final String directoryName) throws IOException {
        final HTTPFTPClient.Entry[] entries = client.listDirectory(directoryName);
        final String targetFilePath = dataSource.resolveSourceFilePath(workspace, directoryName + ".json.gz");
        final Optional<Integer> maxPartNumber = Arrays.stream(entries).map(e -> e.name).filter(
                n -> n.startsWith("part-")).map(n -> Integer.parseInt(StringUtils.split(n, '-')[1])).max(
                Integer::compareTo);
        if (LOGGER.isInfoEnabled()) {
            if (maxPartNumber.isPresent())
                LOGGER.info("Downloading " + directoryName + " in " + (maxPartNumber.get() + 1) + " parts...");
            else
                LOGGER.info("Downloading " + directoryName + "...");
        }
        try (final OutputStream outputStream = new GZIPOutputStream(FileUtils.openOutput(targetFilePath))) {
            for (final HTTPFTPClient.Entry entry : entries) {
                if (entry.name.endsWith(".json")) {
                    final String ftpFilePath = entry.fullUrl.substring(entry.fullUrl.indexOf("ac.uk/") + 6);
                    ftpClient.retrieveFile(ftpFilePath, outputStream);
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
