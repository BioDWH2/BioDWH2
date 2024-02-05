package de.unibi.agbi.biodwh2.brenda.etl;

import de.unibi.agbi.biodwh2.brenda.BrendaDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrendaUpdater extends Updater<BrendaDataSource> {
    private static final String VERSION_URL = "https://www.brenda-enzymes.org/download.php";
    private static final Pattern VERSION_PATTERN = Pattern.compile("Release (2\\d{3}\\.\\d)");
    static final String FILE_NAME = "brenda.json.tar.gz";

    public BrendaUpdater(final BrendaDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find())
            return Version.tryParse(matcher.group(1));
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        if (!dataSource.getBooleanProperty(workspace, BrendaDataSource.LICENSE_ACCEPTED_PROPERTY_KEY))
            throw new UpdaterException(
                    "BRENDA license not yet accepted. Please add the data source property '\"BRENDA\": { \"" +
                    BrendaDataSource.LICENSE_ACCEPTED_PROPERTY_KEY +
                    "\": true }' to your workspace config file after reading the license at " +
                    dataSource.getLicenseUrl());
        try (ReadableByteChannel urlByteChannel = Channels.newChannel(getUrlInputStream());
             FileOutputStream outputStream = new FileOutputStream(
                     dataSource.resolveSourceFilePath(workspace, FILE_NAME))) {
            outputStream.getChannel().transferFrom(urlByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    public static InputStream getUrlInputStream() throws IOException {
        String urlParameters = "dlfile=dl-json&accept-license=1";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", HTTPClient.USER_AGENT);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
            wr.write(postData);
        }
        urlConnection.connect();
        return urlConnection.getInputStream();
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
