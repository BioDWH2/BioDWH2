package de.unibi.agbi.biodwh2.hmdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.hmdb.HMDBDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HMDBUpdater extends Updater<HMDBDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("<td>([0-9]{4})-([0-9]{2})-([0-9]{2})</td>");
    private static final String[] DOWNLOAD_FILE_PATHS = new String[]{
            "/system/downloads/current/sequences/protein.fasta.zip",
            "/system/downloads/current/sequences/gene.fasta.zip", "/system/downloads/current/structures.zip",
            "/system/downloads/current/hmdb_metabolites.zip", "/system/downloads/current/hmdb_proteins.zip"
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
            for (final String downloadFilePath : DOWNLOAD_FILE_PATHS) {
                final String[] fileNameParts = StringUtils.split(downloadFilePath, '/');
                final String fileName = fileNameParts[fileNameParts.length - 1];
                HTTPClient.downloadFileAsBrowser("https://hmdb.ca" + downloadFilePath,
                                                 dataSource.resolveSourceFilePath(workspace, fileName));
            }
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }
}
