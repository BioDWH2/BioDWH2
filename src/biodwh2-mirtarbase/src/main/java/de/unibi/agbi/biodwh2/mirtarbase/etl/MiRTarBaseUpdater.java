package de.unibi.agbi.biodwh2.mirtarbase.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.mirtarbase.MiRTarBaseDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiRTarBaseUpdater extends Updater<MiRTarBaseDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(MiRTarBaseUpdater.class);
    private static final Pattern VERSION_PATTERN = Pattern.compile("Release ([0-9]+\\.[0-9]+(\\.[0-9]+)?) - Download");
    private static final Pattern DOWLOAD_FILE_PATTERN = Pattern.compile(
            "<a href=\"(.+/([a-zA-Z0-9_]+\\.xlsx))\">[a-zA-Z0-9_]+\\.xlsx</a>");
    private static final String VERSION_URL = "https://mirtarbase.cuhk.edu.cn/~miRTarBase/miRTarBase_2022/php/download.php";

    public MiRTarBaseUpdater(final MiRTarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        return matcher.find() ? Version.tryParse(matcher.group(1)) : null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String source;
        try {
            source = HTTPClient.getWebsiteSource(VERSION_URL);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve download urls from '" + VERSION_URL + "'", e);
        }
        final String downloadUrlPrefix = "https://mirtarbase.cuhk.edu.cn/~miRTarBase/miRTarBase_2022/php/";
        final Matcher matcher = DOWLOAD_FILE_PATTERN.matcher(source);
        while (matcher.find()) {
            final String fileName = matcher.group(2);
            final String url = downloadUrlPrefix + matcher.group(1);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Downloading '" + fileName + "'...");
            try {
                HTTPClient.downloadFileAsBrowser(url, dataSource.resolveSourceFilePath(workspace, fileName));
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
            }
        }
        return true;
    }
}
