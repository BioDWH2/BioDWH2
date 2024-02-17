package de.unibi.agbi.biodwh2.omim.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.omim.OMIMDataSource;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OMIMUpdater extends Updater<OMIMDataSource> {
    private static final String OMIM_PAGE_URL = "https://www.omim.org/";
    private static final String MIM2GENE_DOWNLOAD_URL = "https://omim.org/static/omim/data/mim2gene.txt";
    private static final String MIM2GENE_FILENAME = "mim2gene.txt";
    static final String MIMTITLES_FILENAME = "mimTitles.txt";
    static final String GENEMAP2_FILENAME = "genemap2.txt";
    private static final String MORBIDMAP_FILENAME = "morbidmap.txt";
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile(
            "<h5>[\\s\\n]*Updated\\s+([A-Za-z]+)\\s+([0-9]+)(?:th|st|rd|nd)?,\\s+([0-9]{4})[\\s\\n]*</h5>");

    public OMIMUpdater(final OMIMDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String html = getWebsiteSource(OMIM_PAGE_URL);
        final Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(html);
        if (!matcher.find())
            return null;
        final String month = matcher.group(1).toLowerCase(Locale.ROOT);
        return parseVersion(
                matcher.group(3) + "." + TextUtils.monthNameToInt(month.toLowerCase()) + "." + matcher.group(2));
    }

    private Version parseVersion(final String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String downloadKey = dataSource.getStringProperty(workspace, "downloadKey");
        final String urlPrefix = "https://data.omim.org/downloads/" + downloadKey + "/";
        downloadFileAsBrowser(workspace, MIM2GENE_DOWNLOAD_URL, MIM2GENE_FILENAME);
        downloadFileAsBrowser(workspace, urlPrefix + MIMTITLES_FILENAME, MIMTITLES_FILENAME);
        downloadFileAsBrowser(workspace, urlPrefix + GENEMAP2_FILENAME, GENEMAP2_FILENAME);
        downloadFileAsBrowser(workspace, urlPrefix + MORBIDMAP_FILENAME, MORBIDMAP_FILENAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{MIM2GENE_FILENAME, MIMTITLES_FILENAME, GENEMAP2_FILENAME, MORBIDMAP_FILENAME};
    }
}
