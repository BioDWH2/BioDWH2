package de.unibi.agbi.biodwh2.core.net;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AnonymousFTPClient {
    private static final Logger LOGGER = LogManager.getLogger(AnonymousFTPClient.class);
    private FTPClient client;

    public boolean connect(final String url) throws IOException {
        client = new FTPClient();
        client.setConnectTimeout(10000);
        client.connect(url, 21);
        final boolean loginSuccess = client.login("anonymous", "anonymous");
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
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Failed to disconnect from FTP server", e);
            return false;
        }
        return true;
    }

    public void disconnect() throws IOException {
        if (client != null)
            client.disconnect();
        client = null;
    }

    public LocalDateTime getModificationTimeFromServer(final String filePath) {
        try {
            if (client != null)
                return parseFtpDateTime(client.getModificationTime(filePath));
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to get modification time for file '" + filePath + "' from FTP server", e);
        }
        return null;
    }

    private static LocalDateTime parseFtpDateTime(final String dateTimeString) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public boolean tryDownloadFile(final String url, final String outputFilepath) {
        try {
            return downloadFile(url, outputFilepath);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to download file '" + url + "' from FTP server", e);
            return false;
        }
    }

    public boolean downloadFile(final String url, final String outputFilepath) throws IOException {
        try (OutputStream outputStream = FileUtils.openOutput(outputFilepath)) {
            return client.retrieveFile(url, outputStream);
        }
    }
}
