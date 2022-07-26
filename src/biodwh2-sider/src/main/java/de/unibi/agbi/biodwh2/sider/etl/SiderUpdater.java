package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiderUpdater extends Updater<SiderDataSource> {
    private static final String VERSION_URL = "http://sideeffects.embl.de";
    private static final Pattern VERSION_PATTERN = Pattern.compile("SIDER ([1-9]).([0-9]+)");
    private static final String DOWNLOAD_URL_PREFIX = "http://sideeffects.embl.de/media/download/";
    static final String DRUG_NAMES_FILE_NAME = "drug_names.tsv";
    static final String DRUG_ATC_FILE_NAME = "drug_atc.tsv";
    static final String INDICATIONS_FILE_NAME = "meddra_all_label_indications.tsv.gz";
    static final String SIDE_EFFECTS_FILE_NAME = "meddra_all_label_se.tsv.gz";
    static final String FREQUENCIES_FILE_NAME = "meddra_freq.tsv.gz";

    public SiderUpdater(final SiderDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion() throws UpdaterException {
        try {
            final String html = HTTPClient.getWebsiteSource(VERSION_URL);
            final Matcher matcher = VERSION_PATTERN.matcher(html);
            if (matcher.find())
                return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFile(workspace, DRUG_NAMES_FILE_NAME);
        downloadFile(workspace, DRUG_ATC_FILE_NAME);
        downloadFile(workspace, INDICATIONS_FILE_NAME);
        downloadFile(workspace, SIDE_EFFECTS_FILE_NAME);
        downloadFile(workspace, FREQUENCIES_FILE_NAME);
        return true;
    }

    private void downloadFile(final Workspace workspace, final String fileName) throws UpdaterConnectionException {
        try {
            final String targetFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + fileName, targetFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException(fileName, e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                DRUG_NAMES_FILE_NAME, DRUG_ATC_FILE_NAME, INDICATIONS_FILE_NAME, SIDE_EFFECTS_FILE_NAME,
                FREQUENCIES_FILE_NAME
        };
    }
}
