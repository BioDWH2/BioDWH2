package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterOnlyManuallyException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class DrugBankUpdater extends Updater<DrugBankDataSource> {
    private static final String FULL_DATABASE_URL_SUFFIX = "/downloads/all-full-database";
    private static final String DRUG_STRUCTURES_URL_SUFFIX = "/downloads/all-structures";
    private static final String METABOLITE_STRUCTURES_URL_SUFFIX = "/downloads/all-metabolite-structures";

    static final String FULL_DATABASE_FILE_NAME = "drugbank_all_full_database.xml.zip";
    static final String STRUCTURES_SDF_FILE_NAME = "drugbank_all_structures.sdf.zip";
    static final String METABOLITE_STRUCTURES_SDF_FILE_NAME = "drugbank_all_metabolite-structures.sdf.zip";

    public DrugBankUpdater(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final JsonNode json = loadReleasesJson(workspace);
        final String version = getFirstReleaseVersion(json);
        return parseVersion(version);
    }

    private JsonNode loadReleasesJson(final Workspace workspace) throws UpdaterException {
        final String username = dataSource.getStringProperty(workspace, "username");
        final String password = dataSource.getStringProperty(workspace, "password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
            throw new UpdaterOnlyManuallyException();
        final String source = getWebsiteSource("https://go.drugbank.com/releases.json", username, password);
        return parseJsonSource(source);
    }

    private JsonNode parseJsonSource(final String source) throws UpdaterMalformedVersionException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(source);
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    private String getFirstReleaseVersion(final JsonNode json) throws UpdaterMalformedVersionException {
        final JsonNode firstRelease = json.get(0);
        if (firstRelease == null)
            throw new UpdaterMalformedVersionException(json.toString());
        return firstRelease.get("version").asText();
    }

    private Version parseVersion(final String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String username = dataSource.getStringProperty(workspace, "username");
        final String password = dataSource.getStringProperty(workspace, "password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
            throw new UpdaterOnlyManuallyException();
        final JsonNode releases = loadReleasesJson(workspace);
        final JsonNode latestRelease = releases.get(0);
        final String latestReleaseUrl = latestRelease.get("url").asText();
        downloadFileAsBrowser(workspace, latestReleaseUrl + FULL_DATABASE_URL_SUFFIX, FULL_DATABASE_FILE_NAME, username,
                              password);
        downloadFileAsBrowser(workspace, latestReleaseUrl + DRUG_STRUCTURES_URL_SUFFIX, STRUCTURES_SDF_FILE_NAME,
                              username, password);
        downloadFileAsBrowser(workspace, latestReleaseUrl + METABOLITE_STRUCTURES_URL_SUFFIX,
                              METABOLITE_STRUCTURES_SDF_FILE_NAME, username, password);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FULL_DATABASE_FILE_NAME, STRUCTURES_SDF_FILE_NAME, METABOLITE_STRUCTURES_SDF_FILE_NAME};
    }
}
