package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PharmGKBUpdater extends Updater<PharmGKBDataSource> {
    static final String[] FILE_NAMES = {
            "genes.zip", "chemicals.zip", "variants.zip", "phenotypes.zip", "clinicalAnnotations.zip",
            "variantAnnotations.zip", "dosingGuidelines.json.zip", "drugLabels.zip", "pathways-tsv.zip",
            "clinicalVariants.zip", "occurrences.zip", "automated_annotations.zip"
    };

    public PharmGKBUpdater(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
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
                    break;
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return stringDate != null ? convertDateTimeToVersion(stringDate) : null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES)
            downloadFileAsBrowser(workspace, "https://s3.pgkb.org/data/" + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return FILE_NAMES;
    }
}
