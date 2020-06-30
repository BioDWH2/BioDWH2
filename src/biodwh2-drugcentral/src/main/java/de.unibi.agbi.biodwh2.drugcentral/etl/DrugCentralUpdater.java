package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class DrugCentralUpdater extends Updater<DrugCentralDataSource> {
    private static final String DownloadPageUrl = "http://drugcentral.org/ActiveDownload";

    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            String html = HTTPClient.getWebsiteSource(DownloadPageUrl);
            for (String word : html.split(" {4}")) {
                if (word.contains("drugcentral.dump.")) {
                    String version = word.split("href=\"")[1].split("\\.")[3];
                    return parseVersion(
                            version.substring(4) + "." + version.substring(0, 2) + "." + version.substring(2, 4));
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
    protected boolean tryUpdateFiles(Workspace workspace, DrugCentralDataSource dataSource) throws UpdaterException {
        final String dumpFilePath = dataSource.resolveSourceFilePath(workspace, "rawDrugCentral.sql.gz");
        downloadDrugCentralDatabase(dumpFilePath);
        removeOldExtractedTsvFiles(workspace, dataSource);
        extractTsvFilesFromDatabaseDump(workspace, dataSource, dumpFilePath);
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
                if (word.contains("drugcentral.dump."))
                    return new URL(word.split("\"")[3]);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        throw new UpdaterConnectionException("Failed to get database download URL from download page");
    }

    private void removeOldExtractedTsvFiles(final Workspace workspace, final DataSource dataSource) {
        String[] files = dataSource.listSourceFiles(workspace);
        for (String file : files)
            if (file.endsWith(".tsv"))
                //noinspection ResultOfMethodCallIgnored
                new File(dataSource.resolveSourceFilePath(workspace, file)).delete();
    }

    private void extractTsvFilesFromDatabaseDump(final Workspace workspace, final DrugCentralDataSource dataSource,
                                                 final String dumpFilePath) throws UpdaterException {
        try (BufferedReader reader = getBufferedReaderFromFile(dumpFilePath)) {
            PrintWriter writer = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("COPY"))
                    writer = getTsvWriterFromCopyLine(workspace, dataSource, line);
                else if (line.contains("\\.")) {
                    if (writer != null)
                        writer.close();
                    writer = null;
                } else if (writer != null)
                    writer.println(line.replace("\\N", ""));
            }
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private BufferedReader getBufferedReaderFromFile(final String filePath) throws IOException {
        GZIPInputStream zipStream = new GZIPInputStream(new FileInputStream(filePath));
        return new BufferedReader(new InputStreamReader(zipStream, StandardCharsets.UTF_8));
    }

    private PrintWriter getTsvWriterFromCopyLine(final Workspace workspace, final DataSource dataSource,
                                                 final String line) throws FileNotFoundException {
        String tableName = line.split(" ")[1].split("\\.")[1];
        PrintWriter writer = new PrintWriter(dataSource.resolveSourceFilePath(workspace, tableName + ".tsv"));
        String columnNames = StringUtils.join(line.split("\\(")[1].split("\\)")[0].split(", "), "\t");
        writer.println(columnNames);
        return writer;
    }
}
