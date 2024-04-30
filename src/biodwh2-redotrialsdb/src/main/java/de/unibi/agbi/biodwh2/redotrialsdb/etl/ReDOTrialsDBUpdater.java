package de.unibi.agbi.biodwh2.redotrialsdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.redotrialsdb.ReDOTrialsDBDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ReDOTrialsDBUpdater extends Updater<ReDOTrialsDBDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "<span id='Last_Import'>\\s*([0-9]{2}/[0-9]{2}/[0-9]{4})", Pattern.CASE_INSENSITIVE);
    static final String FILE_NAME = "ReDO_Trials_DB.txt";
    private static final String DOWNLOAD_URL = "https://sciencedata.anticancerfund.org/pages/" + FILE_NAME;

    public ReDOTrialsDBUpdater(final ReDOTrialsDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("https://www.anticancerfund.org/en/redo-trials-db");
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            final String[] parts = StringUtils.split(matcher.group(1), '/');
            return new Version(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}