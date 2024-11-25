package de.unibi.agbi.biodwh2.edk.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.edk.EDKDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EDKUpdater extends Updater<EDKDataSource> {
    private static final String[] MONTH_NAMES = {
            "jan", "feb", "march", "april", "may", "june", "july", "aug", "sep", "oct", "nov", "dec"
    };
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "\\[(" + String.join("|", MONTH_NAMES) + "),\\s+([0-9]{4})]", Pattern.CASE_INSENSITIVE);
    private static final String VERSION_URL = "https://ngdc.cncb.ac.cn/edk/";
    private static final String DOWNLOAD_URL_PREFIX = "https://ngdc.cncb.ac.cn/edk/static/";
    public static final String GENE_ASSOCIATIONS_FILE_NAME = "gene_associations.csv";
    public static final String EDITING_SITES_ASSOCIATIONS_FILE_NAME = "editing_sites_associations.csv";
    public static final String VIRUS_ASSOCIATIONS_FILE_NAME = "virus_associations.csv";
    public static final String ABERRANT_ENZYME_ASSOCIATIONS_FILE_NAME = "aberrant_enzyme_associations.csv";

    public EDKUpdater(final EDKDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        Version latestVersion = null;
        while (matcher.find()) {
            final String month = matcher.group(1).toLowerCase();
            for (int i = 0; i < MONTH_NAMES.length; i++) {
                if (MONTH_NAMES[i].equals(month)) {
                    final var version = new Version(Integer.parseInt(matcher.group(2)), i + 1);
                    if (latestVersion == null || latestVersion.compareTo(version) < 0)
                        latestVersion = version;
                    break;
                }
            }
        }
        return latestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + GENE_ASSOCIATIONS_FILE_NAME,
                              GENE_ASSOCIATIONS_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + EDITING_SITES_ASSOCIATIONS_FILE_NAME,
                              EDITING_SITES_ASSOCIATIONS_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + VIRUS_ASSOCIATIONS_FILE_NAME,
                              VIRUS_ASSOCIATIONS_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + ABERRANT_ENZYME_ASSOCIATIONS_FILE_NAME,
                              ABERRANT_ENZYME_ASSOCIATIONS_FILE_NAME);
        return true;
    }
}
