package de.unibi.agbi.biodwh2.rnalocate.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.rnalocate.RNALocateDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RNALocateUpdater extends Updater<RNALocateDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "http://www.rnalocate.org/static/download/";
    static final String EXPERIMENTAL_FILE_NAME = "All RNA subcellular localization information.zip";
    static final String PREDICTED_MRNA_FILE_NAME = "Predicted mRNA subcellular localization information.zip";
    static final String PREDICTED_LNCRNA_FILE_NAME = "Predicted lncRNA subcellular localization information.zip";
    static final String PREDICTED_MIRNA_FILE_NAME = "Predicted miRNA subcellular localization information.zip";
    static final String PREDICTED_SNORNA_FILE_NAME = "Predicted snoRNA subcellular localization information.zip";

    public RNALocateUpdater(final RNALocateDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final List<LocalDateTime> dateTimes = new ArrayList<>();
            for (final String fileName : expectedFileNames()) {
                final LocalDateTime dateTimeExperimental = HTTPClient.peekZipModificationDateTime(
                        DOWNLOAD_URL_PREFIX + StringUtils.replace(fileName, " ", "%20"));
                if (dateTimeExperimental != null)
                    dateTimes.add(dateTimeExperimental);
            }
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
        downloadFile(workspace, EXPERIMENTAL_FILE_NAME);
        downloadFile(workspace, PREDICTED_MRNA_FILE_NAME);
        downloadFile(workspace, PREDICTED_LNCRNA_FILE_NAME);
        downloadFile(workspace, PREDICTED_MIRNA_FILE_NAME);
        downloadFile(workspace, PREDICTED_SNORNA_FILE_NAME);
        return true;
    }

    private void downloadFile(final Workspace workspace, final String fileName) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + StringUtils.replace(fileName, " ", "%20"), fileName);

    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                EXPERIMENTAL_FILE_NAME, PREDICTED_MRNA_FILE_NAME, PREDICTED_LNCRNA_FILE_NAME, PREDICTED_MIRNA_FILE_NAME,
                PREDICTED_SNORNA_FILE_NAME
        };
    }
}
