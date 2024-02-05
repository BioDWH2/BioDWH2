package de.unibi.agbi.biodwh2.mirdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.mirdb.MiRDBDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiRDBUpdater extends Updater<MiRDBDataSource> {
    private static final String VERSION_URL = "https://mirdb.org/mirdb/download.html";
    private static final String DOWNLOAD_URL_PREFIX = "https://mirdb.org/mirdb/";
    static final String FILE_NAME = "miRDB_prediction_result.txt.gz";
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "<td align=\"center\"><b>([0-9]+\\.[0-9]+)</b></td>");

    private String downloadUrl = null;

    public MiRDBUpdater(final MiRDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final String[] parts = StringUtils.splitByWholeSeparator(source, "<p>Current version:</p>", 2);
        if (parts.length == 2) {
            final String versionTableSource = StringUtils.splitByWholeSeparator(parts[1], "</table>", 2)[0];
            final String[] linkParts = StringUtils.splitByWholeSeparator(versionTableSource, "<a href=\"", 2);
            if (linkParts.length == 2)
                downloadUrl = StringUtils.split(linkParts[1], '"')[0];
            final Matcher matcher = VERSION_PATTERN.matcher(versionTableSource);
            if (matcher.find())
                return Version.tryParse(matcher.group(1));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        if (downloadUrl == null)
            return false;
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + downloadUrl, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
