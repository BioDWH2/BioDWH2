package de.unibi.agbi.biodwh2.core.net;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.BinaryUtils;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class HTTPClient {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private HTTPClient() {
    }

    public static void downloadStream(final StreamWithContentLength stream, final String filePath,
                                      final BiConsumer<Long, Long> progressReporter) throws IOException {
        downloadStream(stream, Paths.get(filePath), progressReporter);
    }

    public static void downloadStream(final StreamWithContentLength stream, final Path filePath,
                                      final BiConsumer<Long, Long> progressReporter) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(stream.stream);
             FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            final WritableByteChannel outputChannel = Channels.newChannel(outputStream);
            fastCopy(stream.contentLength, urlByteChannel, outputChannel, progressReporter);
            //outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void downloadStream(final StreamWithContentLength inputStream, final OutputStream outputStream,
                                      final BiConsumer<Long, Long> progressReporter) throws IOException {
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(inputStream.stream)) {
            final WritableByteChannel outputChannel = Channels.newChannel(outputStream);
            fastCopy(inputStream.contentLength, urlByteChannel, outputChannel, progressReporter);
        }
    }

    public static void fastCopy(final Long length, final ReadableByteChannel in, final WritableByteChannel out,
                                final BiConsumer<Long, Long> progressReporter) throws IOException {
        long offset = 0;
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (in.read(buffer) != -1) {
            buffer.flip();
            offset += out.write(buffer);
            buffer.compact();
            if (progressReporter != null)
                progressReporter.accept(offset, length);
        }
        buffer.flip();
        while (buffer.hasRemaining())
            out.write(buffer);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath) throws IOException {
        downloadStream(getUrlInputStream(uri), filePath, null);
    }

    public static void downloadFileAsBrowser(final String uri, final Path filePath,
                                             final BiConsumer<Long, Long> progressReporter) throws IOException {
        downloadStream(getUrlInputStream(uri), filePath, progressReporter);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath,
                                             final BiConsumer<Long, Long> progressReporter) throws IOException {
        downloadStream(getUrlInputStream(uri), filePath, progressReporter);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password), filePath, null);
    }

    public static void downloadFileAsBrowser(final String uri, final Path filePath, final String username,
                                             final String password,
                                             final BiConsumer<Long, Long> progressReporter) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password), filePath, progressReporter);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password,
                                             final BiConsumer<Long, Long> progressReporter) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password), filePath, progressReporter);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password,
                                             final Map<String, String> additionalHeaders) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password, additionalHeaders), filePath, null);
    }

    public static void downloadFileAsBrowser(final String uri, final String filePath, final String username,
                                             final String password, final Map<String, String> additionalHeaders,
                                             final BiConsumer<Long, Long> progressReporter) throws IOException {
        downloadStream(getUrlInputStream(uri, username, password, additionalHeaders), filePath, progressReporter);
    }

    public static String getWebsiteSource(final String url) throws IOException {
        return getWebsiteSource(url, null, null, 0, null);
    }

    public static String getWebsiteSource(final String url,
                                          final Map<String, String> additionalHeaders) throws IOException {
        return getWebsiteSource(url, null, null, 0, additionalHeaders);
    }

    public static String getWebsiteSource(final String url, int retries) throws IOException {
        return getWebsiteSource(url, null, null, retries, null);
    }

    public static String getWebsiteSource(final String url, final String username,
                                          final String password) throws IOException {
        return getWebsiteSource(url, username, password, 0, null);
    }

    public static String getWebsiteSource(final String url, final String username, final String password,
                                          int retries) throws IOException {
        return getWebsiteSource(url, username, password, retries, null);
    }

    public static String getWebsiteSource(final String url, final String username, final String password, int retries,
                                          final Map<String, String> additionalHeaders) throws IOException {
        int counter = 0;
        while (counter <= retries) {
            StringBuilder result = new StringBuilder();
            try (final var stream = getUrlInputStream(url, username, password, additionalHeaders)) {
                final var reader = FileUtils.createBufferedReaderFromStream(stream.stream);
                String inputLine = reader.readLine();
                while (inputLine != null) {
                    result.append(inputLine).append('\n');
                    inputLine = reader.readLine();
                }
            } catch (IOException ex) {
                if (counter < retries) {
                    counter++;
                    try {
                        // Small wait to not overpower the server
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }
                throw ex;
            }
            return result.toString();
        }
        return null;
    }

    public static StreamWithContentLength getUrlInputStream(final String url) throws IOException {
        return getUrlInputStream(url, null, null, null);
    }

    public static StreamWithContentLength getUrlInputStream(final String url, final String username,
                                                            final String password) throws IOException {
        return getUrlInputStream(url, username, password, null);
    }

    public static StreamWithContentLength getUrlInputStream(final String url, final String username,
                                                            final String password,
                                                            final Map<String, String> additionalHeaders) throws IOException {
        var urlConnection = (HttpURLConnection) new URL(url).openConnection();
        if (username != null && password != null)
            urlConnection.setRequestProperty("Authorization", getBasicAuthForCredentials(username, password));
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        if (additionalHeaders != null)
            for (final Map.Entry<String, String> entry : additionalHeaders.entrySet())
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        urlConnection = redirectURLConnectionIfNecessary(urlConnection);
        final var result = new StreamWithContentLength();
        result.stream = urlConnection.getInputStream();
        if (result.stream != null)
            result.contentLength = urlConnection.getContentLengthLong();
        return result;
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
        final var stream = getUrlInputStream(url);
        if (stream.stream != null) {
            final byte[] data = new byte[14];
            final int bytesRead = stream.stream.read(data);
            stream.stream.close();
            if (bytesRead >= 14)
                return BinaryUtils.parseMSDOSDateTime(data[10], data[11], data[12], data[13]);
        }
        return null;
    }

    public static List<JsonNode> findSchemaAnnotationsFromWebsiteSource(final String url) throws IOException {
        final String source = getWebsiteSource(url);
        final List<JsonNode> result = new ArrayList<>();
        final Document document = Jsoup.parse(source);
        final ObjectMapper mapper = new ObjectMapper();
        for (final Element script : document.select("script"))
            if (script.hasAttr("type") && "application/ld+json".equalsIgnoreCase(script.attr("type")))
                mapper.readTree(script.html());
        return result;
    }

    public static class StreamWithContentLength implements AutoCloseable {
        public InputStream stream;
        public Long contentLength;

        @Override
        public void close() throws IOException {
            if (stream != null)
                stream.close();
        }
    }
}
