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
    private Map<String, Integer> monthNameNumberMap = new HashMap<>();

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
            String html = HTTPClient.getWebsiteSource("https://fdasis.nlm.nih.gov/srs/jsp/srs/uniiListDownload.jsp");
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
        downloadFile("https://fdasis.nlm.nih.gov/srs/download/srs/UNIIs.zip",
                     dataSource.resolveSourceFilePath(workspace, "UNIIs.zip"));
        downloadFile("https://fdasis.nlm.nih.gov/srs/download/srs/UNII_Data.zip",
                     dataSource.resolveSourceFilePath(workspace, "UNII_Data.zip"));
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
}
