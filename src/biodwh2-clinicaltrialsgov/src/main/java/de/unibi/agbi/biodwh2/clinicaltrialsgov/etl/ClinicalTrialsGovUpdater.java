package de.unibi.agbi.biodwh2.clinicaltrialsgov.etl;

import de.unibi.agbi.biodwh2.clinicaltrialsgov.ClinicalTrialsGovDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClinicalTrialsGovUpdater extends Updater<ClinicalTrialsGovDataSource> {
    static final String FILE_NAME = "AllPublicXML.zip";
    private static final String DOWNLOAD_URL = "https://clinicaltrials.gov/" + FILE_NAME;

    public ClinicalTrialsGovUpdater(final ClinicalTrialsGovDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        final int updateIntervalDays = Math.max(1, dataSource.getIntegerProperty(workspace, "updateIntervalDays", 7));
        final LocalDate start = LocalDate.parse("01.01.2022", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        final LocalDate now = LocalDate.now();
        final long daysBetween = Duration.between(start.atStartOfDay(), now.atStartOfDay()).toDays();
        final long times = Math.floorDiv(daysBetween, updateIntervalDays);
        final LocalDate versionDate = start.plusDays(times * updateIntervalDays);
        return new Version(versionDate.getYear(), versionDate.getMonthValue(), versionDate.getDayOfMonth());
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
