package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.gene2phenotype.Gen2PhenotypeDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Gen2PhenotypeUpdater extends Updater<Gen2PhenotypeDataSource> {
    private static final String G2P_MAIN_URL = "https://www.ebi.ac.uk/gene2phenotype#";
    private static final String G2P_DOWNLOAD_URL = "https://www.ebi.ac.uk/gene2phenotype/downloads/";
    private static final List<String> FILES = Arrays.asList("CancerG2P.csv", "DDG2P.csv",
                                                            "EyeG2P.csv", "SkinG2P.csv");


    public Gen2PhenotypeUpdater(Gen2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        String[] lines = getG2PPageContent();

        String line = "";
        String dateline = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime newest = LocalDateTime.parse("1970-01-01 00:00:00", formatter);
        LocalDateTime tempDateTime;

        for (int i = 0; i < lines.length; i++) {
            line = lines[i];
            if (line.contains("DD panel") || line.contains("Eye panel") || line.contains("Skin panel") || line.contains(
                    "Cancer panel")) {

                dateline = lines[i + 8];
                dateline = dateline.trim();
                dateline = dateline.replace("<strong>", "");
                dateline = dateline.replace("</strong>", "");
                tempDateTime = LocalDateTime.parse(dateline + " 00:00:00", formatter);

                if (tempDateTime.isAfter(newest)) {
                    newest = tempDateTime;
                }
            }
        }
        System.out.println("########### Version: " + convertDateTimeToVersion(newest));
        return convertDateTimeToVersion(newest);
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        boolean succ = true;
        for (String file : FILES){
            System.out.println("########### Download: " + G2P_DOWNLOAD_URL + file + ".gz");
            succ &= downloadFile(workspace, dataSource, file);
        }
        return false;
    }

    private String[] getG2PPageContent() throws UpdaterConnectionException {
        StringBuilder buffer = new StringBuilder();

        try {
            URL url = new URL(G2P_MAIN_URL);
            InputStream is = url.openStream();
            int ptr = 0;

            while ((ptr = is.read()) != -1) {
                buffer.append((char) ptr);
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to get version from: " + G2P_MAIN_URL, e);
        }
        return buffer.toString().split("\n");
    }

    private boolean downloadFile(Workspace workspace, DataSource dataSource, String fileName) throws UpdaterConnectionException {
        boolean succ;
        String sourceFilePath = dataSource.resolveSourceFilePath(workspace, fileName);

        try {
            URL url = new URL(G2P_DOWNLOAD_URL + fileName + ".gz");
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            GZIPInputStream gZIPInputStream = new GZIPInputStream(is);
            FileOutputStream fileOutputStream = new FileOutputStream(sourceFilePath);

            byte[] buffer = new byte[1024];
            int bytes_read;
            while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes_read);
            }
            gZIPInputStream.close();
            fileOutputStream.close();
            File temp = new File(sourceFilePath);
            succ = temp.exists() && temp.isFile() && temp.length() != 0;
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download: " + G2P_DOWNLOAD_URL + fileName + ".gz", e);
        }

        return succ;
    }
}
