package de.unibi.agbi.biodwh2.tarbase.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.tarbase.TarBaseDataSource;

public class TarBaseUpdater extends Updater<TarBaseDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "https://dianalab.e-ce.uth.gr/tarbasev9/data/";
    static final String[] FILE_NAMES = new String[]{
            "Homo_sapiens_TarBase_v9.tsv.gz", "Mus_musculus_TarBase_v9.tsv.gz", "Viral_species_TarBase-v9.tsv.gz",
            "Other_species_TarBase_v9.tsv.gz"
    };

    public TarBaseUpdater(final TarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        return new Version(9, 0);
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
