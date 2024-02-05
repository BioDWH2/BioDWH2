package de.unibi.agbi.biodwh2.intact.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.intact.IntActDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntActUpdater extends Updater<IntActDataSource> {
    private static final Pattern VERSION_DATE_PATTERN = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})");
    private static final String HUMAN_DOWNLOAD_URL = "http://ftp.ebi.ac.uk/pub/databases/intact/current/psi30/species/human.zip";
    static final String HUMAN_FILE_NAME = "human.zip";

    public IntActUpdater(final IntActDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final HTTPFTPClient.Entry[] entries = new HTTPFTPClient(
                    "http://ftp.ebi.ac.uk/pub/databases/intact/").listDirectory();
            Version newestVersion = null;
            for (final HTTPFTPClient.Entry entry : entries) {
                final Matcher matcher = VERSION_DATE_PATTERN.matcher(entry.name);
                if (matcher.find()) {
                    Version v = Version.tryParse(matcher.group(1).replace('-', '.'));
                    if (v != null && (newestVersion == null || v.compareTo(newestVersion) > 0)) {
                        newestVersion = v;
                    }
                }
            }
            return newestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, HUMAN_DOWNLOAD_URL, HUMAN_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{HUMAN_FILE_NAME};
    }
}
