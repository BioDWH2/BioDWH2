package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import org.apache.commons.lang3.StringUtils;

public class ITISUpdater extends Updater<ITISDataSource> {
    private static final String VERSION_URL = "https://www.itis.gov/downloads/index.html";
    static final String FILE_NAME = "itisMySQLTables.tar.gz";
    private static final String DOWNLOAD_URL = "https://www.itis.gov/downloads/" + FILE_NAME;

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
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
