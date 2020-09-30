package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;

import java.io.IOException;

public class SiderUpdater extends MultiFileFTPUpdater<SiderDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "http://sideeffects.embl.de/media/download/";
    private static final String FTP_PREFIX = "/SIDER/latest/";
    static final String DRUG_NAMES_FILE_NAME = "drug_names.tsv";
    static final String DRUG_ATC_FILE_NAME = "drug_atc.tsv";
    static final String INDICATIONS_FILE_NAME = "meddra_all_label_indications.tsv.gz";
    static final String SIDE_EFFECTS_FILE_NAME = "meddra_all_label_se.tsv.gz";
    static final String FREQUENCIES_FILE_NAME = "meddra_freq.tsv.gz";

    public SiderUpdater(SiderDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + DRUG_NAMES_FILE_NAME,
                                             dataSource.resolveSourceFilePath(workspace, DRUG_NAMES_FILE_NAME));
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + DRUG_ATC_FILE_NAME,
                                             dataSource.resolveSourceFilePath(workspace, DRUG_ATC_FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return super.tryUpdateFiles(workspace);
    }

    @Override
    protected String getFTPAddress() {
        return "xi.embl.de";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{
                FTP_PREFIX + FREQUENCIES_FILE_NAME, FTP_PREFIX + INDICATIONS_FILE_NAME,
                FTP_PREFIX + SIDE_EFFECTS_FILE_NAME
        };
    }

    @Override
    protected String[] getTargetFileNames() {
        return new String[]{FREQUENCIES_FILE_NAME, INDICATIONS_FILE_NAME, SIDE_EFFECTS_FILE_NAME};
    }
}
