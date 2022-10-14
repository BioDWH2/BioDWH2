package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class DrugCentralUpdater extends Updater<DrugCentralDataSource> {
    private static final String SQL_DUMP_FILE_PATH = "rawDrugCentral.sql.gz";
    private static final String DOWNLOAD_PAGE_URL = "https://drugcentral.org/ActiveDownload";
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile(
            "href=\"(https?://[a-zA-Z.\\-/]+drugcentral\\.dump\\.[0-9_]+\\.sql\\.gz)\"");

    public DrugCentralUpdater(final DrugCentralDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        final String url = getDrugCentralFileUrl();
        final String version = StringUtils.split(StringUtils.splitByWholeSeparator(url, "dump.")[1], '.')[0];
        if (version.contains("_")) {
            final String[] versionParts = StringUtils.split(version, '_');
            return parseVersion(versionParts[2] + "." + versionParts[0] + "." + versionParts[1]);
        }
        return parseVersion(version.substring(4, 8) + "." + version.substring(0, 2) + "." + version.substring(2, 4));
    }

    private String getDrugCentralFileUrl() throws UpdaterException {
        try {
            final String html = HTTPClient.getWebsiteSource(DOWNLOAD_PAGE_URL);
            final Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(html);
            if (matcher.find())
                return matcher.group(1);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        throw new UpdaterConnectionException("Failed to get database download URL from download page");
    }

    private Version parseVersion(final String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String dumpFilePath = dataSource.resolveSourceFilePath(workspace, SQL_DUMP_FILE_PATH);
        downloadDrugCentralDatabase(dumpFilePath);
        removeOldExtractedTsvFiles(workspace);
        extractTsvFilesFromDatabaseDump(workspace, dumpFilePath);
        return true;
    }

    private void downloadDrugCentralDatabase(final String dumpFilePath) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(getDrugCentralFileUrl(), dumpFilePath);
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

    private void extractTsvFilesFromDatabaseDump(final Workspace workspace,
                                                 final String dumpFilePath) throws UpdaterException {
        try (BufferedReader reader = getBufferedReaderFromFile(dumpFilePath)) {
            final StringBuilder schema = new StringBuilder();
            PrintWriter writer = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("COPY")) {
                    writer = getTsvWriterFromCopyLine(workspace, line);
                } else if (line.trim().startsWith("\\.")) {
                    if (writer != null) {
                        writer.close();
                    }
                    writer = null;
                } else if (writer != null) {
                    writer.println(line.replace("\\N", ""));
                } else if (!line.startsWith("--")) {
                    schema.append(line).append("\n");
                }
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

    private BufferedReader getBufferedReaderFromFile(final String filePath) throws IOException {
        final GZIPInputStream zipStream = new GZIPInputStream(new FileInputStream(filePath));
        return new BufferedReader(new InputStreamReader(zipStream, StandardCharsets.UTF_8));
    }

    private PrintWriter getTsvWriterFromCopyLine(final Workspace workspace,
                                                 final String line) throws FileNotFoundException {
        final String tableName = StringUtils.split(line, ' ')[1].split("\\.")[1];
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
