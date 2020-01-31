package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class PharmGKBUpdater extends Updater {

    @Override
    public Version getNewestVersion() throws UpdaterException {
        LocalDateTime stringDate = null;

        try {
            File f = File.createTempFile("biodwh2pharmgkb-drugLabels", ".zip");
            HTTPClient.downloadFileAsBrowser("https://s3.pgkb.org/data/drugLabels.zip", f.getAbsolutePath());
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(f));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().contains("CREATED")) {
                    String entry = zipEntry.getName();
                    String d1 = entry.split("_")[1];
                    String d2 = d1.split("\\.")[0] + " 00:00";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    stringDate = LocalDateTime.parse(d2, formatter);
                }
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }

        return convertDateTimeToVersion(stringDate);
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException {
        boolean success = false;
        String[] fileNames = {
                "genes.zip", "drugs.zip", "chemicals.zip", "variants.zip", "phenotypes.zip", "annotations.zip",
                "relationships.zip", "dosingGuidelines.json.zip", "drugLabels.zip", "pathways-tsv.zip",
                "clinicalVariants.zip", "occurrences.zip", "automated_annotations.zip", "occurrences.zip"
        };

        for (String name : fileNames) {
            success = downloadFile(name, workspace, dataSource);
        }

        return success;
    }

    public boolean downloadFile(String fileName, Workspace workspace,
                                DataSource dataSource) throws UpdaterConnectionException {

        try {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
            HTTPClient.downloadFileAsBrowser("https://s3.pgkb.org/data/" + fileName, sourceFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
        }
        return true;
    }

}
