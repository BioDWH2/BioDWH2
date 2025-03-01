package de.unibi.agbi.biodwh2.interpro.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.interpro.InterProDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterProUpdater extends Updater<InterProDataSource> {
    private static final Pattern RELEASE_PATTERN = Pattern.compile("Release (\\d+)\\.(\\d+)");
    private static final String RELEASE_NOTES_URL = "https://ftp.ebi.ac.uk/pub/databases/interpro/current_release/release_notes.txt";
    private static final String DOWNLOAD_URL_PREFIX = "https://ftp.ebi.ac.uk/pub/databases/interpro/current_release/";
    static final String FILE_NAME = "interpro.xml.gz";

    public InterProUpdater(final InterProDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(RELEASE_NOTES_URL);
        final Matcher releaseMatcher = RELEASE_PATTERN.matcher(source);
        if (releaseMatcher.find())
            return new Version(Integer.parseInt(releaseMatcher.group(1)), Integer.parseInt(releaseMatcher.group(2)));
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + FILE_NAME, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
