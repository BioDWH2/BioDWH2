package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterOnlyManuallyException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;

public class DrugBankUpdater extends Updater {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        String source;
        try {
            source = HTTPClient.getWebsiteSource("https://www.drugbank.ca/releases");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        String versionTable = extractVersionTableFromSource(source);
        String version = extractVersionFromTableCell(versionTable);
        return parseVersion(version);
    }

    private String extractVersionTableFromSource(String source) throws UpdaterMalformedVersionException {
        int tableCutIndex = source.indexOf("<div class=\"download-table\">");
        if (tableCutIndex == -1)
            throw new UpdaterMalformedVersionException();
        return source.substring(tableCutIndex);
    }

    private String extractVersionFromTableCell(String source) throws UpdaterMalformedVersionException {
        int firstVersionCellStartIndex = source.indexOf("<td>");
        int firstVersionCellEndIndex = source.indexOf("</td>");
        if (firstVersionCellStartIndex == -1 || firstVersionCellEndIndex == -1)
            throw new UpdaterMalformedVersionException();
        return source.substring(firstVersionCellStartIndex + 4, firstVersionCellEndIndex);
    }

    private Version parseVersion(String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException {
        throw new UpdaterOnlyManuallyException();
    }
}
