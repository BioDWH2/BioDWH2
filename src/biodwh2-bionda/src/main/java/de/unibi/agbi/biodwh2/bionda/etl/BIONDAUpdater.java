package de.unibi.agbi.biodwh2.bionda.etl;

import de.unibi.agbi.biodwh2.bionda.BIONDADataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BIONDAUpdater extends Updater<BIONDADataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("Version ([0-9]+\\.[0-9]+\\.[0-9]+)");
    private static final String VERSION_URL = "http://bionda.mpc.ruhr-uni-bochum.de/start.php";
    private static final String DOWNLOAD_URL = "http://bionda.mpc.ruhr-uni-bochum.de/down/Bionda_complete.tsv";
    static final String FILE_NAME = "Bionda_complete.tsv";

    public BIONDAUpdater(final BIONDADataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find())
            return Version.tryParse(matcher.group(1));
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, FILE_NAME);
        return true;
    }
}
