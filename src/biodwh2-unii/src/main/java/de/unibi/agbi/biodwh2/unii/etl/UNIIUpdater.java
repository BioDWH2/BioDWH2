package de.unibi.agbi.biodwh2.unii.etl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UNIIUpdater extends Updater<UNIIDataSource> {
    private static final String WEBSITE_URL = "https://precision.fda.gov/uniisearch/archive";
    private static final String DOWNLOAD_URL_PREFIX = "https://qnyqxh695c.execute-api.us-east-1.amazonaws.com/production/v1/get-file/";
    private static final Pattern VERSION_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
    static final String UNIIS_FILE_NAME = "UNIIs.zip";
    static final String UNII_DATA_FILE_NAME = "UNII_Data.zip";

    private String newestDataFileName = null;
    private String newestNamesFileName = null;

    public UNIIUpdater(final UNIIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        Version newestVersion = null;
        try {
            final Document document = Jsoup.parse(HTTPClient.getWebsiteSource(WEBSITE_URL));
            for (final Element script : document.select("script")) {
                if ("__NEXT_DATA__".equalsIgnoreCase(script.id())) {
                    newestVersion = parseJsonFileList(script.html());
                    break;
                }
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return newestVersion;
    }

    private Version parseJsonFileList(final String jsonText) throws JsonProcessingException {
        Version newestVersion = null;
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode json = mapper.readTree(jsonText);
        for (final JsonNode file : json.get("props").get("pageProps").get("dataRecords")) {
            final String fileName = file.get("fileName").asText();
            final Matcher versionMatcher = VERSION_PATTERN.matcher(fileName);
            if (versionMatcher.find()) {
                final Version version = Version.tryParse(StringUtils.replace(versionMatcher.group(0), "-", "."));
                if (version != null && (newestVersion == null || version.compareTo(newestVersion) > 0)) {
                    newestVersion = version;
                    newestDataFileName = fileName;
                }
            }
        }
        for (final JsonNode file : json.get("props").get("pageProps").get("namesRecords")) {
            final String fileName = file.get("fileName").asText();
            final Matcher versionMatcher = VERSION_PATTERN.matcher(fileName);
            if (versionMatcher.find()) {
                final Version version = Version.tryParse(StringUtils.replace(versionMatcher.group(0), "-", "."));
                if (version != null && version.equals(newestVersion))
                    newestNamesFileName = fileName;
            }
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFile(DOWNLOAD_URL_PREFIX + newestNamesFileName,
                     dataSource.resolveSourceFilePath(workspace, UNIIS_FILE_NAME));
        downloadFile(DOWNLOAD_URL_PREFIX + newestDataFileName,
                     dataSource.resolveSourceFilePath(workspace, UNII_DATA_FILE_NAME));
        return true;
    }

    private void downloadFile(final String url, final String filePath) throws UpdaterException {
        final File newFile = new File(filePath);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode json = mapper.readTree(HTTPClient.getWebsiteSource(url));
            final String resolvedDownloadUrl = json.get("url").asText();
            FileUtils.copyURLToFile(new URL(resolvedDownloadUrl), newFile);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{UNIIS_FILE_NAME, UNII_DATA_FILE_NAME};
    }
}
