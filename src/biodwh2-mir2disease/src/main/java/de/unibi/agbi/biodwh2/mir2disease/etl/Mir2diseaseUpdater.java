package de.unibi.agbi.biodwh2.mir2disease.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.mir2disease.Mir2diseaseDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mir2diseaseUpdater extends Updater<Mir2diseaseDataSource> {
    private static final String M2D_MAIN_URL = "http://watson.compbio.iupui.edu:8080/miR2Disease";
    private static final String M2D_DOWNLOAD_URL = "http://watson.compbio.iupui.edu:8080/miR2Disease/download/";
    static final String[] FILE_NAMES = new String[]{"miRtar.txt", "diseaseList.txt", "AllEntries.txt"};

    public Mir2diseaseUpdater(final Mir2diseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource(M2D_MAIN_URL);
            final Pattern versionPattern = Pattern.compile("Creation Date: ([a-zA-Z]{3}).([0-9]{1,2}), ([0-9]{4})");
            final Matcher matcher = versionPattern.matcher(source);
            Version newestVersion = null;
            while (matcher.find()) {
                // parse months
                final String[] months = new String[]{
                        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Dec"
                };
                int month = 0;
                for (int i = 0; i < months.length; i++) {
                    if (months[i].equals(matcher.group(1))) {
                        month = i + 1;
                        break;
                    }
                }
                final Version version = new Version(Integer.parseInt(matcher.group(3)), month,
                                                    Integer.parseInt(matcher.group(2)));
                if (newestVersion == null || newestVersion.compareTo(version) < 0)
                    newestVersion = version;
            }
            return newestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to get newest version", e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES) {
            final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
            try {
                HTTPClient.downloadFileAsBrowser(M2D_DOWNLOAD_URL + fileName, filePath);
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
            }
        }
        return true;
    }
}
