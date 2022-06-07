package de.unibi.agbi.biodwh2.tissues.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.tissues.TissuesDataSource;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class TissuesUpdater extends Updater<TissuesDataSource> {
    static final String INTEGRATED_FILE_NAME = "human_tissue_integrated_full.tsv";
    static final String KNOWLEDGE_FILE_NAME = "human_tissue_knowledge_full.tsv";
    static final String EXPERIMENTS_FILE_NAME = "human_tissue_experiments_full.tsv";

    public TissuesUpdater(final TissuesDataSource dataSource) {
        super(dataSource);
    }

    /**
     * TISSUES is a weekly updated web resource, so we assume every monday as a new version.
     */
    @Override
    protected Version getNewestVersion() {
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new Version(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser("https://download.jensenlab.org/human_tissue_integrated_full.tsv",
                                             dataSource.resolveSourceFilePath(workspace, INTEGRATED_FILE_NAME));
            HTTPClient.downloadFileAsBrowser("https://download.jensenlab.org/human_tissue_knowledge_full.tsv",
                                             dataSource.resolveSourceFilePath(workspace, KNOWLEDGE_FILE_NAME));
            HTTPClient.downloadFileAsBrowser("https://download.jensenlab.org/human_tissue_experiments_full.tsv",
                                             dataSource.resolveSourceFilePath(workspace, EXPERIMENTS_FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }
}
