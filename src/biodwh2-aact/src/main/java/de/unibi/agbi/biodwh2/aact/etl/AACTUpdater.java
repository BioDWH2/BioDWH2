package de.unibi.agbi.biodwh2.aact.etl;

import de.unibi.agbi.biodwh2.aact.AACTDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AACTUpdater extends Updater<AACTDataSource> {
    static final String DUMP_FILE_NAME = "pipe-delimited-export.zip";
    private static final String DOWNLOAD_PAGE_URL = "https://aact.ctti-clinicaltrials.org/pipe_files";
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile(
            "/static/exported_files/monthly/[0-9]{8}_pipe-delimited-export\\.zip");

    public AACTUpdater(final AACTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String url = getDownloadFileUrl();
        final String[] parts = StringUtils.split(url, '/');
        final String version = StringUtils.split(parts[parts.length - 1], '_')[0];
        return new Version(Integer.parseInt(version.substring(0, 4)), Integer.parseInt(version.substring(4, 6)),
                           Integer.parseInt(version.substring(6, 8)));
    }

    private String getDownloadFileUrl() throws UpdaterException {
        try {
            final String html = HTTPClient.getWebsiteSource(DOWNLOAD_PAGE_URL);
            final Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(html);
            if (matcher.find())
                return "https://aact.ctti-clinicaltrials.org" + matcher.group(0);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        throw new UpdaterConnectionException("Failed to get database download URL from download page");
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String dumpFilePath = dataSource.resolveSourceFilePath(workspace, DUMP_FILE_NAME);
        try {
            HTTPClient.downloadFileAsBrowser(getDownloadFileUrl(), dumpFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{DUMP_FILE_NAME};
    }
}
