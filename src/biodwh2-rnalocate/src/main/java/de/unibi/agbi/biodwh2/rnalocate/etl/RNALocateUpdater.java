package de.unibi.agbi.biodwh2.rnalocate.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.rnalocate.RNALocateDataSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RNALocateUpdater extends Updater<RNALocateDataSource> {
    private static final String EXPERIMENTAL_FILE_URL = "https://www.rnalocate.org/static/download/All%20RNA%20subcellular%20localization%20information.zip";
    private static final String PREDICTED_MRNA_FILE_URL = "https://www.rnalocate.org/static/download/Predicted%20mRNA%20subcellular%20localization%20information.zip";
    private static final String PREDICTED_LNCRNA_FILE_URL = "https://www.rnalocate.org/static/download/Predicted%20lncRNA%20subcellular%20localization%20information.zip";
    static final String EXPERIMENTAL_FILE_NAME = "All RNA subcellular localization information.zip";
    static final String PREDICTED_MRNA_FILE_NAME = "Predicted mRNA subcellular localization information.zip";
    static final String PREDICTED_LNCRNA_FILE_NAME = "Predicted lncRNA subcellular localization information.zip";

    public RNALocateUpdater(final RNALocateDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final List<LocalDateTime> dateTimes = new ArrayList<>();
            final LocalDateTime dateTimeExperimental = HTTPClient.peekZipModificationDateTime(EXPERIMENTAL_FILE_URL);
            if (dateTimeExperimental != null)
                dateTimes.add(dateTimeExperimental);
            final LocalDateTime dateTimePredictedMRNA = HTTPClient.peekZipModificationDateTime(PREDICTED_MRNA_FILE_URL);
            if (dateTimePredictedMRNA != null)
                dateTimes.add(dateTimePredictedMRNA);
            final LocalDateTime dateTimePredictedLNCRNA = HTTPClient.peekZipModificationDateTime(
                    PREDICTED_MRNA_FILE_URL);
            if (dateTimePredictedLNCRNA != null)
                dateTimes.add(dateTimePredictedLNCRNA);
            if (dateTimes.isEmpty())
                return null;
            LocalDateTime newest = dateTimes.get(0);
            for (int i = 1; i < dateTimes.size(); i++)
                if (dateTimes.get(i).isAfter(newest))
                    newest = dateTimes.get(i);
            return createVersionFromDateTime(newest);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private Version createVersionFromDateTime(final LocalDateTime dateTime) {
        return new Version(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, EXPERIMENTAL_FILE_URL, EXPERIMENTAL_FILE_NAME);
        downloadFileAsBrowser(workspace, PREDICTED_MRNA_FILE_URL, PREDICTED_MRNA_FILE_NAME);
        downloadFileAsBrowser(workspace, PREDICTED_LNCRNA_FILE_URL, PREDICTED_LNCRNA_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{EXPERIMENTAL_FILE_NAME, PREDICTED_MRNA_FILE_NAME, PREDICTED_LNCRNA_FILE_NAME};
    }
}
