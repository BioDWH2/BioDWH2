package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnonymousFTPClient {
    private FTPClient client;

    public AnonymousFTPClient() {
    }

    public boolean connect(String url) throws IOException {
        client = new FTPClient();
        client.connect(url, 21);
        boolean loginSuccess = client.login("anonymous", "anonymous");
        if (!loginSuccess) {
            disconnect();
            return false;
        }
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);
        return true;
    }

    public boolean tryDisconnect() {
        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void disconnect() throws IOException {
        if (client != null)
            client.disconnect();
        client = null;
    }

    public LocalDateTime getModificationTimeFromServer(String filePath) {
        try {
            if (client != null) {
                String dateTime = client.getModificationTime(filePath);
                return parseFtpDateTime(dateTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static LocalDateTime parseFtpDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public boolean tryDownloadFile(String url, String outputFilepath) {
        try {
            return downloadFile(url, outputFilepath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean downloadFile(String url, String outputFilepath) throws IOException {
        File outputFile = new File(outputFilepath);
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        boolean success = client.retrieveFile(url, outputStream);
        outputStream.close();
        return success;
    }
}
