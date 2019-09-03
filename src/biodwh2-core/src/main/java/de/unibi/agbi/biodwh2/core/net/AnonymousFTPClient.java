package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnonymousFTPClient {
    private FTPClient client;

    public AnonymousFTPClient() {
    }

    public boolean tryConnect(String url) {
        try {
            return connect(url);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean connect(String url) throws IOException {
        client = new FTPClient();
        client.connect(url, 21);
        boolean loginSuccess = client.login("anonymous", "anonymous");
        if (!loginSuccess)
            disconnect();
        return loginSuccess;
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static LocalDateTime parseFtpDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
