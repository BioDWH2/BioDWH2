package de.unibi.agbi.biodwh2.hmdb.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.hmdb.HMDBDataSource;
import de.unibi.agbi.biodwh2.hmdb.model.Metabolite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HMDBGraphExporter extends GraphExporter<HMDBDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(HMDBGraphExporter.class);

    public HMDBGraphExporter(final HMDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        exportMetabolites(workspace, graph);
        return false;
    }

    private void exportMetabolites(final Workspace workspace, final Graph graph) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, HMDBUpdater.METABOLITES_XML_FILE_NAME);
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to find file '" + HMDBUpdater.METABOLITES_XML_FILE_NAME + "'");
        try {
            final ZipInputStream zipInputStream = openZipInputStream(zipFile);
            int counter = 1;
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".xml")) {
                    final XmlMapper xmlMapper = new XmlMapper();
                    final FromXmlParser parser = createXmlParser(zipInputStream, xmlMapper);
                    // Skip the first structure token which is the root DrugBank node
                    //noinspection UnusedAssignment
                    JsonToken token = parser.nextToken();
                    while ((token = parser.nextToken()) != null)
                        if (token.isStructStart()) {
                            if (counter % 250 == 0 && LOGGER.isInfoEnabled())
                                LOGGER.info("Exporting metabolites progress " + counter);
                            counter++;
                            exportMetabolite(graph, xmlMapper.readValue(parser, Metabolite.class));
                        }
                }
            }
        } catch (IOException | XMLStreamException e) {
            throw new ExporterFormatException(
                    "Failed to parse the file '" + HMDBUpdater.METABOLITES_XML_FILE_NAME + "'", e);
        }
    }

    private static ZipInputStream openZipInputStream(final File file) throws FileNotFoundException {
        final FileInputStream inputStream = new FileInputStream(file);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return new ZipInputStream(bufferedInputStream);
    }

    private FromXmlParser createXmlParser(final InputStream stream,
                                          final XmlMapper xmlMapper) throws IOException, XMLStreamException {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(stream,
                                                                                   StandardCharsets.UTF_8.name());
        return xmlMapper.getFactory().createParser(streamReader);
    }

    private void exportMetabolite(final Graph graph, final Metabolite metabolite) {
        // TODO
    }
}
