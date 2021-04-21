package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.net.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
            final FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath) throws IOException {
        try (final ReadableByteChannel urlByteChannel = Channels.newChannel(getUrlInputStream(uri))) {
            final FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    @SuppressWarnings("unused")
    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password) throws IOException {
        try (final ReadableByteChannel urlByteChannel = Channels.newChannel(
                getUrlInputStream(uri, username, password))) {
            final FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static String getWebsiteSource(final String url) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        final InputStreamReader inputReader = new InputStreamReader(getUrlInputStream(url), StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputReader);
        String inputLine = bufferedReader.readLine();
        while (inputLine != null) {
            stringBuilder.append(inputLine);
            inputLine = bufferedReader.readLine();
        }
        inputReader.close();
        return stringBuilder.toString();
    }

    public static InputStream getUrlInputStream(final String url) throws IOException {
        return getUrlInputStream(url, null, null);
    }

    public static InputStream getUrlInputStream(final String url, final String username,
                                                final String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        if (username != null && password != null)
            urlConnection.setRequestProperty("Authorization", getBasicAuthForCredentials(username, password));
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        urlConnection = redirectURLConnectionIfNecessary(urlConnection);
        return urlConnection.getInputStream();
    }

    private static String getBasicAuthForCredentials(final String username, final String password) {
        final String credentials = username + ":" + password;
        return "Basic " + Base64.encodeBase64String(credentials.getBytes(StandardCharsets.UTF_8)).trim();
    }

    private static HttpURLConnection redirectURLConnectionIfNecessary(HttpURLConnection connection) throws IOException {
        final String target = connection.getHeaderField("location");
        return target == null ? connection : (HttpURLConnection) new URL(target).openConnection();
    }
}
