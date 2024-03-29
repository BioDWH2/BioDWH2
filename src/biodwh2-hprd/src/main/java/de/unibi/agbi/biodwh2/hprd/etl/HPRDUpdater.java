package de.unibi.agbi.biodwh2.hprd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.hprd.HPRDDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HPRDUpdater extends Updater<HPRDDataSource> {
    private static final String VERSION_URL = "http://hprd.org/download";
    private static final Pattern VERSION_PATTERN = Pattern.compile("HPRD_Release(\\d+)(_\\d+\\.tar\\.gz)");
    static final String FILE_NAME = "HPRD_FLAT_FILES.tar.gz";

    public HPRDUpdater(final HPRDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource(VERSION_URL);
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find()) {
                final String versionNumber = matcher.group(1);
                return new Version(Integer.parseInt(versionNumber), 0);
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(retrieveFileUrl(), dataSource.resolveSourceFilePath(workspace, FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    private String retrieveFileUrl() throws IOException {
        final String source = HTTPClient.getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            return "http://hprd.org/RELEASE" + matcher.group(1) + "/HPRD_FLAT_FILES" + matcher.group(2);
        }
        return null;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
