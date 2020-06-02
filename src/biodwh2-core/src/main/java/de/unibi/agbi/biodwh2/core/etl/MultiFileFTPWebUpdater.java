package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class MultiFileFTPWebUpdater<D extends DataSource> extends Updater<D> {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            Map<String, String> prefixSourceMap = new HashMap<>();
            Version latestVersion = null;
            String separator = System.getProperty("file.separator");
            for (String fileName : getFilePaths()) {
                Path parentPath = Paths.get(fileName).getParent();
                String prefix = parentPath != null ? parentPath.toString().replace(separator, "/") : "";
                if (!prefixSourceMap.containsKey(prefix))
                    prefixSourceMap.put(prefix, HTTPClient.getWebsiteSource(getFTPIndexUrl() + fileName));
                String source = prefixSourceMap.get(prefix);
                Version fileVersion = getVersionForFileName(source, fileName);
                if (latestVersion == null || (fileVersion != null && fileVersion.compareTo(latestVersion) > 0))
                    latestVersion = fileVersion;
            }
            return latestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private static Version getVersionForFileName(final String source, final String fileName) {
        String searchKey = "<a href=\"" + fileName + "\">";
        if (!source.contains(searchKey))
            return null;
        String date = source.split(searchKey)[1].split("<td")[1].split("[<>]")[1].split(" ")[0];
        String[] dateParts = date.split("-");
        return new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                           Integer.parseInt(dateParts[2]));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace, final D dataSource) throws UpdaterException {
        try {
            for (String fileName : getFilePaths()) {
                String localFileName = Paths.get(fileName).getFileName().toString();
                String resolvedFilePath = dataSource.resolveSourceFilePath(workspace, localFileName);
                HTTPClient.downloadFileAsBrowser(getFTPIndexUrl() + fileName, resolvedFilePath);
            }
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    protected abstract String getFTPIndexUrl();

    protected abstract String[] getFilePaths();
}
