package de.unibi.agbi.biodwh2.opentargets.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;

public class OpenTargetsUpdater extends Updater<OpenTargetsDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTargetsUpdater.class);
    private static final String DATA_VERSION_URL = "https://api.platform.opentargets.org/api/v4/graphql?query={meta{dataVersion{year,month,iteration}}}";
    private static final String FTP_BASE_URL = "http://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/json/";
    static final String BASELINE_EXPRESSION_FILE_NAME = "baselineExpression.json";
    static final String CANCER_BIOMARKER_FILE_NAME = "cancerBiomarker.json";
    static final String DISEASE_TO_PHENOTYPE_FILE_NAME = "diseaseToPhenotype.json";
    static final String DISEASES_FILE_NAME = "diseases.json";
    static final String DRUG_WARNINGS_FILE_NAME = "drugWarnings.json";
    static final String ECO_FILE_NAME = "eco.json";
    static final String HPO_FILE_NAME = "hpo.json";
    static final String INDICATION_FILE_NAME = "indication.json";
    static final String INTERACTION_FILE_NAME = "interaction.json";
    static final String INTERACTION_EVIDENCE_FILE_NAME = "interactionEvidence.json";
    static final String MECHANISM_OF_ACTION_FILE_NAME = "mechanismOfAction.json";
    static final String MOLECULE_FILE_NAME = "molecule.json";
    static final String MOUSE_PHENOTYPES_FILE_NAME = "mousePhenotypes.json";
    static final String REACTOME_FILE_NAME = "reactome.json";
    static final String TARGETS_FILE_NAME = "targets.json";

    public OpenTargetsUpdater(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
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
            // Not used: AOTFClickhouse, AOTFElasticsearch, interactionUnmatched
            // TODO: needed? associationByDatasourceDirect, associationByDatasourceIndirect, associationByDatatypeDirect, associationByDatatypeIndirect, associationByOverallDirect, associationByOverallIndirect
            downloadFiles(workspace, client, "baselineExpression");
            downloadFiles(workspace, client, "cancerBiomarker");
            downloadFiles(workspace, client, "diseaseToPhenotype");
            downloadFiles(workspace, client, "diseases");
            downloadFiles(workspace, client, "drugWarnings");
            downloadFiles(workspace, client, "eco");
            // TODO: evidence
            downloadFiles(workspace, client, "hpo");
            downloadFiles(workspace, client, "indication");
            downloadFiles(workspace, client, "interaction");
            downloadFiles(workspace, client, "interactionEvidence");
            // TODO: knownDrugsAggregated
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
        final String targetFilePath = dataSource.resolveSourceFilePath(workspace, directoryName + ".json");
        final Optional<Integer> maxPartNumber = Arrays.stream(entries).map(e -> e.name).filter(
                n -> n.startsWith("part-")).map(n -> Integer.parseInt(StringUtils.split(n, '-')[1])).max(
                Integer::compareTo);
        if (LOGGER.isInfoEnabled()) {
            if (maxPartNumber.isPresent())
                LOGGER.info("Downloading " + directoryName + " in " + (maxPartNumber.get() + 1) + " parts...");
            else
                LOGGER.info("Downloading " + directoryName + "...");
        }
        try (FileOutputStream outputStream = new FileOutputStream(targetFilePath)) {
            for (final HTTPFTPClient.Entry entry : entries) {
                if (entry.name.endsWith(".json")) {
                    final InputStream stream = HTTPClient.getUrlInputStream(entry.fullUrl);
                    IOUtils.copy(stream, outputStream);
                    trySleep(100);
                }
            }
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                BASELINE_EXPRESSION_FILE_NAME, CANCER_BIOMARKER_FILE_NAME, DISEASE_TO_PHENOTYPE_FILE_NAME,
                DISEASES_FILE_NAME, DRUG_WARNINGS_FILE_NAME, ECO_FILE_NAME, HPO_FILE_NAME, INDICATION_FILE_NAME,
                INTERACTION_FILE_NAME, INTERACTION_EVIDENCE_FILE_NAME, MECHANISM_OF_ACTION_FILE_NAME,
                MOLECULE_FILE_NAME, MOUSE_PHENOTYPES_FILE_NAME, REACTOME_FILE_NAME, TARGETS_FILE_NAME
        };
    }
}
