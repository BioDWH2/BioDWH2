package de.unibi.agbi.biodwh2.hmdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.hmdb.HMDBDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HMDBUpdater extends Updater<HMDBDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("<td>([0-9]{4})-([0-9]{2})-([0-9]{2})</td>");
    static final String METABOLITES_XML_FILE_NAME = "hmdb_metabolites.zip";
    static final String PROTEINS_XML_FILE_NAME = "hmdb_proteins.zip";
    static final String STRUCTURES_SDF_FILE_NAME = "structures.zip";
    private static final String[] DOWNLOAD_FILE_NAMES = new String[]{
            STRUCTURES_SDF_FILE_NAME, METABOLITES_XML_FILE_NAME, PROTEINS_XML_FILE_NAME
    };

    public HMDBUpdater(final HMDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource("https://hmdb.ca/downloads");
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            Version latestVersion = null;
            while (matcher.find()) {
                final int year = Integer.parseInt(matcher.group(1));
                final int month = Integer.parseInt(matcher.group(2));
                final int day = Integer.parseInt(matcher.group(3));
                final Version foundVersion = new Version(year, month, day);
                if (latestVersion == null || foundVersion.compareTo(latestVersion) > 0)
                    latestVersion = foundVersion;
            }
            return latestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            for (final String downloadFileName : DOWNLOAD_FILE_NAMES) {
                HTTPClient.downloadFileAsBrowser("https://hmdb.ca/system/downloads/current/" + downloadFileName,
                                                 dataSource.resolveSourceFilePath(workspace, downloadFileName));
            }
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return DOWNLOAD_FILE_NAMES;
    }
}
