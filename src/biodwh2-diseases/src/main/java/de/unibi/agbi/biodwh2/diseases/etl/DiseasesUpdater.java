package de.unibi.agbi.biodwh2.diseases.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.diseases.DiseasesDataSource;

import java.util.Calendar;
import java.util.TimeZone;

public class DiseasesUpdater extends Updater<DiseasesDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "https://download.jensenlab.org/";
    //static final String TEXT_MINING_FULL_FILE_NAME = "human_disease_textmining_full.tsv";
    static final String TEXT_MINING_FILTERED_FILE_NAME = "human_disease_textmining_filtered.tsv";
    //static final String KNOWLEDGE_FULL_FILE_NAME = "human_disease_knowledge_full.tsv";
    static final String KNOWLEDGE_FILTERED_FILE_NAME = "human_disease_knowledge_filtered.tsv";
    //static final String EXPERIMENTS_FULL_FILE_NAME = "human_disease_experiments_full.tsv";
    static final String EXPERIMENTS_FILTERED_FILE_NAME = "human_disease_experiments_filtered.tsv";
    static final String INTEGRATED_FULL_FILE_NAME = "human_disease_integrated_full.tsv";

    private static final String[] FILE_NAMES = new String[]{
            TEXT_MINING_FILTERED_FILE_NAME, KNOWLEDGE_FILTERED_FILE_NAME, EXPERIMENTS_FILTERED_FILE_NAME,
            INTEGRATED_FULL_FILE_NAME
    };

    public DiseasesUpdater(final DiseasesDataSource dataSource) {
        super(dataSource);
    }

    /**
     * DISEASES is a weekly updated web resource, so we assume every monday as a new version.
     */
    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new Version(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
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
