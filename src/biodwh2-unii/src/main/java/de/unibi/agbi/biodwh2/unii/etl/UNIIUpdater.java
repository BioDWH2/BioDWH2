package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UNIIUpdater extends Updater<UNIIDataSource> {
    private static final String WEBSITE_URL = "https://fdasis.nlm.nih.gov/srs/jsp/srs/uniiListDownload.jsp";
    private static final String DOWNLOAD_URL_PREFIX = "https://fdasis.nlm.nih.gov/srs/download/srs/";
    static final String UNIIS_FILE_NAME = "UNIIs.zip";
    static final String UNII_DATA_FILE_NAME = "UNII_Data.zip";

    private final Map<String, Integer> monthNameNumberMap = new HashMap<>();

    public UNIIUpdater(UNIIDataSource dataSource) {
        super(dataSource);
        final String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        for (int i = 0; i < months.length; i++)
            monthNameNumberMap.put(months[i], i + 1);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            String html = HTTPClient.getWebsiteSource(WEBSITE_URL);
            html = StringUtils.splitByWholeSeparator(html, "Last updated: ")[1];
            html = StringUtils.splitByWholeSeparator(html, "</span>")[0];
            return parseVersion(html);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private Version parseVersion(String version) throws UpdaterMalformedVersionException {
        try {
            String[] versionParts = StringUtils.split(version, " ");
            return new Version(Integer.parseInt(versionParts[1]), monthNameNumberMap.get(versionParts[0]));
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        downloadFile(DOWNLOAD_URL_PREFIX + UNIIS_FILE_NAME,
                     dataSource.resolveSourceFilePath(workspace, UNIIS_FILE_NAME));
        downloadFile(DOWNLOAD_URL_PREFIX + UNII_DATA_FILE_NAME,
                     dataSource.resolveSourceFilePath(workspace, UNII_DATA_FILE_NAME));
        return true;
    }

    private void downloadFile(final String url, final String filePath) throws UpdaterException {
        File newFile = new File(filePath);
        try {
            FileUtils.copyURLToFile(new URL(url), newFile);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{UNIIS_FILE_NAME, UNII_DATA_FILE_NAME};
    }
}
