package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OBOOntologyUpdater<D extends DataSource> extends Updater<D> {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{4})");

    public OBOOntologyUpdater(final D dataSource) {
        super(dataSource);
    }

    @Override
    public final Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            return getVersionFromDownloadFile();
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version number", e);
        }
    }

    private Version getVersionFromDownloadFile() throws IOException {
        return getVersionFromOBOUrl(getDownloadUrl(), this::getVersionFromDataVersionLine);
    }

    public static Version getVersionFromOBOUrl(final String url,
                                               final Function<String, Version> dataVersionParser) throws IOException {
        String dateLine = null;
        String dataVersionLine = null;
        String line;
        try (final var stream = HTTPClient.getUrlInputStream(url);
             final var bufferedReader = FileUtils.createBufferedReaderFromStream(stream.stream)) {
            while ((line = bufferedReader.readLine()) != null) {
                final String trimmedLine = line.trim();
                if (trimmedLine.startsWith("data-version:"))
                    dataVersionLine = line;
                else if (trimmedLine.startsWith("date:"))
                    dateLine = line;
                else if (trimmedLine.startsWith("[Term]"))
                    break;
            }
        }
        if (dataVersionLine != null)
            return dataVersionParser.apply(dataVersionLine);
        if (dateLine != null)
            return getVersionFromDate(dateLine);
        return null;
    }

    protected abstract String getDownloadUrl();

    protected abstract Version getVersionFromDataVersionLine(final String dataVersion);

    private static Version getVersionFromDate(final String date) {
        final Matcher matcher = DATE_PATTERN.matcher(date);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(1)));
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, getDownloadUrl(), getTargetFileName());
        return true;
    }

    protected abstract String getTargetFileName();
}
