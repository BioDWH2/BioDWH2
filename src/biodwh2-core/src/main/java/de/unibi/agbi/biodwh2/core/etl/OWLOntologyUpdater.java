package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.function.Function;

public abstract class OWLOntologyUpdater<D extends DataSource> extends Updater<D> {
    public OWLOntologyUpdater(final D dataSource) {
        super(dataSource);
    }

    @Override
    public final Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            return getVersionFromDownloadFile();
        } catch (IOException | XMLStreamException e) {
            throw new UpdaterConnectionException("Failed to retrieve version number", e);
        }
    }

    private Version getVersionFromDownloadFile() throws IOException, XMLStreamException {
        return getVersionFromOWLUrl(getDownloadUrl(), this::getVersionFromDataVersionLine);
    }

    public static Version getVersionFromOWLUrl(final String url,
                                               final Function<String, Version> dataVersionParser) throws IOException, XMLStreamException {
        String dataVersionLine = null;
        final XmlMapper xmlMapper = new XmlMapper();
        try (final var stream = HTTPClient.getUrlInputStream(url); final var parser = FileUtils.createXmlParser(
                stream.stream, xmlMapper)) {
            JsonToken token;
            while ((token = parser.nextToken()) != null)
                if (token.isStructStart()) {
                    try {
                        if ("versionIRI".equals(parser.currentName())) {
                            final var versionIRI = xmlMapper.readValue(parser, VersionIRI.class);
                            dataVersionLine = versionIRI.resource;
                        }
                    } catch (Exception ignored) {
                    }
                }
        }
        return dataVersionLine != null ? dataVersionParser.apply(dataVersionLine) : null;
    }

    protected abstract String getDownloadUrl();

    protected abstract Version getVersionFromDataVersionLine(final String dataVersion);

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, getDownloadUrl(), getTargetFileName());
        return true;
    }

    protected abstract String getTargetFileName();

    private static class VersionIRI {
        @JacksonXmlProperty(isAttribute = true, localName = "resource")
        public String resource;
    }
}
