package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HTTPFTPClient {
    public static class Entry {
        public String fullUrl;
        public String name;
        public String modificationDate;
        public String size;
    }

    private final String url;

    public HTTPFTPClient(final String url) {
        this.url = url;
    }

    public Entry[] listDirectory(final String path) throws IOException {
        final String fullDirectoryUrl = url + "/" + path;
        final String source = HTTPClient.getWebsiteSource(fullDirectoryUrl);
        return parseWebSource(path, source);
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

    private Entry[] parseWebSourceTable(final String path, Element table) {
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
        final Pattern entryPattern = Pattern.compile(
                "^(.+)\\s([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2})\\s+([0-9.]+[KMG]?)");
        final List<Entry> result = new ArrayList<>();
        final String[] lines = StringUtils.split(pre.text(), "\n");
        for (int i = 1; i < lines.length; i++) {
            final Matcher matcher = entryPattern.matcher(StringUtils.stripEnd(lines[i], "\r"));
            if (matcher.matches()) {
                final Entry entry = new Entry();
                entry.name = matcher.group(1).trim();
                entry.modificationDate = matcher.group(2);
                entry.size = matcher.group(3);
                entry.fullUrl = (url + "/" + path + "/" + entry.name).replace("//", "/");
                result.add(entry);
            }
        }
        return result.toArray(new Entry[0]);
    }
}
