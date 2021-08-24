package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HTTPFTPClient {
    public static class Entry {
        public String fullUrl;
        public String name;
        public String modificationDate;
        public String size;
    }

    private static final String PRE_TABLE_ENTRY_REGEX = "<a\\s+href=\"([a-zA-Z0-9-_.:/]+)\">([a-zA-Z0-9-_.&;]+)</a>\\s+(([0-9]{4}-[0-9]{2}-[0-9]{2}|[0-9]{2}-[A-Z][a-z]+-[0-9]{4})\\s+[0-9]{2}:[0-9]{2})\\s+([0-9.]+[KMG]?)";

    private final String url;
    private final Map<String, Entry[]> entryCache;

    public HTTPFTPClient(final String url) {
        this.url = url;
        entryCache = new HashMap<>();
    }

    public Entry[] listDirectory() throws IOException {
        return listDirectory(null);
    }

    public Entry[] listDirectory(final String path) throws IOException {
        final String fullDirectoryUrl = path == null ? url : url + "/" + path;
        if (entryCache.containsKey(fullDirectoryUrl))
            return entryCache.get(fullDirectoryUrl);
        final String source = HTTPClient.getWebsiteSource(fullDirectoryUrl);
        final Entry[] entries = parseWebSource(path, source);
        entryCache.put(fullDirectoryUrl, entries);
        return entries;
    }

    Entry[] parseWebSource(final String path, final String source) {
        final Document document = Jsoup.parse(source);
        final Element table = document.select("table").first();
        if (table != null)
            return parseWebSourceTable(path, table);
        final Element pre = document.select("pre").first();
        if (pre != null)
            return parseWebSourcePre(path, pre);
        return new Entry[0];
    }

    private Entry[] parseWebSourceTable(final String path, final Element table) {
        final List<Entry> result = new ArrayList<>();
        for (final Element row : table.select("tr")) {
            final Elements columns = row.select("td");
            if (columns != null && columns.size() > 0 && !"Parent Directory".equals(columns.get(1).text())) {
                final Entry entry = new Entry();
                entry.name = columns.get(1).text().trim();
                entry.modificationDate = columns.get(2).text().trim();
                entry.size = columns.get(3).text().trim();
                entry.fullUrl = (url + "/" + path + "/" + entry.name).replace("//", "/");
                result.add(entry);
            }
        }
        return result.toArray(new Entry[0]);
    }

    private Entry[] parseWebSourcePre(final String path, final Element pre) {
        final Pattern entryPattern = Pattern.compile(PRE_TABLE_ENTRY_REGEX, Pattern.MULTILINE);
        final List<Entry> result = new ArrayList<>();
        final Matcher matcher = entryPattern.matcher(pre.html());
        while (matcher.find()) {
            final Entry entry = new Entry();
            final String filePath = matcher.group(1).trim();
            try {
                entry.name = FilenameUtils.getName(new URL(filePath).getPath());
            } catch (MalformedURLException ignored) {
                entry.name = matcher.group(2).trim();
            }
            entry.modificationDate = matcher.group(3);
            entry.size = matcher.group(5);
            if (filePath.startsWith("http://") || filePath.startsWith("https://"))
                entry.fullUrl = filePath;
            else
                entry.fullUrl = StringUtils.stripEnd(url, "/") + "/" + StringUtils.stripEnd(path, "/") + "/" +
                                entry.name;
            result.add(entry);
        }
        return result.toArray(new Entry[0]);
    }
}
