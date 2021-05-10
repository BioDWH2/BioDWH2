package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.gene2phenotype.Gen2PhenotypeDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Gen2PhenotypeUpdater extends Updater<Gen2PhenotypeDataSource> {
    private static final String G2P_MAIN_URL = "https://www.ebi.ac.uk/gene2phenotype#";
    private static final String G2P_DOWNLOAD_URL = "https://www.ebi.ac.uk/gene2phenotype/downloads/";
    private static final List<String> FILES_TO_DOWNLOAD = Arrays.asList(
            "CancerG2P.csv.gz",
            "DDG2P.csv.gz",
            "EyeG2P.csv.gz",
            "SkinG2P.csv.gz");


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

        for(int i = 0; i< lines.length; i++){
            line = lines[i];
            if (line.contains("DD panel")
                || line.contains("Eye panel")
                || line.contains("Skin panel")
                || line.contains("Cancer panel")){

                dateline = lines[i+8];
                dateline = dateline.trim();
                dateline = dateline.replace("<strong>", "");
                dateline = dateline.replace("</strong>", "");
                tempDateTime = LocalDateTime.parse(dateline + " 00:00:00", formatter);

                if (tempDateTime.isAfter(newest)){
                    newest = tempDateTime;
                }
            }
        }
        return convertDateTimeToVersion(newest);
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        System.out.println("######### yay download");
        return false;
    }

    private String[] getG2PPageContent() {
        String content_lines[];
        StringBuilder buffer = new StringBuilder();

        try {
            URL url = new URL(G2P_MAIN_URL);
            InputStream is = url.openStream();
            int ptr = 0;

            while ((ptr = is.read()) != -1) {
                buffer.append((char) ptr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString().split("\n");


    }
}
