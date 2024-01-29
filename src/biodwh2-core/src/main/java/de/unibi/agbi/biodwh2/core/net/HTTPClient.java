package de.unibi.agbi.biodwh2.core.net;

import de.unibi.agbi.biodwh2.core.BinaryUtils;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

public final class HTTPClient {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private HTTPClient() {
    }

    @SuppressWarnings("unused")
    public static void downloadFile(final String uri, final String filePath) throws IOException {
        downloadStream(new URL(uri).openStream(), filePath);
    }

    public static void downloadStream(final InputStream stream, final String filePath) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(stream);
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void downloadStream(final InputStream stream, final OutputStream outputStream) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(stream)) {
            final WritableByteChannel outputChannel = Channels.newChannel(outputStream);
            fastCopy(urlByteChannel, outputChannel);
        }
    }

    public static void fastCopy(final ReadableByteChannel in, final WritableByteChannel out) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (in.read(buffer) != -1) {
            buffer.flip();
            out.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining())
            out.write(buffer);
    }

    public static void downloadFileAsBrowser(final String uri, final OutputStream stream) throws IOException {
        downloadStream(getUrlInputStream(uri), stream);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath) throws IOException {
        downloadStream(getUrlInputStream(uri), filePath);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password), filePath);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password,
                                             final Map<String, String> additionalHeaders) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password, additionalHeaders), filePath);
    }

    public static String getWebsiteSource(final String url) throws IOException {
        return getWebsiteSource(url, null, null, 0);
    }

    public static String getWebsiteSource(final String url, int retries) throws IOException {
        return getWebsiteSource(url, null, null, retries);
    }

    public static String getWebsiteSource(final String url, final String username,
                                          final String password) throws IOException {
        return getWebsiteSource(url, username, password, 0);
    }

    public static String getWebsiteSource(final String url, final String username, final String password,
                                          int retries) throws IOException {
        int counter = 0;
        while (counter <= retries) {
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = FileUtils.createBufferedReaderFromStream(
                    getUrlInputStream(url, username, password))) {
                String inputLine = reader.readLine();
                while (inputLine != null) {
                    result.append(inputLine).append('\n');
                    inputLine = reader.readLine();
                }
            } catch (IOException ex) {
                if (counter < retries) {
                    counter++;
                    continue;
                }
                throw ex;
            }
            return result.toString();
        }
        return null;
    }

    public static InputStream getUrlInputStream(final String url) throws IOException {
        return getUrlInputStream(url, null, null, null);
    }

    public static InputStream getUrlInputStream(final String url, final String username,
                                                final String password) throws IOException {
        return getUrlInputStream(url, username, password, null);
    }

    public static InputStream getUrlInputStream(final String url, final String username, final String password,
                                                final Map<String, String> additionalHeaders) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        if (username != null && password != null)
            urlConnection.setRequestProperty("Authorization", getBasicAuthForCredentials(username, password));
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        if (additionalHeaders != null)
            for (final Map.Entry<String, String> entry : additionalHeaders.entrySet())
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        urlConnection = redirectURLConnectionIfNecessary(urlConnection);
        return urlConnection.getInputStream();
    }

    private static String getBasicAuthForCredentials(final String username, final String password) {
        final String credentials = username + ":" + password;
        return "Basic " + Base64.getMimeEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)).trim();
    }

    private static HttpURLConnection redirectURLConnectionIfNecessary(HttpURLConnection connection) throws IOException {
        final String target = connection.getHeaderField("location");
        if (target == null)
            return connection;
        connection = (HttpURLConnection) new URL(target).openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.connect();
        connection = redirectURLConnectionIfNecessary(connection);
        return connection;
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

    public static LocalDateTime peekZipModificationDateTime(final String url) throws IOException {
        final InputStream stream = getUrlInputStream(url);
        if (stream != null) {
            final byte[] data = new byte[14];
            final int bytesRead = stream.read(data);
            stream.close();
            if (bytesRead >= 14)
                return BinaryUtils.parseMSDOSDateTime(data[10], data[11], data[12], data[13]);
        }
        return null;
    }
}
