package de.unibi.agbi.biodwh2.adrecs.etl;

import de.unibi.agbi.biodwh2.adrecs.ADReCSDataSource;
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

public class ADReCSUpdater extends Updater<ADReCSDataSource> {
    private static final Pattern UPDATE_DATE_PATTERN = Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9]{2}");
    private static final String DOWNLOAD_URL_PREFIX = "http://bioinf.xmu.edu.cn/ADReCS/download/";
    static final String DRUG_ADR_FILE_NAME = "Drug_ADR.txt.gz";
    static final String DRUG_ADR_QUANTIFICATION_FILE_NAME = "ADReCS_Drug_ADR_relations_quantification.txt.gz";
    static final String ADR_ONTOLOGY_FILE_NAME = "ADR_ontology.xlsx";
    static final String DRUG_INFO_FILE_NAME = "Drug_information.xlsx";

    public ADReCSUpdater(final ADReCSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        Version version = null;
        final String source = getWebsiteSource("http://bioinf.xmu.edu.cn/ADReCS/update.jsp");
        final Matcher matcher = UPDATE_DATE_PATTERN.matcher(source);
        while (matcher.find()) {
            final Version v = Version.tryParse(StringUtils.replace(matcher.group(0), "/", "."));
            if (v != null && (version == null || v.compareTo(version) > 0))
                version = v;
        }
        return version;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        tryDownloadFile(workspace, DRUG_ADR_FILE_NAME);
        tryDownloadFile(workspace, DRUG_ADR_QUANTIFICATION_FILE_NAME);
        tryDownloadFile(workspace, ADR_ONTOLOGY_FILE_NAME);
        tryDownloadFile(workspace, DRUG_INFO_FILE_NAME);
        return true;
    }

    private void tryDownloadFile(final Workspace workspace, final String fileName) throws UpdaterConnectionException {
        try {
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + fileName,
                                             dataSource.resolveSourceFilePath(workspace, fileName));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + DOWNLOAD_URL_PREFIX + fileName + "'", e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                DRUG_ADR_FILE_NAME, DRUG_ADR_QUANTIFICATION_FILE_NAME, ADR_ONTOLOGY_FILE_NAME, DRUG_INFO_FILE_NAME
        };
    }
}
