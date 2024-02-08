package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DGIdbUpdater extends Updater<DGIdbDataSource> {
    private static class DownloadVersion {
        String versionText;
        Version version;
    }

    private static final String VERSION_URL = "https://dgidb.org/downloads";
    private static final String DOWNLOAD_URL_PREFIX = "https://www.dgidb.org/data/";
    private static final String MAIN_JS_URL_PREFIX = "https://www.dgidb.org/";
    static final String INTERACTIONS_FILE_NAME = "interactions.tsv";
    static final String DRUGS_FILE_NAME = "drugs.tsv";
    static final String GENES_FILE_NAME = "genes.tsv";
    static final String CATEGORIES_FILE_NAME = "categories.tsv";

    private static final Pattern MAIN_JS_PATTERN = Pattern.compile("src=\"(/static/js/main\\.[a-f0-9A-F]+\\.js)\"");
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "\"(2[0-9]{3})-(" + String.join("|", TextUtils.THREE_LETTER_MONTH_NAMES) + ")\"", Pattern.CASE_INSENSITIVE);

    public DGIdbUpdater(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final DownloadVersion newestVersion = getNewestDownloadVersion();
        return newestVersion == null ? null : newestVersion.version;
    }

    private DownloadVersion getNewestDownloadVersion() throws UpdaterException {
        final DownloadVersion[] versions = getDownloadVersions();
        int latestVersionIndex = -1;
        for (int i = 0; i < versions.length; i++)
            if (versions[i] != null && versions[i].version != null)
                latestVersionIndex = latestVersionIndex == -1 || versions[i].version.compareTo(
                        versions[latestVersionIndex].version) > 0 ? i : latestVersionIndex;
        return latestVersionIndex == -1 ? null : versions[latestVersionIndex];
    }

    private DownloadVersion[] getDownloadVersions() throws UpdaterException {
        String source = getWebsiteSource(VERSION_URL);
        final Matcher mainJSMatcher = MAIN_JS_PATTERN.matcher(source);
        if (mainJSMatcher.find())
            source = getWebsiteSource(MAIN_JS_URL_PREFIX + mainJSMatcher.group(1));
        else
            throw new UpdaterMalformedVersionException("Failed to retrieve versions");
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        final List<DownloadVersion> versions = new ArrayList<>();
        while (matcher.find()) {
            final var version = new DownloadVersion();
            version.versionText = StringUtils.strip(matcher.group(0), "\"");
            version.version = new Version(Integer.parseInt(matcher.group(1)),
                                          TextUtils.threeLetterMonthNameToInt(matcher.group(2).toLowerCase()));
            versions.add(version);
        }
        return versions.toArray(new DownloadVersion[0]);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final DownloadVersion newestVersion = getNewestDownloadVersion();
        if (newestVersion == null)
            return false;
        final String urlPrefix = DOWNLOAD_URL_PREFIX + newestVersion.versionText + '/';
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, urlPrefix + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                INTERACTIONS_FILE_NAME, DRUGS_FILE_NAME, GENES_FILE_NAME, CATEGORIES_FILE_NAME
        };
    }
}
