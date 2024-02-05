package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ITISUpdater extends Updater<ITISDataSource> {
    private static final String VERSION_URL = "https://www.itis.gov/downloads/index.html";
    private static final String URL = "https://www.itis.gov/downloads/itisMySQLTables.tar.gz";
    static final String FILE_NAME = "itisMySQLTables.tar.gz";

    public ITISUpdater(final ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        String html = getWebsiteSource(VERSION_URL);
        html = StringUtils.splitByWholeSeparator(html, "Database download files are currently from the")[1];
        html = StringUtils.splitByWholeSeparator(html, "<b>")[1];
        html = StringUtils.splitByWholeSeparator(html, "</b>")[0].trim();
        return parseVersion(html);
    }

    private Version parseVersion(final String version) throws UpdaterMalformedVersionException {
        try {
            String[] versionParts = StringUtils.split(version, "-");
            return new Version(Integer.parseInt(versionParts[2]),
                               TextUtils.threeLetterMonthNameToInt(versionParts[1].toLowerCase()),
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
