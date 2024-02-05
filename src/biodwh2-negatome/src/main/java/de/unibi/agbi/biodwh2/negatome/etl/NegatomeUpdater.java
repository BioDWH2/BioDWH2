package de.unibi.agbi.biodwh2.negatome.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.negatome.NegatomeDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NegatomeUpdater extends Updater<NegatomeDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("<h1>The Negatome Database (\\d)\\.(\\d)</h1>");
    private static final String DOWNLOAD_URL_PREFIX = "http://mips.helmholtz-muenchen.de/proj/ppi/negatome/";
    private static final String[] FILE_NAMES = new String[]{
            "manual.txt", "manual_stringent.txt", "manual_pfam.txt", "pdb.txt", "pdb_stringent.txt", "pdb_pfam.txt",
            "combined.txt", "combined_stringent.txt", "combined_pfam.txt"
    };

    public NegatomeUpdater(final NegatomeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String html = getWebsiteSource(DOWNLOAD_URL_PREFIX);
        final Matcher matcher = VERSION_PATTERN.matcher(html);
        if (!matcher.find())
            return null;
        return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES)
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return FILE_NAMES;
    }
}
