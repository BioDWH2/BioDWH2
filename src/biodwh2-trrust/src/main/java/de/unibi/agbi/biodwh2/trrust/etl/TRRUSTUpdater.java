package de.unibi.agbi.biodwh2.trrust.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.trrust.TRRUSTDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TRRUSTUpdater extends Updater<TRRUSTDataSource> {
    private static final String VERSION_URL = "https://www.grnpedia.org/trrust/downloadnetwork.php";
    private static final String DOWNLOAD_URL_PREFIX = "https://www.grnpedia.org/trrust/data/";
    static final String HUMAN_FILE_NAME = "trrust_rawdata.human.bioc.xml";
    static final String MOUSE_FILE_NAME = "trrust_rawdata.mouse.bioc.xml";
    private static final Pattern VERSION_PATTERN = Pattern.compile("[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}");

    public TRRUSTUpdater(final TRRUSTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource(VERSION_URL);
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find())
                return Version.tryParse(matcher.group(0));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + HUMAN_FILE_NAME,
                                             dataSource.resolveSourceFilePath(workspace, HUMAN_FILE_NAME));
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + MOUSE_FILE_NAME,
                                             dataSource.resolveSourceFilePath(workspace, MOUSE_FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{HUMAN_FILE_NAME, MOUSE_FILE_NAME};
    }
}
