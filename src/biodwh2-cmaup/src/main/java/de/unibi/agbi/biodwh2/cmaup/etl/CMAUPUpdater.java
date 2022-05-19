package de.unibi.agbi.biodwh2.cmaup.etl;

import de.unibi.agbi.biodwh2.cmaup.CMAUPDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMAUPUpdater extends Updater<CMAUPDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("CMAUPv([0-9]+\\.[0-9]+)_");

    public CMAUPUpdater(final CMAUPDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion() throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource("http://bidd.group/CMAUP/download.html");
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find())
                return Version.tryParse(matcher.group(1));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource("http://bidd.group/CMAUP/download.html");
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find()) {
                final String version = matcher.group(1);
                downloadFile(workspace, version, "_download_Plants.txt");
                downloadFile(workspace, version, "_download_Ingredients_All.txt");
                downloadFile(workspace, version, "_download_Ingredients_onlyActive.txt");
                downloadFile(workspace, version, "_download_Targets.txt");
                downloadFile(workspace, version, "_download_Plant_Ingredient_Associations_allIngredients.txt");
                downloadFile(workspace, version, "_download_Plant_Ingredient_Associations_onlyActiveIngredients.txt");
                downloadFile(workspace, version,
                             "_download_Ingredient_Target_Associations_ActivityValues_References.txt");
                return true;
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return false;
    }

    private void downloadFile(final Workspace workspace, final String version,
                              final String fileNameSuffix) throws IOException {
        final String fileName = "CMAUPv" + version + fileNameSuffix;
        HTTPClient.downloadFileAsBrowser("http://bidd.group/CMAUP/downloadFiles/" + fileName,
                                         dataSource.resolveSourceFilePath(workspace, fileName));
    }
}
