package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ITISUpdater extends Updater<ITISDataSource> {
    private static final String VERSION_URL = "https://www.itis.gov/downloads/index.html";
    private static final String URL = "https://www.itis.gov/downloads/itisMySQLTables.tar.gz";
    static final String FILE_NAME = "itisMySQLTables.tar.gz";

    private final Map<String, Integer> monthNameNumberMap = new HashMap<>();

    public ITISUpdater(final ITISDataSource dataSource) {
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
            String html = HTTPClient.getWebsiteSource(VERSION_URL);
            html = StringUtils.splitByWholeSeparator(html, "Database download files are currently from the")[1];
            html = StringUtils.splitByWholeSeparator(html, "<b>")[1];
            html = StringUtils.splitByWholeSeparator(html, "</b>")[0].trim();
            return parseVersion(html);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private Version parseVersion(final String version) throws UpdaterMalformedVersionException {
        try {
            String[] versionParts = StringUtils.split(version, "-");
            return new Version(Integer.parseInt(versionParts[2]), monthNameNumberMap.get(versionParts[1]),
                               Integer.parseInt(versionParts[0]));
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        File newFile = new File(dataSource.resolveSourceFilePath(workspace, FILE_NAME));
        try {
            FileUtils.copyURLToFile(new URL(URL), newFile);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
