package de.unibi.agbi.biodwh2.cancerdrugsdb.etl;

import de.unibi.agbi.biodwh2.cancerdrugsdb.CancerDrugsDBDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CancerDrugsDBUpdater extends Updater<CancerDrugsDBDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "Database build date:\\s+([0-9]{2}/[0-9]{2}/[0-9]{2})", Pattern.CASE_INSENSITIVE);
    static final String FILE_NAME = "cancerdrugsdb.txt";
    private static final String DOWNLOAD_URL = "https://acfdata.coworks.be/" + FILE_NAME;

    public CancerDrugsDBUpdater(final CancerDrugsDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("https://www.anticancerfund.org/en/cancerdrugs-db");
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
