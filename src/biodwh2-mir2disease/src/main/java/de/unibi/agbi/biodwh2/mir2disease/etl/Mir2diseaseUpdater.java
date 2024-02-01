package de.unibi.agbi.biodwh2.mir2disease.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.mir2disease.Mir2diseaseDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mir2diseaseUpdater extends Updater<Mir2diseaseDataSource> {
    private static final String M2D_MAIN_URL = "http://watson.compbio.iupui.edu:8080/miR2Disease";
    private static final String M2D_DOWNLOAD_URL = "http://watson.compbio.iupui.edu:8080/miR2Disease/download/";
    static final String MI_RNA_TARGET_FILE_NAME = "miRtar.txt";
    static final String DISEASE_FILE_NAME = "diseaseList.txt";
    static final String ALL_ENTRIES_FILE_NAME = "AllEntries.txt";

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
                final Version version = new Version(Integer.parseInt(matcher.group(3)),
                                                    TextUtils.threeLetterMonthNameToInt(matcher.group(1).toLowerCase()),
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
        for (final String fileName : expectedFileNames()) {
            final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
            try {
                HTTPClient.downloadFileAsBrowser(M2D_DOWNLOAD_URL + fileName, filePath);
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
            }
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{MI_RNA_TARGET_FILE_NAME, DISEASE_FILE_NAME, ALL_ENTRIES_FILE_NAME};
    }
}
