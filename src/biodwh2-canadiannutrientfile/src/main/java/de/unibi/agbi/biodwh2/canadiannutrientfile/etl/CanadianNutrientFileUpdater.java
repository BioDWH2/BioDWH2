package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CanadianNutrientFileUpdater extends Updater<CanadianNutrientFileDataSource> {
    private static final String CNF_MAIN_URL = "https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/nutrient-data/canadian-nutrient-file-2015-download-files.html";

    private static final String CNF_DOWNLOAD_URL = "https://www.canada.ca/content/dam/hc-sc/migration/hc-sc/fn-an/alt_formats/zip/nutrition/fiche-nutri-data/cnf-fcen-csv.zip";

    private static final String DATE_SEARCH_SEQUENCE = "dateModified";

    private static final Logger LOGGER = LoggerFactory.getLogger(CanadianNutrientFileUpdater.class);

    public CanadianNutrientFileUpdater(CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        String[] lines = getCNFPageContent();

        String date = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime updateDateTime;

        for (String line : lines) {
            if (line.contains(DATE_SEARCH_SEQUENCE)) {
                date = line.split(">")[2];
                date = date.split("<")[0];
                updateDateTime = LocalDateTime.parse(date + " 00:00:00", formatter);

                LOGGER.debug("Version {} found", updateDateTime);
                return convertDateTimeToVersion(updateDateTime);
            }
        }
        LOGGER.error("No Version found for CanadianNutrientFile");
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        Path sourceFilePath = Paths.get(dataSource.resolveSourceFilePath(workspace, "cnf-fcen-csv.zip"));
        Path sourceFolderPath = sourceFilePath.getParent();

        boolean suc = false;

        try (BufferedInputStream in = new BufferedInputStream(new URL(CNF_DOWNLOAD_URL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(sourceFilePath.toFile())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            // checking file sanity
            File temp = sourceFilePath.toFile();
            suc = temp.exists() && temp.isFile() && temp.length() != 0;
        } catch (IOException e) {
            suc = false;
            LOGGER.error("Failed to download {}", CNF_DOWNLOAD_URL);
            throw new UpdaterConnectionException("Failed to download: " + CNF_DOWNLOAD_URL, e);
        }

        if (suc) {
            ZipInputStream zipIn = null;
            try {
                zipIn = new ZipInputStream(new FileInputStream(sourceFilePath.toFile()));
                ZipEntry entry = zipIn.getNextEntry();

                while (entry != null) {
                    if (entry.getName().contains("pdf") || entry.getName().contains("PDF")) {
                        entry = zipIn.getNextEntry();
                        continue;
                    }
                    String filePath = sourceFolderPath + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        extractFile(zipIn, filePath);
                    } else {
                        File dir = new File(filePath);
                        dir.mkdirs();
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
                zipIn.close();

            } catch (IOException e) {
                suc = false;
                LOGGER.error("Failed to unzip {}", sourceFilePath);
                throw new UpdaterConnectionException("Failed to unzip: " + sourceFilePath, e);
            }

        }

        if (suc)
            sourceFilePath.toFile().delete();

        return suc;
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private String[] getCNFPageContent() throws UpdaterConnectionException {
        StringBuilder buffer = new StringBuilder();

        try {
            URL url = new URL(CNF_MAIN_URL);
            InputStream is = url.openStream();
            int ptr = 0;

            while ((ptr = is.read()) != -1) {
                buffer.append((char) ptr);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get version from {}", CNF_MAIN_URL);
            throw new UpdaterConnectionException("Failed to get version from: " + CNF_MAIN_URL, e);
        }
        return buffer.toString().split("\n");
    }
}
