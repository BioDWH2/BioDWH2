package de.unibi.agbi.biodwh2.omim.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.omim.OMIMDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    private final Map<String, Integer> monthNameNumberMap = new HashMap<>();

    public OMIMUpdater(final OMIMDataSource dataSource) {
        super(dataSource);
        final String[] months = {
                "january", "february", "march", "april", "may", "june", "july", "august", "september", "october",
                "november", "december"
        };
        for (int i = 0; i < months.length; i++)
            monthNameNumberMap.put(months[i], i + 1);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final String html = HTTPClient.getWebsiteSource(OMIM_PAGE_URL);
            final Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(html);
            if (!matcher.find())
                return null;
            final String month = matcher.group(1).toLowerCase(Locale.ROOT);
            return parseVersion(matcher.group(3) + "." + monthNameNumberMap.get(month) + "." + matcher.group(2));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
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
        final String downloadKey = dataSource.getProperties(workspace).get("downloadKey");
        downloadFile(MIM2GENE_DOWNLOAD_URL, dataSource.resolveSourceFilePath(workspace, MIM2GENE_FILENAME));
        downloadFile("https://data.omim.org/downloads/" + downloadKey + "/mimTitles.txt",
                     dataSource.resolveSourceFilePath(workspace, MIMTITLES_FILENAME));
        downloadFile("https://data.omim.org/downloads/" + downloadKey + "/genemap2.txt",
                     dataSource.resolveSourceFilePath(workspace, GENEMAP2_FILENAME));
        downloadFile("https://data.omim.org/downloads/" + downloadKey + "/morbidmap.txt",
                     dataSource.resolveSourceFilePath(workspace, MORBIDMAP_FILENAME));
        return true;
    }

    private void downloadFile(final String downloadUrl, final String targetFilePath) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(downloadUrl, targetFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{MIM2GENE_FILENAME, MIMTITLES_FILENAME, GENEMAP2_FILENAME, MORBIDMAP_FILENAME};
    }
}
