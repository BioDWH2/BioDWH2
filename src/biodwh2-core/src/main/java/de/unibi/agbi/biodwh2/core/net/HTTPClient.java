package de.unibi.agbi.biodwh2.core.net;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public final class HTTPClient {
    private HTTPClient() {
    }

    public static void downloadFile(String uri, String filePath) throws IOException {
        ReadableByteChannel urlByteChannel = Channels.newChannel(new URL(uri).openStream());
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
    }

    public static String getWebsiteSource(String url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputReader = new InputStreamReader(getUrlInputStream(url), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null)
            stringBuilder.append(inputLine);
        inputReader.close();
        return stringBuilder.toString();
    }

    private static InputStream getUrlInputStream(String url) throws IOException {
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent",
                                         "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        return urlConnection.getInputStream();
    }
}
