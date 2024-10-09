package de.unibi.agbi.biodwh2.ptmd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.ptmd.PTMDDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PTMDUpdater extends Updater<PTMDDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "Last update:\\s+(" + String.join("|", TextUtils.MONTH_NAMES) +
            ")\\.\\s+([1-3][0-9]|0?[1-9])(st|rd|th),\\s+([0-9]{4})", Pattern.CASE_INSENSITIVE);
    private static final String VERSION_URL = "http://ptmd.biocuckoo.org/download.php";
    private static final String DOWNLOAD_URL_PREFIX = "http://ptmd.biocuckoo.org/download/";
    static final String PROTEIN_INFORMATION_FILE_NAME = "Protein Information.zip";
    static final String PTM_DISEASE_ASSOCIATION_FILE_NAME = "PTM-Disease association.zip";
    static final String PTM_SITES_FILE_NAME = "PTM Sites.zip";

    public PTMDUpdater(final PTMDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        Version latestVersion = null;
        while (matcher.find()) {
            final int year = Integer.parseInt(matcher.group(4));
            final int month = TextUtils.monthNameToInt(matcher.group(1));
            final int day = Integer.parseInt(matcher.group(2));
            final Version foundVersion = new Version(year, month, day);
            if (latestVersion == null || foundVersion.compareTo(latestVersion) > 0)
                latestVersion = foundVersion;
        }
        return latestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + StringUtils.replace(fileName, " ", "%20"), fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{PROTEIN_INFORMATION_FILE_NAME, PTM_DISEASE_ASSOCIATION_FILE_NAME, PTM_SITES_FILE_NAME};
    }
}
