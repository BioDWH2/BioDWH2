package de.unibi.agbi.biodwh2.core.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public final class OnlineVersionCache {
    private static final Logger LOGGER = LogManager.getLogger(OnlineVersionCache.class);
    private static final String VERSIONS_URL = "https://raw.githubusercontent.com/BioDWH2/DataSource-Status/main/result.min.json";

    private static OnlineVersionCache instance;

    private final Map<String, List<DataSourceVersion>> dataSourceVersions;

    private OnlineVersionCache() {
        dataSourceVersions = new HashMap<>();
        try {
            loadVersions(parseJsonSource(HTTPClient.getWebsiteSource(VERSIONS_URL)));
        } catch (IOException e) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Failed to load online data source version cache. Will be ignored.", e);
        }
    }

    private JsonNode parseJsonSource(final String source) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(source);
    }

    private void loadVersions(final JsonNode root) {
        final Iterator<Map.Entry<String, JsonNode>> dataSources = root.fields();
        while (dataSources.hasNext())
            addVersions(dataSources.next());
    }

    private void addVersions(final Map.Entry<String, JsonNode> entry) {
        final String dataSourceId = entry.getKey();
        if (!dataSourceVersions.containsKey(dataSourceId))
            dataSourceVersions.put(dataSourceId, new ArrayList<>());
        for (final JsonNode versionEntry : entry.getValue())
            addVersion(dataSourceId, versionEntry);
    }

    private void addVersion(final String dataSourceId, final JsonNode versionEntry) {
        final String version = versionEntry.get("version").asText();
        final boolean latest = versionEntry.get("latest").asBoolean();
        final Map<String, String> files = new HashMap<>();
        final Iterator<Map.Entry<String, JsonNode>> fileEntries = versionEntry.get("files").fields();
        while (fileEntries.hasNext()) {
            final Map.Entry<String, JsonNode> file = fileEntries.next();
            files.put(file.getKey(), file.getValue().asText());
        }
        dataSourceVersions.get(dataSourceId).add(new DataSourceVersion(Version.tryParse(version), latest, files));
    }

    public static synchronized OnlineVersionCache getInstance() {
        if (instance == null)
            instance = new OnlineVersionCache();
        return instance;
    }

    public boolean hasDataSource(final String dataSourceId) {
        return dataSourceVersions.containsKey(dataSourceId);
    }

    public List<DataSourceVersion> getVersions(final String dataSourceId) {
        return dataSourceVersions.get(dataSourceId);
    }

    public DataSourceVersion getLatest(final String dataSourceId) {
        if (!hasDataSource(dataSourceId))
            return null;
        for (final DataSourceVersion version : dataSourceVersions.get(dataSourceId))
            if (version.isLatest())
                return version;
        return null;
    }
}
