package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
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

public class CanadianNutrientFileUpdater extends Updater<CanadianNutrientFileDataSource> {
    /**
     * Main page of the project. Is used to determine the newest version by parsing the html code.
     */
    private static final String CNF_MAIN_URL = "https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/nutrient-data/canadian-nutrient-file-2015-download-files.html";
    private static final String CNF_DOWNLOAD_URL = "https://www.canada.ca/content/dam/hc-sc/migration/hc-sc/fn-an/alt_formats/zip/nutrition/fiche-nutri-data/cnf-fcen-csv.zip";
    static final String FILE_NAME = "cnf-fcen-csv.zip";

    public CanadianNutrientFileUpdater(final CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(CNF_MAIN_URL);
        final Pattern versionPattern = Pattern.compile("dateModified\">\\s*([0-9]{4}-[0-9]{2}-[0-9]{2})\\s*</time>");
        final Matcher matcher = versionPattern.matcher(source);
        Version newestVersion = null;
        while (matcher.find()) {
            final String[] dateParts = StringUtils.split(matcher.group(1), '-');
            final Version version = new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                                                Integer.parseInt(dateParts[2]));
            if (newestVersion == null || newestVersion.compareTo(version) < 0)
                newestVersion = version;
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(CNF_DOWNLOAD_URL, dataSource.resolveSourceFilePath(workspace, FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + CNF_DOWNLOAD_URL + "'", e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
