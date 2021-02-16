package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class DrugCentralUpdater extends Updater<DrugCentralDataSource> {
    private static final String DownloadPageUrl = "https://drugcentral.org/ActiveDownload";

    public DrugCentralUpdater(DrugCentralDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            String html = HTTPClient.getWebsiteSource(DownloadPageUrl);
            for (String word : html.split(" {4}")) {
                if (word.contains("drugcentral-pgdump_")) {
                    String version = word.split("drugcentral-pgdump_")[1].split("\\.")[0];
                    return parseVersion(
                            version.substring(0, 4) + "." + version.substring(4, 6) + "." + version.substring(6));
                }
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return null;
    }

    private Version parseVersion(String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        final String dumpFilePath = dataSource.resolveSourceFilePath(workspace, "rawDrugCentral.sql.gz");
        downloadDrugCentralDatabase(dumpFilePath);
        removeOldExtractedTsvFiles(workspace);
        extractTsvFilesFromDatabaseDump(workspace, dumpFilePath);
        return true;
    }

    private void downloadDrugCentralDatabase(final String dumpFilePath) throws UpdaterException {
        File newFile = new File(dumpFilePath);
        URL downloadFileUrl = getDrugCentralFileUrl();
        try {
            FileUtils.copyURLToFile(downloadFileUrl, newFile);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private URL getDrugCentralFileUrl() throws UpdaterException {
        try {
            String html = HTTPClient.getWebsiteSource(DownloadPageUrl);
            for (String word : html.split(" {4}"))
                if (word.contains("drugcentral-pgdump_"))
                    return resolveRedirectUrl(new URL(word.split("\"")[3]));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        throw new UpdaterConnectionException("Failed to get database download URL from download page");
    }

    private URL resolveRedirectUrl(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) (url.openConnection());
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        String location = connection.getHeaderField("Location");
        return StringUtils.isNotEmpty(location) ? new URL(location) : url;
    }

    private void removeOldExtractedTsvFiles(final Workspace workspace) {
        String[] files = dataSource.listSourceFiles(workspace);
        for (String file : files)
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
            writer.println(schema.toString());
            writer.close();
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private BufferedReader getBufferedReaderFromFile(final String filePath) throws IOException {
        GZIPInputStream zipStream = new GZIPInputStream(new FileInputStream(filePath));
        return new BufferedReader(new InputStreamReader(zipStream, StandardCharsets.UTF_8));
    }

    private PrintWriter getTsvWriterFromCopyLine(final Workspace workspace,
                                                 final String line) throws FileNotFoundException {
        String tableName = line.split(" ")[1].split("\\.")[1];
        PrintWriter writer = new PrintWriter(dataSource.resolveSourceFilePath(workspace, tableName + ".tsv"));
        String columnNames = StringUtils.join(line.split("\\(")[1].split("\\)")[0].split(", "), "\t");
        writer.println(columnNames);
        return writer;
    }
}
