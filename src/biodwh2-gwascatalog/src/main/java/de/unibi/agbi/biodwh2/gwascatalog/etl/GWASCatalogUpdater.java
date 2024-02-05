package de.unibi.agbi.biodwh2.gwascatalog.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.gwascatalog.GWASCatalogDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GWASCatalogUpdater extends Updater<GWASCatalogDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
    private static final String ASSOCIATIONS_FILE_URL = "https://www.ebi.ac.uk/gwas/api/search/downloads/alternative";
    static final String ASSOCIATIONS_FILE_NAME = "gwas_catalog_v1.0.2-associations.tsv";
    private static final String STUDIES_FILE_URL = "https://www.ebi.ac.uk/gwas/api/search/downloads/studies_alternative";
    static final String STUDIES_FILE_NAME = "gwas_catalog_v1.0.2-studies.tsv";
    private static final String ANCESTRY_FILE_URL = "https://www.ebi.ac.uk/gwas/api/search/downloads/ancestry";
    static final String ANCESTRY_FILE_NAME = "gwas_catalog-ancestry.tsv";

    public GWASCatalogUpdater(final GWASCatalogDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final String fileName = HTTPClient.resolveFileName(ASSOCIATIONS_FILE_URL);
            final Matcher matcher = VERSION_PATTERN.matcher(fileName);
            if (matcher.find())
                return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                                   Integer.parseInt(matcher.group(3)));
            return null;
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version", e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, ASSOCIATIONS_FILE_URL, ASSOCIATIONS_FILE_NAME);
        downloadFileAsBrowser(workspace, STUDIES_FILE_URL, STUDIES_FILE_NAME);
        downloadFileAsBrowser(workspace, ANCESTRY_FILE_URL, ANCESTRY_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{ASSOCIATIONS_FILE_NAME, STUDIES_FILE_NAME, ANCESTRY_FILE_NAME};
    }
}
