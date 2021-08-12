package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KeggUpdater extends Updater<KeggDataSource> {
    static final String DGROUP_FILE_NAME = "dgroup";
    static final String DRUG_FILE_NAME = "drug";
    static final String DISEASE_FILE_NAME = "disease";
    static final String NETWORK_FILE_NAME = "network";
    static final String VARIANT_FILE_NAME = "variant";
    private static final String FTP_BASE_PATH = "pub/kegg/medicus/";
    private static final String HUMAN_GENES_LIST_URL = "http://rest.kegg.jp/list/hsa";
    static final String HUMAN_GENES_LIST_FILE_NAME = "human_genes_list.tsv";
    private static final String COMPOUNDS_LIST_URL = "http://rest.kegg.jp/list/compound";
    static final String COMPOUNDS_LIST_FILE_NAME = "compounds_list.tsv";
    private static final String ORGANISMS_LIST_URL = "http://rest.kegg.jp/list/organism";
    static final String ORGANISMS_LIST_FILE_NAME = "organisms_list.tsv";

    public KeggUpdater(KeggDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        List<LocalDateTime> folderDateTimes = new ArrayList<>();
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTP_BASE_PATH + "dgroup/dgroup"));
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTP_BASE_PATH + "disease/disease"));
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTP_BASE_PATH + "drug/drug"));
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTP_BASE_PATH + "network/network"));
        ftpClient.tryDisconnect();
        Version newestVersion = null;
        for (LocalDateTime dateTime : folderDateTimes) {
            Version dateTimeVersion = dateTime != null ? convertDateTimeToVersion(dateTime) : null;
            if (dateTimeVersion != null && dateTimeVersion.compareTo(newestVersion) >= 0)
                newestVersion = dateTimeVersion;
        }
        return newestVersion;
    }

    private AnonymousFTPClient connectToFTP() throws UpdaterConnectionException {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected;
        try {
            isConnected = ftpClient.connect("ftp.genome.jp");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        if (!isConnected)
            throw new UpdaterConnectionException();
        return ftpClient;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        boolean success = updateFile(workspace, dataSource, ftpClient, "dgroup/" + DGROUP_FILE_NAME);
        success = success && updateFile(workspace, dataSource, ftpClient, "disease/" + DISEASE_FILE_NAME);
        success = success && updateFile(workspace, dataSource, ftpClient, "drug/" + DRUG_FILE_NAME);
        success = success && updateFile(workspace, dataSource, ftpClient, "network/" + NETWORK_FILE_NAME);
        success = success && updateFile(workspace, dataSource, ftpClient, "network/" + VARIANT_FILE_NAME);
        success = success && downloadAPIFile(workspace, HUMAN_GENES_LIST_URL, HUMAN_GENES_LIST_FILE_NAME);
        success = success && downloadAPIFile(workspace, COMPOUNDS_LIST_URL, COMPOUNDS_LIST_FILE_NAME);
        success = success && downloadAPIFile(workspace, ORGANISMS_LIST_URL, ORGANISMS_LIST_FILE_NAME);
        return success;
    }

    private boolean updateFile(Workspace workspace, DataSource dataSource, AnonymousFTPClient ftpClient,
                               String filePath) throws UpdaterException {
        String fileName = Paths.get(filePath).getFileName().toString();
        try {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
            return ftpClient.downloadFile(FTP_BASE_PATH + filePath, sourceFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
        }
    }

    private boolean downloadAPIFile(final Workspace workspace, final String url,
                                    final String fileName) throws UpdaterConnectionException {
        try {
            HTTPClient.downloadFileAsBrowser(url, dataSource.resolveSourceFilePath(workspace, fileName));
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                HUMAN_GENES_LIST_FILE_NAME, COMPOUNDS_LIST_FILE_NAME, ORGANISMS_LIST_FILE_NAME, DGROUP_FILE_NAME,
                DRUG_FILE_NAME, DISEASE_FILE_NAME, NETWORK_FILE_NAME, VARIANT_FILE_NAME
        };
    }
}
