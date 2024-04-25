package de.unibi.agbi.biodwh2.markerdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.markerdb.MarkerDBDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkerDBUpdater extends Updater<MarkerDBDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("<td>([0-9]{4})-([0-9]{2})-([0-9]{2})</td>");
    private static final String PROTEINS_URL = "https://markerdb.ca/pages/download_all_proteins?format=xml";
    private static final String CHEMICALS_URL = "https://markerdb.ca/pages/download_all_chemicals?format=xml";
    private static final String SEQUENCE_VARIANTS_URL = "https://markerdb.ca/pages/download_all_sequence_variants?format=xml";
    private static final String KARYOTYPES_URL = "https://markerdb.ca/pages/download_all_karyotypes?format=xml";
    private static final String DIAGNOSTIC_CHEMICALS_URL = "https://markerdb.ca/pages/download_all_diagnostic_chemicals?format=xml";
    private static final String DIAGNOSTIC_PROTEIN_URL = "https://markerdb.ca/pages/download_all_diagnostic_proteins?format=xml";
    private static final String DIAGNOSTIC_KARYOTYPES_URL = "https://markerdb.ca/pages/download_all_diagnostic_karyotypes?format=xml";
    private static final String PREDICTIVE_GENETICS_URL = "https://markerdb.ca/pages/download_all_predictive_genetics?format=xml";
    private static final String EXPOSURE_CHEMICALS_URL = "https://markerdb.ca/pages/download_all_exposure_chemicals?format=xml";
    static final String PROTEINS_FILE_NAME = "all_proteins.xml";
    static final String CHEMICALS_FILE_NAME = "all_chemicals.xml";
    static final String SEQUENCE_VARIANTS_FILE_NAME = "all_sequence_variants.xml";
    static final String KARYOTYPES_FILE_NAME = "all_karyotypes.xml";
    static final String DIAGNOSTIC_CHEMICALS_FILE_NAME = "all_diagnostic_chemicals.xml";
    static final String DIAGNOSTIC_PROTEIN_FILE_NAME = "all_diagnostic_proteins.xml";
    static final String DIAGNOSTIC_KARYOTYPES_FILE_NAME = "all_diagnostic_karyotypes.xml";
    static final String PREDICTIVE_GENETICS_FILE_NAME = "all_predictive_genetics.xml";
    static final String EXPOSURE_CHEMICALS_FILE_NAME = "all_exposure_chemicals.xml";

    public MarkerDBUpdater(final MarkerDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("https://markerdb.ca/downloads");
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        Version latestVersion = null;
        while (matcher.find()) {
            final int year = Integer.parseInt(matcher.group(1));
            final int month = Integer.parseInt(matcher.group(2));
            final int day = Integer.parseInt(matcher.group(3));
            final Version foundVersion = new Version(year, month, day);
            if (latestVersion == null || foundVersion.compareTo(latestVersion) > 0)
                latestVersion = foundVersion;
        }
        return latestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, PROTEINS_URL, PROTEINS_FILE_NAME);
        downloadFileAsBrowser(workspace, CHEMICALS_URL, CHEMICALS_FILE_NAME);
        downloadFileAsBrowser(workspace, SEQUENCE_VARIANTS_URL, SEQUENCE_VARIANTS_FILE_NAME);
        downloadFileAsBrowser(workspace, KARYOTYPES_URL, KARYOTYPES_FILE_NAME);
        downloadFileAsBrowser(workspace, DIAGNOSTIC_CHEMICALS_URL, DIAGNOSTIC_CHEMICALS_FILE_NAME);
        downloadFileAsBrowser(workspace, DIAGNOSTIC_PROTEIN_URL, DIAGNOSTIC_PROTEIN_FILE_NAME);
        downloadFileAsBrowser(workspace, DIAGNOSTIC_KARYOTYPES_URL, DIAGNOSTIC_KARYOTYPES_FILE_NAME);
        downloadFileAsBrowser(workspace, PREDICTIVE_GENETICS_URL, PREDICTIVE_GENETICS_FILE_NAME);
        downloadFileAsBrowser(workspace, EXPOSURE_CHEMICALS_URL, EXPOSURE_CHEMICALS_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                PROTEINS_FILE_NAME, CHEMICALS_FILE_NAME, SEQUENCE_VARIANTS_FILE_NAME, KARYOTYPES_FILE_NAME,
                DIAGNOSTIC_CHEMICALS_FILE_NAME, DIAGNOSTIC_PROTEIN_FILE_NAME, DIAGNOSTIC_KARYOTYPES_FILE_NAME,
                PREDICTIVE_GENETICS_FILE_NAME, EXPOSURE_CHEMICALS_FILE_NAME
        };
    }
}
