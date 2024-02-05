package de.unibi.agbi.biodwh2.rnainter.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.rnainter.RNAInterDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RNAInterUpdater extends Updater<RNAInterDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(RNAInterUpdater.class);
    private static final String DOWNLOAD_URL_PREFIX = "http://www.rnainter.org/raidMedia/download/";
    static final String RR_FILE_NAME = "Download_data_RR.tar.gz";
    static final String RP_FILE_NAME = "Download_data_RP.tar.gz";
    static final String RD_FILE_NAME = "Download_data_RD.tar.gz";
    static final String RC_FILE_NAME = "Download_data_RC.tar.gz";
    static final String RH_FILE_NAME = "Download_data_RH.tar.gz";

    public RNAInterUpdater(final RNAInterDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("http://www.rnainter.org");
        final String part = StringUtils.splitByWholeSeparator(source, "<meta name=\"description\" content=\"")[1];
        final String versionText = part.split("\"")[0].replace("rnainter", "").replace("version", "").trim();
        return Version.tryParse(versionText);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFile(workspace, 1, RR_FILE_NAME);
        downloadFile(workspace, 2, RP_FILE_NAME);
        downloadFile(workspace, 3, RD_FILE_NAME);
        downloadFile(workspace, 4, RC_FILE_NAME);
        downloadFile(workspace, 5, RH_FILE_NAME);
        return true;
    }

    private void downloadFile(final Workspace workspace, int step, String fileName) throws UpdaterConnectionException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("(" + step + "/5) Downloading file '" + fileName + "'...");
        try {
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + fileName,
                                             dataSource.resolveSourceFilePath(workspace, fileName));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{RR_FILE_NAME, RP_FILE_NAME, RD_FILE_NAME, RC_FILE_NAME, RH_FILE_NAME};
    }
}
