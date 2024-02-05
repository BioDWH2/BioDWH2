package de.unibi.agbi.biodwh2.guidetopharmacology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.guidetopharmacology.GuideToPharmacologyDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GuideToPharmacologyUpdater extends Updater<GuideToPharmacologyDataSource> {
    private static final String SQL_DUMP_FILE_PATH = "public_iuphardb.zip";
    private static final String BASE_PAGE_URL = "https://www.guidetopharmacology.org/";
    private static final String DOWNLOAD_PAGE_URL = BASE_PAGE_URL + "download.jsp";
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile(
            "/DATA/public_iuphardb_v([0-9]{4}\\.[0-9]+).zip");

    public GuideToPharmacologyUpdater(final GuideToPharmacologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final Map<Version, String> versions = getFileUrlsWithVersion();
        final Optional<Version> maxVersion = versions.keySet().stream().max(Version::compareTo);
        return maxVersion.orElse(null);
    }

    private Map<Version, String> getFileUrlsWithVersion() throws UpdaterException {
        final Map<Version, String> result = new HashMap<>();
        final String html = getWebsiteSource(DOWNLOAD_PAGE_URL);
        final Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(html);
        while (matcher.find())
            result.put(Version.tryParse(matcher.group(1)), BASE_PAGE_URL + matcher.group(0));
        if (!result.isEmpty())
            return result;
        throw new UpdaterConnectionException("Failed to get database download URL from download page");
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String dumpFilePath = dataSource.resolveSourceFilePath(workspace, SQL_DUMP_FILE_PATH);
        downloadDatabase(dumpFilePath);
        removeOldExtractedTsvFiles(workspace);
        extractTsvFilesFromDatabaseDump(workspace);
        return true;
    }

    private void downloadDatabase(final String dumpFilePath) throws UpdaterException {
        try {
            final Map<Version, String> versions = getFileUrlsWithVersion();
            final Optional<Version> maxVersion = versions.keySet().stream().max(Version::compareTo);
            if (maxVersion.isPresent())
                HTTPClient.downloadFileAsBrowser(versions.get(maxVersion.get()), dumpFilePath);
            else
                throw new UpdaterConnectionException("Failed to get database download URL from download page");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private void removeOldExtractedTsvFiles(final Workspace workspace) {
        final String[] files = dataSource.listSourceFiles(workspace);
        for (final String file : files)
            if (file.endsWith(".tsv") || file.endsWith(".sql"))
                //noinspection ResultOfMethodCallIgnored
                new File(dataSource.resolveSourceFilePath(workspace, file)).delete();
    }

    private void extractTsvFilesFromDatabaseDump(final Workspace workspace) throws UpdaterException {
        try (BufferedReader reader = getBufferedReaderFromFile(workspace)) {
            final StringBuilder schema = new StringBuilder();
            PrintWriter writer = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("COPY"))
                    writer = getTsvWriterFromCopyLine(workspace, line);
                else if (line.trim().startsWith("\\.")) {
                    if (writer != null)
                        writer.close();
                    writer = null;
                } else if (writer != null)
                    writer.println(line.replace("\\N", ""));
                else
                    schema.append(line).append("\n");
            }
            if (writer != null)
                writer.close();
            writer = new PrintWriter(dataSource.resolveSourceFilePath(workspace, "schema.sql"));
            writer.println(schema);
            writer.close();
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private BufferedReader getBufferedReaderFromFile(final Workspace workspace) throws IOException {
        final ZipInputStream zipStream = FileUtils.openZip(workspace, dataSource, SQL_DUMP_FILE_PATH);
        ZipEntry entry;
        do {
            entry = zipStream.getNextEntry();
        } while (entry != null && !entry.getName().endsWith(".dmp"));
        return new BufferedReader(new InputStreamReader(zipStream, StandardCharsets.UTF_8));
    }

    private PrintWriter getTsvWriterFromCopyLine(final Workspace workspace,
                                                 final String line) throws FileNotFoundException {
        final String fullTableName = StringUtils.split(line, ' ')[1];
        final String[] fullTableNameParts = fullTableName.split("\\.");
        final String tableName = fullTableNameParts[fullTableNameParts.length - 1];
        final PrintWriter writer = new PrintWriter(dataSource.resolveSourceFilePath(workspace, tableName + ".tsv"));
        final String columnNames = StringUtils.join(line.split("\\(")[1].split("\\)")[0].split(", "), '\t');
        writer.println(columnNames);
        return writer;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{SQL_DUMP_FILE_PATH};
    }
}
