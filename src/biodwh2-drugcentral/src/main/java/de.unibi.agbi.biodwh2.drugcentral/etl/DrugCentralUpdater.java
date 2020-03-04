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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class DrugCentralUpdater extends Updater<DrugCentralDataSource> {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            String html = HTTPClient.getWebsiteSource("http://drugcentral.org/download");
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

    private URL getDrugCentralFileURL() throws UpdaterException {
        try {
            String html = HTTPClient.getWebsiteSource("http://drugcentral.org/download");
            for (String word : html.split(" {4}")) {
                if (word.contains("drugcentral.dump.")) {
                    return new URL(word.split("\"")[3]);
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
        String line;
        String dumpFilePath = dataSource.resolveSourceFilePath(workspace, "rawDrugCentral.sql.gz");
        File newFile = new File(dumpFilePath);
        URL dcURL = getDrugCentralFileURL();
        try {
            FileUtils.copyURLToFile(dcURL, newFile);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new GZIPInputStream(new FileInputStream(dumpFilePath)),
                                          StandardCharsets.UTF_8));
            PrintWriter writer = null;
            boolean copy = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("COPY")) {
                    String tableName = line.split(" ")[1].split("\\.")[1];
                    String attributes = StringUtils.join(line.split("\\(")[1].split("\\)")[0].split(", "), "\t");
                    copy = true;
                    writer = new PrintWriter(dataSource.resolveSourceFilePath(workspace, tableName + ".tsv"));
                    writer.println(attributes);
                } else if (line.contains("\\.")) {
                    copy = false;
                    if (writer != null) {
                        writer.close();
                        writer = null;
                    }
                } else if (copy)
                    writer.println(line);
            }
            reader.close();
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }
}
