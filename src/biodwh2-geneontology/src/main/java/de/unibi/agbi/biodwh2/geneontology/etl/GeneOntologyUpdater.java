package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GeneOntologyUpdater extends Updater<GeneOntologyDataSource> {
    private static final String DownloadUrl = "http://current.geneontology.org/ontology/go.obo";

    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            return getVersionFromDownloadFile();
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version number", e);
        }
    }

    private Version getVersionFromDownloadFile() throws IOException {
        InputStreamReader inputReader = new InputStreamReader(HTTPClient.getUrlInputStream(DownloadUrl),
                                                              StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        bufferedReader.readLine();
        String versionLine = bufferedReader.readLine();
        String[] versionParts = versionLine.replace("data-version: releases/", "").split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, GeneOntologyDataSource dataSource) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(DownloadUrl, dataSource.resolveSourceFilePath(workspace, "go.obo"));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download 'go.obo'", e);
        }
        return true;
    }
}
