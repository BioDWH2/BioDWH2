package de.unibi.agbi.biodwh2.string.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.string.STRINGDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class STRINGUpdater extends Updater<STRINGDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(STRINGUpdater.class);
    private static final String DOWNLOAD_PAGE_URL = "https://string-db.org/cgi/download?species_text=Homo+sapiens";
    private static final Pattern FILE_URL_PATTERN = Pattern.compile(
            "https://stringdb-static\\.org/download/([a-zA-Z.]+\\.v)([0-9]+\\.[0-9]+)(/9606\\.\\1\\2)?\\.(txt|fa)(\\.gz)?");
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\.v[0-9]+\\.[0-9]+");

    private final Map<String, String> fileDownloadUrls = new HashMap<>();

    public STRINGUpdater(final STRINGDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        Version version = null;
        final String source = getWebsiteSource(DOWNLOAD_PAGE_URL);
        final Matcher matcher = FILE_URL_PATTERN.matcher(source);
        fileDownloadUrls.clear();
        while (matcher.find()) {
            if (version == null)
                version = Version.tryParse(matcher.group(2));
            fileDownloadUrls.put(matcher.group(1).substring(0, matcher.group(1).length() - 2), matcher.group(0));
        }
        return version;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFile(workspace, "protein.links.full");
        downloadFile(workspace, "protein.physical.links.full");
        downloadFile(workspace, "protein.info");
        downloadFile(workspace, "protein.aliases");
        downloadFile(workspace, "species");
        downloadFile(workspace, "species.tree");
        downloadFile(workspace, "clusters.info");
        downloadFile(workspace, "clusters.proteins");
        downloadFile(workspace, "clusters.tree");
        return true;
    }

    private void downloadFile(final Workspace workspace, final String filePrefix) throws UpdaterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Downloading " + filePrefix);
        final String url = fileDownloadUrls.get(filePrefix);
        if (url == null)
            throw new UpdaterConnectionException(
                    "Failed to retrieve download url for file prefix '" + filePrefix + "'");
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            fileName = VERSION_PATTERN.matcher(fileName).replaceAll("");
            HTTPClient.downloadFileAsBrowser(url, dataSource.resolveSourceFilePath(workspace, fileName));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file from '" + url + "'", e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                "9606.protein.links.full.txt.gz", "9606.protein.physical.links.full.txt.gz", "9606.protein.info.txt.gz",
                "9606.protein.aliases.txt.gz", "species.txt", "species.tree.txt", "9606.clusters.info.txt.gz",
                "9606.clusters.proteins.txt.gz", "9606.clusters.tree.txt.gz"
        };
    }
}
