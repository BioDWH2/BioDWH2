package de.unibi.agbi.biodwh2.core.net;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public final class HTTPClient {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36";

    private HTTPClient() {
    }

    @SuppressWarnings("unused")
    public static void downloadFile(final String uri, final String filePath) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(new URL(uri).openStream());
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(getUrlInputStream(uri));
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(getUrlInputStream(uri, username, password));
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static String getWebsiteSource(final String url) throws IOException {
        return getWebsiteSource(url, null, null);
    }

    public static String getWebsiteSource(final String url, final String username,
                                          final String password) throws IOException {
        final StringBuilder result = new StringBuilder();
        try (BufferedReader reader = FileUtils.createBufferedReaderFromStream(
                getUrlInputStream(url, username, password))) {
            String inputLine = reader.readLine();
            while (inputLine != null) {
                result.append(inputLine).append('\n');
                inputLine = reader.readLine();
            }
        }
        return result.toString();
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

    public static String resolveUrlLocation(final String url) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        final String target = urlConnection.getHeaderField("location");
        urlConnection.disconnect();
        return target != null ? target : url;
    }

    public static String resolveFileName(final String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        String contentDisposition = urlConnection.getHeaderField("Content-Disposition");
        urlConnection = redirectURLConnectionIfNecessary(urlConnection);
        final String redirectedUrl = urlConnection.getURL().toString();
        contentDisposition = contentDisposition != null ? contentDisposition : urlConnection.getHeaderField(
                "Content-Disposition");
        urlConnection.disconnect();
        if (contentDisposition != null) {
            final String[] parts = StringUtils.split(contentDisposition, ';');
            for (final String part : parts) {
                if (part.trim().startsWith("filename=")) {
                    String fileName = StringUtils.split(part.trim(), "=", 2)[1];
                    return StringUtils.strip(fileName, "\" \t");
                }
            }
        }
        return FilenameUtils.getName(redirectedUrl);
    }
}
