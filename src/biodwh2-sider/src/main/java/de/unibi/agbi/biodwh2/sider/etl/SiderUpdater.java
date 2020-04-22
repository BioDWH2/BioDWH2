package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;

import java.io.IOException;

public class SiderUpdater extends MultiFileFTPUpdater<SiderDataSource> {
    @Override
    protected boolean tryUpdateFiles(Workspace workspace, SiderDataSource dataSource) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser("http://sideeffects.embl.de/media/download/drug_names.tsv",
                                             dataSource.resolveSourceFilePath(workspace, "drug_names.tsv"));
            HTTPClient.downloadFileAsBrowser("http://sideeffects.embl.de/media/download/drug_atc.tsv",
                                             dataSource.resolveSourceFilePath(workspace, "drug_atc.tsv"));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return super.tryUpdateFiles(workspace, dataSource);
    }

    @Override
    protected String getFTPAddress() {
        return "xi.embl.de";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{
                "/SIDER/latest/meddra_freq.tsv.gz", "/SIDER/latest/meddra_all_label_indications.tsv.gz",
                "/SIDER/latest/meddra_all_label_se.tsv.gz"
        };
    }

    @Override
    protected String[] getTargetFileNames() {
        return new String[]{
                "meddra_all_indications.tsv.gz", "meddra_all_se.tsv.gz", "meddra_freq.tsv.gz",
                "meddra_all_label_indications.tsv.gz", "meddra_all_label_se.tsv.gz"
        };
    }
}
