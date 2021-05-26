package de.unibi.agbi.biodwh2.ema.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.ema.EMADataSource;

import java.io.IOException;
import java.util.Calendar;

/**
 * https://www.ema.europa.eu/en/medicines/download-medicine-data
 */
public class EMAUpdater extends Updater<EMADataSource> {
    private static final String EPAR_TABLE_URL = "https://www.ema.europa.eu/sites/default/files/Medicines_output_european_public_assessment_reports.xlsx";
    static final String EPAR_TABLE_FILE_NAME = "Medicines_output_european_public_assessment_reports.xlsx";
    private static final String HMPC_TABLE_URL = "https://www.ema.europa.eu/sites/default/files/Medicines_output_herbal_medicines.xlsx";
    static final String HMPC_TABLE_FILE_NAME = "Medicines_output_herbal_medicines.xlsx";

    public EMAUpdater(final EMADataSource dataSource) {
        super(dataSource);
    }

    /**
     * EMA updates these medicine data tables once a day
     */
    @Override
    public Version getNewestVersion() {
        final Calendar today = Calendar.getInstance();
        return new Version(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(EPAR_TABLE_URL,
                                             dataSource.resolveSourceFilePath(workspace, EPAR_TABLE_FILE_NAME));
            HTTPClient.downloadFileAsBrowser(HMPC_TABLE_URL,
                                             dataSource.resolveSourceFilePath(workspace, HMPC_TABLE_FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }
}
