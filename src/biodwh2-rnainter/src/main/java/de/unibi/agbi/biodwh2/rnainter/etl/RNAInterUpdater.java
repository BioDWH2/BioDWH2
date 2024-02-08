package de.unibi.agbi.biodwh2.rnainter.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.rnainter.RNAInterDataSource;
import org.apache.commons.lang3.StringUtils;

public class RNAInterUpdater extends Updater<RNAInterDataSource> {
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
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{RR_FILE_NAME, RP_FILE_NAME, RD_FILE_NAME, RC_FILE_NAME, RH_FILE_NAME};
    }
}
