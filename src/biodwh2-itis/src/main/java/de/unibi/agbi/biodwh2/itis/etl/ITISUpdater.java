package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import org.apache.commons.lang3.StringUtils;

public class ITISUpdater extends Updater<ITISDataSource> {
    private static final String VERSION_URL = "https://www.itis.gov/DisplayPresentDate";
    static final String FILE_NAME = "itisMySQLTables.tar.gz";
    private static final String DOWNLOAD_URL = "https://www.itis.gov/downloads/" + FILE_NAME;

    public ITISUpdater(final ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String text = getWebsiteSource(VERSION_URL).strip();
        final String[] parts = StringUtils.split(text, "-");
        if (parts.length != 3)
            return null;
        return new Version(Integer.parseInt(parts[2]), TextUtils.threeLetterMonthNameToInt(parts[1]),
                           Integer.parseInt(parts[0]));
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
