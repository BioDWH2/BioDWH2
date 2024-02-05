package de.unibi.agbi.biodwh2.rnadisease.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.rnadisease.RNADiseaseDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RNADiseaseUpdater extends Updater<RNADiseaseDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "href=\".*RNADiseasev(\\d+\\.\\d+)_RNA-disease_experiment_all.zip\">");
    private static final String VERSION_URL = "http://www.rnadisease.org/download";
    private static final String DOWNLOAD_URL_PREFIX = "http://www.rnadisease.org/static/download/";
    private static final String DOWNLOAD_FILE_PREFIX = "RNADiseasev";
    static final String ALL_EXPERIMENTAL_FILE_NAME = "RNA-disease_experiment_all.zip";
    static final String MIRNA_PREDICTED_FILE_NAME = "RNA-disease_miRNA_predict.zip";
    static final String LNCRNA_PREDICTED_FILE_NAME = "RNA-disease_lncRNA_predict.zip";
    static final String CIRCRNA_PREDICTED_FILE_NAME = "RNA-disease_circRNA_predict.zip";
    static final String PIRNA_PREDICTED_FILE_NAME = "RNA-disease_piRNA_predict.zip";

    private String lastVersion;

    public RNADiseaseUpdater(final RNADiseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            lastVersion = matcher.group(1);
            return Version.tryParse(lastVersion);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        if (lastVersion == null)
            getNewestVersion(workspace);
        final String urlPrefix = DOWNLOAD_URL_PREFIX + DOWNLOAD_FILE_PREFIX + lastVersion + "_";
        downloadFileAsBrowser(workspace, urlPrefix + ALL_EXPERIMENTAL_FILE_NAME, ALL_EXPERIMENTAL_FILE_NAME);
        downloadFileAsBrowser(workspace, urlPrefix + MIRNA_PREDICTED_FILE_NAME, MIRNA_PREDICTED_FILE_NAME);
        downloadFileAsBrowser(workspace, urlPrefix + LNCRNA_PREDICTED_FILE_NAME, LNCRNA_PREDICTED_FILE_NAME);
        downloadFileAsBrowser(workspace, urlPrefix + CIRCRNA_PREDICTED_FILE_NAME, CIRCRNA_PREDICTED_FILE_NAME);
        downloadFileAsBrowser(workspace, urlPrefix + PIRNA_PREDICTED_FILE_NAME, PIRNA_PREDICTED_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                ALL_EXPERIMENTAL_FILE_NAME, MIRNA_PREDICTED_FILE_NAME, LNCRNA_PREDICTED_FILE_NAME,
                CIRCRNA_PREDICTED_FILE_NAME, PIRNA_PREDICTED_FILE_NAME
        };
    }
}
