package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HGNCUpdater extends Updater {
    public HGNCUpdater() {
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public Version getNewestVersion() {
        String filePath = "pub/databases/genenames/new/tsv/hgnc_complete_set.txt";
        String dateTimeString = tryGetModificationTimeFromServer(filePath);
        if (dateTimeString == null)
            return null;
        LocalDateTime dateTime = parseFtpDateTime(dateTimeString);
        return convertDateTimeToVersion(dateTime);
    }

    private static String tryGetModificationTimeFromServer(String filePath) {
        try {
            FTPClient ftpClient = createAnonymousFtpClient();
            if (ftpClient != null) {
                String dateTimeString = ftpClient.getModificationTime(filePath);
                ftpClient.disconnect();
                return dateTimeString;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static FTPClient createAnonymousFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect("ftp.ebi.ac.uk", 21);
        boolean loginSuccess = ftpClient.login("anonymous", "anonymous");
        if (!loginSuccess) {
            ftpClient.disconnect();
            return null;
        }
        return ftpClient;
    }

    private static LocalDateTime parseFtpDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    private static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
