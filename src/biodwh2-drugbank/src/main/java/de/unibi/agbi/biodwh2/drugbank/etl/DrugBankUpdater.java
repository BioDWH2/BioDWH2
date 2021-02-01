package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterOnlyManuallyException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;

import java.io.*;
import java.util.Map;

public class DrugBankUpdater extends Updater<DrugBankDataSource> {
    private static final String FULL_DATABASE_URL_SUFFIX = "/downloads/all-full-database";
    private static final String DRUG_STRUCTURES_URL_SUFFIX = "/downloads/all-structures";
    private static final String METABOLITE_STRUCTURES_URL_SUFFIX = "/downloads/all-metabolite-structures";

    public DrugBankUpdater(DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        final JsonNode json = loadReleasesJson();
        final String version = getFirstReleaseVersion(json);
        return parseVersion(version);
    }

    private JsonNode loadReleasesJson() throws UpdaterException {
        final String source;
        try {
            source = HTTPClient.getWebsiteSource("https://www.drugbank.ca/releases.json");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return parseJsonSource(source);
    }

    private JsonNode parseJsonSource(String source) throws UpdaterMalformedVersionException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(source);
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    private String getFirstReleaseVersion(JsonNode json) throws UpdaterMalformedVersionException {
        final JsonNode firstRelease = json.get(0);
        if (firstRelease == null)
            throw new UpdaterMalformedVersionException(json.toString());
        return firstRelease.get("version").asText();
    }

    private Version parseVersion(String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        final Map<String, String> drugBankProperties = dataSource.getProperties(workspace);
        final String username = drugBankProperties.getOrDefault("username", null);
        final String password = drugBankProperties.getOrDefault("password", null);
        if (username != null && username.length() > 0 && password != null && password.length() > 0) {
            final JsonNode releases = loadReleasesJson();
            final JsonNode latestRelease = releases.get(0);
            final String latestReleaseUrl = latestRelease.get("url").asText();
            try {
                String filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_full_database.xml.zip");
                HTTPClient.downloadFileAsBrowser(latestReleaseUrl + FULL_DATABASE_URL_SUFFIX, filePath, username,
                                                 password);
                filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_structures.sdf.zip");
                HTTPClient.downloadFileAsBrowser(latestReleaseUrl + DRUG_STRUCTURES_URL_SUFFIX, filePath, username,
                                                 password);
                filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_metabolite-structures.sdf.zip");
                HTTPClient.downloadFileAsBrowser(latestReleaseUrl + METABOLITE_STRUCTURES_URL_SUFFIX, filePath,
                                                 username, password);
                return true;
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download files", e);
            }
        }
        throw new UpdaterOnlyManuallyException();
    }
}
