package de.unibi.agbi.biodwh2.cancerdrugsdb.etl;

import de.unibi.agbi.biodwh2.cancerdrugsdb.CancerDrugsDBDataSource;
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

public class CancerDrugsDBUpdater extends Updater<CancerDrugsDBDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "Database build date:\\s+([0-9]{2}/[0-9]{2}/[0-9]{2})", Pattern.CASE_INSENSITIVE);
    static final String FILE_NAME = "cancerdrugsdb.txt";
    private static final String DOWNLOAD_URL = "https://acfdata.coworks.be/" + FILE_NAME;

    public CancerDrugsDBUpdater(final CancerDrugsDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource("https://www.anticancerfund.org/en/cancerdrugs-db");
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find()) {
                final String[] parts = StringUtils.split(matcher.group(1), '/');
                return new Version(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
            }
            return null;
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve website source", e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String outputFilePath = dataSource.resolveSourceFilePath(workspace, FILE_NAME);
        try {
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL, outputFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + DOWNLOAD_URL + "'", e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
