package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.net.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("WeakerAccess")
public final class HTTPClient {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private HTTPClient() {
    }

    @SuppressWarnings("unused")
    public static void downloadFile(final String uri, final String filePath) throws IOException {
        try (final ReadableByteChannel urlByteChannel = Channels.newChannel(new URL(uri).openStream())) {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath) throws IOException {
        try (final ReadableByteChannel urlByteChannel = Channels.newChannel(getUrlInputStream(uri))) {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    @SuppressWarnings("unused")
    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password) throws IOException {
        try (final ReadableByteChannel urlByteChannel = Channels.newChannel(
                getUrlInputStream(uri, username, password))) {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static String getWebsiteSource(final String url) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        final InputStreamReader inputReader = new InputStreamReader(getUrlInputStream(url), StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputReader);
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null)
            stringBuilder.append(inputLine);
        inputReader.close();
        return stringBuilder.toString();
    }

    public static InputStream getUrlInputStream(final String url) throws IOException {
        final URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        return urlConnection.getInputStream();
    }

    public static InputStream getUrlInputStream(final String url, final String username,
                                                final String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        final String credentials = username + ":" + password;
        final String basicAuth = "Basic " + Base64.encodeBase64String(credentials.getBytes(StandardCharsets.UTF_8))
                                                  .trim();
        urlConnection.setRequestProperty("Authorization", basicAuth);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        final String target = urlConnection.getHeaderField("location");
        if (target != null)
            urlConnection = (HttpURLConnection) new URL(target).openConnection();
        return urlConnection.getInputStream();
    }
}
