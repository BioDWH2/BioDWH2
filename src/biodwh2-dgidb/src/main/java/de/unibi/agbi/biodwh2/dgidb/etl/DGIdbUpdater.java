package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DGIdbUpdater extends Updater<DGIdbDataSource> {
    private static class DownloadVersion {
        Version version;
        final Map<String, String> files = new HashMap<>();
    }

    private static final String LATEST_RELEASE_URL = "https://dgidb.org/downloads";
    private static final String DOWNLOAD_URL_PREFIX = "https://www.dgidb.org/";

    public DGIdbUpdater(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final DownloadVersion newestVersion = getNewestDownloadVersion();
        return newestVersion == null ? null : newestVersion.version;
    }

    private DownloadVersion getNewestDownloadVersion() throws UpdaterConnectionException {
        final DownloadVersion[] versions = getDownloadVersions();
        int latestVersionIndex = -1;
        for (int i = 0; i < versions.length; i++)
            if (versions[i] != null && versions[i].version != null)
                latestVersionIndex = latestVersionIndex == -1 || versions[i].version.compareTo(
                        versions[latestVersionIndex].version) > 0 ? i : latestVersionIndex;
        return latestVersionIndex == -1 ? null : versions[latestVersionIndex];
    }

    private DownloadVersion[] getDownloadVersions() throws UpdaterConnectionException {
        final String source;
        try {
            source = HTTPClient.getWebsiteSource(LATEST_RELEASE_URL);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        final Document document = Jsoup.parse(source);
        final Element table = document.selectFirst("table#tsv_downloads");
        final Element tableBody = table.selectFirst("tbody");
        final List<DownloadVersion> versions = new ArrayList<>();
        for (Element row : tableBody.select("tr"))
            versions.add(parseVersionRow(row.select("td")));
        return versions.toArray(new DownloadVersion[0]);
    }

    private DownloadVersion parseVersionRow(final Elements row) {
        DownloadVersion version = new DownloadVersion();
        version.version = parseVersion(row.get(0).html());
        for (int i = 1; i < row.size(); i++) {
            final Element link = row.get(i).selectFirst("a");
            version.files.put(link.html(), link.attr("href"));
        }
        return version;
    }

    private Version parseVersion(final String value) {
        final String[] parts = value.split("-");
        return new Version(Integer.parseInt(parts[0]), TextUtils.threeLetterMonthNameToInt(parts[1].toLowerCase()));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final DownloadVersion newestVersion = getNewestDownloadVersion();
        if (newestVersion == null)
            return false;
        for (final String fileName : newestVersion.files.keySet())
            downloadFile(workspace, dataSource, DOWNLOAD_URL_PREFIX + newestVersion.files.get(fileName), fileName);
        return true;
    }

    private void downloadFile(final Workspace workspace, final DataSource dataSource, final String url,
                              final String fileName) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(url, dataSource.resolveSourceFilePath(workspace, fileName));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file \"" + url + "\"", e);
        }
    }
}
