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
        String fullDirectoryUrl = url + "/" + path;
        String source = HTTPClient.getWebsiteSource(fullDirectoryUrl);
        return parseWebSource(path, source);
    }

    Entry[] parseWebSource(final String path, final String source) {
        Document document = Jsoup.parse(source);
        Element table = document.select("table").first();
        if (table != null)
            return parseWebSourceTable(path, table);
        Element pre = document.select("pre").first();
        if (pre != null)
            return parseWebSourcePre(path, pre);
        return null;
    }

    private Entry[] parseWebSourceTable(final String path, Element table) {
        List<Entry> result = new ArrayList<>();
        for (Element row : table.select("tr")) {
            Elements columns = row.select("td");
            if (columns != null && columns.size() > 0 && !"Parent Directory".equals(columns.get(1).text())) {
                Entry entry = new Entry();
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
        Pattern entryPattern = Pattern.compile(
                "^(.+)\\s([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2})\\s+([0-9.]+[KMG]?)");
        List<Entry> result = new ArrayList<>();
        String[] lines = StringUtils.split(pre.text(), "\n");
        for (int i = 1; i < lines.length; i++) {
            Matcher matcher = entryPattern.matcher(StringUtils.stripEnd(lines[i], "\r"));
            if (matcher.matches()) {
                Entry entry = new Entry();
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
