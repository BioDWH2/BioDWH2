package de.unibi.agbi.biodwh2.ema.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.ema.EMADataSource;

import java.util.Calendar;

/**
 * https://www.ema.europa.eu/en/medicines/download-medicine-data
 */
public class EMAUpdater extends Updater<EMADataSource> {
    static final String EPAR_TABLE_FILE_NAME = "medicines_output_european_public_assessment_reports_en.xlsx";
    static final String HMPC_TABLE_FILE_NAME = "medicines_output_herbal_medicines_en.xlsx";
    private static final String EPAR_TABLE_URL =
            "https://www.ema.europa.eu/system/files/documents/other/" + EPAR_TABLE_FILE_NAME;
    private static final String HMPC_TABLE_URL =
            "https://www.ema.europa.eu/system/files/documents/other/" + HMPC_TABLE_FILE_NAME;

    public EMAUpdater(final EMADataSource dataSource) {
        super(dataSource);
    }

    /**
     * EMA updates these medicine data tables once a day
     */
    @Override
    public Version getNewestVersion(final Workspace workspace) {
        final Calendar today = Calendar.getInstance();
        return new Version(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, EPAR_TABLE_URL, EPAR_TABLE_FILE_NAME);
        downloadFileAsBrowser(workspace, HMPC_TABLE_URL, HMPC_TABLE_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{EPAR_TABLE_FILE_NAME, HMPC_TABLE_FILE_NAME};
    }
}
