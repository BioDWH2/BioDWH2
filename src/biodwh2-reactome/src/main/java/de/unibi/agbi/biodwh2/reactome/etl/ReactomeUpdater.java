package de.unibi.agbi.biodwh2.reactome.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sql.SQLToTSVConverter;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.reactome.ReactomeDataSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class ReactomeUpdater extends Updater<ReactomeDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(ReactomeUpdater.class);
    private static final String DOWNLOAD_URL_PREFIX = "https://reactome.org/download/current/databases/";
    static final String FILE_NAME = "gk_current.sql.gz";

    public ReactomeUpdater(final ReactomeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("https://reactome.org/ContentService/data/database/version").trim();
        if (NumberUtils.isCreatable(source))
            return new Version(Integer.parseInt(source), 0);
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + FILE_NAME, FILE_NAME);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Removing old extracted tables...");
        for (final String fileName : dataSource.listSourceFiles(workspace))
            if (!FILE_NAME.equals(fileName))
                FileUtils.safeDelete(dataSource.resolveSourceFilePath(workspace, fileName));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Extracting Reactome sql tables as tsv...");
        try (final GZIPInputStream inputStream = FileUtils.openGzip(workspace, dataSource, FILE_NAME)) {
            if (!SQLToTSVConverter.process(inputStream, dataSource.getSourceFolderPath(workspace)))
                throw new UpdaterException("Failed to extract sql tables as tsv from file '" + FILE_NAME + "'");
        } catch (IOException e) {
            throw new UpdaterException("Failed to extract sql tables as tsv from file '" + FILE_NAME + "'", e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
